package db.user;

import main.DBConnectionPool;
import org.json.JSONObject;
import utilities.JsonHelper;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Виталий on 31.05.2015.
 */
public class CreateUserServlet extends HttpServlet {

    private DataSource dataSource;
    DBConnectionPool connectionPool;
    Connection conn = null;

    public CreateUserServlet(DataSource dataSource, DBConnectionPool connectionPool){
        this.dataSource = dataSource;
        this.connectionPool = connectionPool;
    }

    private boolean valideteUser(JSONObject jso){
        fixUserProperty(jso, "name");
        fixUserProperty(jso, "username");
        fixUserProperty(jso, "about");

        return isEmailValid(jso);
    }

    private void fixUserProperty(JSONObject jso, String prop){
        if (jso.get(prop) == JSONObject.NULL){
            jso.remove(prop);
            jso.put(prop, "");
        }
    }

    private boolean isEmailValid(JSONObject jso) {
        return !(jso.get("email") == JSONObject.NULL);
    }

    public void doPost(HttpServletRequest request,
                       HttpServletResponse response) throws ServletException, IOException {

        response.setContentType("application/json;charset=utf-8");

        org.json.JSONObject jsonRequest = JsonHelper.getJSONFromRequest(request, "CreateUserServlet");

        JSONObject jsonResponse = new JSONObject();

        if ( (!jsonRequest.has("isAnonymous")) ){
            jsonRequest.put("isAnonymous", "0");
        }else{
            if(jsonRequest.getBoolean("isAnonymous") == true ){
                jsonRequest.remove("isAnonymous");
                jsonRequest.put("isAnonymous", "1");
            }else{
                jsonRequest.remove("isAnonymous");
                jsonRequest.put("isAnonymous", "0");
            }

        }

        // Database
        PreparedStatement pstmt = null;
        try {
            conn = dataSource.getConnection();
            connectionPool.printStatus();

            if (! valideteUser(jsonRequest)){
                errorResponse(response, jsonResponse);
                return;
            }
            String name = jsonRequest.getString("name");
            String username = jsonRequest.getString("username");
            String email = jsonRequest.getString("email");
            String about = jsonRequest.getString("about");

            pstmt = conn.prepareStatement(
                    "INSERT INTO users (name, username, email, about, isAnonymous) VALUES (?, ?, ?, ?, ?);");

            pstmt.setString(1, name);
            pstmt.setString(2, username);
            pstmt.setString(3, email);
            pstmt.setString(4, about);
            pstmt.setString(5, jsonRequest.getString("isAnonymous"));

            pstmt.executeUpdate();

            pstmt = conn.prepareStatement("SELECT * FROM users WHERE email=?");
            pstmt.setString(1, jsonRequest.getString("email"));

            ResultSet rs = null;

            rs = pstmt.executeQuery();


            Map<String, Object> user = new HashMap<>();

            while(rs.next()){
                //Parse values
                user.put("about", rs.getString("about"));
                user.put("email", rs.getString("email"));
                user.put("id", new Integer(rs.getString("id")));
                user.put("isAnonymous", rs.getString("isAnonymous").equals("1")?true:false);
                user.put("name", rs.getString("name"));
                if( rs.getString("username") == null){
                    user.put("username", null);
                    //user.put("username", "");
                }else {
                    user.put("username", rs.getString("username"));
                }
            }

            jsonResponse.put("code", 0);
            jsonResponse.put("response", user);
        }
        catch (SQLException ex){
            //System.out.println("SQLException caught");
            //System.out.println("---");
            while ( ex != null ){
                //System.out.println("Message   : " + ex.getMessage());
                //System.out.println("SQLState  : " + ex.getSQLState());
                //System.out.println("ErrorCode : " + ex.getErrorCode());
                if (ex.getErrorCode() == 1062) {
                    jsonResponse.put("code", 5);
                    jsonResponse.put("response", "User with email: " + jsonRequest.getString("email") + " is exist");
                }else{
                    jsonResponse.put("code", 2);
                    jsonResponse.put("response", "JSON is not correct");
                    System.out.println(ex.getMessage());
                }
                //System.out.println("---");
                ex = ex.getNextException();
            }
        }
        catch (Exception ex){

            errorResponse(response, jsonResponse);
            System.out.println("Other Error in CreateUserServlet.");
            return;
        } finally {
            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        //Database!!!!

        response.getWriter().println(jsonResponse);
    }

    private void errorResponse(HttpServletResponse response, JSONObject jsonResponse) throws IOException {
        jsonResponse.put("code", 2);
        jsonResponse.put("response", "JSON is not correct");
        response.getWriter().println(jsonResponse);
    }
}
