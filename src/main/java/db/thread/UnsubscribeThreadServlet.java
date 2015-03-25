package db.thread;

import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Виталий on 25.03.2015.
 */
public class UnsubscribeThreadServlet extends HttpServlet {
    private Connection connection;
    public  UnsubscribeThreadServlet(Connection connection){ this.connection = connection; }

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
        Map<String, Object> responseMap = new HashMap<>();

        // Database
        try {
            Statement sqlQuery = connection.createStatement();
            String sqlUpdate;

            sqlUpdate = "DELETE FROM threadsubscribe WHERE user=\'" +jsonObject.get("user")+ "\' AND threadid=\'" +jsonObject.get("thread")+ "\';";

            sqlQuery.executeUpdate(sqlUpdate);

            responseMap.put("thread", jsonObject.get("thread"));
            responseMap.put("user", jsonObject.get("user"));
            jsonResponse.put("code", 0);

            jsonResponse.put("response", responseMap);
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
            System.out.println("Other Error in unSubscribeThreadServlet.");
        }
        //Database!!!!

        response.getWriter().println(jsonResponse);
    }

}
