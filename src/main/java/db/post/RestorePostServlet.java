package db.post;

import main.DBConnectionPool;
import main.Main;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by vitaly on 21.06.15.
 */
public class RestorePostServlet extends HttpServlet {

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

        long postId = 0;
        if (JSONRequest.has("post")) {
            postId = JSONRequest.getLong("post");
        } else {
            status = 3;
            message = "Incorrect JSON";
        }

        int result = 0;
        Connection conn = null;
        String query;
        Statement sqlQuery = null;
        try {
            conn = Main.dataSource.getConnection();
            Main.connectionPool.printStatus();
            sqlQuery = conn.createStatement();

            if (status == 0) {
                query = "update post set isDeleted = 0 where id = " + postId + ";";
                result = sqlQuery.executeUpdate(query);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (result == 0) {
            status = 1;
            message = "There is no such post";
        }
        try {
            createResponse(response, status, message, postId);
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

    private void createResponse(HttpServletResponse response, short status, String message, long postId) throws IOException, SQLException {
        response.setContentType("json;charset=UTF-8");
        response.setHeader("Cache-Control", "no-cache");
        response.setStatus(HttpServletResponse.SC_OK);

        JSONObject obj = new JSONObject();
        JSONObject data = new JSONObject();
        if (status == 0) {
            data.put("post", postId);
        }
        obj.put("response", status == 0? data: message);
        obj.put("code", status);
        response.getWriter().write(obj.toString());
    }
}
