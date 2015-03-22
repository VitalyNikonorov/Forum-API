package db.user;

import org.json.JSONObject;
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
 * Created by Виталий on 22.03.2015.
 */
public class FollowUserServlet extends HttpServlet {
    private Connection connection;
    public FollowUserServlet(Connection connection){ this.connection = connection; }

    public void doPost(HttpServletRequest request,
                       HttpServletResponse response) throws ServletException, IOException {

        StringBuffer jb = new StringBuffer();
        String line = null;
        Map<String, Object> responseMap =  new HashMap<>();
        JSONObject jsonResponse = new JSONObject();

        try {
            BufferedReader reader = request.getReader();
            while ((line = reader.readLine()) != null)
                jb.append(line);
        } catch (Exception e) { /*report an error*/ }

        JSONObject jsonObject = new JSONObject(jb.toString());

        // Database
        try {
            Statement sqlQuery = connection.createStatement();
            ResultSet rs = null;

            String queryStr;
            int id1=0, id2=0;
            rs = sqlQuery.executeQuery("SELECT id FROM user WHERE email=\'" +jsonObject.get("follower")+ "\';");
            while(rs.next()){
                //get id
                id1 = new Integer(rs.getString("id"));
            }

            rs = sqlQuery.executeQuery("SELECT id FROM user WHERE email=\'" +jsonObject.get("followee")+ "\';");
            while(rs.next()){
                //get id
                id2 = new Integer(rs.getString("id"));
            }

            queryStr = "INSERT INTO follow VALUES ( " +id1+ ", " +id2+ " );";
            sqlQuery.executeUpdate(queryStr);

            //Response JSON
            String sqlSelect = "SELECT * FROM user WHERE email=\'" +jsonObject.get("follower")+ "\'";
            rs = sqlQuery.executeQuery(sqlSelect);

            while(rs.next()){
                //Parse values
                responseMap.put("about", rs.getString("about"));
                responseMap.put("email", rs.getString("email"));
                responseMap.put("id", new Integer(rs.getString("id")));
                responseMap.put("isAnonymous", new Boolean(rs.getString("isAnonymous")));
                responseMap.put("name", rs.getString("name"));
                responseMap.put("username", rs.getString("username"));
            }

            //following
            String[] following;
            sqlSelect = "SELECT Res.email FROM user R LEFT JOIN follow Fol ON R.id=Fol.id1 JOIN user Res ON Fol.id2=Res.id WHERE R.id=" + responseMap.get("id") +";";
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
            sqlSelect = "SELECT Res.email FROM user R LEFT JOIN follow Fol ON R.id=Fol.id2 JOIN user Res ON Fol.id1=Res.id WHERE R.id=" + responseMap.get("id") +";";
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
            int[] subscriptions;
            sqlSelect = "SELECT Sub.threadId FROM user R LEFT JOIN subscribe Sub ON R.id=Sub.userid WHERE R.id=" + responseMap.get("id") +";";
            rs = sqlQuery.executeQuery(sqlSelect);
            size= 0;
            if (rs != null)
            {
                rs.beforeFirst();
                rs.last();
                size = rs.getRow();
            }
            subscriptions = new int[size];
            rs.beforeFirst();
            i = 0;
            while(rs.next()){
                //Parse values
                subscriptions[i] = new Integer(rs.getString("threadId"));
                i++;
            }

            responseMap.put("following", following);
            responseMap.put("followers", followers);
            responseMap.put("subscriptions", subscriptions);

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
            System.out.println("Other Error in FollowUserServlet.");
        }
        //Database!!!!

        response.getWriter().println(jsonResponse);
    }

}
