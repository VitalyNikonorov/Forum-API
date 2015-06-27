package db.user;

import main.DBConnectionPool;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.*;
import java.util.*;

/**
 * Created by Виталий on 23.03.2015.
 */

public class UserListPostsServlet extends HttpServlet {
    private DataSource dataSource;
    DBConnectionPool connectionPool;
    Connection conn = null;

    public UserListPostsServlet(DataSource dataSource, DBConnectionPool connectionPool){
        this.dataSource = dataSource;
        this.connectionPool = connectionPool;
    }

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

        String since = request.getParameter("since");

        // Database
        Statement sqlQuery = null;
        try {
            conn = dataSource.getConnection();
            connectionPool.printStatus();

            sqlQuery = conn.createStatement();
            ResultSet rs = null;

            String sqlSelect = "SELECT * FROM post WHERE user_email=\'" +userEmail+ "\' " ;

            if (since != null) {
                sqlSelect = sqlSelect + "AND date_of_creating > '" + since + "\' ";
            }

            sqlSelect = sqlSelect + " ORDER BY " + " date_of_creating " +order;

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
                tempResponseMap.put("date", rs.getString("date_of_creating").substring(0, 19));
                tempResponseMap.put("forum", rs.getString("forum"));
                tempResponseMap.put("id", rs.getInt("id"));
                tempResponseMap.put("isApproved", rs.getBoolean("isApproved"));
                tempResponseMap.put("isHighlighted", rs.getInt("isHighlighted") == 1 ? true : false);
                tempResponseMap.put("isEdited", rs.getBoolean("isEdited"));
                tempResponseMap.put("isSpam", rs.getBoolean("isSpam"));
                tempResponseMap.put("isDeleted", rs.getBoolean("isDeleted"));
                tempResponseMap.put("message", rs.getString("message"));
                tempResponseMap.put("likes", rs.getInt("likes"));
                tempResponseMap.put("dislikes", rs.getInt("dislikes"));
                tempResponseMap.put("points", rs.getInt("likes") - rs.getInt("dislikes"));
                String temp = rs.getString("parent");

                if (temp.equals("")) {
                    tempResponseMap.put("parent", JSONObject.NULL);
                }else {
                    int indexLast = temp.lastIndexOf("_");
                    tempResponseMap.put("parent", Integer.parseInt(temp.substring(indexLast + 1)));
                }
                tempResponseMap.put("thread", rs.getInt("thread"));
                tempResponseMap.put("user", rs.getString("user_email"));

                listOfResponseMap.add(i, tempResponseMap);
                i++;
            }

            jsonResponse.put("code", 0);
            jsonResponse.put("response", listOfResponseMap);
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
        }finally {
            if (sqlQuery != null) {
                try {
                    sqlQuery.close();
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
}
