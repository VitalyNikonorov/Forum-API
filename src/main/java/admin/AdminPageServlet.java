package admin;

import main.TimeHelper;
import temletor.PageGenerator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Виталий on 15.03.2015.
 */
public class AdminPageServlet extends HttpServlet {
    public static final String adminPageURL = "/admin";
    private Connection connection;
    public AdminPageServlet(Connection connection){ this.connection = connection; }

    public void doGet(HttpServletRequest request,
                      HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        Map<String, Object> pageVariables = new HashMap<>();
        String timeString = request.getParameter("shutdown");

        if (timeString != null) {
            int timeMS = Integer.valueOf(timeString);
            System.out.print("Server will be down after: "+ timeMS + " ms");
            TimeHelper.sleep(timeMS);
            System.out.print("\nShutdown");
            try {
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
                System.out.println("Other Error in AdminPageServlet.");
            }

            System.exit(0);
        }

        response.getWriter().println(PageGenerator.getPage("admin.tml", pageVariables));
    }
}