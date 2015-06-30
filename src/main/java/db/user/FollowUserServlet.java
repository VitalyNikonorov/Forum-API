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
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Виталий on 22.03.2015.
 */
public class FollowUserServlet extends HttpServlet {

    public void doPost(HttpServletRequest request,
                       HttpServletResponse response) throws ServletException, IOException {

        StringBuffer jb = new StringBuffer();
        String line = null;
        JSONObject jsonResponse = new JSONObject();

        try {
            BufferedReader reader = request.getReader();
            while ((line = reader.readLine()) != null)
                jb.append(line);
        } catch (Exception e) { /*report an error*/ }

        JSONObject jsonObject = new JSONObject(jb.toString());

        Statement sqlQuery = null;
        Connection conn = null;
        // Database
        try {
            conn = Main.dataSource.getConnection();
            Main.connectionPool.printStatus();

            sqlQuery = conn.createStatement();
            ResultSet rs = null;

            String queryStr;
            int id1=0, id2=0;
            rs = sqlQuery.executeQuery("SELECT id FROM users WHERE email=\'" +jsonObject.get("follower")+ "\';");
            while(rs.next()){
                //get id
                id1 = new Integer(rs.getString("id"));
            }

            rs = sqlQuery.executeQuery("SELECT id FROM users WHERE email=\'" +jsonObject.get("followee")+ "\';");
            while(rs.next()){
                //get id
                id2 = new Integer(rs.getString("id"));
            }

            queryStr = "INSERT INTO follow VALUES ( " +id1+ ", " +id2+ " );";
            sqlQuery.executeUpdate(queryStr);

            //Response
            jsonResponse = db.user.UserInfo.getFullUserInfo(conn, jsonObject.get("follower").toString());
        }
        catch (SQLException ex){
            //ex.printStackTrace();
            System.out.print("Duplicate or other in follow servlet");
        }catch (Exception ex){
            ex.printStackTrace();
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
