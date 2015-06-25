package main;

import admin.AdminPageServlet;
import db.ClearServlet;
import db.Status;
import db.forum.*;
import db.post.*;
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

        Server server = new Server(8080);
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        DBConnect mysql = new DBConnect();
        Connection connection = mysql.getConnection();

        // DATABASE
        //Database!!!!

        Servlet clear = new ClearServlet(connection);
        Servlet status = new Status(connection);
        Servlet AdminPage = new AdminPageServlet(connection);

        System.out.append("Starting at port: ").append(String.valueOf(8080)).append('\n');
        context.addServlet(new ServletHolder(clear), "/db/api/clear/");
        context.addServlet(new ServletHolder(status), "/db/api/status/");
        context.addServlet(new ServletHolder(AdminPage), "/admin");


        //USER
            //SERVLETS
        Servlet createUser = new CreateUserServlet(connection);
        Servlet getUserDetails = new GetUserDetailsServlet(connection);
        Servlet followUser = new FollowUserServlet(connection);
        Servlet listFollowers = new UserListFollowersServlet(connection);
        Servlet listFollowing = new UserListFollowingServlet(connection);
        Servlet userListPosts = new UserListPostsServlet(connection);
        Servlet unfollowUser = new UnfollowUserServlet(connection);
        Servlet updateProfile = new UpdateProfileServlet(connection);
            //CONTEXT
        context.addServlet(new ServletHolder(createUser), "/db/api/user/create/");
        context.addServlet(new ServletHolder(getUserDetails), "/db/api/user/details/");
        context.addServlet(new ServletHolder(followUser), "/db/api/user/follow/");
        context.addServlet(new ServletHolder(listFollowers), "/db/api/user/listFollowers/");
        context.addServlet(new ServletHolder(listFollowing), "/db/api/user/listFollowing/");
        context.addServlet(new ServletHolder(userListPosts), "/db/api/user/listPosts/");
        context.addServlet(new ServletHolder(unfollowUser), "/db/api/user/unfollow/");
        context.addServlet(new ServletHolder(updateProfile), "/db/api/user/updateProfile/");


        //FORUM
        Servlet createForum = new CreateForumServlet(connection);
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
        Servlet createThread = new CreateThreadServlet(connection);
        Servlet getThreadDetails = new GetThreadDetailsServlet(connection);
        Servlet closeThread = new CloseThreadServlet(connection);
        Servlet openThread = new OpenThreadServlet(connection);
        Servlet removeThread = new RemoveThreadServlet(connection);
        Servlet restoreThread = new RestoreThreadServlet(connection);
        Servlet updateThread = new UpdateThreadServlet(connection);
        Servlet voteThread = new VoteThreadServlet(connection);
        Servlet subscribeThread = new SubscribeThreadServlet(connection);
        Servlet unsubscribeThread = new UnsubscribeThreadServlet(connection);
        Servlet listThread = new ListThreadsServlet(connection);
        Servlet listPostsThread = new ListPostsThreadServlet(connection);

        //CONTEXT
        context.addServlet(new ServletHolder(createThread), "/db/api/thread/create/");
        context.addServlet(new ServletHolder(getThreadDetails), "/db/api/thread/details/");
        context.addServlet(new ServletHolder(closeThread), "/db/api/thread/close/");
        context.addServlet(new ServletHolder(openThread), "/db/api/thread/open/");
        context.addServlet(new ServletHolder(removeThread), "/db/api/thread/remove/");
        context.addServlet(new ServletHolder(restoreThread), "/db/api/thread/restore/");
        context.addServlet(new ServletHolder(updateThread), "/db/api/thread/update/");
        context.addServlet(new ServletHolder(voteThread), "/db/api/thread/vote/");
        context.addServlet(new ServletHolder(subscribeThread), "/db/api/thread/subscribe/");
        context.addServlet(new ServletHolder(unsubscribeThread), "/db/api/thread/unsubscribe/");
        context.addServlet(new ServletHolder(listThread), "/db/api/thread/list/");
        context.addServlet(new ServletHolder(listPostsThread), "/db/api/thread/listPosts/");

        //POST
        Servlet createPost = new CreatePostServlet(connection);
        Servlet getPostDetails = new GetPostDetailsServlet(connection);
        Servlet listPosts = new ListPostsServlet(connection);
        Servlet removePost = new RemovePostServlet(connection);
        Servlet restorePost = new RestorePostServlet(connection);
        Servlet updatePost = new UpdatePostServlet(connection);
        Servlet votePost = new VotePostServlet(connection);

        //CONTEXT
        context.addServlet(new ServletHolder(createPost), "/db/api/post/create/");
        context.addServlet(new ServletHolder(getPostDetails), "/db/api/post/details/");
        context.addServlet(new ServletHolder(listPosts), "/db/api/post/list/");
        context.addServlet(new ServletHolder(removePost), "/db/api/post/remove/");
        context.addServlet(new ServletHolder(restorePost), "/db/api/post/restore/");
        context.addServlet(new ServletHolder(updatePost), "/db/api/post/update/");
        context.addServlet(new ServletHolder(votePost), "/db/api/post/vote/");

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