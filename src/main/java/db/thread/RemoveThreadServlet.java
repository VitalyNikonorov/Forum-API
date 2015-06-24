package db.thread;

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

/**
 * Created by vitaly on 23.06.15.
 */
public class RemoveThreadServlet extends HttpServlet {

    private Connection connection;
    public RemoveThreadServlet(Connection connection){ this.connection = connection; }

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

        long threadId= JSONRequest.getLong("thread");
        if (threadId == 0) {
            status = 3;
            message = "Incorrect JSON";
        }

        try {
            Statement sqlQuery = connection.createStatement();

            if (status == 0) {
                String query = "update thread set isDeleted = 1 where id = " + threadId + ";";
                int result = sqlQuery.executeUpdate(query);

                if (result != 0) {
                    query = "update post set isDeleted = 1 where thread = " + threadId + ";";
                    result = sqlQuery.executeUpdate(query);
                } else {
                    status = 1;
                    message = "There is no such POST";
                }
            }
            createResponse(response, status, message, threadId);
            sqlQuery.close();
            sqlQuery = null;
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createResponse(HttpServletResponse response, short status, String message, long threadId) throws IOException, SQLException {
        response.setContentType("json;charset=UTF-8");
        response.setHeader("Cache-Control", "no-cache");
        response.setStatus(HttpServletResponse.SC_OK);

        JSONObject obj = new JSONObject();
        JSONObject data = new JSONObject();
        if (status == 0) {
            data.put("thread", threadId);
        }
        obj.put("response", status == 0? data: message);
        obj.put("code", status);
        response.getWriter().write(obj.toString());
    }
}