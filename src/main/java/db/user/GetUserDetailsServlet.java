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
        JSONObject jsonResponse = new JSONObject();
        String userEmail = request.getParameter("user");
        try {
            PreparedStatement pstmt = connection.prepareStatement("SELECT * FROM user WHERE email=?");
            pstmt.setString(1, userEmail);

            ResultSet rs = null;
            rs = pstmt.executeQuery();

            Map<String, Object> user = new HashMap<>();

            while(rs.next()){
                //Parse values
                if( rs.getString("about").equals("")){
                    user.put("about", JSONObject.NULL);
                }else {
                    user.put("about", rs.getString("about"));
                }

                user.put("email", rs.getString("email"));
                user.put("id", new Integer(rs.getString("id")));
                user.put("isAnonymous", rs.getString("isAnonymous").equals("1")?true:false);
                user.put("name", rs.getString("name"));

                if( rs.getString("name").equals("")){
                    user.put("name", JSONObject.NULL);
                }else {
                    user.put("name", rs.getString("name"));
                }

                if( rs.getString("username").equals("")){
                    user.put("username", JSONObject.NULL);
                }else {
                    user.put("username", rs.getString("username"));
                }
            }


            //following
            String[] following;
            Statement sqlQuery = connection.createStatement();
            String sqlSelect = "SELECT Res.email FROM follow Fol LEFT JOIN user R ON R.id=Fol.id1 JOIN user Res ON Fol.id2=Res.id WHERE R.id=" + user.get("id")+ ";";
            rs = sqlQuery.executeQuery(sqlSelect);
            int size= 0;
            if (rs != null)
            {
                rs.beforeFirst();
                rs.last();
                size = rs.getRow();
            }
            following = new String[size];
            rs.beforeFirst();
            int i = 0;
            while(rs.next()){
                //Parse values
                following[i]=rs.getString("email");
                i++;
            }

            //followers
            String[] followers;
            sqlSelect = "SELECT Res.email FROM follow Fol LEFT JOIN user R ON R.id=Fol.id2 JOIN user Res ON Fol.id1=Res.id WHERE R.id=" + user.get("id") + ";";
            rs = sqlQuery.executeQuery(sqlSelect);
            size= 0;
            if (rs != null)
            {
                rs.beforeFirst();
                rs.last();
                size = rs.getRow();
            }
            followers = new String[size];
            rs.beforeFirst();
            i = 0;
            while(rs.next()){
                //Parse values
                followers[i]=rs.getString("email");
                i++;
            }

            //subscriptions

            sqlSelect = "SELECT Sub.threadId FROM subscribe Sub LEFT JOIN user R ON R.id=Sub.userid WHERE R.id=" + user.get("id") + ";";
            rs = sqlQuery.executeQuery(sqlSelect);
            size= 0;
            if (rs != null)
            {
                rs.beforeFirst();
                rs.last();
                size = rs.getRow();
            }


            int[] subscriptions = new int[size];
            rs.beforeFirst();
            i = 0;
            while (rs.next()) {
                //Parse values
                subscriptions[i] = new Integer(rs.getString("threadId"));
                i++;
            }
            user.put("subscriptions", subscriptions);

            user.put("following", following);
            user.put("followers", followers);
            rs.close(); rs=null;

            jsonResponse.put("code", 0);
            jsonResponse.put("response", user);


            System.out.println("\ndetails query:" + userEmail + " \n" + jsonResponse);
        }catch (SQLException ex){
            System.out.println("SQLException caught");
            System.out.println("---");
            while ( ex != null ){
                System.out.println("Message   : " + ex.getMessage());
                System.out.println("SQLState  : " + ex.getSQLState());
                System.out.println("ErrorCode : " + ex.getErrorCode());
                jsonResponse.put("code", 2);
                jsonResponse.put("response", "JSON is not correct");
                System.out.println(ex.getMessage());

                System.out.println("---");
                ex = ex.getNextException();
            }
        }

        response.getWriter().println(jsonResponse);
    }
}