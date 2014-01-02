package il.cadan.doitwhenimthere.bl;

import il.cadan.doitwhenimthere.Category;
import il.cadan.doitwhenimthere.LocationMission;
import il.cadan.doitwhenimthere.Mission;

import java.util.List;

public interface IController 
{
	List<Mission> getMissionsList();
	Mission getMission(long id);
	void addMission(Mission toAdd);
	void deleteMission(long toDelete);
	void updateMission(Mission toUpdate);
	void deleteMany(List<Long> toDelete);
	void changeCategory(Category newCategory);
	void updateViewModelInBackground();
	void updateViewModel();
	List<LocationMission> getAllLocationMissions();
	void snooze(Mission m,long delay);
	
	void startLocationNofificationService();
}
