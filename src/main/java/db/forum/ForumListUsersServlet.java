package db.forum;

import db.user.UserInfo;
import main.DBConnectionPool;
import main.Main;
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

        Connection conn = null;
        ResultSet resultSet;
        Statement sqlQuery = null;
        try {
            conn = Main.dataSource.getConnection();
            Main.connectionPool.printStatus();

            sqlQuery = conn.createStatement();
/*id in (1, 3, 6, 7, 8) */
            String sqlSelect = "select * from users where email = in (select distinct user_email from post where forum =\'" +forum+ "\') ";

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
            createResponse(conn, response, status, message, resultSet);

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        Main.connectionPool.printStatus();
    }

    private void createResponse(Connection conn, HttpServletResponse response, short status, String message, ResultSet resultSet) throws IOException, SQLException {

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