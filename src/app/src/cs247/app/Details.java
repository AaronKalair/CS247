package cs247.app;


import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.widget.TextView;


public class Details extends Activity{
	
	private DBAdmin dbadmin = new DBAdmin(this);
	private SQLiteDatabase db;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		db = dbadmin.getWritableDatabase();
		TextView tv = new TextView(this);
		tv.setBackgroundColor(0xFFEEEEEE);
		tv.setTextColor(0xFF111111);
		tv.setPadding(5, 5, 5, 5);
		tv.setMovementMethod(LinkMovementMethod.getInstance());
		tv.setAutoLinkMask(Linkify.WEB_URLS);

		// Get the ID of the alert we want more info on or -1 if it failed to pass a value in
		long item = getIntent().getLongExtra("alertid", -1);
				
		if( item != -1 ) {
			Cursor cursor = getDetails(item);
			cursor.moveToFirst();
			
			
		    tv.setText(
		    "Title:\n" + cursor.getString(1) + 
		    "\n\n" + 
		    "Source:\n" + cursor.getString(2) +
		    "\n\n" +
		    "More Info:\n" + cursor.getString(3) +
		    "\n\n" +
		    "Suggestion:\n" + cursor.getString(4) +
		    "\n\n" +
		    "Reasoning:\n" + cursor.getString(5));
		    cursor.close();
		} else {
			tv.setText("Sorry news item not found");
		}
		
		setContentView(tv);
	}
	
	private Cursor getDetails(long item) {
		
		// Columns to get
    	String[] col = {"_id", "title", "link", "description", "suggestions", "reasoning", "importance", "timestamp"};
    	
    	// Select the row where _ID = the id of the alert we want 
		Cursor x = db.query("alerts", col, "_ID = ?", new String[] {String.valueOf(item)}, null, null, null);
	    //startManagingCursor(x);
	    return x;
	}
	
	
	

}

