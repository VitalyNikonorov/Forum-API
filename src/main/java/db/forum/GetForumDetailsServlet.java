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
import java.sql.*;

/**
 * Created by vitaly on 14.06.15.
 */
public class GetForumDetailsServlet extends HttpServlet {

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
        Connection conn = null;
        try {
            conn = Main.dataSource.getConnection();
            Main.connectionPool.printStatus();
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
        }catch(SQLException ex) {
            ex.printStackTrace();
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
        return data;
    }

}
