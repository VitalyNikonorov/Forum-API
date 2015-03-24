package db.post;

import org.json.JSONObject;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Виталий on 24.03.2015.
 */
public class PostInfo {
    public static Map<String, Object> getFullPostInfo(Connection connection, int post) {
        // Database
        Map<String, Object> responseMap =  new HashMap<>();
        JSONObject jsonResponse = new JSONObject();
        // Database
        try {
            Statement sqlQuery = connection.createStatement();
            ResultSet rs = null;

            String sqlSelect = "SELECT * FROM post WHERE id=\'" +post+ "\'";
            rs = sqlQuery.executeQuery(sqlSelect);

            while(rs.next()){
                //Parse values
                responseMap.put("date", rs.getString("date"));
                responseMap.put("forum", rs.getString("forum"));
                responseMap.put("message", rs.getString("message"));
                responseMap.put("user", rs.getString("user"));

                responseMap.put("id", new Integer(rs.getString("id")));
                responseMap.put("thread", new Integer(rs.getString("thread")));
                if (rs.getString("parent") == null){
                    responseMap.put("parent", JSONObject.NULL);
                }else{
                    responseMap.put("parent", new Integer(rs.getString("parent")));
                }


                if (rs.getString("isApproved") == null){
                    responseMap.put("isApproved", JSONObject.NULL);
                }else{
                    responseMap.put("isApproved", new Boolean(rs.getString("isApproved")));
                }

                if (rs.getString("isDeleted") == null){
                    responseMap.put("isDeleted", JSONObject.NULL);
                }else{
                    responseMap.put("isDeleted", new Boolean(rs.getString("isDeleted")));
                }

                if (rs.getString("isEdited") == null){
                    responseMap.put("isEdited", JSONObject.NULL);
                }else{
                    responseMap.put("isEdited", new Boolean(rs.getString("isEdited")));
                }

                if (rs.getString("isHighlighted") == null){
                    responseMap.put("isHighlighted", JSONObject.NULL);
                }else{
                    responseMap.put("isHighlighted", new Boolean(rs.getString("isHighlighted")));
                }

                if (rs.getString("isSpam") == null){
                    responseMap.put("isSpam", JSONObject.NULL);
                }else{
                    responseMap.put("isSpam", new Boolean(rs.getString("isSpam")));
                }

            }

                //Likes
                sqlSelect = "SELECT COUNT(userid) AS likes FROM postlikes WHERE postid=" + post +";";

                rs = sqlQuery.executeQuery(sqlSelect);
                int likes = 0, dislikes = 0;
                while(rs.next()){
                    likes = new Integer(rs.getString("likes"));
                    responseMap.put("likes", likes);
                }

                //dislikes
                sqlSelect = "SELECT COUNT(userid) AS dislikes FROM postdislikes WHERE postid=" + post +";";
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
            System.out.println("Other Error in PostInfo.");
        }
        //Database!!!!
        return responseMap;
    }
}
