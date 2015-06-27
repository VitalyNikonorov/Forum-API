package db.user;

import main.DBConnectionPool;
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

    private DataSource dataSource;
    DBConnectionPool connectionPool;
    Connection conn = null;

    public FollowUserServlet(DataSource dataSource, DBConnectionPool connectionPool){
        this.dataSource = dataSource;
        this.connectionPool = connectionPool;
    }

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
        // Database
        try {
            conn = dataSource.getConnection();
            connectionPool.printStatus();

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
           /* System.out.println("SQLException caught");
            System.out.println("---");
            while ( ex != null ){
                System.out.println("Message   : " + ex.getMessage());
                System.out.println("SQLState  : " + ex.getSQLState());
                System.out.println("ErrorCode : " + ex.getErrorCode());
                System.out.println("---");
                ex = ex.getNextException();
            }
            */
        }catch (Exception ex){
            //System.out.println("Other Error in FollowUserServlet.");
        } finally {
            if (sqlQuery != null) {
                try {
                    sqlQuery.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        //Database!!!!

        response.getWriter().println(jsonResponse);
    }

}
