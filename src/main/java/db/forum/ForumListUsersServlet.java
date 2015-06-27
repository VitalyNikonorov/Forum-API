package db.forum;

import db.user.UserInfo;
import main.DBConnectionPool;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Map;

/**
 * Created by vitaly on 20.06.15.
 */
public class ForumListUsersServlet extends HttpServlet {

    private DataSource dataSource;
    DBConnectionPool connectionPool;
    Connection conn = null;

    public ForumListUsersServlet(DataSource dataSource, DBConnectionPool connectionPool){
        this.dataSource = dataSource;
        this.connectionPool = connectionPool;
    }

    public void doGet(HttpServletRequest request,
                      HttpServletResponse response) throws ServletException, IOException {

        String forum = request.getParameter("forum");
        String order = request.getParameter("order");
        if (order == null){
            order = "desc";
        }

        String since_id = request.getParameter("since_id");
        if (since_id == null){
            since_id = "-1";
        }
        String limit = request.getParameter("limit");

        short status = 0;
        String message = "";

        ResultSet resultSet;
        Statement sqlQuery = null;
        try {
            conn = dataSource.getConnection();
            connectionPool.printStatus();

            sqlQuery = conn.createStatement();
/*select * from users where email in (select distinct user from posts where forum = 'tdxz4h8oaq') order by name desc limit 70;*/

            String sqlSelect = "select * from users where id in (1, 3, 6, 7, 8) "; // /*email = \'iroax@gmail.com" + /*in (select distinct user_email from post where forum =\'" +forum+ */ "\') ";

           /*
            String sqlSelect = "SELECT DISTINCT U.id FROM post P JOIN users U ON P.user_email = U.email WHERE (P.forum=\'" +forum+ "\' " +
                "AND U.id > "+since_id+") ORDER BY U.name " +order;*/

            if (!since_id.equals("-1")){
                sqlSelect = sqlSelect + "AND id > "+since_id+" ORDER BY name " +order;
            }else{
                sqlSelect = sqlSelect + " ORDER BY name " +order;
            }

            if (limit != null){
                sqlSelect = sqlSelect + " LIMIT " +limit +";";
            }else{
                sqlSelect = sqlSelect + ";";
            }

            resultSet = sqlQuery.executeQuery(sqlSelect);
            createResponse(response, status, message, resultSet);

            sqlQuery.close();
            sqlQuery = null;

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

        ArrayList<Map<String, Object>> listOfResponse =  new ArrayList<Map<String, Object>>();

        if (status != 0 || resultSet == null) {
            obj.put("response", message);
        } else {
            int i = 0;
            while (resultSet.next()) {
                listOfResponse.add(i, UserInfo.getFullUserInfoById(conn, resultSet.getInt("id")));
                i++;
            }
            obj.put("response", listOfResponse);
        }
        obj.put("code", status);
        response.getWriter().write(obj.toString());
    }
}