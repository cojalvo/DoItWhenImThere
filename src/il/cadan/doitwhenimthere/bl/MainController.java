package il.cadan.doitwhenimthere.bl;

import il.cadan.doitwhenimthere.Category;
import il.cadan.doitwhenimthere.DoITConstance;
import il.cadan.doitwhenimthere.LocationMission;
import il.cadan.doitwhenimthere.MapManager;
import il.cadan.doitwhenimthere.Mission;
import il.cadan.doitwhenimthere.ViewModel;
import il.cadan.doitwhenimthere.dal.DataAccesObject;
import il.cadan.doitwhenimthere.dal.IDataAccesObject;
import il.cadan.doitwhenimthere.services.LocationReminderService;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.os.Parcel;
import android.widget.Toast;

public class MainController implements IController {
	private Context context;
	private Category currentCategory=Category.Personal;
	private static IController instance;
	private ViewModel vm;
	private IDataAccesObject dao;
	private NotificationManager nf;

	private MainController(Context context) {
		this.context = context;
		dao = DataAccesObject.getInstance(context);
		vm = new ViewModel(context);
		nf=new NotificationManager(context);
	}

	public static IController getInstance(Context context)
	{
		if(instance==null)
			instance=new  MainController(context);
		return instance;
	}

	@Override
	public List<Mission> getMissionsList() {
		//return vm.getAllMissions();
		switch (currentCategory) {
		case Other:
			return vm.getAllOtherMissions();
		case Personal:
			return vm.getAllPersonalMissions();
		case Work:
			return vm.getAllWorkMissions();

		default:
			return vm.getAllMissions();
		}
	}

	@Override
	public void addMission(Mission toAdd) {
		dao.open();
		toAdd.setCategory(currentCategory);
		toAdd = dao.addNewMission(toAdd);
		dao.close();
		vm.addNewMission(toAdd);
		nf.updateNotification(toAdd);
		invokeViewModelUpdated();
	}

	@Override
	public void deleteMission(long toDelete) {
		dao.open();
		dao.deleteMission(toDelete);
		vm.removMission(toDelete);
		dao.close();
		nf.cancelNotification(toDelete);
		invokeViewModelUpdated();
	}

	@Override
	public void updateMission(Mission toUpdate) {
		toUpdate.setCategory(currentCategory);
		dao.updateMission(toUpdate);
		vm.updateMission(toUpdate);
		if(toUpdate.getDone())
			nf.cancelNotification(toUpdate);
		else 
			nf.updateNotification(toUpdate);
		invokeViewModelUpdated();

	}

	private void invokeViewModelUpdated() {
		Intent inti = new Intent();
		inti.setAction(DoITConstance.VIEW_MODEL_UPDATED);
		if (context != null)
			context.sendBroadcast(inti);
	}
	
	private void invokeMapMissionDelete()
	{
		Intent inti = new Intent();
		inti.setAction(DoITConstance.MAP_MISSIONL_UPDATED);
		if (context != null)
			context.sendBroadcast(inti);
	}
	
	public void updateViewModelInBackground()
	{
		vm.updateViewModelInBackground(new ApplicationCallaback<Integer>() {
			
			@Override
			public void writeToParcel(Parcel arg0, int arg1) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public int describeContents() {
				// TODO Auto-generated method stub
				return 0;
			}
			
			@Override
			public void done(Integer retObj, Exception e) {
				if(retObj==0)
					invokeViewModelUpdated();
			}
		});
	}

	@Override
	public void deleteMany(List<Long> toDelete) {
		dao.open();
		Boolean updateMap=false;
		for (Long id : toDelete) {
			if(vm.getMission(id) instanceof LocationMission)
				updateMap=true;
			dao.deleteMission(id);
			vm.removMission(id);
		}
		dao.close();
		//if(updateMap)
			//invokeMapMissionDelete();
		//no need to update the view since the item is being remove from the adapter.
		invokeViewModelUpdated();
		
		
	}

	@Override
	public Mission getMission(long id) {
		try
		{
			return vm.getMission(id);
		}
		catch(Exception e)
		{
			Toast.makeText(context, "View model is null", 300).show();
			return null;
		}
	}

	@Override
	public List<LocationMission> getAllLocationMissions() {
		return vm.getAllLocationMissions();
	}

	@Override
	public void snooze(Mission m, long delay) {
		nf.snooze(m, delay);
		
	}

	@Override
	public void changeCategory(Category newCategory) {
		if(newCategory==currentCategory) return;
		currentCategory=newCategory;
		invokeViewModelUpdated();
	}

	@Override
	public void updateViewModel() {
		vm.updateViewModel();
		
	}
	
    private boolean isMyServiceRunning(Class serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

	@Override
	public void startLocationNofificationService() {
		if ((isMyServiceRunning(LocationReminderService.class)))
		    return;
		Intent i = new Intent(context, LocationReminderService.class);
		context.startService(i);
		
	}
	

}
