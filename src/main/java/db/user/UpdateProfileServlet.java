package db.user;

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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Виталий on 23.03.2015.
 */
public class UpdateProfileServlet extends HttpServlet {

    public void doPost(HttpServletRequest request,
                       HttpServletResponse response) throws ServletException, IOException {

        StringBuffer jb = new StringBuffer();
        String line = null;
        try {
            BufferedReader reader = request.getReader();
            while ((line = reader.readLine()) != null)
                jb.append(line);
        } catch (Exception e) { /*report an error*/ }

        JSONObject jsonObject = new JSONObject(jb.toString());
        JSONObject jsonResponse = new JSONObject();

        // Database
        Statement sqlQuery = null;
        Connection conn = null;
        try {
            conn = Main.dataSource.getConnection();
            Main.connectionPool.printStatus();

            sqlQuery = conn.createStatement();
            String sqlUpdate;

            sqlUpdate = "UPDATE users SET name=\'" + jsonObject.get("name") + "\', about=\'" + jsonObject.get("about") + "\' WHERE email=\'" + jsonObject.get("user") + "\';";
            sqlQuery.executeUpdate(sqlUpdate);
            jsonResponse = db.user.UserInfo.getFullUserInfo(conn, jsonObject.get("user").toString());

        } catch (SQLException ex) {
            ex.printStackTrace();
        } catch (Exception ex) {
            System.out.println("Other Error in UpdateProfileUserServlet.");
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
        //Database!!!!
        response.getWriter().println(jsonResponse);
    }
}
