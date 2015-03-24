package db.forum;

import db.user.UserInfo;
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
                JSONObject userInfo = UserInfo.getFullUserInfo(connection, userMap.get("email").toString());
                responseMap.put("user", userInfo.get("response"));
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
