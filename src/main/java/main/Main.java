package main;

import admin.AdminPageServlet;
import db.ClearServlet;
import db.forum.*;
import db.post.CreatePostServlet;
import db.post.GetPostDetailsServlet;
import db.thread.*;
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

        Server server = new Server(8081);
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        Connection connection = null;

        // DATABASE

        try {
            DriverManager.registerDriver(new com.mysql.jdbc.Driver());
            connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/testdb","test", "test");
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

        System.out.append("Starting at port: ").append(String.valueOf(8081)).append('\n');
        context.addServlet(new ServletHolder(clear), "/db/api/clear/");
        context.addServlet(new ServletHolder(AdminPage), "/admin");

        //FORUM
            //SERVLETS
        Servlet createForum = new CreateForumServlet(connection);
        Servlet getForumDetails = new GetForumDetailsServlet(connection);
        Servlet listOfThreads = new ForumListOfThreadsServlet(connection);
        Servlet listOfUsers = new ForumListOfUsersServlet(connection);
        Servlet listOfPosts = new ForumListOfPostsServlet(connection);
            //context
        context.addServlet(new ServletHolder(createForum), "/db/api/forum/create/");
        context.addServlet(new ServletHolder(getForumDetails), "/db/api/forum/details/");
        context.addServlet(new ServletHolder(listOfThreads), "/db/api/forum/listThreads/");
        context.addServlet(new ServletHolder(listOfUsers), "/db/api/forum/listUsers/");
        context.addServlet(new ServletHolder(listOfPosts), "/db/api/forum/listPosts/");

        //POST
        //SERVLETS
        Servlet createPost = new CreatePostServlet(connection);
        Servlet getPostDetails = new GetPostDetailsServlet(connection);

        //context
        context.addServlet(new ServletHolder(createPost), "/db/api/post/create/");
        context.addServlet(new ServletHolder(getPostDetails), "/db/api/post/details/");

        //THREAD
        //SERVLETS
        Servlet createThread = new CreateThreadServlet(connection);
        Servlet getThreaddetalis = new GetThreadDetailsServlet(connection);
        Servlet getListOfThreads = new ListOfThreadsServlet(connection);
        Servlet closeThread = new CloseThreadServlet(connection);
        Servlet openThread = new OpenThreadServlet(connection);
        Servlet removeThread = new RemoveThreadServlet(connection);
        Servlet restoreThread = new RestoreThreadServlet(connection);
        Servlet subscribeThread = new SubscribeThreadServlet(connection);
        Servlet unsubscribeThread = new UnsubscribeThreadServlet(connection);
        Servlet updateThread = new UpdateThreadServlet(connection);
        Servlet voteThread = new VoteThreadServlet(connection);

        //context
        context.addServlet(new ServletHolder(createThread), "/db/api/thread/create/");
        context.addServlet(new ServletHolder(getThreaddetalis), "/db/api/thread/details/");
        context.addServlet(new ServletHolder(getListOfThreads), "/db/api/thread/list/");
        context.addServlet(new ServletHolder(closeThread), "/db/api/thread/close/");
        context.addServlet(new ServletHolder(openThread), "/db/api/thread/open/");
        context.addServlet(new ServletHolder(removeThread), "/db/api/thread/remove/");
        context.addServlet(new ServletHolder(restoreThread), "/db/api/thread/restore/");
        context.addServlet(new ServletHolder(subscribeThread), "/db/api/thread/subscribe/");
        context.addServlet(new ServletHolder(unsubscribeThread), "/db/api/thread/unsubscribe/");
        context.addServlet(new ServletHolder(updateThread), "/db/api/thread/update/");
        context.addServlet(new ServletHolder(voteThread), "/db/api/thread/vote/");


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