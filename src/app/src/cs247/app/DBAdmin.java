package cs247.app;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBAdmin extends SQLiteOpenHelper {
	
	private static final String DATABASE_NAME = "alerts.db";
	private static final int DATABASE_VERSION = 4;
	
	// Create a helper object for the Events database
	public DBAdmin(Context ctx) {
		super(ctx, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE ALERTS ( _id INTEGER PRIMARY KEY AUTOINCREMENT, title VARCHAR(500), link VARCHAR(500), " 
					+ "description TEXT(10000), suggestions TEXT(10000), reasoning TEXT(10000), importance TINYINT, timestamp TIMESTAMP); ");
	}
	
	
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
		db.execSQL("DROP TABLE IF EXISTS ALERTS");
		onCreate(db);
	}
}
