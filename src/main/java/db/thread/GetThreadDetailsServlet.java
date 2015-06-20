package db.thread;

import db.user.UserInfo;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.*;
import java.util.Map;

/**
 * Created by vitaly on 14.06.15.
 */
public class GetThreadDetailsServlet extends HttpServlet {
    private Connection connection;

    public GetThreadDetailsServlet(Connection connection){
        this.connection = connection;
    }

    public void doGet(HttpServletRequest request,
                      HttpServletResponse response) throws ServletException, IOException {
        Map<String, String[]> paramMap = request.getParameterMap();

        String thread = request.getParameter("thread");
        String[] related = paramMap.get("related");
        short status = 0;

        try {
            createResponse(response, status, thread, related);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createResponse(HttpServletResponse response, short status, String thread, String[] related) throws IOException, SQLException {
        response.setContentType("json;charset=UTF-8");
        response.setHeader("Cache-Control", "no-cache");
        response.setStatus(HttpServletResponse.SC_OK);

        JSONObject obj = new JSONObject();
        String message = "";
        boolean user = false;
        boolean forum = false;

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

        JSONObject data;

        if (status == 0) {
            data = getThreadDetailsById(Integer.parseInt(thread), user, forum);
        } else {
            data = new JSONObject();
            data.put("error", message);
        }

        obj.put("response", data);
        obj.put("code", status);
        response.getWriter().write(obj.toString());
    }


    public JSONObject getThreadDetailsById(int id, boolean user, boolean forum) throws IOException, SQLException{
        //Остановился тут!!!!!!!


        ResultSet resultSet;
        ResultSet resultSetCount;

        PreparedStatement pstmt = connection.prepareStatement("select * from thread where id = ?");
        pstmt.setInt(1, id);

        resultSet = pstmt.executeQuery();

        PreparedStatement pstmtCountPosts = connection.prepareStatement("select count(*) as amount from post where thread = " + id + " and isDeleted = 0;");

        resultSetCount = pstmtCountPosts.executeQuery();

        JSONObject data = new JSONObject();

        if (resultSet.next() && resultSetCount.next()) {
            data.put("date", resultSet.getString("date_of_creating").substring(0, 19));
            data.put("dislikes", resultSet.getInt("dislikes"));
            if (forum) {
                //TODO  - исправить - вынести в отельную функцию


                JSONObject forumData = new JSONObject();
                try {
                    ResultSet resultSetForum;

                    PreparedStatement pstmtForum = connection.prepareStatement("select * from forum where short_name = ?");
                    pstmtForum.setString(1, resultSet.getString("forum"));
                    resultSetForum = pstmtForum.executeQuery();

                    if (resultSetForum.next()) {
                        forumData.put("user", resultSetForum.getString("user_email"));
                        forumData.put("name", resultSetForum.getString("name"));
                        forumData.put("id", resultSetForum.getInt("id"));
                        forumData.put("short_name", resultSetForum.getString("short_name"));
                    }

                }catch(SQLException ex) {
                    System.out.println("SQLException caught");
                    System.out.println("---");
                    while (ex != null) {
                        System.out.println("Message   : " + ex.getMessage());
                        System.out.println("SQLState  : " + ex.getSQLState());
                        System.out.println("ErrorCode : " + ex.getErrorCode());
                        System.out.println(ex.getMessage());
                    }
                    System.out.println("---");
                    ex = ex.getNextException();
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
                data.put("user", UserInfo.getFullUserInfo(connection, resultSet.getString("user_email")).get("response"));
            } else {
                data.put("user", resultSet.getString("user_email"));
            }
        } else {
            String message = "There is no thread with such id!";
            data.put("error", message);
        }

        resultSet.close();
        resultSet = null;

        resultSetCount.close();
        resultSetCount = null;

        return data;
    }

}
