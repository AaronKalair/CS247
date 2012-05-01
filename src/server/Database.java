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
    
    /* Inserts a IP into the ip_whitelist table
     * @param ip String representation of the IP to insert e.g. 192.156.454.5
     */
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
    
    /* Inserts a IP into the ip_whitelist table
     * @param ip String representation of the IP to check for e.g. 192.156.454.5
     * @return true if the IP is in the table, false otherwise
     */
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
    
    /* Removes a IP from the ip_whitelist table
     * @param ip String representation of the IP to remove e.g. 192.156.454.5
     */
    public void removeIP(String ip) {
    
        PreparedStatement deleteStatement = null;        
        String IpToDelete = "DELETE FROM ip_whitelist WHERE ip_address = ?";
        
        try {
        
            deleteStatement = conn.prepareStatement(IpToDelete);
            deleteStatement.setString(1, ip);
            deleteStatement.executeUpdate();
        }
        
        catch (Exception e) {
           e.printStackTrace();
        }
        
    }
    
    /* Inserts an alert into the android_alerts table
     * @param title The title of the alert
     * @param link The link to the source of the alert
     * @param importance A integer representing how important this alert is, pass in -1 if you do not want to use this parameter
     */
    public void insertAlert(String title, String link, int importance) {
        
        PreparedStatement addAlert = null;
        
        String insertAlert = "INSERT INTO android_alerts (title, link, importance) values(?, ?, ?)";
        
        try {
            addAlert = conn.prepareStatement(insertAlert);
            addAlert.setString(1, title);
            addAlert.setString(2, link);
            addAlert.setInt(3, importance);
            addAlert.executeUpdate();
        }
        
        catch (Exception e) {
           e.printStackTrace();
        }
    
    }
    
    /* Inserts an alert into the android_alerts table
     * @param title The title of the alert
     * @param link The link to the source of the alert
     * @param description The main body of text about this alert.
     * @param suggestions What we think they should do based on this alert
     * @param importance A integer representing how important this alert is, pass in -1 if you do not want to use this parameter
     * @return the id of the alert that was inserted.
     */
    public int insertAlert(String title, String link, String description, String suggestions, int importance) {
        
        PreparedStatement addAlert = null;
        int alert_id = -5;
        
        String insertAlert = "INSERT INTO android_alerts (title, link, description, suggestions, importance) values(?, ?, ?, ? , ?)";
        
        try {
            // Get it to return the value of the alert_id column
            addAlert = conn.prepareStatement(insertAlert, new String[]{"alert_id"});
            addAlert.setString(1, title);
            addAlert.setString(2, link);
            addAlert.setString(3, description);
            addAlert.setString(4, suggestions);
            addAlert.setInt(5, importance);
            addAlert.executeUpdate();
            // Get the returned value
            ResultSet rs = addAlert.getGeneratedKeys();
            // Move the pointer to the start of the results
            rs.next();
            // Get the alert_id value
            alert_id = rs.getInt(1);
            
        }
        
        catch (Exception e) {
           e.printStackTrace();
        }
        
        return alert_id;

    }
    
    /* Gets an alert from the android_alerts table by its ID as an array
     * @param id The id of the alert you want to get
     * @return array representing part of the row returned 0 = Title, 1 = Link, 2 = Description, 3 = Suggestions
     */
   public String[] getAlertByIDAsArray(int alert_id) {
        
        PreparedStatement getAlert = null;
        ResultSet rs = null;
        String[] Results = new String[4];
        
        String alertToGet = "SELECT * FROM android_alerts WHERE alert_id = ? ";
        
        try {
            getAlert = conn.prepareStatement(alertToGet);
            getAlert.setInt(1, alert_id);
            rs = getAlert.executeQuery();
            // Move the pointer to the start of the results
            rs.next();
            Results[0] = rs.getString(2);
            Results[1] = rs.getString(3);
            Results[2] = rs.getString(4);
            Results[3] = rs.getString(5);
        }
        
        catch (Exception e) {
           e.printStackTrace();
        }
        
        return Results;        

    } 
    
    /* Gets an alert from the android_alerts table by its ID as a result set object
     * @param id The id of the alert you want to get
     * @return ResultSet representing all the rows returned 1 = Alert ID, 2 = Title, 3 = Link, 4 = Description, 5 = Suggestions
     */
    public ResultSet getAlertByIDAsResultSetObject(int alert_id) {
        
        PreparedStatement getAlert = null;
        ResultSet rs = null;
        
        String alertToGet = "SELECT * FROM android_alerts WHERE alert_id = ? ";
        
        try {
            getAlert = conn.prepareStatement(alertToGet);
            getAlert.setInt(1, alert_id);
            rs = getAlert.executeQuery();
        }
        
        catch (Exception e) {
           e.printStackTrace();
        }
        
        return rs;    

    }
    
    /* Gets all alerts from the android_alerts table that were created after the timestamp passed in
     * @param timestamp Timestamp in the format YYYY-MM-DD HH:MM:SS  
     * @return ResultSet representing the rows returned 1 = Alert ID, 2 = Title, 3 = Link, 4 = Description, 5 = Suggestions
     */
    public ResultSet getAllAlertsSinceXAsResultSetObject(String timestamp) {
        
        PreparedStatement getAlerts = null;
        ResultSet rs = null;
        
        String alertsToGet = "SELECT * FROM android_alerts WHERE time_stamp >= ? ";
        
        try {
            getAlerts = conn.prepareStatement(alertsToGet);
            getAlerts.setString(1, timestamp);
            rs = getAlerts.executeQuery();
        }
        
        catch (Exception e) {
           e.printStackTrace();
        }
        
        return rs;    

    }
    

}
