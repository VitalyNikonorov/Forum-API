package db.user;

import main.DBConnectionPool;
import main.Main;
import org.json.JSONObject;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Created by Виталий on 22.03.2015.
 */


public class GetUserDetailsServlet extends HttpServlet {

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Connection conn = null;
        try {
            conn = Main.dataSource.getConnection();
            Main.connectionPool.printStatus();
            response.setContentType("application/json;charset=utf-8");
            String userEmail = request.getParameter("user");
            JSONObject jsonResponse = db.user.UserInfo.getFullUserInfo(conn, userEmail);
            response.getWriter().println(jsonResponse);
        }
        catch (Exception ex){
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
    }
}