package il.cadan.doitwhenimthere;

import java.util.Date;

public class Mission implements Comparable<Mission> {
	private long id;
	private String title;
	private String dscription;
	private Date startTime;
	private Date endTime;
	private Boolean done = false;
	private ReminderFrequency reminderFrequency = ReminderFrequency.Off;
	private Category category;

	public Mission(Mission mission) 
	{
		if(mission!=null)
		{
			this.id=mission.getId();
			this.title=mission.getTitle();
			this.dscription=mission.getDscription();
			this.startTime=mission.getStartTime();
			this.endTime=mission.getEndTime();
			this.done=mission.getDone();
			this.reminderFrequency=mission.getReminderFrequency();
		}
			
		
	}
	public Mission()
	{
		
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDscription() {
		return dscription;
	}

	public void setDscription(String dscription) {
		this.dscription = dscription;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public Boolean getDone() {
		return done;
	}

	public void setDone(Boolean done) {
		this.done = done;
	}

	public ReminderFrequency getReminderFrequency() {
		return reminderFrequency;
	}

	public void setReminderFrequency(ReminderFrequency reminderFrequency) {
		this.reminderFrequency = reminderFrequency;
	}

	@Override
	public int compareTo(Mission another) 
	{
		if (another.getStartTime() == null && this.getStartTime() == null)
			return 0;
		if (this.getStartTime() != null && another.getStartTime() == null)
			return -1;
		if (another.getStartTime() != null && this.getStartTime() == null)
			return 1;
		return this.getStartTime().compareTo(another.getStartTime());
	}
	public Category getCategory() {
		return category;
	}
	public void setCategory(Category category) {
		this.category = category;
	}

}

