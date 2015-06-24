package db;

import db.user.UserInfo;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.*;


/**
 * Created by vitaly on 23.06.15.
 */
public class Status extends HttpServlet {
    private Connection connection;
    public Status(Connection connection){ this.connection = connection; }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        response.setContentType("json;charset=UTF-8");
        response.setHeader("Cache-Control", "no-cache");
        response.setStatus(HttpServletResponse.SC_OK);
        JSONObject jsonResponse = new JSONObject();
        JSONObject responseMap = new JSONObject();
        int users = 0;
        int threads = 0;
        int forums = 0;
        int posts = 0;

        try {
            Statement sqlQuery = connection.createStatement();
            ResultSet rs = null;
            String query;

            query = "SELECT COUNT(*) FROM users;";
            rs = sqlQuery.executeQuery(query);

            while(rs.next()){
                //Parse values
                users = rs.getInt(1);
            }

            query = "SELECT COUNT(*) FROM forum;";
            rs = sqlQuery.executeQuery(query);

            while(rs.next()){
                //Parse values
                forums = rs.getInt(1);
            }

            query = "SELECT COUNT(*) FROM thread;";
            rs = sqlQuery.executeQuery(query);

            while(rs.next()){
                //Parse values
                threads = rs.getInt(1);
            }

            query = "SELECT COUNT(*) FROM post;";
            rs = sqlQuery.executeQuery(query);

            while(rs.next()){
                //Parse values
                posts = rs.getInt(1);
            }

            responseMap.put("user", users);
            responseMap.put("forum", forums);
            responseMap.put("thread", threads);
            responseMap.put("post", posts);

            jsonResponse.put("code", 0);
            jsonResponse.put("response", responseMap);

            sqlQuery.close();
            rs.close(); rs = null;
        }catch (SQLException ex){
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
        response.getWriter().println(jsonResponse);
    }
}
