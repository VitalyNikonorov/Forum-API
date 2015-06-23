package db.user;

import org.json.JSONObject;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Виталий on 24.03.2015.
 */
public class UserInfo {

    public static JSONObject getFullUserInfo(Connection connection, String userMail) {
            // Database
        JSONObject jsonResponse = new JSONObject();
        String userEmail = userMail;
        try {
            PreparedStatement pstmt = connection.prepareStatement("SELECT * FROM users WHERE email=?");
            pstmt.setString(1, userEmail);

            ResultSet rs = null;
            rs = pstmt.executeQuery();

            Map<String, Object> user = new HashMap<>();

            while(rs.next()){
                //Parse values
                if( rs.getString("about").equals("")){
                    user.put("about", JSONObject.NULL);
                }else {
                    user.put("about", rs.getString("about"));
                }

                user.put("email", rs.getString("email"));
                user.put("id", new Integer(rs.getString("id")));
                user.put("isAnonymous", rs.getString("isAnonymous").equals("1")?true:false);
                user.put("name", rs.getString("name"));

                if( rs.getString("name").equals("")){
                    user.put("name", JSONObject.NULL);
                }else {
                    user.put("name", rs.getString("name"));
                }

                if( rs.getString("username").equals("")){
                    user.put("username", JSONObject.NULL);
                }else {
                    user.put("username", rs.getString("username"));
                }
            }


            //following
            String[] following;
            Statement sqlQuery = connection.createStatement();
            String sqlSelect = "SELECT Res.email FROM follow Fol LEFT JOIN users U ON U.id=Fol.follower_id JOIN users Res ON Fol.followee_id=Res.id WHERE U.id=" + user.get("id")+ ";";
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
            sqlSelect = "SELECT Res.email FROM follow Fol LEFT JOIN users U ON U.id=Fol.followee_id JOIN users Res ON Fol.follower_id=Res.id WHERE U.id=" + user.get("id") + ";";
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

            sqlSelect = "SELECT Sub.thread_id FROM subscribtion Sub LEFT JOIN users R ON R.id=Sub.user_id WHERE R.id=" + user.get("id") + ";";
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
                subscriptions[i] = new Integer(rs.getString("thread_id"));
                i++;
            }
            user.put("subscriptions", subscriptions);

            user.put("following", following);
            user.put("followers", followers);
            rs.close(); rs=null;

            jsonResponse.put("code", 0);
            jsonResponse.put("response", user);

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
            System.out.println("Other Error in userinfo.");
        }
        //Database!!!!
        return jsonResponse;
    }


    public static Map<String, Object> getFullUserInfoById(Connection connection, int id) {
        // Database
        Map<String, Object> user = new HashMap<>();
        try {
            PreparedStatement pstmt = connection.prepareStatement("SELECT * FROM users WHERE id=?");
            pstmt.setInt(1, id);

            ResultSet rs = null;
            rs = pstmt.executeQuery();



            while(rs.next()){
                //Parse values
                if( rs.getString("about").equals("")){
                    user.put("about", JSONObject.NULL);
                }else {
                    user.put("about", rs.getString("about"));
                }

                user.put("email", rs.getString("email"));
                user.put("id", new Integer(rs.getString("id")));
                user.put("isAnonymous", rs.getString("isAnonymous").equals("1")?true:false);
                user.put("name", rs.getString("name"));

                if( rs.getString("name").equals("")){
                    user.put("name", JSONObject.NULL);
                }else {
                    user.put("name", rs.getString("name"));
                }

                if( rs.getString("username").equals("")){
                    user.put("username", JSONObject.NULL);
                }else {
                    user.put("username", rs.getString("username"));
                }
            }


            //following
            String[] following;
            Statement sqlQuery = connection.createStatement();
            String sqlSelect = "SELECT Res.email FROM follow Fol LEFT JOIN users U ON U.id=Fol.follower_id JOIN users Res ON Fol.followee_id=Res.id WHERE U.id=" + user.get("id")+ ";";
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
            sqlSelect = "SELECT Res.email FROM follow Fol LEFT JOIN users U ON U.id=Fol.followee_id JOIN users Res ON Fol.follower_id=Res.id WHERE U.id=" + user.get("id") + ";";
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

            sqlSelect = "SELECT Sub.thread_id FROM subscribtion Sub LEFT JOIN users R ON R.id=Sub.user_id WHERE R.id=" + user.get("id") + ";";
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
                subscriptions[i] = new Integer(rs.getString("thread_id"));
                i++;
            }
            user.put("subscriptions", subscriptions);

            user.put("following", following);
            user.put("followers", followers);
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
            System.out.println("Other Error in userinfo.");
        }
        //Database!!!!
        return user;
    }


    public static int getUserIdByEmail(Connection connection, String userMail) {
        // Database
        JSONObject jsonResponse = new JSONObject();
        String userEmail = userMail;
        int id = -1;
        try {
            PreparedStatement pstmt = connection.prepareStatement("SELECT id FROM users WHERE email=?");
            pstmt.setString(1, userEmail);

            ResultSet rs = null;
            rs = pstmt.executeQuery();

            while(rs.next()){
                //Parse values
                id = new Integer(rs.getString("id"));
            }
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
            System.out.println("Other Error in userinfo.");
        }
        //Database!!!!
        return id;
    }

}
