package db.thread;

import db.user.UserInfo;
import main.Main;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.sql.*;

/**
 * Created by vitaly on 23.06.15.
 */
public class VoteThreadServlet extends HttpServlet {

    public void doPost(HttpServletRequest request,
                       HttpServletResponse response) throws ServletException, IOException {

        StringBuffer jb = new StringBuffer();
        String line = null;
        try {
            BufferedReader reader = request.getReader();
            while ((line = reader.readLine()) != null)
                jb.append(line);
        } catch (Exception e) { /*report an error*/ }

        JSONObject JSONRequest = new JSONObject(jb.toString());

        short status = 0;
        String message = "";

        long threadId = 0;
        int vote = 0;
        if (JSONRequest.has("thread") && JSONRequest.has("vote")) {
            threadId = JSONRequest.getLong("thread");
            vote = JSONRequest.getInt("vote");
        }
        if (vote != 1 && vote != -1 || threadId == 0) {
            status = 3;
            message = "Incorrect request JSON";
        }

        int result = 0;
        String query;
        Connection connection = null;
        try {
            connection = Main.dataSource.getConnection();
            Main.connectionPool.printStatus();

            Statement sqlQuery = connection.createStatement();
            if (status == 0) {
                String likes = vote > 0 ? "likes" : "dislikes";
                query = "update thread set " + likes + " = " + likes + " + 1" + " where id = " + threadId + ";";
                result = sqlQuery.executeUpdate(query);
            }

            if (result == 0) {
                status = 1;
                message = "There is no such thread";
            }
            createResponse(connection, response, status, message, threadId);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void createResponse(Connection connection, HttpServletResponse response, short status, String message, long threadId) throws IOException, SQLException {

        response.setContentType("json;charset=UTF-8");
        response.setHeader("Cache-Control", "no-cache");
        response.setStatus(HttpServletResponse.SC_OK);

        JSONObject data = null;
        if (status == 0) {
            data = getThreadDetailsById(connection, (int)threadId, false, false);
        }
        if (data == null) {
            status = 1;
            message = "There is no such thread";
        }
        JSONObject obj = new JSONObject();
        if (status == 0) {
            obj.put("response", data);
        } else {
            obj.put("response", message);
        }
        obj.put("code", status);
        response.getWriter().write(obj.toString());
    }

    //TODO - вынести в вфункцию

    public JSONObject getThreadDetailsById(Connection connection, int id, boolean user, boolean forum) throws IOException, SQLException{

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
                data.put("user", UserInfo.getFullUserInfo(connection, resultSet.getString("user_email")).get("response"));
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
