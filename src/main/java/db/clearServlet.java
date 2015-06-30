package db;

import main.Main;
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
        JSONObject jsonResponse = new JSONObject();

                    /* Database*/
        Connection connection = null;
        try {
            connection = Main.dataSource.getConnection();
            Main.connectionPool.printStatus();

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
            ex.printStackTrace();
        }
        catch (Exception ex){
            System.out.println("Other Error ClearServlet.");
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        jsonResponse.put("code", 0);
        jsonResponse.put("response", "OK");
                       /* /Database!!!! */
        response.getWriter().println(jsonResponse);
    }

}

