package db.post;

import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by vitaly on 20.06.15.
 */
public class CreatePostServlet  extends HttpServlet {
    private Connection connection;

    public CreatePostServlet(){
        try {
            DriverManager.registerDriver(new com.mysql.jdbc.Driver());
            this.connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/testdb","root", "");
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
            System.out.println("Error in CreateForumServlet while taking connection");
        }

    }

    public void doPost(HttpServletRequest request,
                       HttpServletResponse response) throws ServletException, IOException {

        StringBuffer jb = new StringBuffer();
        String line = null;
        try {
            BufferedReader reader = request.getReader();
            while ((line = reader.readLine()) != null)
                jb.append(line);
        } catch (Exception e) {
            System.out.print("Error by parsing CreateForum request request to JSON");
        }

        JSONObject jsonRequest = new JSONObject(jb.toString());

        short status = 0;
        String message = "";

        boolean isDeleted = false;
        boolean isApproved = false;
        boolean isHighlighted = false;
        boolean isEdited = false;
        boolean isSpam = false;

        if (jsonRequest.has("isDeleted")) {
            isDeleted = jsonRequest.getBoolean("isDeleted");
        }
        if (jsonRequest.has("isApproved")) {
            isApproved = jsonRequest.getBoolean("isApproved");
        }
        if (jsonRequest.has("isHighlighted")) {
            isHighlighted = jsonRequest.getBoolean("isHighlighted");
        }
        if (jsonRequest.has("isEdited")) {
            isEdited = jsonRequest.getBoolean("isEdited");
        }
        if (jsonRequest.has("isSpam")) {
            isSpam = jsonRequest.getBoolean("isSpam");
        }


        long parentId = 0;
        if (jsonRequest.has("parent")) {
            parentId = jsonRequest.get("parent") == null ? 0 : jsonRequest.getLong("parent") ;
        }

        String forum = (String)jsonRequest.get("forum");
        String user = (String)jsonRequest.get("user");
        String messagePost = (String)jsonRequest.get("message");
        long thread =  jsonRequest.getLong("thread");
        String date = (String)jsonRequest.get("date");
        int result;

        String matPath = "";
        if (parentId != 0) {
            String parent = null;

            try {
                parent = getParentPathByParentId(parentId);
            } catch (SQLException e) {
                e.printStackTrace();
            }

            if (parent == null) {
                status = 1;
                message = "There is no so parent";
            } else {
                if (parent.equals("")) {
                    matPath = String.format("%03d", parentId);
                } else {
                    matPath = parent + String.format("_%03d", parentId);
                }
            }
        }

        JSONObject data = null;

        if (status == 0) {
            PreparedStatement pstmt;
            try {
                if (checkForum(forum)) {
                     if (checkUser(user)) {
                         pstmt = connection.prepareStatement(
                                 "INSERT INTO post (isDeleted, isEdited, isApproved, isSpam, isHighlighted, user_email, forum, thread, parent, message, date_of_creating) " +
                                         "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);");

                            pstmt.setBoolean(1, isDeleted);
                            pstmt.setBoolean(2, isEdited);
                            pstmt.setBoolean(3, isApproved);
                            pstmt.setBoolean(4, isSpam);
                            pstmt.setBoolean(5, isHighlighted);
                            pstmt.setString(6, user);
                            pstmt.setString(7, forum);
                            pstmt.setLong(8, thread);
                            pstmt.setString(9, matPath);
                            pstmt.setString(10, messagePost);
                            pstmt.setString(11, date);

                            pstmt.executeUpdate();
                            pstmt.close();

                            /////////////////
                            int id = -1;

                            try {
                                pstmt = connection.prepareStatement("SELECT * FROM post WHERE forum=? AND user_email=? AND date_of_creating=?");
                                pstmt.setString(1, forum);
                                pstmt.setString(2, user);
                                pstmt.setString(3, date);

                                ResultSet rs = null;
                                rs = pstmt.executeQuery();

                                if (rs != null && rs.next()) {
                                    id = rs.getInt("id");
                                }

                                rs.close();
                                rs = null;

                            } catch (SQLException e) {
                                e.printStackTrace();
                            }

                            if (id != -1) {
                                data = new JSONObject();
                                data.put("date", date);
                                data.put("forum", forum);
                                data.put("id", id);
                                data.put("isApproved", isApproved);
                                data.put("isHighlighted", isHighlighted);
                                data.put("isEdited", isEdited);
                                data.put("isSpam", isSpam);
                                data.put("isDeleted", isDeleted);
                                data.put("message", messagePost);

                                if (matPath.equals("")) {
                                    data.put("parent", JSONObject.NULL);
                                } else {
                                    // TODO
                                    int indexLast = matPath.lastIndexOf("_");
                                    data.put("parent", Integer.parseInt(matPath.substring(indexLast + 1)));
                                }
                                data.put("thread", thread);
                                data.put("user", user);
                            } else {
                                status = 1;
                                message = "Some error in postCerateServlet";
                            }

                        }else {
                            status = 1;
                            message = "There is now this user";
                        }
                }else{
                    status = 1;
                    message = "There is now such forum";
                }
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }
        try {
            createResponse(response, status, message, data);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createResponse(HttpServletResponse response, short status, String message, JSONObject data) throws IOException, SQLException {
        response.setContentType("json;charset=UTF-8");
        response.setHeader("Cache-Control", "no-cache");
        response.setStatus(HttpServletResponse.SC_OK);

        JSONObject obj = new JSONObject();
        if (status != 0) {
            data = new JSONObject();
            data.put("error", message);
        }
        obj.put("response", data);
        obj.put("code", status);
        response.getWriter().write(obj.toString());
    }

    public String getParentPathByParentId(long parentId) throws SQLException {

        ResultSet resultSet;
        PreparedStatement pstmt = connection.prepareStatement(
                "select parent from post where id = ? ;");

        pstmt.setLong(1, parentId);
        resultSet = pstmt.executeQuery();

        String parent = null;
        try {
            if(resultSet.next()) {
                parent = resultSet.getString("parent");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        resultSet.close();
        resultSet = null;
        return parent;
    }


    public boolean checkUser(String email) throws SQLException {

        boolean response = false;
        PreparedStatement pstmt = connection.prepareStatement("SELECT * FROM users WHERE email=?");
        pstmt.setString(1, email);

        ResultSet rs = null;
        rs = pstmt.executeQuery();
        if (rs.next()) response = true;
        rs.close();
        rs = null;
        return response;
    }


    public boolean checkForum(String short_name) throws SQLException {

        boolean response = false;

        PreparedStatement pstmt = connection.prepareStatement("select * from forum where short_name = ?");
        pstmt.setString(1, short_name);
        ResultSet rs = pstmt.executeQuery();

        if (rs.next()) response = true;
        rs.close();
        rs = null;
        return response;
    }
}