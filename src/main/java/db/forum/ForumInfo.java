package db.forum;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Виталий on 24.03.2015.
 */
public class ForumInfo {
    public static Map<String, Object> getShortForumInfo(Connection connection, String short_name) {
        // Database
        Map<String, Object> responseMap =  new HashMap<>();
        String sqlSelect = null;
        // Database
        try {
            Statement sqlQuery = connection.createStatement();
            ResultSet rs = null;

            sqlSelect = "SELECT * FROM forum WHERE short_name=\'" +short_name+ "\';";
            rs = sqlQuery.executeQuery(sqlSelect);

            while(rs.next()){
                //Display values
                responseMap.put("name", rs.getString("name"));
                responseMap.put("short_name", rs.getString("short_name"));
                responseMap.put("user", rs.getString("user"));
                responseMap.put("id", new Integer(rs.getString("id")));
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
            System.out.println("Other Error in shortDetailsForumServlet.");
        }
        //Database!!!!
        return responseMap;
    }
}
