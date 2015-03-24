package db.thread;

import org.json.JSONObject;
import temletor.SqlWrapper;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Виталий on 24.03.2015.
 */
public class CreateThreadServlet extends HttpServlet {
    private Connection connection;
    public CreateThreadServlet(Connection connection){ this.connection = connection; }

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

        if (!jsonObject.has("isDeleted")){
            jsonObject.put("isDeleted", false);
        }


        // Database
        try {
            Statement sqlQuery = connection.createStatement();
            String queryStr;

            String[] params = {"forum", "title", "isClosed", "user", "date", "message", "slug", "isDeleted"};
            queryStr = SqlWrapper.getInsertQuery("thread", params, jsonObject);
            sqlQuery.executeUpdate(queryStr);

            responseMap = ThreadInfo.getShortThreadInfo(connection, jsonObject.getString("date"), jsonObject.getString("user"));
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
            System.out.println("Other Error in CreateThreadServlet.");
        }
        //Database!!!!

        response.getWriter().println(jsonResponse);
    }

}
