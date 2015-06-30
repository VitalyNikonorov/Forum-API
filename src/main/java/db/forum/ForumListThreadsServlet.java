package db.forum;

import db.user.UserInfo;
import main.DBConnectionPool;
import main.Main;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Map;

/**
 * Created by vitaly on 20.06.15.
 */
public class ForumListThreadsServlet extends HttpServlet {

    public void doGet(HttpServletRequest request,
                      HttpServletResponse response) throws ServletException, IOException {

        Map<String, String[]> paramMap = request.getParameterMap();
        String forum = paramMap.containsKey("forum") ? paramMap.get("forum")[0] : null;
        String order = paramMap.containsKey("order") ? paramMap.get("order")[0] : null;
        String since = paramMap.containsKey("since") ? paramMap.get("since")[0] : null;
        String limit = paramMap.containsKey("limit") ? paramMap.get("limit")[0] : null;
        String[] related = paramMap.get("related");

        short status = 0;
        String message = "";

        ResultSet resultSet;
        Statement sqlQuery = null;
        Connection conn = null;
        try {
            conn = Main.dataSource.getConnection();
            Main.connectionPool.printStatus();

            sqlQuery = conn.createStatement();

            String sqlSelect = "SELECT * FROM thread WHERE forum=\'" +forum+ "\' " ;

            if (since != null) {
                sqlSelect = sqlSelect + "AND date_of_creating > '" + since + "\' ";
            }

            sqlSelect = sqlSelect + " ORDER BY " + " date_of_creating " +order;


            if (limit != null){
                sqlSelect = sqlSelect + " LIMIT " +limit +";";
            }else{
                sqlSelect = sqlSelect + ";";
            }

            resultSet = sqlQuery.executeQuery(sqlSelect);

            createResponse(conn, response, status, message, resultSet, related);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        Main.connectionPool.printStatus();
    }

    private void createResponse(Connection conn, HttpServletResponse response, short status, String message, ResultSet resultSet, String[] related) throws IOException, SQLException {
        response.setContentType("json;charset=UTF-8");
        response.setHeader("Cache-Control", "no-cache");
        response.setStatus(HttpServletResponse.SC_OK);

        JSONObject obj = new JSONObject();
        JSONObject data = new JSONObject();

        ArrayList<JSONObject> listOfResponse =  new ArrayList<JSONObject>();

        if (status != 0 || resultSet == null) {
            data.put("error", message);
            obj.put("response", data);
        } else {
            boolean user = false;
            boolean forum = false;
            if (related != null) {
                if (related != null) {
                    for (String aRelated : related) {
                        switch (aRelated) {
                            case "user":
                                user = true;
                                break;
                            case "forum":
                                forum = true;
                                break;
                            default:
                                status = 3;
                                message = "Wrong JSON";
                        }
                    }
                }
            }
            if (status == 0) {
                int i = 0;
                while (resultSet.next()) {
                    listOfResponse.add(i, getThreadDetailsById(conn, resultSet.getInt("id"), user, forum));
                    i++;
                }
                obj.put("response", listOfResponse);
            } else {
                data.put("error", message);
                obj.put("response", data);
            }
        }
        obj.put("code", status);
        response.getWriter().write(obj.toString());
    }

    public JSONObject getThreadDetailsById(Connection conn, int id, boolean user, boolean forum) throws IOException, SQLException{

        ResultSet resultSet;
        ResultSet resultSetCount;

        PreparedStatement pstmt = conn.prepareStatement("select * from thread where id = ?");
        pstmt.setInt(1, id);

        resultSet = pstmt.executeQuery();

        PreparedStatement pstmtCountPosts = conn.prepareStatement("select count(*) as amount from post where thread = " + id + " and isDeleted = 0;");

        resultSetCount = pstmtCountPosts.executeQuery();

        JSONObject data = new JSONObject();

        if (resultSet.next() && resultSetCount.next()) {
            data.put("date", resultSet.getString("date_of_creating").substring(0, 19));
            data.put("dislikes", resultSet.getInt("dislikes"));
            if (forum) {
                //TODO  - исправить - вынести в отельную функцию


                JSONObject forumData = new JSONObject();
                PreparedStatement pstmtForum = null;
                try {
                    ResultSet resultSetForum;

                    pstmtForum = conn.prepareStatement("select * from forum where short_name = ?");
                    pstmtForum.setString(1, resultSet.getString("forum"));
                    resultSetForum = pstmtForum.executeQuery();

                    if (resultSetForum.next()) {
                        forumData.put("user", resultSetForum.getString("user_email"));
                        forumData.put("name", resultSetForum.getString("name"));
                        forumData.put("id", resultSetForum.getInt("id"));
                        forumData.put("short_name", resultSetForum.getString("short_name"));
                    }
                }catch(SQLException ex) {
                    ex.printStackTrace();
                }
                data.put("forum", forumData);

            } else {
                data.put("forum", resultSet.getString("forum"));
            }
            data.put("id", resultSet.getInt("id"));
            data.put("isClosed", resultSet.getBoolean("isClosed"));
            data.put("isDeleted", resultSet.getBoolean("isDeleted"));
            data.put("likes", resultSet.getInt("likes"));
            data.put("message", resultSet.getString("message"));
            data.put("points", resultSet.getInt("likes") - resultSet.getInt("dislikes") );
            data.put("posts", resultSetCount.getInt("amount"));
            data.put("slug", resultSet.getString("slug"));
            data.put("title", resultSet.getString("title"));
            if (user) {
                data.put("user", UserInfo.getFullUserInfo(conn, resultSet.getString("user_email")).get("response"));
            } else {
                data.put("user", resultSet.getString("user_email"));
            }
        } else {
            String message = "There is no thread with such id!";
            data.put("error", message);
        }

        return data;
    }

}
