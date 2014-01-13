package il.cadan.doitwhenimthere.dal;

import java.util.List;

import il.cadan.doitwhenimthere.Mission;
import il.cadan.doitwhenimthere.bl.ApplicationCallaback;

public interface IBackupManagerObject {
	
	 void backup(List<Mission> toBackup,ApplicationCallaback<Integer> callBack);
	 void restore(ApplicationCallaback<List<Mission>> callBack);
}
