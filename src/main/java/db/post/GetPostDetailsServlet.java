package db.post;

import db.forum.ForumInfo;
import db.thread.ThreadInfo;
import db.user.UserInfo;
import javafx.geometry.Pos;
import org.eclipse.jetty.server.Authentication;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Виталий on 24.03.2015.
 */
public class GetPostDetailsServlet extends HttpServlet {
    private Connection connection;
    public GetPostDetailsServlet(Connection connection){ this.connection = connection; }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        int post = new Integer(request.getParameter("post"));
        Map<String, String[]> params =  request.getParameterMap();
        String[] related = params.get("related");

        Map<String, Object> responseMap =  new HashMap<>();
        Map<String, Object> userMap =  new HashMap<>();
        Map<String, Object> threadMap =  new HashMap<>();
        Map<String, Object> forumMap =  new HashMap<>();
        JSONObject jsonResponse = new JSONObject();

        responseMap = PostInfo.getFullPostInfo(connection, post);

        if (related != null){
            for(int i=0; i<related.length; i++){
                switch (related[i]) {
                    case "user":
                        userMap = UserInfo.getShortUserInfo(connection, responseMap.get("user").toString());
                        responseMap.put("user", userMap);
                        break;
                    case "thread":
                        threadMap = ThreadInfo.getShortThreadInfoById(connection, new Integer(responseMap.get("thread").toString()));
                        responseMap.put("thread", threadMap);
                        break;
                    case "forum":
                        forumMap = ForumInfo.getShortForumInfo(connection, responseMap.get("forum").toString());
                        responseMap.put("forum", forumMap);
                        break;
                }

            }
        }
        jsonResponse.put("code", 0);
        jsonResponse.put("response", responseMap);


        //Database!!!!
        response.getWriter().println(jsonResponse);
    }
}
