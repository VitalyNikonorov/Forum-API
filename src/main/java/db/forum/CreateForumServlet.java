package db.forum;

import org.json.JSONObject;
import temletor.PageGenerator;
import temletor.SqlWrapper;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Виталий on 15.03.2015.
 */
public class CreateForumServlet extends HttpServlet {
    private Connection connection;
    public CreateForumServlet(Connection connection){ this.connection = connection; }

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

        // Database
        try {
            Statement sqlQuery = connection.createStatement();
            ResultSet rs = null;

            String queryStr;

            String[] params = {"name", "short_name", "user"};
            queryStr = SqlWrapper.getInsertQuery("forum", params, jsonObject);
            sqlQuery.executeUpdate(queryStr);

            String sqlSelect = "SELECT * FROM forum WHERE name=\'" +jsonObject.get("name")+ "\'";
            rs = sqlQuery.executeQuery(sqlSelect);

            while(rs.next()){
                //Display values
                responseMap.put("name", rs.getString("name"));
                responseMap.put("short_name", rs.getString("short_name"));
                responseMap.put("id", new Integer(rs.getString("id")));
            }

            responseMap.put("user", jsonObject.get("user"));
            jsonResponse.put("code", 0);
            jsonResponse.put("response", responseMap);
            rs.close(); rs=null;
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
            System.out.println("Other Error in CreateForumServlet.");
        }
        //Database!!!!

        response.getWriter().println(jsonResponse);
    }


}
