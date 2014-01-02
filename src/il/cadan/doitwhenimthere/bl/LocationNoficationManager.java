package il.cadan.doitwhenimthere.bl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.google.android.gms.maps.model.LatLng;

import il.cadan.doitwhenimthere.DoITConstance;
import il.cadan.doitwhenimthere.LocationMission;
import il.cadan.doitwhenimthere.Mission;
import il.cadan.doitwhenimthere.ReminderFrequency;
import il.cadan.doitwhenimthere.dal.DataAccesObject;
import il.cadan.doitwhenimthere.dal.IDataAccesObject;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.text.format.DateUtils;

public class LocationNoficationManager {
	IDataAccesObject dao;
	private Context context;
	private HashMap<Long, Mission> notifiedMission=new HashMap<Long, Mission>();
	List<LocationMission> missionList;
	private BroadcastReceiver viewModelUpdatedReciever;
	private AlarmManager alarmManager;

	public void notify(Location loc) 
	{
		if(loc==null) return;
		for (LocationMission lMission : missionList) 
		{
			if(shouldNotify(lMission, loc))
			{
				notifiedMission.put(lMission.getId(), lMission);
				notify(lMission);
			}
			else if(shouldRemoveFromNotifiedList(lMission, loc))
				notifiedMission.remove(lMission);
		}
	}

	private void notify(LocationMission m)
	{
		builSndSendNotificationToEvent(m);
	}
	LocationNoficationManager(Context context) {
		this.context = context;
		dao = DataAccesObject.getInstance(context);
		updateLocationMissionList();
		registerViewModelReciever();
		alarmManager = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
	}

	private void updateLocationMissionList() {
		missionList = new ArrayList<LocationMission>();
		List<Mission> lm = dao.getAllMission();
		for (Mission mission : lm) {
			if (mission instanceof LocationMission)
				missionList.add((LocationMission) mission);
		}
	}

	private void registerViewModelReciever() {
		IntentFilter filterSend = new IntentFilter();
		filterSend.addAction(DoITConstance.VIEW_MODEL_UPDATED);
		filterSend.addAction(DoITConstance.MAP_MISSIONL_UPDATED);
		viewModelUpdatedReciever = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				if (intent.getAction().equals(DoITConstance.VIEW_MODEL_UPDATED)
						|| intent.getAction().equals(
								DoITConstance.MAP_MISSIONL_UPDATED)) {
					updateLocationMissionList();
				}
			}
		};
		context.registerReceiver(viewModelUpdatedReciever, filterSend);
	}
	
	private Boolean shouldNotify(LocationMission m,Location myLocation)
	{
		//never notify
		if(m.getRadiusNotification()<0) return false;
		//was notified
		if(notifiedMission.containsKey(m.getId())) return false;
		//mission done, dont notify
		if(m.getDone())  return false;
		//check the distance
		return distanceFromPoints(m.getLocation(), myLocation) < m.getRadiusNotification();
	}
	
	private Boolean shouldRemoveFromNotifiedList(LocationMission m,Location myLocation)
	{
		if(!notifiedMission.containsKey(m.getId())) return false;
		if(m.getRadiusNotification()<0) return false;
		return distanceFromPoints(m.getLocation(), myLocation) > m.getRadiusNotification();
	}
	
	private double distanceFromPoints(LatLng p1,Location p2)
	{
		Location p1Loc=new Location("P1");
		p1Loc.setLatitude(p1.latitude);
		p1Loc.setLongitude(p1.longitude);
		return p1Loc.distanceTo(p2);
	}
	private PendingIntent builSndSendNotificationToEvent(LocationMission mission) {
		Intent activityIntent = new Intent(context,
				ReminderBroadCastReceiver.class);
		activityIntent
				.setAction("il.ac.asenkar.brodcast_receiver_costum_reciver");
		activityIntent.putExtra("mission_id", mission.getId());
		PendingIntent pendingInent = PendingIntent.getBroadcast(context,
				(int) mission.getId(), activityIntent, 0);
		alarmManager.set(AlarmManager.RTC_WAKEUP, new Date().getTime(), pendingInent);// (AlarmManager.RTC_WAKEUP,
																						// mission.getStartTime(),													// pendingInent);
		return pendingInent;
	}

}
