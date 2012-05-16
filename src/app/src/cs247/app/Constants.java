package cs247.app;

import android.provider.BaseColumns;

public interface Constants extends BaseColumns {
	
	public static final String TABLE_NAME = "alerts";
	
	public static final String ID = "id";
	public static final String TITLE = "title";
	public static final String LINK = "link";
	public static final String DESCRIPTION = "description";
	public static final String SUGGESTIONS = "suggestions";
	public static final String IMPORTANCE = "importance";
	public static final String TIME = "time";
	

}

