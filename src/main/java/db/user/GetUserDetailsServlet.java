package db.user;

import org.json.JSONObject;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import java.io.IOException;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Created by Виталий on 22.03.2015.
 */
/*public class GetUserDetailsServlet extends HttpServlet {
    private Connection connection;
    public GetUserDetailsServlet(Connection connection){ this.connection = connection; }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String userEmail = request.getParameter("user");
        JSONObject jsonResponse = UserInfo.getFullUserInfo(connection, userEmail);
        response.getWriter().println(jsonResponse);
    }
}*/


public class GetUserDetailsServlet extends HttpServlet {
    private Connection connection;
    public GetUserDetailsServlet(){
        try {
            DriverManager.registerDriver(new com.mysql.jdbc.Driver());
            this.connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/testdb","test", "test");
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
        } }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json;charset=utf-8");
        String userEmail = request.getParameter("user");
        JSONObject jsonResponse = UserInfo.getFullUserInfo(connection,userEmail);
        response.getWriter().println(jsonResponse);
    }
}