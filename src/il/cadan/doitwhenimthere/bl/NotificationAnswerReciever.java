package il.cadan.doitwhenimthere.bl;

import java.util.Date;

import il.cadan.doitwhenimthere.DoITConstance;
import il.cadan.doitwhenimthere.LocationMission;
import il.cadan.doitwhenimthere.MainActivity;
import il.cadan.doitwhenimthere.Mission;
import il.cadan.doitwhenimthere.ParsingHelper;
import il.cadan.doitwhenimthere.R;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.support.v4.app.NotificationCompat;
import android.text.format.DateUtils;
import android.widget.Toast;

public class NotificationAnswerReciever extends BroadcastReceiver {

	
	@Override
	public void onReceive(Context context, Intent intent) {
		long id=intent.getLongExtra("id", -1);
		IController controller=MainController.getInstance(context);
		Mission m=controller.getMission(id);
		//if m is null than we hav to update the view model 
		if(m==null)
		{
			controller.updateViewModel();
			 m=controller.getMission(id);
			 //in case now it still null than the mission is not exist.
			 if(m==null) return;
			
		}
		if(DoITConstance.ACTION_DONE.equals(intent.getAction()))
		{
			Toast.makeText(context, "Great!!!,mission was marked as done.", 300).show();
			m.setDone(true);
			controller.updateMission(m);
		}
		else if(DoITConstance.ACTION_SNOOZE.equals(intent.getAction()))
		{
			updateNotificationforSnoozeTime(context, m);
			return;
//			Toast.makeText(context, "I will remind you in 10 minutes", 300).show();
//			controller.snooze(m, DateUtils.MINUTE_IN_MILLIS*1);
		}
		else if(DoITConstance.ACTION_IGNORE.equals(intent.getAction()))
		{
			Toast.makeText(context, "Ok, i will now leave you.", 300).show();
		}
		else if(DoITConstance.ACTION_SNOOZE_ONE_HOUR.equals(intent.getAction()))
		{
			snooze(context, controller, DateUtils.HOUR_IN_MILLIS, m,"one hour");
		}
		else if(DoITConstance.ACTION_SNOOZE_TEN_MINUTE.equals(intent.getAction()))
		{
			snooze(context, controller, DateUtils.MINUTE_IN_MILLIS*10, m,"10 minutes");
		}
		else if(DoITConstance.ACTION_SNOOZE_THREE_HOUR.equals(intent.getAction()))
		{
			snooze(context, controller, DateUtils.HOUR_IN_MILLIS*3, m,"3 hours");
		}
		CancelNotification(context,m.getId());
		
	}
	private void snooze(Context context,IController controller, long time,Mission m,String message)
	{

		Toast.makeText(context, "I will remind you in " +message, 300).show();
		controller.snooze(m, time);
	}
	 public  void CancelNotification(Context ctx,long l) {
	        String  s = Context.NOTIFICATION_SERVICE;
	        NotificationManager mNM = (NotificationManager) ctx.getSystemService(s);
	        mNM.cancel((int) l);
	    }

	 private void updateNotificationforSnoozeTime(Context context,Mission m)
	 {
		 NotificationManager manager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
//			manager.notify((int)m.getId(), builder.build());
			
			
			Intent resultIntent = new Intent(context, MainActivity.class);
			resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | 
			        Intent.FLAG_ACTIVITY_CLEAR_TASK);
			     
			// Because clicking the notification launches a new ("special") activity, 
			// there's no need to create an artificial back stack.
			PendingIntent resultPendingIntent =
			         PendingIntent.getActivity(
			         context,
			         0,
			         resultIntent,
			         PendingIntent.FLAG_UPDATE_CURRENT
			);
			
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
			
			// Sets up the Snooze and Dismiss action buttons that will appear in the
			// big view of the notification.
			Intent dismissIntent = new Intent();
			dismissIntent.setAction(DoITConstance.ACTION_SNOOZE_TEN_MINUTE);
			dismissIntent.putExtra("id", m.getId());
			PendingIntent piDismiss = PendingIntent.getBroadcast(context,(int) m.getId(), dismissIntent, PendingIntent.FLAG_UPDATE_CURRENT);

			Intent snoozeIntent = new Intent();
			snoozeIntent.putExtra("id", m.getId());
			snoozeIntent.setAction(DoITConstance.ACTION_SNOOZE_ONE_HOUR);
			PendingIntent piSnooze = PendingIntent.getBroadcast(context, (int) m.getId(), snoozeIntent, PendingIntent.FLAG_UPDATE_CURRENT);
			
			Intent ignoreIntent = new Intent();
			ignoreIntent.putExtra("id", m.getId());
			ignoreIntent.setAction(DoITConstance.ACTION_SNOOZE_THREE_HOUR);
			PendingIntent piIgnore = PendingIntent.getBroadcast(context, (int) m.getId(), ignoreIntent, PendingIntent.FLAG_UPDATE_CURRENT);
			
			try
			{
			// Constructs the Builder object.
			NotificationCompat.Builder builder =
			        new NotificationCompat.Builder(context)
			        .setSmallIcon(R.drawable.ic_launcher)
			        .setContentTitle(m.getTitle())
			        .setContentText(content)
			        .setDefaults(Notification.FLAG_AUTO_CANCEL) // requires VIBRATE permission
			        /*
			         * Sets the big view "big text" style and supplies the
			         * text (the user's reminder message) that will be displayed
			         * in the detail area of the expanded notification.
			         * These calls are ignored by the support library for
			         * pre-4.1 devices.
			         */
			        .setStyle(new NotificationCompat.BigTextStyle()
			                .bigText(m.getTitle()))
			        .addAction (R.drawable.ic_reminder_white,
			                "10 minutes", piDismiss)
			        .addAction (R.drawable.ic_reminder_white,"1 hour", piSnooze).addAction(R.drawable.ic_reminder_white, "3 hours", piIgnore).setAutoCancel(true).setContentIntent(resultPendingIntent);

			// This sets the pending intent that should be fired when the user clicks the
			// notification. Clicking the notification launches a new activity.
			manager.notify((int) m.getId(), builder.build());
			}
			catch(Exception e)
			{
				Toast.makeText(context,e.getMessage(), 300).show();
			}
	 }
}
