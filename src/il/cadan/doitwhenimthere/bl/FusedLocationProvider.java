package il.cadan.doitwhenimthere.bl;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationListener;

import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.format.DateUtils;
import android.util.Log;
import android.widget.Toast;

public class FusedLocationProvider implements
		GooglePlayServicesClient.ConnectionCallbacks,
		GooglePlayServicesClient.OnConnectionFailedListener {

	private static final String TAG = FusedLocationProvider.class.getName();
	private Context context;
	private LocationClient locationClient;
	private LocationRequest request;
	private LocationNoficationManager lManager;

	private int googletimeVal = (int) (DateUtils.SECOND_IN_MILLIS*30), googledisVal = 0;

	public FusedLocationProvider(Context context) {
		this.context = context;

		locationClient = new LocationClient(context, this, this);

		request = new LocationRequest();
		request.setPriority(LocationRequest.PRIORITY_LOW_POWER);
		request.setInterval(0);
		lManager=new LocationNoficationManager(context);

	}

	public void start() {

		Toast.makeText(context, "Start fused location base provider", 300).show();
		request.setInterval(googletimeVal);
		//request.setFastestInterval(googletimeVal);
		request.setSmallestDisplacement(googledisVal);
		Log.d(TAG, "INTERVAL:+ GOOGLE-" + googletimeVal + "      GOOGLE-"
				+ googledisVal);
		if (locationClient.isConnected()) {
			locationClient.disconnect();
		}
		int isGooglePlayServiceAvilable = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(context);
		if (isGooglePlayServiceAvilable == ConnectionResult.SUCCESS)
			locationClient.connect();
		else
		{
			Toast.makeText(context, "Google Play Service does not avilable", 300).show();
			Log.e(TAG, "Google Play Service does not avilable");
		}

	}

	public void stop() {
		if (locationClient.isConnected()) {
			locationClient.removeLocationUpdates(googleListener);
			locationClient.disconnect();
		}

	}

	LocationListener googleListener = new LocationListener() {

		private Location loc;

		public void onLocationChanged(Location location) {

			Log.i(TAG, "googleListener..");
			//Toast.makeText(context, "location changed, check for notification",300).show();
			lManager.notify(location);
		}
	};

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		/*
		 * Google Play services can resolve some errors it detects. If the error
		 * has a resolution, try sending an Intent to start a Google Play
		 * services activity that can resolve error.
		 */
		if (result.hasResolution()) {
			try {
				// Start an Activity that tries to resolve the error
				result.startResolutionForResult((Activity) context, 0);
				/*
				 * Thrown if Google Play services canceled the original
				 * PendingIntent
				 */
			} catch (IntentSender.SendIntentException e) {
				// Log the error
				e.printStackTrace();
			}
		} else {
			Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onConnected(Bundle connectionHint) {
		// Display the connection status
		// Toast.makeText(context, "Connected", Toast.LENGTH_SHORT).show();
		locationClient.requestLocationUpdates(request, googleListener);

	}

	@Override
	public void onDisconnected() {
		// locationClient.removeLocationUpdates(googleListener);
	}
}
