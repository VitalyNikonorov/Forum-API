package db.thread;

import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by vitaly on 14.06.15.
 */
public class CreateThreadServlet extends HttpServlet {

    private Connection connection;

    public CreateThreadServlet() {
        try {
            DriverManager.registerDriver(new com.mysql.jdbc.Driver());
            this.connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/testdb", "root", "");
            Statement sqlQuery = connection.createStatement();
        } catch (SQLException ex) {
            System.out.println("SQLException caught");
            System.out.println("---");
            while (ex != null) {
                System.out.println("Message   : " + ex.getMessage());
                System.out.println("SQLState  : " + ex.getSQLState());
                System.out.println("ErrorCode : " + ex.getErrorCode());
                System.out.println("---");
                ex = ex.getNextException();
            }
        } catch (Exception ex) {
            System.out.println("Error in CreateForumServlet while taking connection");
        }
    }

    public void doPost(HttpServletRequest request,
                       HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json;charset=utf-8");
        JSONObject jsonResponse = new JSONObject();

        short status = 0;
        String message = "";

        StringBuffer jb = new StringBuffer();
        String line = null;
        try {
            BufferedReader reader = request.getReader();
            while ((line = reader.readLine()) != null)
                jb.append(line);
        } catch (Exception e) {
            System.out.print("Error by parsing CreateThread request to JSON");
        }

        JSONObject jsonRequest = new JSONObject(jb.toString());

        boolean isDeleted = false;
        if (jsonRequest.has("isDeleted")) {
            isDeleted = jsonRequest.getBoolean("isDeleted");
        }

        boolean isClosed = false;
        if (jsonRequest.has("isClosed")) {
            isClosed = jsonRequest.getBoolean("isClosed");
        }

        try {
            PreparedStatement pstmt = connection.prepareStatement(
                    "INSERT INTO thread (isDeleted, isClosed, user_email, forum, message, title, slug, date_of_creating) VALUES (?, ?, ?, ?, ?, ?, ?, ?);");

            pstmt.setBoolean(1, isDeleted);
            pstmt.setBoolean(2, isClosed);
            pstmt.setString(3, (String) jsonRequest.get("user"));
            pstmt.setString(4, (String)jsonRequest.get("forum"));
            pstmt.setString(5, (String)jsonRequest.get("message"));
            pstmt.setString(6, (String)jsonRequest.get("title"));
            pstmt.setString(7, (String)jsonRequest.get("slug"));
            pstmt.setString(8, (String)jsonRequest.get("date"));

            pstmt.executeUpdate();
            pstmt.close();

            pstmt = connection.prepareStatement("SELECT * FROM thread WHERE forum=? AND slug = ?");
            pstmt.setString(1, (String) jsonRequest.get("forum"));
            pstmt.setString(2, (String) jsonRequest.get("slug"));

            ResultSet rs = null;
            rs = pstmt.executeQuery();

            Map<String, Object> responseMap = new HashMap<>();
            if (status == 0) {
                if (rs.next()) {
                    //Parse values
                    responseMap.put("id", rs.getInt("id"));
                    responseMap.put("isDeleted", rs.getString("isDeleted").equals("1")?true:false);
                    responseMap.put("isClosed", rs.getString("isClosed").equals("1")?true:false);
                    responseMap.put("user", rs.getString("user_email"));
                    responseMap.put("forum", rs.getString("forum"));
                    responseMap.put("message", rs.getString("message"));
                    responseMap.put("title", rs.getString("title"));
                    responseMap.put("slug", rs.getString("slug"));
                    responseMap.put("date", rs.getString("date_of_creating"));

                }
            } else {
                status = 3;
                message = "There is NO THREAD for this request";
            }
            rs.close();
            rs = null;

            jsonResponse.put("response", status == 0? responseMap : message);
            jsonResponse.put("code", status);

        }catch(SQLException ex){
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
        response.getWriter().println(jsonResponse);
    }
}
