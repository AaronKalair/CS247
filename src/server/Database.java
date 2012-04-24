package CS247;

import java.sql.*;

public class Database {
    
    // Connect to the database TODO: connection pooling
    public Database() {
    
        Connection conn = null;
        // Default port is 3306
        String url = "jdbc:mysql://localhost:3306/";
        // Make sure you have the CS247 database created or this wont work
        String dbName = "CS247";
        // Note: you'll need to download this from: http://dev.mysql.com/downloads/mirror.php?id=407252#mirrors and make sure its in your classpath
        String driver = "com.mysql.jdbc.Driver";
        // Our database credentials
        String userName = "root"; 
        // A super secure password
        String password = "cs247";
        
        // Actually connect to the database
        try {
            Class.forName(driver).newInstance();
            conn = DriverManager.getConnection(url+dbName,userName,password);
        } 
          
        catch (Exception e) {
            e.printStackTrace();
        }
        
        closeConnection(conn);
        
        
    }
    
    // Not sure we need this, as we want to reuse connections ?
    public void closeConnection(Connection x) {
        try {
            x.close();
        }
        
        catch (Exception e) {
            e.printStackTrace();
        }
    }

}
