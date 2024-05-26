package listeners;

import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/*
 * Web application lifecycle listener that handles cleanup tasks when the web application is stopped.
 */
@WebListener
public class AppContextListener implements ServletContextListener {

    /*
     * Called when the servlet context is initialised.
     * Not used in this context, but serves as a placeholder for future initialisation tasks.
     */
    @Override
    public void contextInitialized(ServletContextEvent sce) {
    	// No specific initialisation tasks in current setup.
    }

    /*
     * Handles clean-up tasks when the web application context is destroyed.
     * This includes deregistering JDBC drivers and stopping abandoned connection cleanup threads.
     */
    @Override
    public void contextDestroyed(ServletContextEvent sce) {

    	// Deregister all JDBC drivers registered by this web application.
        Enumeration<Driver> drivers = DriverManager.getDrivers();
        while (drivers.hasMoreElements()) {
            Driver driver = drivers.nextElement();
            try {
                DriverManager.deregisterDriver(driver);
                System.out.println("Deregistering JDBC driver: " + driver);
            } catch (SQLException e) {
                System.out.println("Error deregistering driver: " + e.getMessage());
            }
        }
        
     // Explicitly shut down the MySQL abandoned connection cleanup thread to prevent memory leaks.
     com.mysql.cj.jdbc.AbandonedConnectionCleanupThread.checkedShutdown();
		System.out.println("AbandonedConnectionCleanupThread has been shutdown");
    }
}