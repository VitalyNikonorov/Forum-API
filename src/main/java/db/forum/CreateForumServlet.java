package db.forum;

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
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by vitaly on 14.06.15.
 */
public class CreateForumServlet  extends HttpServlet {

    public void doPost(HttpServletRequest request,
                       HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json;charset=utf-8");
        JSONObject jsonResponse = new JSONObject();
        Connection conn = null;
        short status = 0;
        String message = "";

        StringBuffer jb = new StringBuffer();
        String line = null;
        try {
            BufferedReader reader = request.getReader();
            while ((line = reader.readLine()) != null)
                jb.append(line);
        } catch (Exception e) {
            System.out.print("Error by parsing CreateForum request request to JSON");
        }

        JSONObject jsonRequest = new JSONObject(jb.toString());

        if (jsonRequest.get("user") == null || jsonRequest.get("name") == null || jsonRequest.get("short_name") == null) {
            status = 3;
            message = "Wrong Request";
        }
        PreparedStatement pstmt = null;
        try {
            conn = Main.dataSource.getConnection();
            Main.connectionPool.printStatus();

            pstmt = conn.prepareStatement(
                    "INSERT INTO forum (user_email, name, short_name) VALUES (?, ?, ?);");

            pstmt.setString(1, jsonRequest.getString("user"));
            pstmt.setString(2, jsonRequest.getString("name"));
            pstmt.setString(3, jsonRequest.getString("short_name"));

            pstmt.executeUpdate();
            pstmt = conn.prepareStatement("SELECT * FROM forum WHERE short_name=?");
            pstmt.setString(1, jsonRequest.getString("short_name"));

            ResultSet rs = null;
            rs = pstmt.executeQuery();

            Map<String, Object> responseMap = new HashMap<>();
            if (status == 0) {
                if (rs.next()) {
                    //Parse values
                    responseMap.put("id", rs.getInt("id"));
                    responseMap.put("name", rs.getString("name"));
                    responseMap.put("short_name", rs.getString("short_name"));
                    responseMap.put("user", rs.getString("user_email"));
                }
            } else {
                status = 3;
                message = "There is NO FORUM for this request";
            }

            jsonResponse.put("response", status == 0? responseMap : message);
            jsonResponse.put("code", status);

        }catch(SQLException ex){
            //ex.printStackTrace();
            System.out.print("Duplicate UNIQ KEY in create Forum Servlet");
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
        response.getWriter().println(jsonResponse);
    }
}
