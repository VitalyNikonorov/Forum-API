package db.user;

import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Виталий on 23.03.2015.
 */
public class UserListFollowersServlet extends HttpServlet {
    private Connection connection;
    public UserListFollowersServlet(Connection connection){ this.connection = connection; }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Map<String, Object> responseMap =  new HashMap<>();
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
            Statement sqlQuery = connection.createStatement();
            ResultSet rs = null;

            String sqlSelect = "SELECT * FROM user WHERE email=\'" +userEmail+ "\'";
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
            sqlSelect = "SELECT Res.email FROM follow Fol LEFT JOIN user R ON R.id=Fol.id1 JOIN user Res ON Fol.id2=Res.id WHERE R.id=" + responseMap.get("id")+ ";";
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
            sqlSelect = "SELECT Res.email FROM follow Fol LEFT JOIN user R ON R.id=Fol.id2 JOIN user Res ON Fol.id1=Res.id WHERE R.id=" + responseMap.get("id") ;

            if ( since_id != null){
                sqlSelect = sqlSelect + " AND  Res.id >= " +since_id;
            }

            if ( max_id != null){
                sqlSelect = sqlSelect + " AND  Res.id <= " +max_id;
            }

            sqlSelect = sqlSelect + " ORDER BY " +" Res.name " +order;

            if (limit != null){
                sqlSelect = sqlSelect + " LIMIT " +limit +";";
            }else{
                sqlSelect = sqlSelect + ";";
            }
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

            sqlSelect = "SELECT Sub.threadId FROM subscribe Sub LEFT JOIN user R ON R.id=Sub.userid WHERE R.id=" + responseMap.get("id") + ";";
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
            responseMap.put("subscriptions", subscriptions);

            responseMap.put("following", following);
            responseMap.put("followers", followers);


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
            System.out.println("Other Error in DetailsUserServlet.");
        }
        //Database!!!!
        response.getWriter().println(jsonResponse);
    }
}
