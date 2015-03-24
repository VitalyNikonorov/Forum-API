package db.forum;

import db.user.GetUserDetailsServlet;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Created by Виталий on 24.03.2015.
 */
public class ForumListOfUsersServlet extends HttpServlet {
    private Connection connection;
    public ForumListOfUsersServlet(Connection connection){ this.connection = connection; }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String short_name = request.getParameter("forum");
        String since_id = request.getParameter("since_id");
        String max_id = request.getParameter("max_id");
        String order = request.getParameter("order");
        String limit = request.getParameter("limit");
        if (order == null) {
            order = "desc";
        }

        ArrayList<Map<String, Object>> listOfResponseMap =  new ArrayList<Map<String, Object>>();
        ArrayList<Object> responseArray = new ArrayList<>();
        JSONObject jsonResponse = new JSONObject();

        // Database
        try {
            Statement sqlQuery = connection.createStatement();
            ResultSet rs = null;
            int count = 0;
            String sqlSelect = "SELECT DISTINCT U.email FROM post P LEFT JOIN forum F ON P.forum=F.short_name JOIN user U ON U.id=P.user WHERE F.short_name=\'" +short_name+ "\'";

            if ( since_id != null){
                sqlSelect = sqlSelect + " AND  U.id >= \'" +since_id+"\'";
            }
            if ( max_id != null){
                sqlSelect = sqlSelect + " AND  U.id <= \'" +max_id+"\'";
            }

            sqlSelect = sqlSelect + " ORDER BY U.name " +order;

            if (limit != null){
                sqlSelect = sqlSelect + " LIMIT " +limit +";";
            }else{
                sqlSelect = sqlSelect + ";";
            }
            rs = sqlQuery.executeQuery(sqlSelect);
            while(rs.next()){
                //Parse values
                Map<String, Object> tempResponseMap =  new HashMap<>();
                tempResponseMap.put("email", rs.getString("email"));
                listOfResponseMap.add(count, tempResponseMap);
                count++;
            }

            for (int j=0; j<count; j++){
                JSONObject userInfo = UserInfo.getFullUserInfo(connection, listOfResponseMap.get(j).get("email").toString());
                responseArray.add(j, userInfo.get("response"));
            }
            jsonResponse.put("code", 0);
            jsonResponse.put("response", responseArray);
            rs.close();
            rs = null;
        } catch (SQLException ex) {
            System.out.println("SQLException caught");
            System.out.println("---");
            while (ex != null) {
                System.out.println("Message   : " + ex.getMessage());
                System.out.println("SQLState  : " + ex.getSQLState());
                System.out.println("ErrorCode : " + ex.getErrorCode());
                System.out.println("---");
                ex = ex.getNextException();
            }
        } catch (Exception ex) {
            System.out.println("Other Error in DetailsForumServlet.");
        }
        //Database!!!
        response.getWriter().println(jsonResponse);
    }
}
