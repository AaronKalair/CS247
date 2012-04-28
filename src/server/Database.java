package CS247;

import java.sql.*;

public class Database {

    private Connection conn;
    
    // Connect to the database TODO: connection pooling
    Database() {
    
        this.conn = null;
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
    
    // Inserts a IP into the ip_whitelist table
    public void insertIP(String ip) {
            
        PreparedStatement addIP = null;
        
        String insertIP = "INSERT INTO ip_whitelist (ip_address) values(?)";
        
        try {
            addIP = conn.prepareStatement(insertIP);
            addIP.setString(1, ip);
            addIP.executeUpdate();
        }
        
        catch (Exception e) {
           e.printStackTrace();
        }
    
    }
    
    // Checks if a given IP is in the ip_whitelist table
    public boolean validIP(String ip) {
    
        PreparedStatement checkIP = null;
        ResultSet results = null;
        int numberOfResults = -100;
        
        String IpToCheck = "SELECT ip_address FROM ip_whitelist WHERE ip_address = ?";
        
        try {
        
            checkIP = conn.prepareStatement(IpToCheck);
            checkIP.setString(1, ip);
            results = checkIP.executeQuery();
            
            // JavaDB has no way to find out the number of rows returned without itterating through them (yes really)
            while ( results.next() ) {
                String id = results.getString(1);
            }
            
            results.last();
            numberOfResults = results.getRow();
        }
        
        catch (Exception e) {
           e.printStackTrace();
        }

        if( numberOfResults == 1) {
            return true;
        }
        
        else {
            return false;
        }     
 
    }

}
