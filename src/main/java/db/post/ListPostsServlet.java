package db.post;

import db.user.UserInfo;
import main.DBConnectionPool;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;

/**
 * Created by vitaly on 21.06.15.
 */
public class ListPostsServlet extends HttpServlet {


    private DataSource dataSource;
    DBConnectionPool connectionPool;
    Connection conn = null;

    public ListPostsServlet(DataSource dataSource, DBConnectionPool connectionPool) {
        this.dataSource = dataSource;
        this.connectionPool = connectionPool;
    }


    public void doGet(HttpServletRequest request,
                      HttpServletResponse response) throws ServletException, IOException {

        short status = 0;
        String message = "";

        String forum = request.getParameter("forum");
        String thread_str = request.getParameter("thread");
        String order = request.getParameter("order");
        String since = request.getParameter("since");
        String limit = request.getParameter("limit");

        if(forum == null && thread_str == null) {
            status = 3;
            message = "Incorrect JSON";
        }
        Statement sqlQuery = null;

        try {
            conn = dataSource.getConnection();
            connectionPool.printStatus();
            sqlQuery = conn.createStatement();
            ResultSet resultSet = null;
            String sqlSelect;

            if (status == 0) {
                if(forum == null) {

                    sqlSelect = "SELECT * FROM post WHERE thread=\'" +thread_str+ "\' " ;

                    if (since != null) {
                        sqlSelect = sqlSelect + "AND date_of_creating > '" + since + "\' ";
                    }

                    sqlSelect = sqlSelect + " ORDER BY " + " date_of_creating " +order;

                    if (limit != null){
                        sqlSelect = sqlSelect + " LIMIT " +limit +";";
                    }else{
                        sqlSelect = sqlSelect + ";";
                    }

                    resultSet = sqlQuery.executeQuery(sqlSelect);
                } else {

                    sqlSelect = "SELECT * FROM post WHERE forum=\'" +forum+ "\'" ;
                    if (since != null) {
                        sqlSelect = sqlSelect + "AND date_of_creating > '" + since + "\' ";
                    }

                    sqlSelect = sqlSelect + " ORDER BY " + " date_of_creating " +order;

                    if (limit != null){
                        sqlSelect = sqlSelect + " LIMIT " +limit +";";
                    }else{
                        sqlSelect = sqlSelect + ";";
                    }

                    resultSet = sqlQuery.executeQuery(sqlSelect);
                }
            }
            createResponse(response, status, message, resultSet);

            resultSet.close();
            resultSet = null;

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
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
    }

    private void createResponse(HttpServletResponse response, short status, String message, ResultSet resultSet) throws IOException, SQLException {

        response.setContentType("json;charset=UTF-8");
        response.setHeader("Cache-Control", "no-cache");
        response.setStatus(HttpServletResponse.SC_OK);

        JSONObject obj = new JSONObject();
        ArrayList<JSONObject> listOfResponse =  new ArrayList<JSONObject>();

        if (status != 0 || resultSet == null) {
            obj.put("response", message);
        } else {
            int i = 0;
            while (resultSet.next()) {

                JSONObject data = new JSONObject();
                data.put("date", resultSet.getString("date_of_creating").substring(0, 19));
                data.put("forum", resultSet.getString("forum"));
                data.put("id", resultSet.getInt("id"));
                data.put("isApproved", resultSet.getBoolean("isApproved"));
                data.put("isHighlighted", resultSet.getInt("isHighlighted") == 1 ? true : false);
                data.put("isEdited", resultSet.getBoolean("isEdited"));
                data.put("isSpam", resultSet.getBoolean("isSpam"));
                data.put("isDeleted", resultSet.getBoolean("isDeleted"));
                data.put("message", resultSet.getString("message"));
                data.put("likes", resultSet.getInt("likes"));
                data.put("dislikes", resultSet.getInt("dislikes"));
                data.put("points", resultSet.getInt("likes") - resultSet.getInt("dislikes"));
                String temp = resultSet.getString("parent");

                if (temp.equals("")) {
                    data.put("parent", JSONObject.NULL);
                }else {
                    int indexLast = temp.lastIndexOf("_");
                    data.put("parent", Integer.parseInt(temp.substring(indexLast + 1)));
                }
                data.put("thread", resultSet.getInt("thread"));
                data.put("user", resultSet.getString("user_email"));
                listOfResponse.add(i, data);
                i++;
            }
            obj.put("response", listOfResponse);
        }
        obj.put("code", status);
        response.getWriter().write(obj.toString());
    }
}