package il.cadan.doitwhenimthere;

import com.google.android.gms.maps.model.LatLng;

public class LocationMission extends Mission
{
	//the location on the map
	private LatLng location;
	//the radius to notify on the mission
	private int radiusNotification;
	
	private String locationName;
	

	public LocationMission(Mission mission)
	{
		super(mission);
	}
	public LocationMission(){}
	public LatLng getLocation() {
		return location;
	}

	public void setLocation(LatLng location) {
		this.location = location;
	}

	public int getRadiusNotification() {
		return radiusNotification;
	}

	public void setRadiusNotification(int radiusNotification) {
		this.radiusNotification = radiusNotification;
	}

	public String getLocationName() {
		return locationName;
	}

	public void setLocationName(String locationName) {
		this.locationName = locationName;
	}
}
