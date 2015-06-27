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
import java.sql.*;

/**
 * Created by vitaly on 14.06.15.
 */
public class GetForumDetailsServlet extends HttpServlet {

    private DataSource dataSource;
    DBConnectionPool connectionPool;
    Connection conn = null;

    public GetForumDetailsServlet(DataSource dataSource, DBConnectionPool connectionPool){
        this.dataSource = dataSource;
        this.connectionPool = connectionPool;
    }

    public void doGet(HttpServletRequest request,
                      HttpServletResponse response) throws ServletException, IOException {

        String forum = request.getParameter("forum");
        String related = request.getParameter("related");

        try {
            createResponse(response, related, forum);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createResponse(HttpServletResponse response, String related, String short_name) throws IOException, SQLException {

        response.setContentType("json;charset=UTF-8");
        response.setHeader("Cache-Control", "no-cache");
        response.setStatus(HttpServletResponse.SC_OK);

        JSONObject jsonResponse = new JSONObject();
        JSONObject data = null;

        short status = 0;
        String message = "";

        if (related != null && !related.equals("user")) {
            status = 3;
            message = "Wrong JSON";
        } else {
            data = getForumDetails(short_name, related);
            if (data == null) {
                status = 1;
                message = "There is no forum for this request";
            }
        }

        jsonResponse.put("response", status == 0 ? data: message);
        jsonResponse.put("code", status);

        response.getWriter().write(jsonResponse.toString());
    }

    public JSONObject getForumDetails(String short_name, String related){

        JSONObject data = new JSONObject();
        PreparedStatement pstmt = null;
        try {
            conn = dataSource.getConnection();
            connectionPool.printStatus();
            ResultSet resultSet = null;

            pstmt = conn.prepareStatement("select * from forum where short_name = ?");
            pstmt.setString(1, short_name);
            resultSet = pstmt.executeQuery();

            if (resultSet.next()) {
                if (related != null) {
                    data.put("user", UserInfo.getFullUserInfo(conn, resultSet.getString("user_email")).get("response"));
                } else {
                    data.put("user", resultSet.getString("user_email"));
                }
                data.put("name", resultSet.getString("name"));
                data.put("id", resultSet.getInt("id"));
                data.put("short_name", resultSet.getString("short_name"));
            } else {
                data = null;
            }
            resultSet.close();
            resultSet = null;


            }catch(SQLException ex) {
            //System.out.println("SQLException caught");
            //System.out.println("---");
            /*while (ex != null) {
                System.out.println("Message   : " + ex.getMessage());
                System.out.println("SQLState  : " + ex.getSQLState());
                System.out.println("ErrorCode : " + ex.getErrorCode());
                System.out.println(ex.getMessage());
            }
            System.out.println("---");
            ex = ex.getNextException();*/
        } finally {
            if (pstmt != null) {
                try {
                    pstmt.close();
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
        return data;
    }

}
