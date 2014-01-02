package il.cadan.doitwhenimthere.dal;

import il.cadan.doitwhenimthere.Mission;

import java.util.List;

public interface IDataAccesObject
{
	void open();
	void close();
	Mission addNewMission(Mission mission);
	List<Mission> getAllMission();
	void updateMission(Mission toUpdate);
	void deleteMission(long id);
	void getMission(String id);
}
