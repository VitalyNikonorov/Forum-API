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

public class UnfollowUserServlet extends HttpServlet {

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
        Map<String, Object> responseMap =  new HashMap<>();

        Statement sqlQuery = null;
        // Database
        Connection conn = null;
        try {
            conn = Main.dataSource.getConnection();
            Main.connectionPool.printStatus();

            sqlQuery = conn.createStatement();
            ResultSet rs = null;
            String sqlSelect;
            int follower = 0, followee = 0;

            sqlSelect = "SELECT id FROM users WHERE email=\'" +jsonObject.get("follower") +"\';";
            rs = sqlQuery.executeQuery(sqlSelect);
            while(rs.next()){
                //Parse values
                follower = new Integer(rs.getString("id"));
            }

            sqlSelect = "SELECT id FROM users WHERE email=\'" +jsonObject.get("followee") +"\';";
            rs = sqlQuery.executeQuery(sqlSelect);
            while(rs.next()){
                //Parse values
                followee = new Integer(rs.getString("id"));
            }

            sqlSelect = "DELETE FROM follow WHERE follower_id=" +follower+ " AND followee_id=" +followee+ ";";
            sqlQuery.executeUpdate(sqlSelect);


            jsonResponse = db.user.UserInfo.getFullUserInfo(conn, jsonObject.get("follower").toString());
        }
        catch (SQLException ex){
            ex.printStackTrace();
        }
        catch (Exception ex){
            System.out.println("Other Error in DetailsUserServlet.");
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
