package cs247.app;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class MyC2dmReceiver extends BroadcastReceiver {
	
	// 10.0.2.2 is an alias to the host running the emulator.
	private CS247ServerConnection server_connection = new CS247ServerConnection("10.0.2.2");
	
	public MyC2dmReceiver(){
		super();
		if(server_connection.isConnected()) server_connection.disconnect();
	}
    
	@Override
	// Listen for any messages coming in from the C2DM Server
	public void onReceive(Context context, Intent intent) {
	    if (intent.getAction().equals( "com.google.android.c2dm.intent.REGISTRATION") ) {
	        handleRegistration(context, intent);
	    }
	    else if (intent.getAction().equals("com.google.android.c2dm.intent.RECEIVE")) {
	       handleMessage(context, intent);
	    }
	}
	
	// Deals with registering a device with the C2DM service
	private void handleRegistration(Context context, Intent intent) {
	
	    String registration = intent.getStringExtra("registration_id");
	    
	    // If we got an error back
	    if (intent.getStringExtra("error") != null) {
	        // Registration failed, should try again later.
	        Log.d("c2dm", "registration failed");
	        String error = intent.getStringExtra("error");
	        if (error == "SERVICE_NOT_AVAILABLE") {
	            Log.d("c2dm", "SERVICE_NOT_AVAILABLE");
	        } else if (error == "ACCOUNT_MISSING") {
	            Log.d("c2dm", "ACCOUNT_MISSING");
	        } else if (error == "AUTHENTICATION_FAILED") {
	            Log.d("c2dm", "AUTHENTICATION_FAILED");
	        } else if (error == "TOO_MANY_REGISTRATIONS") {
	            Log.d("c2dm", "TOO_MANY_REGISTRATIONS");
	        } else if (error == "INVALID_SENDER") {
	            Log.d("c2dm", "INVALID_SENDER");
	        } else if (error == "PHONE_REGISTRATION_ERROR") {
	            Log.d("c2dm", "PHONE_REGISTRATION_ERROR");
	        }
	    } 
	    
	    // If we unregistered
	    else if (intent.getStringExtra("unregistered") != null) {
	        Log.d("c2dm", "unregistered");
	    } 
	    
	    // If we registered the device send the ID up to the server
	    else if (registration != null) {
	    	Log.d("c2dm", "registration took place id:  " + registration);
	    	// Send our ID up to the server
	    	if(!server_connection.isConnected()) server_connection.connect();
	        server_connection.registerC2DM(registration);
	        server_connection.disconnect();
	    }
	    
	}
	
	// Deal with a message when it is received
	protected void handleMessage(Context context, Intent intent) {
		
		// DB things
	    DBAdmin dbadmin = new DBAdmin(context);
		SQLiteDatabase db = dbadmin.getWritableDatabase();
		
		// Get the id of the alert from the message
	    String message = intent.getExtras().getString("id");
	    // Connect to the server and pull down the alert
	    if(!server_connection.isConnected()) server_connection.connect();
	    String[] alert = server_connection.getAlertFromServer(Integer.parseInt(message));
	    
	    try {
        	ContentValues values = new ContentValues();
            values.put("title", alert[0] );
            values.put("link", alert[1] );
            values.put("description", alert[2] );
            values.put("suggestions", alert[3] );
            values.put("reasoning", alert[4]);
            values.put("timestamp", alert[5] );
            db.insertOrThrow("alerts", null, values);
    	} catch (Exception e) {
    		Log.e("DB", "Insert failed from push alert");
    	}
	    server_connection.disconnect();
	}
}