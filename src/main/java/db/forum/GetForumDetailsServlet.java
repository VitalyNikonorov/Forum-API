package db.forum;

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
 * Created by Виталий on 24.03.2015.
 */
public class GetForumDetailsServlet extends HttpServlet {
    private Connection connection;
    public GetForumDetailsServlet(Connection connection){ this.connection = connection; }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String short_name = request.getParameter("forum");
        String related = request.getParameter("related");

        Map<String, Object> responseMap =  new HashMap<>();
        Map<String, Object> userMap =  new HashMap<>();
        JSONObject jsonResponse = new JSONObject();

        // Database
        try {
            Statement sqlQuery = connection.createStatement();
            ResultSet rs = null;

            String sqlSelect = "SELECT * FROM forum WHERE short_name=\'" +short_name+ "\'";
            rs = sqlQuery.executeQuery(sqlSelect);

            while(rs.next()){
                //Parse values
                responseMap.put("name", rs.getString("name"));
                responseMap.put("short_name", rs.getString("short_name"));
                responseMap.put("id", new Integer(rs.getString("id")));
                userMap.put("email", rs.getString("user"));
            }

            if (related != null) {
                //User info put

                sqlSelect = "SELECT * FROM user WHERE email=\'" +userMap.get("email")+ "\'";
                rs = sqlQuery.executeQuery(sqlSelect);

                while(rs.next()){
                    //Parse values
                    userMap.put("about", rs.getString("about"));
                    userMap.put("id", new Integer(rs.getString("id")));
                    userMap.put("isAnonymous", new Boolean(rs.getString("isAnonymous")));
                    userMap.put("name", rs.getString("name"));
                    userMap.put("username", rs.getString("username"));
                }

                //following
                String[] following;
                sqlSelect = "SELECT Res.email FROM follow Fol LEFT JOIN user R ON R.id=Fol.id1 JOIN user Res ON Fol.id2=Res.id WHERE R.id=" + userMap.get("id")+ ";";
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
                sqlSelect = "SELECT Res.email FROM follow Fol LEFT JOIN user R ON R.id=Fol.id2 JOIN user Res ON Fol.id1=Res.id WHERE R.id=" + userMap.get("id") + ";";
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

                sqlSelect = "SELECT Sub.threadId FROM subscribe Sub LEFT JOIN user R ON R.id=Sub.userid WHERE R.id=" + userMap.get("id") + ";";
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
                userMap.put("subscriptions", subscriptions);

                userMap.put("following", following);
                userMap.put("followers", followers);

                responseMap.put("user", userMap);
            }
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
            System.out.println("Other Error in DetailsForumServlet.");
        }
        //Database!!!!
        response.getWriter().println(jsonResponse);
    }

}
