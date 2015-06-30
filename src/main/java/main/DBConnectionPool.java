package main;

import org.apache.commons.dbcp.ConnectionFactory;
import org.apache.commons.dbcp.DriverManagerConnectionFactory;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.PoolingDataSource;
import org.apache.commons.pool.impl.GenericObjectPool;
import utilities.XPathAdapter;

import javax.sql.DataSource;

/**
 * Created by vitaly on 27.06.15.
 */
public class DBConnectionPool {
    public static final String DRIVER = "com.mysql.jdbc.Driver";
    public static final String URL = "jdbc:mysql://localhost:3306/" + XPathAdapter.getValue("resources/config.xml", "/class/MySQL.DB");
    public static final String USERNAME = XPathAdapter.getValue("resources/config.xml", "/class/MySQL.user");
    public static final String PASSWORD = XPathAdapter.getValue("resources/config.xml", "/class/MySQL.pass");

    private GenericObjectPool connectionPool = null;

    public DataSource setUp() throws Exception {
        //
        // Load JDBC Driver class.
        //
        Class.forName(DBConnectionPool.DRIVER).newInstance();

        //
        // Creates an instance of GenericObjectPool that holds our
        // pool of connections object.
        //
        connectionPool = new GenericObjectPool();
        connectionPool.setMaxActive(new Integer(XPathAdapter.getValue("resources/config.xml", "/class/Max")));

        //
        // Creates a connection factory object which will be use by
        // the pool to create the connection object. We passes the
        // JDBC url info, username and password.
        //
        ConnectionFactory cf = new DriverManagerConnectionFactory(
                DBConnectionPool.URL,
                DBConnectionPool.USERNAME,
                DBConnectionPool.PASSWORD);

        //
        // Creates a PoolableConnectionFactory that will wraps the
        // connection object created by the ConnectionFactory to add
        // object pooling functionality.
        //
        PoolableConnectionFactory pcf =
                new PoolableConnectionFactory(cf, connectionPool,
                        null, null, false, true);
        return new PoolingDataSource(connectionPool);
    }

    public GenericObjectPool getConnectionPool() {
        return connectionPool;
    }

    public void printStatus() {
        /*System.out.println("Max   : " + getConnectionPool().getMaxActive() + "; " +
                "Active: " + getConnectionPool().getNumActive() + "; " +
                "Idle  : " + getConnectionPool().getNumIdle());*/
    }
}
