package il.cadan.doitwhenimthere.dal;

import il.cadan.doitwhenimthere.Category;
import il.cadan.doitwhenimthere.LocationMission;
import il.cadan.doitwhenimthere.Mission;
import il.cadan.doitwhenimthere.ReminderFrequency;
import il.cadan.doitwhenimthere.bl.ApplicationCallaback;

import java.util.ArrayList;
import java.util.List;

import com.google.android.gms.maps.model.LatLng;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class BackupManager implements IBackupManagerObject {

	@Override
	public void backup(final List<Mission> toBackup,
			final ApplicationCallaback<Integer> callBack) {
		final ParseUser current = ParseUser.getCurrentUser();
		if (current == null) {
			if (callBack != null)
				callBack.done(1, null);
			return;
		}
		final ParseRelation<ParseObject> missions = current
				.getRelation("missions");
		ParseQuery<ParseObject> q = missions.getQuery();
		q.findInBackground(new FindCallback<ParseObject>() {

			@Override
			public void done(List<ParseObject> l, ParseException e) {
				if (e == null) {
					for (ParseObject mis : l) {
						missions.remove(mis);
						mis.deleteEventually();
					}
					final List<ParseObject> parseMissions = new ArrayList<ParseObject>();
					for (Mission mission : toBackup) {
						parseMissions.add(fromMissionToParseObject(mission));
						// missions.add(fromMissionToParseObject(mission));
					}
					ParseObject.saveAllInBackground(parseMissions,
							new SaveCallback() {

								@Override
								public void done(ParseException e) {
									if (e == null) {
										for (ParseObject parseMission : parseMissions) {
											missions.add(parseMission);
										}
										current.saveInBackground(new SaveCallback() {

											@Override
											public void done(ParseException arg0) {
												if (callBack != null)
													callBack.done(0, null);

											}
										});
									}

								}
							});

				} else {
					if (callBack != null)
						callBack.done(1, null);
				}
			}
		});

	}

	@Override
	public void restore(final ApplicationCallaback<List<Mission>> callBack) 
	{
		final List<Mission> retLis=new ArrayList<Mission>();
		ParseUser current=ParseUser.getCurrentUser();
		current.getRelation("missions").getQuery().findInBackground(new FindCallback<ParseObject>() {
			
			@Override
			public void done(List<ParseObject> poList, ParseException arg1) {
				
				for (ParseObject po:poList) {
					retLis.add(fromParseObjectToMission(po));
				}
				if(callBack!=null)
					callBack.done(retLis, null);
			}
		});

	}

	private Mission fromParseObjectToMission(ParseObject po) {
		Mission m = new Mission();
		m.setTitle(po.getString("title"));
		m.setDscription(po.getString("description"));
		m.setCategory(Category.valueOf(po.getString("category")));
		m.setDone(po.getBoolean("done"));
		if(po.getBoolean("startTimeSpecified"))
		{
			m.setStartTime(po.getDate("startTime"));
			m.setReminderFrequency(ReminderFrequency.valueOf(po.getString("reminderFreq")));
		}
		if(po.getBoolean("isLocationMission"))
		{
			LocationMission lm=new LocationMission(m);
			lm.setLocation(new LatLng(po.getDouble("lat"), po.getDouble("lng")));
			lm.setLocationName(po.getString("locationName"));
			lm.setRadiusNotification(po.getInt("radius"));
			m=lm;
		}
		return m;
	}

	private ParseObject fromMissionToParseObject(Mission m) {
		ParseObject mission;
		if (m instanceof LocationMission)
			mission = fromMissionToParseObject((LocationMission) m);
		else {
			mission = new ParseObject("mission");
			mission.put("isLocationMission", false);
		}
		mission.put("title", m.getTitle());
		mission.put("description", m.getDscription());
		mission.put("category", m.getCategory().toString());
		mission.put("done", m.getDone());
		mission.put("startTimeSpecified",false);
		if (m.getStartTime() != null) {
			mission.put("startTimeSpecified", true);
			mission.put("startTime", m.getStartTime());
			mission.put("reminderFreq", m.getReminderFrequency().toString());
		}
		return mission;

	}

	private ParseObject fromMissionToParseObject(LocationMission locMission) {
		ParseObject locationMissionPo = new ParseObject("mission");
		locationMissionPo.put("isLocationMission", true);
		locationMissionPo.put("lat", locMission.getLocation().latitude);
		locationMissionPo.put("lng", locMission.getLocation().longitude);
		locationMissionPo.put("locationName", locMission.getLocationName());
		locationMissionPo.put("radius", locMission.getRadiusNotification());
		return locationMissionPo;

	}

}
