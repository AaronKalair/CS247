package cs247.app;


import android.app.ListActivity;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;



public class CS247App extends ListActivity implements OnClickListener {

	private DBAdmin dbadmin = new DBAdmin(this);
	private SQLiteDatabase db;
	private CS247ServerConnection server_connection;
	private Cursor list_cursor = null;
	public static final String PREFS_NAME = "settings";

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        // 10.0.2.2 is an alias to the host running the emulator.
        server_connection = new CS247ServerConnection("10.0.2.2");
        
        // Create an object of type view for the continue button
        Button refreshButton = (Button)findViewById(R.id.refresh);
        // Set up a listener to wait for it to be clicked
        refreshButton.setOnClickListener(this);
        db = dbadmin.getWritableDatabase();
        
        list_cursor = setupListView();
        
        // Register the device for C2DM if nessecary
        
        // Restore preferences
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        boolean registered = settings.getBoolean("registered", false);
        
        if ( !registered ) {
        	registerForC2DM("your google account");
        	// Note that we have registered for C2DM in the preferences 
        	SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean("registered", true);
            // Commit the edits!
            editor.commit();
        }
    }
    
    private Cursor setupListView() {
    	// Columns to display
    	String[] col_display = {"title", "suggestions"};
    	String[] col_get = {"_id", "title", "suggestions", "timestamp"};
    	// Layout items to use to display them
    	int[] to = {R.id.title, R.id.suggestions};
    	
        Cursor list_cursor = db.query("alerts", col_get, null, null, null, null, "timestamp DESC");
        startManagingCursor(list_cursor);
    	
    	SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.item, list_cursor, col_display, to);
        setListAdapter(adapter);
        return list_cursor;
	}
    
    private void updateListView(){
    	list_cursor.requery();
    }

	// Tell the app what to do when view v is clicked
    public void onClick(View v) {
    	// Switch based on the id of the view
    	switch(v.getId()){
			case R.id.refresh:
				refresh();
				break;
    	}
    }
            
    // Get the timestamp of the latest alert
    private String getLatestAlert(){
    	String res = "2000-01-01 00:00:00";
    	// Execute Query
        Cursor cursor = db.rawQuery("SELECT timestamp FROM alerts ORDER BY timestamp DESC LIMIT 1", null);
        cursor.moveToFirst();
        
        try {
        	res = cursor.getString(0);
        } catch(Exception e){
        }
        cursor.close();
        return res;
    }
    
    // Get alerts from the server and insert them into the db then display all the alerts
    public synchronized void refresh(){
    	Log.d(ACTIVITY_SERVICE, "REFRESH!");
    	// Get the timestamp of the newest alert
        String time = getLatestAlert();
        if(!server_connection.isConnected()) server_connection.connect();
    	String[] alerts = server_connection.pollForAlerts(time);
        setNetworkIndicator();
        
        if(alerts != null){
	        for(int i = 0; i < alerts.length; i += 6) {
	        	// Insert this row into the DB
	        	try {
		        	ContentValues values = new ContentValues();
		        	Log.d("debug", alerts[i]);
		            values.put("title", alerts[i] );
		            values.put("link", alerts[i+1] );
		            values.put("description", alerts[i+2] );
		            values.put("suggestions", alerts[i+3] );
		            values.put("reasoning", alerts[i+4]);
		            values.put("timestamp", alerts[i+5] );
		            db.insertOrThrow("alerts", null, values);
	        	} catch (Exception e) {
	        		e.printStackTrace();
	        		finish();
	        	}
	        }
	        updateListView();
        }
    }
    
    // Register the app for C2DM service.
    private void registerForC2DM(String email) {
    	Intent registrationIntent = new Intent("com.google.android.c2dm.intent.REGISTER");
    	registrationIntent.putExtra("app", PendingIntent.getBroadcast(this, 0, new Intent(), 0)); // boilerplate
    	registrationIntent.putExtra("sender", email);
    	startService(registrationIntent);
    	server_connection.registerC2DM(email);
    	Log.e("C2DM", "Registering");
    }
    
    private void unregisterForC2DM(String email){
    	Intent unregIntent = new Intent("com.google.android.c2dm.intent.UNREGISTER");
    	unregIntent.putExtra("app", PendingIntent.getBroadcast(this, 0, new Intent(), 0));
    	unregIntent.putExtra("sender", email);
    	startService(unregIntent);
    	server_connection.unregisterC2DM(email);
    	Log.e("C2DM", "Unregistering");
    }
        
    private void setNetworkIndicator() {
        // Indicator if we are connected to the internet or not
        ConnectivityManager connectivityMgr = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityMgr.getActiveNetworkInfo();
        
        if((info!=null && info.isConnectedOrConnecting() && server_connection.isConnected())){
        	ImageView img = (ImageView)findViewById(R.id.status);
        	img.setImageDrawable(getResources().getDrawable(R.raw.on));
        	TextView text = (TextView)findViewById(R.id.status_text);
        	text.setText("Online");
        } else {
        	ImageView img = (ImageView)findViewById(R.id.status);
        	img.setImageDrawable(getResources().getDrawable(R.raw.off));
        	TextView text = (TextView)findViewById(R.id.status_text);
        	text.setText("Offline");
        }	
    }
    
    @Override
    public void onDestroy(){
    	super.onDestroy();
    	if(server_connection != null && server_connection.isConnected()){
    		server_connection.disconnect();
    	}
    }
    
    @Override
    public void onResume(){
    	super.onResume();
    	if(server_connection != null){
    		refresh();
    	}
    }
    
    @Override
    // When a news item is clicked display a page showing more details
    protected void onListItemClick(ListView l, View v, int position, long id) {
 	    // We want to run the contents of details.class in this case
 		Intent i = new Intent(this, Details.class);
 		// Include the id of the alert so we know which one to display data on
 		i.putExtra("alertid", id);
 		// Start it (be sure to declare it in AndroidManifest.xml)
 		startActivity(i); 
    }
}
