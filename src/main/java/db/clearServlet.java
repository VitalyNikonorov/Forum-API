package db;

import temletor.PageGenerator;

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
    public void doGet(HttpServletRequest request,
                      HttpServletResponse response) throws ServletException, IOException {

        Map<String, Object> pageVariables = new HashMap<>();

        Connection connection = null ;


                    /* Database*/
        try {
            DriverManager.registerDriver(new com.mysql.jdbc.Driver());

            connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/testdb","test", "test");

            Statement sqlQuery = connection.createStatement();
            ResultSet rs = null;

            sqlQuery.executeUpdate("DELETE FROM user;");
            sqlQuery.executeUpdate("DELETE FROM forum;");

            rs.close(); rs=null;
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
            System.out.println("Other Error in Main.");
        }
                       /* /Database!!!! */


        response.getWriter().println(PageGenerator.getPage("clearResponse.html", pageVariables));
    }


    public void doPost(HttpServletRequest request,
                       HttpServletResponse response) throws ServletException, IOException {


        Map<String, Object> pageVariables = new HashMap<>();
        response.getWriter().println(PageGenerator.getPage("clearResponse.html", pageVariables));
    }

}

