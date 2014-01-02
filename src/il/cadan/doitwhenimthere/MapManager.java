package il.cadan.doitwhenimthere;


import il.cadan.doitwhenimthere.bl.ApplicationCallaback;
import il.cadan.doitwhenimthere.bl.IController;
import il.cadan.doitwhenimthere.bl.MainController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Point;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.SystemClock;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.GoogleMap.OnMyLocationChangeListener;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

public class MapManager implements InfoWindowAdapter {
	private static MapManager instance = null;
	private HashMap<Long, Marker> missionMatkerDictionary;
	private HashMap<Marker, Long> markerMissionDictionary;
	private IController controller;
	private GoogleMap map = null;
	private Context context;
	private LatLng lastlongClicked = null;
	private BroadcastReceiver viewModelUpdatedReciever;

	public void resetLastLongClicked() {
		lastlongClicked = null;
	}

	public void setOnMapLongClickListener(OnMapLongClickListener listener) {
		if (listener != null)
			this.map.setOnMapLongClickListener(listener);
	}

	public void setOnMapClickListener(OnMapClickListener listener) {
		if (listener != null)
			this.map.setOnMapClickListener(listener);
	}

	public void setOnMarkerClickListener(OnMarkerClickListener listener) {
		if (listener != null)
			this.map.setOnMarkerClickListener(listener);
	}

	private void setOnMyLocationChangedListener() {
		if (map != null)
			map.setOnMyLocationChangeListener(new OnMyLocationChangeListener() {

				@Override
				public void onMyLocationChange(Location location) {
				}
			});
	}

	public LatLng getLastLongClicked() {
		return lastlongClicked;
	}

	public MapManager(Context context, GoogleMap map, int mapType) {
		this.context = context;
		this.map = map;
		this.map.setMapType(mapType);
		this.map.getUiSettings().setZoomControlsEnabled(false);
		this.map.setMyLocationEnabled(true);
		this.map.setInfoWindowAdapter(this);
		controller = MainController.getInstance(context);
		markerMissionDictionary = new HashMap<Marker, Long>();
		missionMatkerDictionary = new HashMap<Long, Marker>();
		setOnMyLocationChangedListener();
		registerViewModelReciever();
	}

	public static void resetInstance() {
		instance = null;
	}

	public void disableMap() {
		if (map != null)
			map.getUiSettings().setAllGesturesEnabled(false);
	}

	public void enableMap() {
		if (map != null)
			map.getUiSettings().setAllGesturesEnabled(true);
	}

	public void moveCameraToMission(long missionId) {
		Marker marker;
		if (missionId != -1) {
			marker = missionMatkerDictionary.get(missionId);
			CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(
					marker.getPosition(), 18);
			map.animateCamera(cameraUpdate);
			marker.showInfoWindow();
		}
	}
	
