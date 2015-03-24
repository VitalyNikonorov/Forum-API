package db.thread;

import db.forum.ForumInfo;
import db.user.UserInfo;
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
 * Created by Виталий on 25.03.2015.
 */
public class GetThreadDetailsServlet extends HttpServlet {
    private Connection connection;
    public GetThreadDetailsServlet(Connection connection){ this.connection = connection; }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        JSONObject jsonResponse = new JSONObject();
        Map<String, String[]> params =  request.getParameterMap();
        Map<String, Object> userMap =  new HashMap<>();
        Map<String, Object> forumMap =  new HashMap<>();
        String[] related = params.get("related");
        int thread = new Integer(request.getParameter("thread"));
        Map<String, Object> responseMap = ThreadInfo.getFullThreadInfoById(connection, thread);

        if (related != null){
            for(int i=0; i<related.length; i++){
                switch (related[i]) {
                    case "user":
                        userMap = UserInfo.getShortUserInfo(connection, responseMap.get("user").toString());
                        responseMap.put("user", userMap);
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
        response.getWriter().println(jsonResponse);
    }

}
