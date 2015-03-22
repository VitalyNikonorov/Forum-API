package db.user;

import org.json.JSONObject;
import temletor.SqlWrapper;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import java.io.IOException;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Виталий on 22.03.2015.
 */
public class GetUserDetailsServlet extends HttpServlet {
    private Connection connection;
    public GetUserDetailsServlet(Connection connection){ this.connection = connection; }

 /*   public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String userEmail = request.getParameter("user");
        Map<String, Object> responseMap =  new HashMap<>();
        JSONObject jsonResponse = new JSONObject();
        jsonResponse.put("code", 0);


        // Database
        Connection connection = null;
        try {
            Statement sqlQuery = connection.createStatement();
            ResultSet rs = null;

            String queryStr;

            String[] params = {"name", "username", "email", "about", "isAnonymous"};
            queryStr = SqlWrapper.getInsertQuery("user", params, jsonObject);
            //queryStr = "INSERT INTO user (name, username, email, about, isAnonymous) VALUES (\'" +jsonObject.get("name")+ "\', \'" +jsonObject.get("username")+ "\', \'"+jsonObject.get("email")+"\', \'" +jsonObject.get("about")+ "\'"+ ", \'" +jsonObject.get("isAnonymous")+ "\');";
            sqlQuery.executeUpdate(queryStr);

            String sqlSelect = "SELECT * FROM user WHERE email=\'" +jsonObject.get("email")+ "\'";
            rs = sqlQuery.executeQuery(sqlSelect);

            while(rs.next()){
                //Display values
                responseMap.put("about", rs.getString("about"));
                responseMap.put("email", rs.getString("email"));
                responseMap.put("id", new Integer(rs.getString("id")));
                responseMap.put("isAnonymous", new Boolean(rs.getString("isAnonymous")));
                responseMap.put("name", rs.getString("name"));
                responseMap.put("username", rs.getString("username"));
            }

            jsonResponse.put("code", 0);
            jsonResponse.put("response", responseMap);
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
            System.out.println("Other Error in CreateUserServlet.");
        }
        //Database!!!!


        response.getWriter().println();
    }*/
}
