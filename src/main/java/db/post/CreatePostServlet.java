package db.post;

import org.json.JSONObject;
import temletor.SqlWrapper;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by Виталий on 24.03.2015.
 */
public class CreatePostServlet extends HttpServlet {
    private Connection connection;
    public CreatePostServlet(Connection connection){ this.connection = connection; }

    public void doPost(HttpServletRequest request,
                       HttpServletResponse response) throws ServletException, IOException {

        StringBuffer jb = new StringBuffer();
        String line = null;
        try {
            BufferedReader reader = request.getReader();
            while ((line = reader.readLine()) != null)
                jb.append(line);
        } catch (Exception e) { /*report an error*/ }

        JSONObject jsonRequest = new JSONObject(jb.toString());
        Set<String> jsonRequestKeys = jsonRequest.keySet();


        JSONObject jsonResponse = new JSONObject();
        Map<String, Object> responseMap =  new HashMap<>();

        // Database
        try {
            Statement sqlQuery = connection.createStatement();
            ResultSet rs = null;

            String queryStr;

            String[] fullparams = {"date", "thread", "message", "user", "forum", "parent", "isApproved", "isHighlighted", "isEdited", "isSpam", "isDeleted"};

            int size = 0;
            for (int i=0; i<fullparams.length; i++){
                if (jsonRequestKeys.contains(fullparams[i])){
                    size++;
                }
            }
            String[] params = new String[size];

            for (int i=0, j=0; i<fullparams.length; i++){
                if (jsonRequestKeys.contains(fullparams[i])){
                    params[j]=fullparams[i];
                    j++;
                }
            }

            queryStr = SqlWrapper.getInsertQuery("post", params, jsonRequest);
            sqlQuery.executeUpdate(queryStr);


            String sqlSelect = "SELECT * FROM post WHERE date=\'" +jsonRequest.get("date")+ "\' AND user=\'" +jsonRequest.get("user")+ "\';";
            rs = sqlQuery.executeQuery(sqlSelect);

            while(rs.next()){
                //Display values
                //responseMap.put("date", rs.getString("date"));
                responseMap.put("date", jsonRequest.get("date"));
                responseMap.put("thread", rs.getString("thread"));
                responseMap.put("id", new Integer(rs.getString("id")));
                responseMap.put("isApproved", new Boolean(rs.getString("isApproved")));
                responseMap.put("isHighlighted", new Boolean(rs.getString("isHighlighted")));
                responseMap.put("isEdited", new Boolean(rs.getString("isEdited")));
                responseMap.put("isSpam", new Boolean(rs.getString("isSpam")));
                responseMap.put("isDeleted", new Boolean(rs.getString("isDeleted")));
                responseMap.put("user", rs.getString("user"));
                responseMap.put("forum", rs.getString("forum"));
                if (rs.getString("parent") != null ) {
                    responseMap.put("parent", rs.getString("parent"));
                }
            }

            jsonResponse.put("code", 0);
            jsonResponse.put("response", responseMap);
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
            System.out.println("Other Error in CreatePostServlet.");
        }
        //Database!!!!

        response.getWriter().println(jsonResponse);
    }
}
