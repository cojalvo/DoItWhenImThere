package il.cadan.doitwhenimthere;

public class AdapterMissionWrapper
{
	private Mission mission;
	private Boolean isSection=false;
	private String sectionText=null;
	public Mission getMission() {
		return mission;
	}
	public void setMission(Mission mission) {
		this.mission = mission;
	}
	public Boolean getIsSection() {
		return isSection;
	}
	public void setIsSection(Boolean isSection) {
		this.isSection = isSection;
	}
	public String getSectionText() {
		return sectionText;
	}
	public void setSectionText(String sectionText) {
		this.sectionText = sectionText;
	}
}