	public void moveCameraLocation(final LatLng location) {
		final Handler h=new Handler();
		h.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				MarkerOptions markerOptions = new MarkerOptions();
				markerOptions.position(location);
				markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_nav_location_marker));
				final Marker m=map.addMarker(markerOptions);
				h.postDelayed(new Runnable() {
					
					@Override
					public void run() {
						m.remove();
						
					}
				}, DateUtils.MINUTE_IN_MILLIS);
				
			}
		}, 500);
			CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(
					location, 18);
			map.animateCamera(cameraUpdate);
			//drawLineFromMeToPoint(location);
			
	}

	public void addMissionMarker(LocationMission mission) {
		// create and config the marker Option
		MarkerOptions markerOptions = new MarkerOptions();
		markerOptions.position(mission.getLocation());
		if(mission.getDone())
			markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_icecream2));
		else{
			switch (mission.getCategory()) {
			case Other:
				markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_green));
				break;
			case Personal:
				markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_blue));
				break;
			case Work:
				markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_yellow));
				break;

			default:
				break;
			}
		}
		// markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.event_marker));
		Marker missionMarker = map.addMarker(markerOptions);
		// animateMarkerInsert(mission.getLocation(), missionMarker);
		missionMatkerDictionary.put(mission.getId(), missionMarker);
		markerMissionDictionary.put(missionMarker, mission.getId());
	}

	public static LatLng getmyLocation() {
		if (instance != null) {
			Location loc = instance.map.getMyLocation();
			if (loc == null)
				return null;
			return new LatLng(loc.getLatitude(), loc.getLongitude());
		}
		return null;
	}

	public Long getMissionIdFromMarker(Marker marker) {
		if (markerMissionDictionary.containsKey(marker))
			return markerMissionDictionary.get(marker);
		return null;
	}

	public GoogleMap getMap() {
		return map;
	}

	public float getDistanceFromMe(String id) {
		Marker dest;
		if (missionMatkerDictionary.containsKey(id))
			dest = missionMatkerDictionary.get(id);
		else
			return -1;
		return getDistanceFromMe(dest.getPosition());
	}

	private float getDistanceFromMe(LatLng p) {
		Location destLocation = new Location("point A");
		destLocation.setLatitude(p.latitude);
		destLocation.setLongitude(p.longitude);
		Location myLocation = map.getMyLocation();
		if (myLocation == null)
			return -1;
		return myLocation.distanceTo(destLocation);
	}

	public float getDistanceFromMe(Marker marker) {
		if (marker == null)
			return -1;
		return getDistanceFromMe(marker.getPosition());
	}

	@Override
	public View getInfoContents(Marker marker) {
		if(!markerMissionDictionary.containsKey(marker)) return null;
		  	LayoutInflater lf = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
		  	View v=lf.inflate(R.layout.info_window_event_content_layout, null);

		    // get the marker (which is a person) information
		    
		    LocationMission currEvent = (LocationMission) controller.getMission(markerMissionDictionary.get(marker));

		    // set the image view
		    /*
		     * ImageView imageView = (ImageView)
		     * v.findViewById(R.id.info_window_event_imageView);
		     * imageView.setImageDrawable();
		     */
		    // set the Name
		    TextView name = (TextView) v.findViewById(R.id.info_window_event_name);
		    name.setText(currEvent.getTitle());

		    // set the Date
		    TextView status = (TextView) v.findViewById(R.id.info_window_description);
		    String desc=currEvent.getDscription();
		    if(desc.equals("")||desc==null)
		    	status.setVisibility(View.GONE);
		    else
		    {
		    	status.setText(currEvent.getDscription());
		    }

		    // set the time
		    TextView time = (TextView) v.findViewById(R.id.info_window_event_time);
		    if(currEvent.getStartTime()==null)
		    	time.setVisibility(View.GONE);
		    else
		    	time.setText(ParsingHelper.fromDateToString(currEvent.getStartTime(), "dd/MM/yyyy hh:mm"));

		    // set the Location
		    TextView location = (TextView) v.findViewById(R.id.info_window_location_name);
		    location.setText(currEvent.getLocationName());

		    // set the dictance
		    TextView dis = (TextView) v.findViewById(R.id.info_window_distance);
		    dis.setText(getDistanceStringFromMarker(marker));
		    return v;
	}

	@Override
	public View getInfoWindow(Marker marker) {
		// TODO Auto-generated method stub
		return null;
	}

	public void getAddresFromLatLng(final LatLng ll,
			final ApplicationCallaback<String> callBack) {
		new AsyncTask<Location, Void, String>() {

			@Override
			protected String doInBackground(Location... params) {
				Geocoder geocoder = new Geocoder(context, Locale.getDefault());
				// Get the current location from the input parameter list
				// Create a list to contain the result address
				List<Address> addresses = null;
				try {
					/*
					 * Return 1 address.
					 */
					addresses = geocoder.getFromLocation(ll.latitude,
							ll.longitude, 1);
				} catch (IOException e1) {
					Log.e("LocationSampleActivity",
							"IO Exception in getFromLocation()");
					e1.printStackTrace();
					return ("Unable to get address");
				} catch (IllegalArgumentException e2) {
					// Error message to post in the log
					String errorString = "Illegal arguments "
							+ Double.toString(ll.latitude) + " , "
							+ Double.toString(ll.longitude)
							+ " passed to address service";
					Log.e("LocationSampleActivity", errorString);
					e2.printStackTrace();
					return errorString;
				}
				// If the reverse geocode returned an address
				if (addresses != null && addresses.size() > 0) {
					// Get the first address
					Address address = addresses.get(0);
					String firstPart = "";
					String secondPart = "";
					String thirdPart = "";
					firstPart=address.getAddressLine(0);
					if(firstPart==null || firstPart.equals(""))
					{
						firstPart="";
					}
					else
						firstPart+=", ";
					secondPart = address.getLocality();
					if (secondPart == null || secondPart.equals(""))
						secondPart = "";
					else
						secondPart+=", ";
					thirdPart = address.getCountryName();
					if (thirdPart == null)
						thirdPart = "";

					String addressText = String.format("%s%s%s",
					// If there's a street address, add it
							firstPart,
							// Locality is usually a city
							secondPart,
							// The country of the address
							thirdPart);
					// Return the text
					return addressText;
				} else {
					return "No address found";
				}
			}

			@Override
			protected void onPostExecute(String result) {
				if (callBack != null)
					callBack.done(result, null);

			}
		}.execute();
	}

	public void getAddressFromString(final String addres,
			final ApplicationCallaback<List<Address>> callBack) {
		new AsyncTask<String, Void, List<Address> >()
		{

			@Override
			protected List<Address> doInBackground(String... params) {
				Geocoder coder = new Geocoder(context);
				List<Address> address = null;

				try {
					address = coder.getFromLocationName(addres,100);
					return address;
				} catch (Exception e) {
						return address;
				}
			}

			@Override
			protected void onPostExecute(List<Address> result) {
				if(result==null)
					result=new ArrayList<Address>();
				if(callBack!=null)
					callBack.done(result, null);
			}
			

		}.execute();
	}

	private void clearMissions() {
		for (Marker m : missionMatkerDictionary.values()) {
			m.remove();
		}
		missionMatkerDictionary.clear();
		markerMissionDictionary.clear();
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
						clearMissions();
						map.clear();
						List<LocationMission> newList = controller
								.getAllLocationMissions();
						for (LocationMission locationMission : newList) {
							addMissionMarker(locationMission);
						}
					}
				}
			};
			context.registerReceiver(viewModelUpdatedReciever, filterSend);
		}

	private void animateMarkerInsert(LatLng dest, final Marker marker) {
		final LatLng target = dest;

		final long duration = 400;
		final Handler handler = new Handler();
		final long start = SystemClock.uptimeMillis();
		Projection proj = map.getProjection();

		Point startPoint = proj.toScreenLocation(target);
		startPoint.y = 0;
		final LatLng startLatLng = proj.fromScreenLocation(startPoint);

		final Interpolator interpolator = new LinearInterpolator();
		handler.post(new Runnable() {
			@Override
			public void run() {
				long elapsed = SystemClock.uptimeMillis() - start;
				float t = interpolator.getInterpolation((float) elapsed
						/ duration);
				double lng = t * target.longitude + (1 - t)
						* startLatLng.longitude;
				double lat = t * target.latitude + (1 - t)
						* startLatLng.latitude;
				marker.setPosition(new LatLng(lat, lng));
				if (t < 1.0) {
					// Post again 10ms later.
					handler.postDelayed(this, 50);
				} else {
					// animation ended
				}
			}
		});
	}

	public void unRegisterRecivers() {
		context.unregisterReceiver(viewModelUpdatedReciever);
	}
	
    private String getDistanceStringFromMarker(Marker marker)
    {
	float dist = this.getDistanceFromMe(marker);
	if (dist > 0)
	{
	    String unit;
	    String finalDist;
	    if (dist > 1000)
	    {
		unit = "km";

		finalDist = String.format("%.2f", dist / 1000);
	    }
	    else
	    {
		unit = "m";
		finalDist = String.format("%.0f", dist);
	    }

	    return ("About " + finalDist + " " + unit + " " + "from me.");
	}
	return "Unown destance.";

    }
//
//    private void drawLineFromMeToPoint(LatLng p)
//    {
//    	map.addPolyline(new PolylineOptions().add(p,this.getmyLocation()).width(3).color(Color.BLACK));
//    }
}
