package db.thread;

import db.user.UserInfo;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;

import static java.lang.Integer.parseInt;
import static java.lang.String.format;

/**
 * Created by vitaly on 23.06.15.
 */
public class ListPostsThreadServlet extends HttpServlet {

    private Connection connection;
    public ListPostsThreadServlet(Connection connection){ this.connection = connection; }

    public void doGet(HttpServletRequest request,
                      HttpServletResponse response) throws ServletException, IOException {

        short status = 0;
        String message = "";

        String thread_str = request.getParameter("thread");
        String asc = request.getParameter("order");
        String since = request.getParameter("since");
        String limit = request.getParameter("limit");
        String sort = request.getParameter("sort");

        int sortType = -1;
        if (sort == null) {
            sort = "flat";
        }

        switch (sort) {
            case "flat": sortType = 0; break;
            case "tree": sortType = 1; break;
            case "parent_tree": sortType = 2; break;
        }

        if(thread_str == null || Integer.parseInt(thread_str) == 0 || sortType == -1) {
            status = 3;
            message = "Incorrect JSON";
        }


        try {

            StringBuilder query = new StringBuilder();
            ResultSet resultSet = null;
            Statement sqlQuery = connection.createStatement();

            if (status == 0) {
                switch (sortType) {
                    case 0:
                        query
                                .append("select id from post ")
                                .append("where thread = ")
                                .append(parseInt(thread_str))
                                .append(" ");
                        appendDateAndAscAndLimit(query, since, asc, limit);
                        break;
                    case 1:
                        query
                                .append("select id from post ")
                                .append("where thread = ").append(parseInt(thread_str)).append(" ");
                        if (since != null) {
                            query.append("and date_of_creating > '").append(since).append("' ");
                        }
                        query.append("order by parent, date_of_creating ");
                        appendLimitAndAsc(query, limit, asc);
                        break;
                    case 2:
                        String subQuery = "select id from post where thread = " + parseInt(thread_str) + " and parent = '' order by date_of_creating limit " + limit + ";";
                        Statement statementSub = connection.createStatement();
                        ResultSet resultSetSub = statementSub.executeQuery(subQuery);

                        StringBuilder parents = new StringBuilder();
                        parents.append("('000'");
                        try {
                            while (resultSetSub.next()) {
                                parents.append(" '" + format("%03d", resultSetSub.getInt("id")) + "'");
                            }
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        parents.append(')');

                        resultSetSub.close();
                        resultSetSub = null;

                        query
                                .append("select id from post where thread = ")
                                .append(parseInt(thread_str))
                                .append(" and LEFT(parent, 3) in ").append(parents).append(";");
                }

                resultSet = sqlQuery.executeQuery(query.toString());
            }

            createResponse(response, status, message, resultSet);

            resultSet.close();
            resultSet = null;
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    private void createResponse(HttpServletResponse response, short status, String message, ResultSet resultSet) throws IOException, SQLException {

        response.setContentType("json;charset=UTF-8");
        response.setHeader("Cache-Control", "no-cache");
        response.setStatus(HttpServletResponse.SC_OK);

        JSONObject obj = new JSONObject();

        ArrayList<JSONObject> listOfResponse =  new ArrayList<JSONObject>();

        if (status != 0 || resultSet == null) {
            JSONObject data = new JSONObject();
            data.put("error", message);
            obj.put("response", data);
        } else {
            int i = 0;
            while (resultSet.next()) {
                listOfResponse.add(i, getPostDetails(resultSet.getInt("id")));
                i++;
            }
            obj.put("response", listOfResponse);
        }
        obj.put("code", status);
        response.getWriter().write(obj.toString());
    }

    public static void appendDateAndAscAndLimit(StringBuilder query, String since, String asc, String limit) {
        if (since != null) {
            query
                    .append("and date_of_creating > '")
                    .append(since)
                    .append("' ");
        }
        query.append("order by date_of_creating ");
        appendLimitAndAsc(query, limit, asc);
    }

    public static void appendLimitAndAsc(StringBuilder query, String limit, String asc) {
        if (asc == null) {
            query.append("desc ");
        } else {
            query.append(asc).append(" ");
        }
        if (limit != null) {
            query.append("limit ").append(limit);
        }
        query.append(";");
    }


    public JSONObject getPostDetails(int id) throws IOException, SQLException {

        JSONObject data = new JSONObject();
        ResultSet resultSet;
        try {

            PreparedStatement pstmt = connection.prepareStatement("select * from post where id = ?");
            pstmt.setInt(1, id);
            resultSet = pstmt.executeQuery();

            if (resultSet.next()) {
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

}