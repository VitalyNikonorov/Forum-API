package db.thread;

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
import java.util.InputMismatchException;
import java.util.Map;

/**
 * Created by Виталий on 25.03.2015.
 */
public class ListOfThreadsServlet extends HttpServlet {
    private Connection connection;
    public ListOfThreadsServlet(Connection connection){ this.connection = connection; }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Map<String, Object> responseMap =  new HashMap<>();
        JSONObject jsonResponse = new JSONObject();
        ArrayList<Integer> listOfId =  new ArrayList<Integer>();
        ArrayList<Map<String, Object>> listOfResponseMap =  new ArrayList<Map<String, Object>>();

        String user = request.getParameter("user");
        String forum = request.getParameter("forum");
        String limit = request.getParameter("limit");
        String since = request.getParameter("since");
        String order = request.getParameter("order");

        if (order == null){
            order = "desc";
        }

        if ( !(order.equals("asc") || order.equals("desc")) ) {
            jsonResponse.put("code", 3);
            jsonResponse.put("response", "Incorrect order parameter");
            response.getWriter().println(jsonResponse);
        }

        // Database
        try {
            Statement sqlQuery = connection.createStatement();
            ResultSet rs = null;

            String sqlSelect = "SELECT id FROM thread WHERE ( (forum=\'" +forum+ "\' OR user=\'" +user+ "\') ";

            if (since != null){
                sqlSelect = sqlSelect + " AND date>=\'" +since+ "\' ";
            }

            sqlSelect += ") ORDER BY date " +order;

            if (limit != null){
                sqlSelect = sqlSelect + " LIMIT " +limit+ ";";
            }else{
                sqlSelect = sqlSelect + ";";
            }

            rs = sqlQuery.executeQuery(sqlSelect);
            int count = 0;

            while(rs.next()){
                //Parse values
                listOfId.add(count, new Integer(rs.getString("id")));
                count++;
            }

            for (int i=0; i<count; i++){
                listOfResponseMap.add(i, ThreadInfo.getFullThreadInfoById(connection, listOfId.get(i)));
            }

            jsonResponse.put("code", 0);
            jsonResponse.put("response", listOfResponseMap);
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
            System.out.println("Other Error in DetailsUserServlet.");
        }
        //Database!!!!
        response.getWriter().println(jsonResponse);
    }

}
