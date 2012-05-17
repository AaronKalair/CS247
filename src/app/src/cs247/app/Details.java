package cs247.app;


import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import org.w3c.dom.Text;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;


public class Details extends Activity{
	
	private DBAdmin dbadmin = new DBAdmin(this);
	private SQLiteDatabase db;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		db = dbadmin.getWritableDatabase();
		TextView tv = new TextView(this);
		// Get the ID of the alert we want more info on or -1 if it failed to pass a value in
		long item = getIntent().getLongExtra("alertid", -1);
				
		if( item != -1 ) {
			Cursor cursor = getDetails(item);
			cursor.moveToFirst();
			
			
		    tv.setText(cursor.getString(1) + 
		    "\n\n" + 
		    "Source: " + cursor.getString(2) +
		    "\n\n" +
		    "More Info: " + cursor.getString(3) +
		    "\n\n" +
		    "We think you should: " + cursor.getString(4));
		}
		
		else{
			tv.setText("Sorry news item not found");
		}
		
		setContentView(tv);
	}
	
	private Cursor getDetails(long item) {
		
		// Columns to get
    	String[] col = {"_id", "title", "link", "description", "suggestions", "importance", "timestamp"};
    	
    	// Select the row where _ID = the id of the alert we want 
		Cursor x = db.query("alerts", col, "_ID = ?", new String[] {String.valueOf(item)}, null, null, null);
	    startManagingCursor(x);
	    return x;
	}
	
	
	

}

