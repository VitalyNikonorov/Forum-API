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
import javax.sql.DataSource;
import java.sql.*;

/**
 * Created by Виталий on 15.03.2015.
 */
public class Main {
    public static DBConnectionPool connectionPool;
    public static DataSource dataSource;

    public static void main(String[] args) throws Exception {

        Server server = new Server(80);
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        connectionPool = new DBConnectionPool();
        dataSource = connectionPool.setUp();
        connectionPool.printStatus();

        //Database!!!!

        Servlet clear = new ClearServlet();
        Servlet status = new Status();
        Servlet AdminPage = new AdminPageServlet();

        //System.out.append("Starting at port: ").append(String.valueOf(8080)).append('\n');
        context.addServlet(new ServletHolder(clear), "/db/api/clear/");
        context.addServlet(new ServletHolder(status), "/db/api/status/");
        context.addServlet(new ServletHolder(AdminPage), "/admin");


        //USER
            //SERVLETS
        Servlet createUser = new CreateUserServlet();
        Servlet getUserDetails = new GetUserDetailsServlet();
        Servlet followUser = new FollowUserServlet();
        Servlet listFollowers = new UserListFollowersServlet();
        Servlet listFollowing = new UserListFollowingServlet();
        Servlet userListPosts = new UserListPostsServlet();
        Servlet unfollowUser = new UnfollowUserServlet();
        Servlet updateProfile = new UpdateProfileServlet();
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
        Servlet createForum = new CreateForumServlet();
        Servlet getForumDetails = new GetForumDetailsServlet();
        Servlet forumListPostServlet = new ForumListPostsServlet();
        Servlet forumListThreadsServlet = new ForumListThreadsServlet();
        Servlet forumListUsersServlet = new ForumListUsersServlet();

        //CONTEXT
        context.addServlet(new ServletHolder(createForum), "/db/api/forum/create/");
        context.addServlet(new ServletHolder(getForumDetails), "/db/api/forum/details/");
        context.addServlet(new ServletHolder(forumListPostServlet), "/db/api/forum/listPosts/");
        context.addServlet(new ServletHolder(forumListThreadsServlet), "/db/api/forum/listThreads/");
        context.addServlet(new ServletHolder(forumListUsersServlet), "/db/api/forum/listUsers/");

        //THREAD
        Servlet createThread = new CreateThreadServlet();
        Servlet getThreadDetails = new GetThreadDetailsServlet();
        Servlet closeThread = new CloseThreadServlet();
        Servlet openThread = new OpenThreadServlet();
        Servlet removeThread = new RemoveThreadServlet();
        Servlet restoreThread = new RestoreThreadServlet();
        Servlet updateThread = new UpdateThreadServlet();
        Servlet voteThread = new VoteThreadServlet();
        Servlet subscribeThread = new SubscribeThreadServlet();
        Servlet unsubscribeThread = new UnsubscribeThreadServlet();
        Servlet listThread = new ListThreadsServlet();
        Servlet listPostsThread = new ListPostsThreadServlet();

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
        Servlet createPost = new CreatePostServlet();
        Servlet getPostDetails = new GetPostDetailsServlet();
        Servlet listPosts = new ListPostsServlet();
        Servlet removePost = new RemovePostServlet();
        Servlet restorePost = new RestorePostServlet();
        Servlet updatePost = new UpdatePostServlet();
        Servlet votePost = new VotePostServlet();

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