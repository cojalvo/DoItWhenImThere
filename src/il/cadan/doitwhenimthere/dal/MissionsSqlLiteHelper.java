package il.cadan.doitwhenimthere.dal;

import il.cadan.doitwhenimthere.Mission;

import java.util.List;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class MissionsSqlLiteHelper extends SQLiteOpenHelper {

	// DataBase Members
	public static final int DATABASE_VERSION = 1;
	public static final String COLUMN_ID = "_id";
	public static final String DATABASE_NAME = "DoIt.db";
	public static final String TABLE_NAME = "Missions";
	public static final String COLUMN_TITLE = "Title";
	public static final String COLUMN_DESCRIPTION = "Description";
	public static final String COLUMN_DONE = "Done";
	public static final String COLUMN_DATE = "Date";
	public static final String COLUMN_REMINDER_FREQ = "ReminderFrequency";
	public static final String COLUMN_IS_LOCATION = "IsLocation";
	public static final String COLUMN_LAT = "Lat";
	public static final String COLUMN_LNG = "Lng";
	public static final String COLUMN_RADIUS = "Radius";
	public static final String COLUMN_LOCATION_NAME = "LocationName";
	public static final String COLUMN_CATEGORY = "Category";

	public MissionsSqlLiteHelper(Context context, String name,
			CursorFactory factory, int version) {
		super(context, name, factory, version);
	}

	public MissionsSqlLiteHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		createTable(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Drop older table if existed
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
		// Create tables again
		onCreate(db);
	}

	private void createTable(SQLiteDatabase db) {
		if (db == null)
			db = getWritableDatabase();
		String CREATE_MISSION_TABLE = "CREATE TABLE " + TABLE_NAME + "("
				+ COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ COLUMN_TITLE + " TEXT, "+COLUMN_DONE + " INTEGER, " + COLUMN_DESCRIPTION + " TEXT,"
				+ COLUMN_DATE + " INTEGER,"+ COLUMN_REMINDER_FREQ + " TEXT," + COLUMN_IS_LOCATION + " INTEGER,"
				+ COLUMN_LAT + " REAL," + COLUMN_LNG + " REAL,"
				+ COLUMN_LOCATION_NAME + " TEXT," + COLUMN_RADIUS + " INTEGER, "+COLUMN_CATEGORY+" TEXT"
				+ ");";
		db.execSQL(CREATE_MISSION_TABLE);

	}

}
