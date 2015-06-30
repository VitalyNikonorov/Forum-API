package db.post;

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
import java.util.Map;

/**
 * Created by vitaly on 20.06.15.
 */
public class GetPostDetailsServlet extends HttpServlet {
    public void doGet(HttpServletRequest request,
                      HttpServletResponse response) throws ServletException, IOException {
        Map<String, String[]> paramMap = request.getParameterMap();
        int id = Integer.parseInt(paramMap.containsKey("post") ? paramMap.get("post")[0] : "0");
        String[] related = paramMap.get("related");
        Connection conn = null;
        try {
            conn = Main.dataSource.getConnection();
            Main.connectionPool.printStatus();

            createResponse(conn, response, id, related);
        } catch (SQLException e) {
            e.printStackTrace();
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
    }

    private void createResponse(Connection conn, HttpServletResponse response, int id, String[] related) throws IOException, SQLException {
        response.setContentType("json;charset=UTF-8");
        response.setHeader("Cache-Control", "no-cache");
        response.setStatus(HttpServletResponse.SC_OK);

        JSONObject obj = new JSONObject();
        boolean user = false;
        boolean forum = false;
        boolean thread = false;
        String message = "";
        short status = 0;

        if (id == 0) {
            status = 3;
            message = "Incorrect JSON";
        }

        if (related != null) {
            for (String aRelated : related) {
                switch (aRelated) {
                    case "user":
                        user = true;
                        break;
                    case "forum":
                        forum = true;
                        break;
                    case "thread":
                        thread = true;
                        break;
                    default:
                        status = 3;
                        message = "Incorrect JSON";
                }
            }
        }
        JSONObject data;
        data = getPostDetails(conn, id, user, thread, forum);
        if (data == null) {
            status = 1;
            message = "There is no such post";
        }
        obj.put("response", status == 0 ? data: message);
        obj.put("code", status);
        response.getWriter().write(obj.toString());
    }

    public JSONObject getPostDetails(Connection conn, int id, boolean user, boolean thread, boolean forum) throws IOException, SQLException {

        JSONObject data = new JSONObject();
        ResultSet resultSet = null;
        try {
            PreparedStatement stmt;
            stmt = conn.prepareStatement("select * from post where id = ?");
            stmt.setInt(1, id);
            resultSet = stmt.executeQuery();

            if (resultSet.next()) {
                data.put("date", resultSet.getString("date_of_creating").substring(0, 19));
                if (forum) {
                    //////////// TODO в функцию
                    data.put("forum", getForumDetails(conn, resultSet.getString("forum")));
                } else {
                    data.put("forum", resultSet.getString("forum"));
                }
                data.put("id", resultSet.getInt("id"));
                data.put("isApproved", resultSet.getBoolean("isApproved"));
                data.put("isHighlighted", resultSet.getInt("isHighlighted") == 1 ? true : false);
                data.put("isEdited", resultSet.getBoolean("isEdited"));
                data.put("isSpam", resultSet.getBoolean("isSpam"));
                data.put("isDeleted", resultSet.getBoolean("isDeleted"));
                data.put("message", resultSet.getString("message"));
                data.put("likes", resultSet.getInt("likes"));
                data.put("dislikes", resultSet.getInt("dislikes"));
                data.put("points", resultSet.getInt("likes") - resultSet.getInt("dislikes"));
                String temp = resultSet.getString("parent");

                if (temp.equals("")) {
                    data.put("parent", JSONObject.NULL);
                }else {
                    int indexLast = temp.lastIndexOf("_");
                    data.put("parent", Integer.parseInt(temp.substring(indexLast + 1)));
                }

                if (thread) {
                    data.put("thread", getThreadDetailsById(conn, resultSet.getInt("thread")));
                } else {
                    data.put("thread", resultSet.getInt("thread"));
                }

                if (user) {
                    data.put("user", UserInfo.getFullUserInfo(conn, resultSet.getString("user_email")).get("response"));
                } else {
                    data.put("user", resultSet.getString("user_email"));
                }

            } else {
                data = null;
            }

        }catch(SQLException ex) {
            ex.printStackTrace();
        }
        Main.connectionPool.printStatus();
        return data;
    }


    public JSONObject getForumDetails(Connection conn, String short_name){

        JSONObject data = new JSONObject();
        try {
            ResultSet resultSet;

            PreparedStatement pstmt = conn.prepareStatement("select * from forum where short_name = ?");
            pstmt.setString(1, short_name);
            resultSet = pstmt.executeQuery();

            if (resultSet.next()) {
                data.put("user", resultSet.getString("user_email"));
                data.put("name", resultSet.getString("name"));
                data.put("id", resultSet.getInt("id"));
                data.put("short_name", resultSet.getString("short_name"));
            } else {
                data = null;
            }
        }catch(SQLException ex) {
            ex.printStackTrace();
    }
        return data;
    }


    public JSONObject getThreadDetailsById(Connection conn, int id) throws IOException, SQLException{

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
            data.put("forum", resultSet.getString("forum"));
            data.put("id", resultSet.getInt("id"));
            data.put("isClosed", resultSet.getBoolean("isClosed"));
            data.put("isDeleted", resultSet.getBoolean("isDeleted"));
            data.put("likes", resultSet.getInt("likes"));
            data.put("message", resultSet.getString("message"));
            data.put("points", resultSet.getInt("likes") - resultSet.getInt("dislikes") );
            data.put("posts", resultSetCount.getInt("amount"));
            data.put("slug", resultSet.getString("slug"));
            data.put("title", resultSet.getString("title"));
            data.put("user", resultSet.getString("user_email"));

        } else {
            String message = "There is no thread with such id!";
            data.put("error", message);
        }

        return data;
    }

}
