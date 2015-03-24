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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Виталий on 24.03.2015.
 */
//Доделать после Posts!!!!!!!!!!!!!!!!!!!!!!!
public class ForumListOfPostsServlet extends HttpServlet {
    private Connection connection;
    public ForumListOfPostsServlet(Connection connection){ this.connection = connection; }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String short_name = request.getParameter("forum");
        String since = request.getParameter("since");
        Map<String, String[]> params =  request.getParameterMap();
        String order = request.getParameter("order");
        String limit = request.getParameter("limit");

        String[] related = params.get("related");
        if ( order == null){
            order = "desc";
        }

        Map<String, Object> responseMap =  new HashMap<>();
        Map<String, Object> userMap =  new HashMap<>();
        JSONObject jsonResponse = new JSONObject();

        // Database
        try {
            Statement sqlQuery = connection.createStatement();
            ResultSet rs = null;
            String subSqlSelect = null;
            ResultSet rsSub = null;
/*
            String sqlSelect = "SELECT * FROM thread WHERE thread.forum =\'" +short_name+ "\'";

            if ( since != null){
                sqlSelect = sqlSelect + " AND  thread.date >= \'" +since+"\'";
            }

            sqlSelect = sqlSelect + " ORDER BY " +" thread.date " +order;

            if (limit != null){
                sqlSelect = sqlSelect + " LIMIT " +limit +";";
            }else{
                sqlSelect = sqlSelect + ";";
            }

            rs = sqlQuery.executeQuery(sqlSelect);

            ArrayList<Map<String, Object>> listOfResponseMap =  new ArrayList<Map<String, Object>>();
            int i = 0;

            while(rs.next()){
                //Parse values
                Map<String, Object> tempResponseMap =  new HashMap<>();
                tempResponseMap.put("date", rs.getString("date")); //Почему-то режет последний символ
                tempResponseMap.put("forum", rs.getString("forum"));
                tempResponseMap.put("id", new Integer(rs.getString("id")));
                tempResponseMap.put("isClosed", new Boolean(rs.getString("isClosed")));
                tempResponseMap.put("isDeleted", new Boolean(rs.getString("isDeleted")));
                tempResponseMap.put("message", rs.getString("message"));
                tempResponseMap.put("slug", rs.getString("slug"));
                tempResponseMap.put("title",  rs.getString("title"));
                tempResponseMap.put("user", rs.getString("user"));
                listOfResponseMap.add(i, tempResponseMap);
                i++;
            }

            for (int j=0; j<i; j++){
                //Posts
                subSqlSelect = "SELECT COUNT(T.id) AS posts FROM post P LEFT JOIN thread T ON P.thread=T.id WHERE T.id=" + listOfResponseMap.get(j).get("id") +";";
                rsSub = sqlQuery.executeQuery(subSqlSelect);
                while(rsSub.next()){
                    listOfResponseMap.get(j).put("posts", rsSub.getString("posts"));
                }

                //Likes
                subSqlSelect = "SELECT COUNT(userid) AS likes FROM threadlikes WHERE threadid=" + listOfResponseMap.get(j).get("id") +";";
                rsSub = sqlQuery.executeQuery(subSqlSelect);
                int likes = 0, dislikes = 0;
                while(rsSub.next()){
                    likes = new Integer(rsSub.getString("likes"));
                    listOfResponseMap.get(j).put("likes", likes);
                }

                //dislikes
                subSqlSelect = "SELECT COUNT(userid) AS dislikes FROM threaddislikes WHERE threadid=" + listOfResponseMap.get(j).get("id") +";";
                rsSub = sqlQuery.executeQuery(subSqlSelect);

                while(rsSub.next()){
                    dislikes = new Integer(rsSub.getString("dislikes"));
                    listOfResponseMap.get(j).put("dislikes", dislikes);
                }
                listOfResponseMap.get(j).put("points", (likes-dislikes));
            }

            if (related.equals("user")) {
                for (int j = 0; j < i; j++) {
                    sqlSelect = "SELECT * FROM user WHERE email=\'" + listOfResponseMap.get(j).get("user") + "\';";
                    rs = sqlQuery.executeQuery(sqlSelect);
                    while(rs.next()){
                        //Display values
                        Map<String, Object> tempUserMap =  new HashMap<>();
                        tempUserMap.put("about", rs.getString("about"));
                        tempUserMap.put("email", rs.getString("email"));
                        tempUserMap.put("id", new Integer(rs.getString("id")));
                        tempUserMap.put("isAnonymous", new Boolean(rs.getString("isAnonymous")));
                        tempUserMap.put("name", rs.getString("name"));
                        tempUserMap.put("username", rs.getString("username"));
                        listOfResponseMap.get(j).put("user", tempUserMap);
                    }
                }
            } else if(related.equals("forum")){
                for (int j = 0; j < i; j++) {
                    sqlSelect = "SELECT * FROM forum WHERE short_name=\'" +listOfResponseMap.get(j).get("forum")+ "\';";
                    rs = sqlQuery.executeQuery(sqlSelect);

                    while(rs.next()) {
                        //Display values
                        Map<String, Object> tempForumMap =  new HashMap<>();
                        tempForumMap.put("name", rs.getString("name"));
                        tempForumMap.put("short_name", rs.getString("short_name"));
                        tempForumMap.put("id", new Integer(rs.getString("id")));
                        tempForumMap.put("user", rs.getString("user"));
                        listOfResponseMap.get(j).put("forum", tempForumMap);
                    }
                }

            }

            jsonResponse.put("response", listOfResponseMap);
            rs.close(); rs=null;
            */
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
