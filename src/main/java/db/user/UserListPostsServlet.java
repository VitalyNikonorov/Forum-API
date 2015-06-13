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
import java.util.*;

/**
 * Created by Виталий on 23.03.2015.
 */

public class UserListPostsServlet extends HttpServlet {
    private Connection connection;
    public UserListPostsServlet(Connection connection){ this.connection = connection; }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {


        JSONObject jsonResponse = new JSONObject();
/*
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

        String since = request.getParameter("since");

        // Database
        try {
            Statement sqlQuery = connection.createStatement();
            ResultSet rs = null;

            String sqlSelect = "SELECT P.*, U.email FROM post P JOIN users U ON P.user=U.id WHERE U.email=\'" +userEmail+ "\'";
            sqlSelect = sqlSelect + " ORDER BY " +" P.date " +order;
            if (limit != null){
                sqlSelect = sqlSelect + " LIMIT " +limit +";";
            }else{
                sqlSelect = sqlSelect + ";";
            }
            rs = sqlQuery.executeQuery(sqlSelect);
            ArrayList<Map<String, Object>> listOfResponseMap =  new ArrayList<Map<String, Object>>();
            String subSqlSelect = null;
            ResultSet rsSub = null;
            int i = 0;

            while(rs.next()){
                //Parse values
                Map<String, Object> tempResponseMap =  new HashMap<>();
                tempResponseMap.put("date", rs.getString("date")); //Почему-то режет последний символ
                tempResponseMap.put("id", new Integer(rs.getString("id")));
                tempResponseMap.put("forum", rs.getString("forum"));
                tempResponseMap.put("isApproved", new Boolean(rs.getString("isApproved")));
                tempResponseMap.put("isDeleted", new Boolean(rs.getString("isDeleted")));
                tempResponseMap.put("isEdited", new Boolean(rs.getString("isEdited")));
                tempResponseMap.put("isHighlighted", new Boolean(rs.getString("isHighlighted")));
                tempResponseMap.put("isSpam", new Boolean(rs.getString("isSpam")));
                tempResponseMap.put("message", rs.getString("message"));
                if( rs.getString("parent") != null) {
                    tempResponseMap.put("parent", new Integer(rs.getString("parent")));
                }else{
                    tempResponseMap.put("parent", null);
                }
                tempResponseMap.put("thread",  new Integer(rs.getString("thread")));
                tempResponseMap.put("user", rs.getString("email"));

                listOfResponseMap.add(i, tempResponseMap);
                i++;
            }

            for (int j=0; j<i; j++){
                //Likes
                subSqlSelect = "SELECT COUNT(id) AS likes FROM likes L JOIN users U ON L.userid=U.id WHERE L.postid=" + listOfResponseMap.get(j).get("id") +";";

                rsSub = sqlQuery.executeQuery(subSqlSelect);
                int likes = 0, dislikes = 0;
                while(rsSub.next()){
                    likes = new Integer(rsSub.getString("likes"));
                    listOfResponseMap.get(j).put("likes", likes);
                }

                //dislikes
                subSqlSelect = "SELECT COUNT(id) AS dislikes FROM dislikes D JOIN users U ON D.userid=U.id WHERE D.postid=" + listOfResponseMap.get(j).get("id") +";";
                rsSub = sqlQuery.executeQuery(subSqlSelect);

                while(rsSub.next()){
                    dislikes = new Integer(rsSub.getString("dislikes"));
                    listOfResponseMap.get(j).put("dislikes", dislikes);
                }
                listOfResponseMap.get(j).put("points", (likes-dislikes));
            }



            jsonResponse.put("code", 0);
            jsonResponse.put("response", listOfResponseMap);

            rsSub.close(); rsSub=null;
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
            System.out.println("Other Error in UserListPostsServlet.");
        }*/
        //Database!!!!
        response.getWriter().println(jsonResponse);
    }
}
