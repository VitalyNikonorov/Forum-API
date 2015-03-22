package db.user;


import org.json.JSONObject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.sql.*;


import java.text.ParseException;
import java.util.HashMap;

import java.util.Map;
import java.util.Objects;

/**
 * Created by Виталий on 15.03.2015.
 */
public class CreateUserServlet extends HttpServlet {

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
        Map<String, Object> responseMap =  new HashMap<>();;

        if (!jsonObject.has("isAnonymous")){
            jsonObject.put("isAnonymous", "false");
        }

        Connection connection = null ;
        // Database
        try {
            DriverManager.registerDriver(new com.mysql.jdbc.Driver());

            connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/testdb","test", "test");

            Statement sqlQuery = connection.createStatement();
            ResultSet rs = null;

            String queryStr;


            queryStr = "INSERT INTO user (name, username, email, about, isAnonymous) VALUES (\'" +jsonObject.get("name")+ "\', \'" +jsonObject.get("username")+ "\', \'"+jsonObject.get("email")+"\', \'" +jsonObject.get("about")+ "\'"+ ", \'" +jsonObject.get("isAnonymous")+ "\');";
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

        response.getWriter().println(jsonResponse);
    }

}
