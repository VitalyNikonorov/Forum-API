package db.thread;

import org.json.JSONObject;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Виталий on 25.03.2015.
 */
public class ThreadInfo {
    public static Map<String, Object> getShortThreadInfo(Connection connection, String date, String user) {
        // Database__
        Map<String, Object> responseMap =  new HashMap<>();
        String sqlSelect = null;
        // Database
        try {
            Statement sqlQuery = connection.createStatement();
            ResultSet rs = null;

            sqlSelect = "SELECT * FROM thread WHERE date=\'" +date+ "\' AND user=\'" +user+ "\';";
            rs = sqlQuery.executeQuery(sqlSelect);

            while(rs.next()){
                //Display values
                responseMap.put("date", rs.getString("date").substring(0, 19));
                responseMap.put("forum", rs.getString("forum"));
                responseMap.put("id", new Integer(rs.getString("id")));
                responseMap.put("isClosed", new Boolean(rs.getString("isClosed")));
                if (rs.getString("isDeleted") == null){
                    responseMap.put("isDeleted", JSONObject.NULL);
                }else{
                    responseMap.put("isDeleted", new Boolean(rs.getString("isDeleted")));
                }
                responseMap.put("message", rs.getString("message"));
                responseMap.put("slug", rs.getString("slug"));
                responseMap.put("title", rs.getString("title"));
                responseMap.put("user", rs.getString("user"));
            }
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
            System.out.println("Other Error in shortDetailsThreadServlet.");
        }
        //Database!!!!
        return responseMap;

    }

    public static Map<String, Object> getShortThreadInfoById(Connection connection, int id) {
        // Database
        Map<String, Object> responseMap =  new HashMap<>();
        String sqlSelect = null;
        // Database
        try {
            Statement sqlQuery = connection.createStatement();
            ResultSet rs = null;

            sqlSelect = "SELECT * FROM thread WHERE id=\'" +id+ "\';";
            rs = sqlQuery.executeQuery(sqlSelect);

            while(rs.next()){
                //Display values
                //System.out.println("213123 = " + rs.getString("date"));
                //String date = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date((request.getParameter("date")))
                //String date = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(rs.getString("date"));
                //System.out.print(date);
                responseMap.put("date", rs.getString("date").substring(0, 19));
                responseMap.put("forum", rs.getString("forum"));
                responseMap.put("id", new Integer(rs.getString("id")));
                if(rs.getString("isClosed").equals(null))
                responseMap.put("isClosed", new Boolean(rs.getString("isClosed")));
                if (rs.getString("isDeleted") == null){
                    responseMap.put("isDeleted", JSONObject.NULL);
                }else{
                    responseMap.put("isDeleted", new Boolean(rs.getString("isDeleted")));
                }
                responseMap.put("message", rs.getString("message"));
                responseMap.put("slug", rs.getString("slug"));
                responseMap.put("title", rs.getString("title"));
                responseMap.put("user", rs.getString("user"));
            }
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
            System.out.println("Other Error in shortDetailsThreadServlet.");
        }
        //Database!!!!
        return responseMap;
    }

    public static Map<String, Object> getFullThreadInfoById(Connection connection, int id) {
        // Database
        Map<String, Object> responseMap =  new HashMap<>();
        String sqlSelect = null;
        // Database
        try {
            Statement sqlQuery = connection.createStatement();
            ResultSet rs = null;
            responseMap = ThreadInfo.getShortThreadInfoById(connection, id);

            //Likes
            sqlSelect = "SELECT COUNT(userid) AS likes FROM threadlikes WHERE threadid=" + id +";";

            rs = sqlQuery.executeQuery(sqlSelect);
            int likes = 0, dislikes = 0;
            while(rs.next()){
                likes = new Integer(rs.getString("likes"));
                responseMap.put("likes", likes);
            }

            //dislikes
            sqlSelect = "SELECT COUNT(userid) AS dislikes FROM threaddislikes WHERE threadid=" + id +";";
            rs = sqlQuery.executeQuery(sqlSelect);

            while(rs.next()){
                dislikes = new Integer(rs.getString("dislikes"));
                responseMap.put("dislikes", dislikes);
            }
            responseMap.put("points", (likes - dislikes));


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
            System.out.println("Other Error in fullDetailsThreadServletById.");
        }
        //Database!!!!
        return responseMap;
    }

}
