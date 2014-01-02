package il.cadan.doitwhenimthere;

import il.cadan.doitwhenimthere.bl.ApplicationCallaback;
import il.cadan.doitwhenimthere.dal.DataAccesObject;
import il.cadan.doitwhenimthere.dal.IDataAccesObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.os.AsyncTask;

public class ViewModel {
	private HashMap<Long, Mission> missions;
	private HashMap<Long, Mission> personalMissions;
	private HashMap<Long, Mission> workMissions;
	private HashMap<Long, Mission> otherMissions;
	private HashMap<Long, LocationMission> locationMisions;
	private IDataAccesObject dao;
	private Context context;

	public ViewModel(Context context) {
		this.context = context;
		missions = new HashMap<Long, Mission>();
		locationMisions=new HashMap<Long, LocationMission>();
		personalMissions=new HashMap<Long, Mission>();
		workMissions=new HashMap<Long, Mission>();
		otherMissions=new HashMap<Long, Mission>();
		dao = DataAccesObject.getInstance(context);
	}

	public void updateViewModel() {
		missions.clear();
		List<Mission> retKist = dao.getAllMission();
		for (Mission mission : retKist) {
			missions.put(mission.getId(), mission);
			switch (mission.getCategory()) {
			case Other:
				otherMissions.put(mission.getId(), mission);
				break;
			case Personal:
				personalMissions.put(mission.getId(), mission);
				break;
			case Work:
				workMissions.put(mission.getId(), mission);
				break;

			default:
				break;
			}
			if(mission instanceof LocationMission)
				locationMisions.put(mission.getId(), (LocationMission) mission);
		}
	}

	public void updateViewModelInBackground(
			final ApplicationCallaback<Integer> callBack) {
		new AsyncTask<Integer, Integer, Integer>() {

			@Override
			protected Integer doInBackground(Integer... arg0) {
				updateViewModel();
				return 0;
			}

			@Override
			protected void onPostExecute(Integer result) {
				super.onPostExecute(result);
				if (callBack != null)
					callBack.done(result, null);
			}
		}.execute();
	}

	public List<Mission> getAllMissions() {
		return new ArrayList<Mission>(missions.values());
	}
	public List<Mission> getAllPersonalMissions() {
		return new ArrayList<Mission>(personalMissions.values());
	}
	public List<Mission> getAllWorkMissions() {
		return new ArrayList<Mission>(workMissions.values());
	}
	public List<Mission> getAllOtherMissions() {
		return new ArrayList<Mission>(otherMissions.values());
	}
	public List<LocationMission> getAllLocationMissions()
	{
		return new ArrayList<LocationMission>(locationMisions.values());
	}

	public void addNewMission(Mission toAdd) {
		if (toAdd != null)
			missions.put(toAdd.getId(), toAdd);
		if(toAdd instanceof LocationMission)
			locationMisions.put(toAdd.getId(), (LocationMission) toAdd);
		switch (toAdd.getCategory()) {
		case Other:
			otherMissions.put(toAdd.getId(), toAdd);
			break;
		case Work:
			workMissions.put(toAdd.getId(), toAdd);
			break;
		case Personal:
			personalMissions.put(toAdd.getId(), toAdd);
			break;

		default:
			break;
		}
		
	}

	public void removMission(long id) {
		if (missions.containsKey(id))
			missions.remove(id);
		if(locationMisions.containsKey(id))
			locationMisions.remove(id);
		if(personalMissions.containsKey(id))
			personalMissions.remove(id);
		if(workMissions.containsKey(id))
			workMissions.remove(id);
		if(otherMissions.containsKey(id))
			otherMissions.remove(id);
	}

	public Mission getMission(long id) {
		if (missions.containsKey(id))
			return missions.get(id);
		return null;
	}

	public void updateMission(Mission toUpdate) {
		if (missions.containsKey(toUpdate.getId()))
			missions.put(toUpdate.getId(), toUpdate);
		if(locationMisions.containsKey(toUpdate.getId()))
			locationMisions.put(toUpdate.getId(), (LocationMission) toUpdate);
		if (personalMissions.containsKey(toUpdate.getId()))
			personalMissions.put(toUpdate.getId(), toUpdate);
		if (workMissions.containsKey(toUpdate.getId()))
			workMissions.put(toUpdate.getId(), toUpdate);
		if (otherMissions.containsKey(toUpdate.getId()))
			otherMissions.put(toUpdate.getId(), toUpdate);
	}

}
