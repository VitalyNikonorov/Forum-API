package db.forum;

import db.user.UserInfo;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList ;
import java.util.Map;

public class ForumListPostsServlet extends HttpServlet {

    private Connection connection;
    public ForumListPostsServlet(Connection connection){ this.connection = connection; }

    public void doGet(HttpServletRequest request,
                      HttpServletResponse response) throws ServletException, IOException {

        Map<String, String[]> paramMap = request.getParameterMap();
        String forum = paramMap.containsKey("forum") ? paramMap.get("forum")[0] : null;
        String order = paramMap.containsKey("order") ? paramMap.get("order")[0] : null;
        String since = paramMap.containsKey("since") ? paramMap.get("since")[0] : null;
        String limit = paramMap.containsKey("limit") ? paramMap.get("limit")[0] : null;
        String[] related = paramMap.get("related");

        short status = 0;
        String message = "";
        if(forum == null) {
            status = 3;
            message = "Incorrect JSON";
        }

        ResultSet resultSet = null;
        try {
            Statement sqlQuery = connection.createStatement();


            String sqlSelect = "SELECT * FROM post WHERE forum=\'" +forum+ "\' AND date_of_creating > \'" + since +"\'" ;
            sqlSelect = sqlSelect + " ORDER BY " + " date_of_creating " +order;

            if (limit != null){
                sqlSelect = sqlSelect + " LIMIT " +limit +";";
            }else{
                sqlSelect = sqlSelect + ";";
            }

            if (status == 0) {
                resultSet = sqlQuery.executeQuery(sqlSelect);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            createResponse(response, status, message, resultSet, related);

            resultSet.close();
            resultSet = null;
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createResponse(HttpServletResponse response, short status, String message, ResultSet resultSet, String[] related) throws IOException, SQLException {

        response.setContentType("json;charset=UTF-8");
        response.setHeader("Cache-Control", "no-cache");
        response.setStatus(HttpServletResponse.SC_OK);

        JSONObject obj = new JSONObject();
        JSONObject data = new JSONObject();

        ArrayList<JSONObject> listOfResponseMap =  new ArrayList<JSONObject>();

        if (status != 0 || resultSet == null) {
            data.put("error", message);
            obj.put("response", data);
        } else {
            boolean user = false;
            boolean forum = false;
            boolean thread = false;
            if (related != null) {
                for (String aRelated : related) {
                    switch (aRelated) {
                        case "user":
                            user = true;
                            break;
                        case "forum":
                            forum = true;
                            break;
                        case "thread":
                            thread = true;
                            break;
                        default:
                            status = 3;
                            message = "Incorrect JSON";
                    }
                }
            }

            if (status == 0) {
                int i = 0;
                while (resultSet.next()) {
                    listOfResponseMap.add(i, getPostDetails(resultSet.getInt("id"), user, thread, forum));
                    i++;
                }
                obj.put("response", listOfResponseMap);
            } else {
                data.put("error", message);
                obj.put("response", data);
            }
        }
        obj.put("code", status);
        response.getWriter().write(obj.toString());
    }


    public JSONObject getPostDetails(int id, boolean user, boolean thread, boolean forum) throws IOException, SQLException {

        JSONObject data = new JSONObject();
        ResultSet resultSet;
        try {

            PreparedStatement pstmt = connection.prepareStatement("select * from post where id = ?");
            pstmt.setInt(1, id);
            resultSet = pstmt.executeQuery();

            if (resultSet.next()) {
                data.put("date", resultSet.getString("date_of_creating").substring(0, 19));
                if (forum) {
                    //////////// TODO в функцию
                    data.put("forum", getForumDetails(resultSet.getString("forum")));
                } else {
                    data.put("forum", resultSet.getString("forum"));
                }
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

                if (thread) {
                    data.put("thread", getThreadDetailsById(resultSet.getInt("thread")));
                } else {
                    data.put("thread", resultSet.getInt("thread"));
                }

                if (user) {
                    data.put("user", UserInfo.getFullUserInfo(connection, resultSet.getString("user_email")).get("response"));
                } else {
                    data.put("user", resultSet.getString("user_email"));
                }

            } else {
                data = null;
            }

            resultSet.close();
            resultSet = null;

        }catch(SQLException ex) {
            System.out.println("SQLException caught");
            System.out.println("---");
            while (ex != null) {
                System.out.println("Message   : " + ex.getMessage());
                System.out.println("SQLState  : " + ex.getSQLState());
                System.out.println("ErrorCode : " + ex.getErrorCode());
                System.out.println(ex.getMessage());
            }
            System.out.println("---");
            ex = ex.getNextException();
        }

        return data;
    }


    public JSONObject getForumDetails(String short_name){

        JSONObject data = new JSONObject();
        try {
            ResultSet resultSet;

            PreparedStatement pstmt = connection.prepareStatement("select * from forum where short_name = ?");
            pstmt.setString(1, short_name);
            resultSet = pstmt.executeQuery();

            if (resultSet.next()) {
                data.put("user", resultSet.getString("user_email"));
                data.put("name", resultSet.getString("name"));
                data.put("id", resultSet.getInt("id"));
                data.put("short_name", resultSet.getString("short_name"));
            } else {
                data = null;
            }

            resultSet.close();
            resultSet = null;

        }catch(SQLException ex) {
            System.out.println("SQLException caught");
            System.out.println("---");
            while (ex != null) {
                System.out.println("Message   : " + ex.getMessage());
                System.out.println("SQLState  : " + ex.getSQLState());
                System.out.println("ErrorCode : " + ex.getErrorCode());
                System.out.println(ex.getMessage());
            }
            System.out.println("---");
            ex = ex.getNextException(); //TODO
        }
        return data;
    }


    public JSONObject getThreadDetailsById(int id) throws IOException, SQLException{

        ResultSet resultSet;
        ResultSet resultSetCount;

        PreparedStatement pstmt = connection.prepareStatement("select * from thread where id = ?");
        pstmt.setInt(1, id);

        resultSet = pstmt.executeQuery();

        PreparedStatement pstmtCountPosts = connection.prepareStatement("select count(*) as amount from post where thread = " + id + " and isDeleted = 0;");

        resultSetCount = pstmtCountPosts.executeQuery();

        JSONObject data = new JSONObject();

        if (resultSet.next() && resultSetCount.next()) {
            data.put("date", resultSet.getString("date_of_creating").substring(0, 19));
            data.put("dislikes", resultSet.getInt("dislikes"));
            data.put("forum", resultSet.getString("forum"));
            data.put("id", resultSet.getInt("id"));
            data.put("isClosed", resultSet.getBoolean("isClosed"));
            data.put("isDeleted", resultSet.getBoolean("isDeleted"));
            data.put("likes", resultSet.getInt("likes"));
            data.put("message", resultSet.getString("message"));
            data.put("points", resultSet.getInt("likes") - resultSet.getInt("dislikes") );
            data.put("posts", resultSetCount.getInt("amount"));
            data.put("slug", resultSet.getString("slug"));
            data.put("title", resultSet.getString("title"));
            data.put("user", resultSet.getString("user_email"));

        } else {
            String message = "There is no thread with such id!";
            data.put("error", message);
        }

        resultSet.close();
        resultSet = null;

        resultSetCount.close();
        resultSetCount = null;

        return data;
    }



}
