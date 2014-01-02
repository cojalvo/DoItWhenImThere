package il.cadan.doitwhenimthere.services;

import il.cadan.doitwhenimthere.bl.FusedLocationProvider;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

public class LocationReminderService extends Service {

	private FusedLocationProvider fusedLocaionProvider;

	// TODO Should add the interval time to the preference
	@Override
	public IBinder onBind(Intent intent) {

		return null;
	}

	@Override
	public void onCreate() {
		 fusedLocaionProvider = new FusedLocationProvider(
					getApplicationContext());

	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i("cadan", "Service has started");
		fusedLocaionProvider.start();
		return START_STICKY;
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		fusedLocaionProvider.stop();

	}
}
