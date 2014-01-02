package il.cadan.doitwhenimthere.bl;

import il.cadan.doitwhenimthere.Mission;
import il.cadan.doitwhenimthere.ReminderFrequency;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;

import com.google.android.gms.common.data.Freezable;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.format.DateUtils;
import android.util.Log;

/**
 * the Notification Manager Class is the class that manages the adding, removing
 * and updating the notifications for the user the class holds a HashMap of the
 * current Notification applied for the current user, every time the
 * updateNotification method is called the class then deside which Notification
 * to update remove or add.
 * 
 * @author Jacob
 * 
 */
public class NotificationManager {
	HashMap<Long, Mission> eventsList = new HashMap<Long, Mission>();
	String currUserParseId;
	AlarmManager alarmManager;
	Context appContext;

	public NotificationManager(Context appContext) {
		this.appContext = appContext;
		alarmManager = (AlarmManager) appContext
				.getSystemService(Context.ALARM_SERVICE);
	}

	/**
	 * this method is being used for new events created on this device
	 * 
	 * @param event
	 */
	public void updateNotification(Mission mission) {
		if (mission != null) {
			Long key = mission.getId();
			this.cancelNotification(mission);
			this.builSndSendNotificationToEvent(mission);
			// add e
			eventsList.put(key, mission);
		}
	}

	private PendingIntent builSndSendNotificationToEvent(Mission mission) {
		long interval = 0;
		if (mission.getStartTime() == null || mission.getStartTime().before(new Date()))
			return null;
		ReminderFrequency rf = mission.getReminderFrequency();
		switch (rf) {
		case Off:
			return null;
		case Once:
			break;

		case OnceDay:
			interval = AlarmManager.INTERVAL_DAY;
			break;
		case OnceMonth:
			interval = AlarmManager.INTERVAL_DAY * 30;
			break;

		case OnceWeek:
			interval = DateUtils.WEEK_IN_MILLIS;
			break;
		case OnceYear:
			interval = DateUtils.YEAR_IN_MILLIS;
			break;

		default:
			return null;
		}
		Intent activityIntent = new Intent(appContext,
				ReminderBroadCastReceiver.class);
		activityIntent
				.setAction("il.ac.asenkar.brodcast_receiver_costum_reciver");
		activityIntent.putExtra("mission_id", mission.getId());
		PendingIntent pendingInent = PendingIntent.getBroadcast(appContext,
				(int) mission.getId(), activityIntent, 0);
		alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, mission
				.getStartTime().getTime(), interval, pendingInent);// (AlarmManager.RTC_WAKEUP,
																	// mission.getStartTime(),
																	// pendingInent);
		return pendingInent;
	}

	public void cancelNotification(Mission mission) {
		if (mission == null)
			return;
		cancelNotification(mission.getId());
	}

	public void cancelNotification(long id) {

		Intent stopIntent = new Intent(appContext,
				ReminderBroadCastReceiver.class);
		stopIntent.setAction("il.ac.asenkar.brodcast_receiver_costum_reciver");
		PendingIntent stopFriday = PendingIntent.getService(appContext,
				(int) id, stopIntent, 0);
		AlarmManager stopManager = (AlarmManager) appContext
				.getSystemService(appContext.ALARM_SERVICE);
		stopManager.cancel(stopFriday);
	}
	
	public void snooze(Mission m,long delay)
	{
		Intent activityIntent = new Intent(appContext,
				ReminderBroadCastReceiver.class);
		activityIntent
				.setAction("il.ac.asenkar.brodcast_receiver_costum_reciver");
		activityIntent.putExtra("mission_id", m.getId());
		PendingIntent pendingInent = PendingIntent.getBroadcast(appContext,
				(int) m.getId(), activityIntent, 0);
		alarmManager.set(AlarmManager.RTC_WAKEUP,new Date().getTime()+delay, pendingInent);// (AlarmManager.RTC_WAKEUP,
																	// mission.getStartTime(),
																	// pendingInent);
	}

}