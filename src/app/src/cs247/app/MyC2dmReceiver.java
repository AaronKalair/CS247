package cs247.app;

import java.io.OutputStream;
import java.net.Socket;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class MyC2dmReceiver extends BroadcastReceiver {

	@Override
	// Listen for any messages coming in from the C2DM Server
	public void onReceive(Context context, Intent intent) {
	    if (intent.getAction().equals( "com.google.android.c2dm.intent.REGISTRATION") ) {
	        handleRegistration(context, intent);
	    }
	    else if (intent.getAction().equals("com.google.android.c2dm.intent.RECEIVE")) {
	       // handleMessage(context, intent);
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
	        
	        // Tell the server we unregistered this device
	        Socket connection = null;
        	// Open a socket to the server
        	try {
        		connection = new Socket("owens server", 9000);
        	}
        	catch (Exception e) {
        		Log.e("Socket", "Unable to open socket");
        	}
        	
        	// Tell the server what our registration id is
        	try {
    			OutputStream send = connection.getOutputStream();
    			String stringToSend = "1";
    			send.write( stringToSend.getBytes() );
    		} 
        	catch (Exception e) {
    			Log.e("Socket", "Unable to open output stream");
    		} 
	    } 
	    
	    // If we registered the device send the ID up to the server
	    else if (registration != null) {
	    	Log.d("c2dm", "registration took place  " + registration);
	    	
	    	Socket connection = null;
        	// Open a socket to the server
        	try {
        		connection = new Socket("owens server", 9000);
        	}
        	catch (Exception e) {
        		Log.e("Socket", "Unable to open socket");
        	}
        	
        	// Tell the server what our registration id is
        	try {
    			OutputStream send = connection.getOutputStream();
    			String stringToSend = "0 " + registration;
    			send.write( stringToSend.getBytes() );
    		} 
        	catch (Exception e) {
    			Log.e("Socket", "Unable to open output stream");
    		} 
	    }
	    
	}
	
	// Deal with a message when it is recieved
	protected void handleMessage(Context context, Intent intent) {
	    String message = intent.getExtras().getString("alertid");
	        
    	// Connect to the server and pull down the alert with this id
	    Socket connection = null;
    	// Open a socket to the server
    	try {
    		connection = new Socket("owens server", 9000);
    	}
    	catch (Exception e) {
    		Log.e("Socket", "Unable to open socket");
    	}
    	
    	// Tell the server what our registration id is
    	try {
			OutputStream send = connection.getOutputStream();
			String stringToSend = "2 " + message;
			send.write( stringToSend.getBytes() );
		} 
    	catch (Exception e) {
			Log.e("Socket", "Unable to open output stream");
		}
	}
}