package db.thread;

import org.json.JSONObject;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Виталий on 25.03.2015.
 */
public class ThreadInfo {
    public static Map<String, Object> getShortThreadInfo(Connection connection, String date, String user) {
        // Database
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
                responseMap.put("date", rs.getString("date"));
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
                responseMap.put("date", rs.getString("date"));
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
}
