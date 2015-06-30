package db.thread;

import main.Main;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import static db.user.UserInfo.getUserIdByEmail;

/**
 * Created by vitaly on 23.06.15.
 */
public class SubscribeThreadServlet extends HttpServlet {

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
        String email = (String)JSONRequest.get("user");

        if (JSONRequest.has("thread") && email != null) {
            threadId = JSONRequest.getLong("thread");
        } else {
            status = 2;
            message = "Incorrect REQUEST";
        }

        int result = 0;
        String query;
        Connection connection = null;

        try {
            connection = Main.dataSource.getConnection();
            Main.connectionPool.printStatus();

            int userId = getUserIdByEmail(connection, email);
            if (userId < 1) {
                status = 1;
                message = "There is no such USER";
            }

            Statement sqlQuery = null;

            if (status == 0) {
                sqlQuery = connection.createStatement();
                query = "INSERT INTO subscribtion VALUES (" + userId + " , " + threadId + ") ;";
                result = sqlQuery.executeUpdate(query);
                if (result == 0) {
                    status = 1;
                    message = "There is no such THREAD";
                }
            }
            createResponse(response, status, message, threadId, email);
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) {
                JSONObject obj = new JSONObject();
                obj.put("code", 5);
                obj.put("response", "Duplicate");
                //System.out.println("OOOOOOOOOOOOOOOOOUUUUUUUUUUUUUCCCCCCCHHHHHHH");
                response.getWriter().write(obj.toString());
            }
            //e.printStackTrace();
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

    private void createResponse(HttpServletResponse response, short status, String message, long threadId, String email) throws IOException, SQLException {

        response.setContentType("json;charset=UTF-8");
        response.setHeader("Cache-Control", "no-cache");
        response.setStatus(HttpServletResponse.SC_OK);

        JSONObject obj = new JSONObject();
        JSONObject data = new JSONObject();
        if (status != 0) {
            data.put("error", message);
        } else {
            data.put("thread", threadId);
            data.put("user", email);
        }
        obj.put("response", data);
        obj.put("code", status);
        response.getWriter().write(obj.toString());
    }
}
