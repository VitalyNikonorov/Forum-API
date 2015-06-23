package db.user;

import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Виталий on 23.03.2015.
 */
public class UserListFollowersServlet extends HttpServlet {
    private Connection connection;
    public UserListFollowersServlet(Connection connection){ this.connection = connection; }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        JSONObject jsonResponse = new JSONObject();

        String userEmail = request.getParameter("user");
        String limit = request.getParameter("limit");
        String order = request.getParameter("order");



        if (order == null){
            order = "desc";
        }

        if ( !(order.equals("asc") || order.equals("desc")) ) {
            jsonResponse.put("code", 3);
            jsonResponse.put("response", "Incorrect order parameter");
            response.getWriter().println(jsonResponse);
        }

        String since_id = request.getParameter("since_id");
        String max_id = request.getParameter("max_id");

        // Database
        try {
            PreparedStatement pstmt = connection.prepareStatement("SELECT * FROM users WHERE email=?");
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

            String sqlSelect = "SELECT * FROM follow WHERE followee_id= " + user.get("id");

            if ( since_id != null){
                sqlSelect = sqlSelect + " AND follower_id >= " +since_id;
            }

            if ( max_id != null){
                sqlSelect = sqlSelect + " AND  follower_id <= " +max_id;
            }

            sqlSelect = sqlSelect + " ORDER BY " + " follower_id " +order;

            if (limit != null){
                sqlSelect = sqlSelect + " LIMIT " +limit +";";
            }else{
                sqlSelect = sqlSelect + ";";
            }

            Statement sqlQuery = connection.createStatement();
            rs = sqlQuery.executeQuery(sqlSelect);

            int size= 0;
            if (rs != null)
            {
                rs.beforeFirst();
                rs.last();
                size = rs.getRow();
            }

            int[] followers = new int[size];
            rs.beforeFirst();
            int i = 0;

            while(rs.next()){
                //Parse values
                followers[i]=rs.getInt("follower_id");
                i++;
            }
            List<Map<String, Object>> arrayResponse = new ArrayList<Map<String, Object>>();

            for (int j = 0; j < size; j++){
                arrayResponse.add(UserInfo.getFullUserInfoById(connection, followers[j]));
            }

            jsonResponse.put("code", 0);
            jsonResponse.put("response", arrayResponse);
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
            System.out.println("Other Error in UserListFollowersServlet.");
        }
        //Database!!!!
        response.getWriter().println(jsonResponse);
    }
}
