package main;

import utilities.XPathAdapter;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by vitaly on 25.06.15.
 */
public class DBConnect {
    private static Connection connection = null;

    public DBConnect() {
        try {
            DriverManager.registerDriver(new com.mysql.jdbc.Driver());
            connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/" + XPathAdapter.getValue("resources/config.xml", "/class/MySQL.DB"),
                    XPathAdapter.getValue("resources/config.xml", "/class/MySQL.user"),
                    XPathAdapter.getValue("resources/config.xml", "/class/MySQL.pass"));
        } catch (SQLException ex) {
            System.out.println("SQLException caught");
            System.out.println("---");
            while (ex != null) {
                System.out.println("Message   : " + ex.getMessage());
                System.out.println("SQLState  : " + ex.getSQLState());
                System.out.println("ErrorCode : " + ex.getErrorCode());
                System.out.println("---");
                ex = ex.getNextException();
            }
        } catch (Exception ex) {
            System.out.println("Other Error in Main.");
        }
    }

    public Connection getConnection(){
        return connection;
    }
}
