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

    private Connection connection;
    public ClearServlet(Connection connection){ this.connection = connection; }

    public void doPost(HttpServletRequest request,
                       HttpServletResponse response) throws ServletException, IOException {
        JSONObject jsonResponse = new JSONObject();

                    /* Database*/
        try {

            Statement sqlQuery = connection.createStatement();

            sqlQuery.executeUpdate("DELETE FROM users;");
            sqlQuery.executeUpdate("DELETE FROM forum;");
            sqlQuery.executeUpdate("DELETE FROM post;");
            sqlQuery.executeUpdate("DELETE FROM thread;");
            sqlQuery.executeUpdate("DELETE FROM subscribtion;");
            sqlQuery.executeUpdate("DELETE FROM follow;");

            sqlQuery.close();
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

