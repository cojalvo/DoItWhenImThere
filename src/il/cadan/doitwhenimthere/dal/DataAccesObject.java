package il.cadan.doitwhenimthere.dal;

import il.cadan.doitwhenimthere.Category;
import il.cadan.doitwhenimthere.LocationMission;
import il.cadan.doitwhenimthere.Mission;
import il.cadan.doitwhenimthere.ReminderFrequency;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.android.gms.maps.model.LatLng;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class DataAccesObject implements IDataAccesObject {

	// Database fields
	private SQLiteDatabase database;
	private MissionsSqlLiteHelper dbHelper;
	private String[] allColumns = { 
			MissionsSqlLiteHelper.COLUMN_ID,
			MissionsSqlLiteHelper.COLUMN_TITLE,
			MissionsSqlLiteHelper.COLUMN_DESCRIPTION,
			MissionsSqlLiteHelper.COLUMN_DONE,
			MissionsSqlLiteHelper.COLUMN_DATE,
			MissionsSqlLiteHelper.COLUMN_REMINDER_FREQ,
			MissionsSqlLiteHelper.COLUMN_IS_LOCATION,
			MissionsSqlLiteHelper.COLUMN_LAT,
			MissionsSqlLiteHelper.COLUMN_LNG,
			MissionsSqlLiteHelper.COLUMN_RADIUS,
			MissionsSqlLiteHelper.COLUMN_LOCATION_NAME,
			MissionsSqlLiteHelper.COLUMN_CATEGORY
			};
	
	private static IDataAccesObject instance=null;

	private DataAccesObject(Context context) {
		dbHelper = new MissionsSqlLiteHelper(context);
	}

	public static IDataAccesObject getInstance(Context context)
	{
		if(instance==null)
			instance=new DataAccesObject(context);
		return instance;
	}
	@Override
	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	@Override
	public void close() {
		dbHelper.close();
	}

	@Override
	public Mission addNewMission(Mission mission) {
		open();
		ContentValues values = new ContentValues();
		values.put(MissionsSqlLiteHelper.COLUMN_TITLE, mission.getTitle());
		values.put(MissionsSqlLiteHelper.COLUMN_DESCRIPTION,
				mission.getDscription());
		values.put(MissionsSqlLiteHelper.COLUMN_DONE,
				0);
		values.put(MissionsSqlLiteHelper.COLUMN_CATEGORY, mission.getCategory().toString());
		if (mission.getStartTime() != null)
		{
			values.put(MissionsSqlLiteHelper.COLUMN_DATE, mission
					.getStartTime().getTime());
		values.put(MissionsSqlLiteHelper.COLUMN_REMINDER_FREQ, mission
				.getReminderFrequency().toString());
		}
		else
			values.put(MissionsSqlLiteHelper.COLUMN_DATE, "");
		if (mission instanceof LocationMission) {
			LocationMission lm = (LocationMission) mission;
			values.put(MissionsSqlLiteHelper.COLUMN_IS_LOCATION, 1);
			values.put(MissionsSqlLiteHelper.COLUMN_LAT,
					lm.getLocation().latitude);
			values.put(MissionsSqlLiteHelper.COLUMN_LNG,
					lm.getLocation().longitude);
			values.put(MissionsSqlLiteHelper.COLUMN_RADIUS,
					lm.getRadiusNotification());
			values.put(MissionsSqlLiteHelper.COLUMN_LOCATION_NAME,
					lm.getLocationName());
		} else {
			values.put(MissionsSqlLiteHelper.COLUMN_IS_LOCATION, 0);
		}
		long insertId = database.insert(MissionsSqlLiteHelper.TABLE_NAME, null,
				values);
		mission.setId(insertId);
		close();
		return mission;
	}

	@Override
	public List<Mission> getAllMission() {
		
		open();
		List<Mission> missions = new ArrayList<Mission>();

		Cursor cursor = database.query(MissionsSqlLiteHelper.TABLE_NAME,
				allColumns, null, null, null, null, null);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Mission mission = cursorToMission(cursor);
			missions.add(mission);
			cursor.moveToNext();
		}
		// make sure to close the cursor
		cursor.close();
		close();
		return missions;
	}

	@Override
	public void updateMission(Mission toUpdate) {
		int done=0;
		if(toUpdate.getDone())
			done=1;
		open();
		ContentValues values = new ContentValues();
		values.put(MissionsSqlLiteHelper.COLUMN_TITLE, toUpdate.getTitle());
		values.put(MissionsSqlLiteHelper.COLUMN_DESCRIPTION,
				toUpdate.getDscription());
		values.put(MissionsSqlLiteHelper.COLUMN_DONE,
				done);
		values.put(MissionsSqlLiteHelper.COLUMN_CATEGORY, toUpdate.getCategory().toString());
		if (toUpdate.getStartTime() != null)
		{
			values.put(MissionsSqlLiteHelper.COLUMN_DATE, toUpdate
					.getStartTime().getTime());
		values.put(MissionsSqlLiteHelper.COLUMN_REMINDER_FREQ, toUpdate
				.getReminderFrequency().toString());
		}
		else
			values.put(MissionsSqlLiteHelper.COLUMN_DATE, "");
		if (toUpdate instanceof LocationMission) {
			LocationMission lm = (LocationMission) toUpdate;
			values.put(MissionsSqlLiteHelper.COLUMN_IS_LOCATION, 1);
			values.put(MissionsSqlLiteHelper.COLUMN_LAT,
					lm.getLocation().latitude);
			values.put(MissionsSqlLiteHelper.COLUMN_LNG,
					lm.getLocation().longitude);
			values.put(MissionsSqlLiteHelper.COLUMN_RADIUS,
					lm.getRadiusNotification());
			values.put(MissionsSqlLiteHelper.COLUMN_LOCATION_NAME,
					lm.getLocationName());
		} else {
			values.put(MissionsSqlLiteHelper.COLUMN_IS_LOCATION, 0);
		}
		String id = null;
		id+=toUpdate.getId();
		String[] args=new String[]{id};
		database.update(MissionsSqlLiteHelper.TABLE_NAME, values, "_id="+toUpdate.getId(),null);
		close();

	}

	@Override
	public void deleteMission(long id) {
		System.out.println("Comment deleted with id: " + id);
		database.delete(MissionsSqlLiteHelper.TABLE_NAME, MissionsSqlLiteHelper.COLUMN_ID
				+ " = " + id, null);
	}

	@Override
	public void getMission(String id) {
		// TODO Auto-generated method stub

	}

	

	private Mission cursorToMission(Cursor cursor) {
		Mission mission = null;
		long id;
		String title = null;
		String description = null;
		String reminderFreq=null;
		int isLocation;
		double lat, lng;
		String locationName = null;
		int radius = 0;
		long date;
		int done=0;
		String stCategory=null;
		id = cursor.getLong(0);
		title = cursor.getString(1);
		description = cursor.getString(2);
		done=cursor.getInt(3);
		date = cursor.getLong(4);
		reminderFreq=cursor.getString(5);
		isLocation = cursor.getInt(6);
		stCategory=cursor.getString(11);

		if (isLocation == 1) {
			lat = cursor.getDouble(7);
			lng = cursor.getDouble(8);
			radius=cursor.getInt(9);
			locationName = cursor.getString(10);
			mission=new LocationMission();
			LocationMission lm=(LocationMission) mission;
			lm.setLocation(new LatLng(lat, lng));
			lm.setLocationName(locationName);
			lm.setRadiusNotification(radius);
			
		}
		else
		{
			mission=new Mission();
		}
		mission.setTitle(title);
		mission.setDscription(description);
		mission.setId(id);
		mission.setCategory(Category.valueOf(stCategory));
		if(done==0)
			mission.setDone(false);
		else
			mission.setDone(true);
		if(date!=0)
		{
			mission.setStartTime(new Date(date));
			mission.setReminderFrequency(ReminderFrequency.valueOf(reminderFreq));
		}
		return mission;
	}

}
