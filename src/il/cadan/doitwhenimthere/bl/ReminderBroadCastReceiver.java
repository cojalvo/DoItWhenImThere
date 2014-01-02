package il.cadan.doitwhenimthere.bl;

import il.cadan.doitwhenimthere.DoITConstance;
import il.cadan.doitwhenimthere.LocationMission;
import il.cadan.doitwhenimthere.MainActivity;
import il.cadan.doitwhenimthere.Mission;
import il.cadan.doitwhenimthere.ParsingHelper;
import il.cadan.doitwhenimthere.R;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
@SuppressLint("NewApi")
public class ReminderBroadCastReceiver extends BroadcastReceiver {
	@SuppressLint("NewApi")
	@Override
	public void onReceive(Context context, Intent intent) {
		try {
			IController controller = MainController.getInstance(context);
			if (controller == null) {
				Log.i(ReminderBroadCastReceiver.class.toString(),
						"Controller is null");
				Toast.makeText(context, "Controller is null", 300).show();
			}
			final int missionId = (int) intent.getLongExtra("mission_id", -1);
			if (missionId == -1)
				return;
			Mission m = controller.getMission(missionId);
			if (m == null) {
				Toast.makeText(context, "Mission is null try update view model",
						300).show();
				controller.updateViewModel();
				m = controller.getMission(missionId);
				if(m==null) return;
			}
			Vibrator v = (Vibrator) context
					.getSystemService(Context.VIBRATOR_SERVICE);
			v.vibrate(700);
			String content = "";
			if(m.getStartTime()!=null)
			{
				content=ParsingHelper.fromDateToString(m.getStartTime(), "dd/MM/yyyy hh:mm \n");
			}
			if(m instanceof LocationMission)
			{
				LocationMission lm=(LocationMission) m;
				content+= lm.getLocationName();
			}

			NotificationManager manager = (NotificationManager) context
					.getSystemService(Context.NOTIFICATION_SERVICE);
			// manager.notify((int)m.getId(), builder.build());

			Intent resultIntent = new Intent(context, MainActivity.class);
			resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
					| Intent.FLAG_ACTIVITY_CLEAR_TASK);

			// Because clicking the notification launches a new ("special")
			// activity,
			// there's no need to create an artificial back stack.
			PendingIntent resultPendingIntent = PendingIntent
					.getActivity(context, 0, resultIntent,
							PendingIntent.FLAG_UPDATE_CURRENT);

			// Sets up the Snooze and Dismiss action buttons that will appear in
			// the
			// big view of the notification.
			Intent dismissIntent = new Intent();
			dismissIntent.setAction(DoITConstance.ACTION_DONE);
			dismissIntent.putExtra("id", m.getId());
			PendingIntent piDismiss = PendingIntent.getBroadcast(context,
					(int) m.getId(), dismissIntent,
					PendingIntent.FLAG_UPDATE_CURRENT);

			Intent snoozeIntent = new Intent();
			snoozeIntent.putExtra("id", m.getId());
			snoozeIntent.setAction(DoITConstance.ACTION_SNOOZE);
			PendingIntent piSnooze = PendingIntent.getBroadcast(context,
					(int) m.getId(), snoozeIntent,
					PendingIntent.FLAG_UPDATE_CURRENT);

			Intent ignoreIntent = new Intent();
			ignoreIntent.putExtra("id", m.getId());
			ignoreIntent.setAction(DoITConstance.ACTION_IGNORE);
			PendingIntent piIgnore = PendingIntent.getBroadcast(context,
					(int) m.getId(), ignoreIntent,
					PendingIntent.FLAG_UPDATE_CURRENT);

			// Constructs the Builder object.
			NotificationCompat.Builder builder = new NotificationCompat.Builder(
					context)
					.setSmallIcon(R.drawable.ic_launcher)
					.setContentTitle(m.getTitle())
					.setContentText(content)
					.setDefaults(Notification.DEFAULT_ALL)
					// requires VIBRATE permission
					/*
					 * Sets the big view "big text" style and supplies the text
					 * (the user's reminder message) that will be displayed in
					 * the detail area of the expanded notification. These calls
					 * are ignored by the support library for pre-4.1 devices.
					 */
					.setStyle(
							new NotificationCompat.BigTextStyle().bigText(m
									.getTitle()))
					.addAction(R.drawable.ic_good, "Done", piDismiss)
					.addAction(R.drawable.ic_reminder_white, "Snooze", piSnooze)
					.addAction(R.drawable.ic_gotit, "Got It", piIgnore)
					.setAutoCancel(true).setContentIntent(resultPendingIntent);

			// This sets the pending intent that should be fired when the user
			// clicks the
			// notification. Clicking the notification launches a new activity.
			manager.notify((int) m.getId(), builder.build());
		} catch (Exception e) {
			Toast.makeText(context, e.getMessage(), 300).show();
		}
	}

}
