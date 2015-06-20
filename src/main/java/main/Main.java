package main;

import admin.AdminPageServlet;
import db.ClearServlet;
import db.forum.*;
import db.post.CreatePostServlet;
import db.post.GetPostDetailsServlet;
import db.thread.CreateThreadServlet;
import db.thread.GetThreadDetailsServlet;
import db.user.*;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import javax.servlet.Servlet;
import java.sql.*;

/**
 * Created by Виталий on 15.03.2015.
 */
public class Main {
    public static void main(String[] args) throws Exception {

        Server server = new Server(8080);
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        Connection connection = null;

        // DATABASE

        try {
            DriverManager.registerDriver(new com.mysql.jdbc.Driver());
            connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/testdb","root", "");
            Statement sqlQuery = connection.createStatement();
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
            System.out.println("Other Error in Main.");
        }
        //Database!!!!

        Servlet clear = new ClearServlet();
        Servlet AdminPage = new AdminPageServlet(connection);

        System.out.append("Starting at port: ").append(String.valueOf(8080)).append('\n');
        context.addServlet(new ServletHolder(clear), "/db/api/clear/");
        context.addServlet(new ServletHolder(AdminPage), "/admin");


        //USER
            //SERVLETS
        Servlet createUser = new CreateUserServlet(connection);
        Servlet getUserDetails = new GetUserDetailsServlet(connection);
        Servlet followUser = new FollowUserServlet(connection);
        Servlet listFollowers = new UserListFollowersServlet(connection);
        Servlet listFollowing = new UserListFollowingServlet(connection);
        Servlet listPosts = new UserListPostsServlet(connection);
        Servlet unfollowUser = new UnfollowUserServlet(connection);
        Servlet updateProfile = new UpdateProfileServlet(connection);
            //CONTEXT
        context.addServlet(new ServletHolder(createUser), "/db/api/user/create/");
        context.addServlet(new ServletHolder(getUserDetails), "/db/api/user/details/");
        context.addServlet(new ServletHolder(followUser), "/db/api/user/follow/");
        context.addServlet(new ServletHolder(listFollowers), "/db/api/user/listFollowers/");
        context.addServlet(new ServletHolder(listFollowing), "/db/api/user/listFollowing/");
        context.addServlet(new ServletHolder(listPosts), "/db/api/user/listPosts/");
        context.addServlet(new ServletHolder(unfollowUser), "/db/api/user/unfollow/");
        context.addServlet(new ServletHolder(updateProfile), "/db/api/user/updateProfile/");


        //FORUM
        Servlet createForum = new CreateForumServlet();
        Servlet getForumDetails = new GetForumDetailsServlet(connection);
        Servlet forumListPostServlet = new ForumListPostsServlet(connection);
        Servlet forumListThreadsServlet = new ForumListThreadsServlet(connection);
        Servlet forumListUsersServlet = new ForumListUsersServlet(connection);

        //CONTEXT
        context.addServlet(new ServletHolder(createForum), "/db/api/forum/create/");
        context.addServlet(new ServletHolder(getForumDetails), "/db/api/forum/details/");
        context.addServlet(new ServletHolder(forumListPostServlet), "/db/api/forum/listPosts/");
        context.addServlet(new ServletHolder(forumListThreadsServlet), "/db/api/forum/listThreads/");
        context.addServlet(new ServletHolder(forumListUsersServlet), "/db/api/forum/listUsers/");

        //THREAD
        Servlet createThread = new CreateThreadServlet();
        Servlet getThreadDetails = new GetThreadDetailsServlet(connection);

        //CONTEXT
        context.addServlet(new ServletHolder(createThread), "/db/api/thread/create/");
        context.addServlet(new ServletHolder(getThreadDetails), "/db/api/thread/details/");

        //POST
        Servlet createPost = new CreatePostServlet();
        Servlet getPostDetails = new GetPostDetailsServlet(connection);

        //CONTEXT
        context.addServlet(new ServletHolder(createPost), "/db/api/post/create/");
        context.addServlet(new ServletHolder(getPostDetails), "/db/api/post/details/");


        //Static
        ResourceHandler resource_handler = new ResourceHandler();
        resource_handler.setDirectoriesListed(true);
        resource_handler.setResourceBase("static");

        HandlerList handlers = new HandlerList();
        handlers.setHandlers(new Handler[]{resource_handler, context});

        server.setHandler(handlers);


        server.start();
        server.join();
    }
}

/*В бд follower id1 - кто подписан, id2 - на кого*/