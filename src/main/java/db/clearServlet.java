package db;

import org.json.JSONObject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Виталий on 19.03.2015.
 */
public class ClearServlet extends HttpServlet {

    public void doPost(HttpServletRequest request,
                       HttpServletResponse response) throws ServletException, IOException {

        Connection connection = null ;
        JSONObject jsonResponse = new JSONObject();

                    /* Database*/
        try {
            DriverManager.registerDriver(new com.mysql.jdbc.Driver());

            connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/testdb","test", "test");

            Statement sqlQuery = connection.createStatement();

            sqlQuery.executeUpdate("DELETE FROM user;");
            sqlQuery.executeUpdate("DELETE FROM forum;");
            sqlQuery.executeUpdate("DELETE FROM post;");
            sqlQuery.executeUpdate("DELETE FROM thread;");
            sqlQuery.executeUpdate("DELETE FROM dislikes;");
            sqlQuery.executeUpdate("DELETE FROM follow;");
            sqlQuery.executeUpdate("DELETE FROM likes;");
            sqlQuery.executeUpdate("DELETE FROM postdislikes;");
            sqlQuery.executeUpdate("DELETE FROM postlikes;");
            sqlQuery.executeUpdate("DELETE FROM subscribe;");
            sqlQuery.executeUpdate("DELETE FROM threaddislikes;");
            sqlQuery.executeUpdate("DELETE FROM threadlikes;");
            sqlQuery.executeUpdate("DELETE FROM threadsubscribe;");
            connection.close();
        }
        catch (SQLException ex){
            System.out.println("SQLException caught");
            System.out.println("---");
            while ( ex != null ){
                System.out.println("Message   : " + ex.getMessage());
                System.out.println("SQLState  : " + ex.getSQLState());
                System.out.println("ErrorCode : " + ex.getErrorCode());
                System.out.println("---");
                ex = ex.getNextException();
            }
        }
        catch (Exception ex){
            System.out.println("Other Error ClearServlet.");
        }
        jsonResponse.put("code", 0);
        jsonResponse.put("response", "OK");
                       /* /Database!!!! */
        response.getWriter().println(jsonResponse);
    }

}

