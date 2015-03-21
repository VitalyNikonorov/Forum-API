package main;

import admin.AdminPageServlet;
import db.ClearServlet;
import db.forum.CreateForumServlet;
import db.user.CreateUserServlet;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import javax.servlet.Servlet;

/**
 * Created by Виталий on 15.03.2015.
 */
public class Main {
    public static void main(String[] args) throws Exception {

        Server server = new Server(8081);
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);


        Servlet clear = new ClearServlet();
        Servlet AdminPage = new AdminPageServlet();

        System.out.append("Starting at port: ").append(String.valueOf(8081)).append('\n');
        context.addServlet(new ServletHolder(clear), "/db/api/clear");
        context.addServlet(new ServletHolder(AdminPage), "/admin");

        //FORUM
            //SERVLETS
        Servlet createForum = (Servlet) new CreateForumServlet();
            //context
        context.addServlet(new ServletHolder(createForum), "/db/api/forum/create");


        //USER
            //SERVLETS
        Servlet createUser = (Servlet) new CreateUserServlet();
            //CONTEXT
        context.addServlet(new ServletHolder(createUser), "/db/api/user/create");

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
