package il.cadan.doitwhenimthere;

import java.util.Calendar;
import java.util.Date;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

public class UpdateLocationMissionDialog extends AddNewLocationMissionDialog
{
	private String title;
	private String description;
	private Boolean withDateAndTime;
	private Date dateAndTime;
	private ReminderFrequency reminderFreq;
	private Boolean done;
	double lat;
	double lng;
	String locationName;
	private Boolean locNot;
	int rad;
	public static UpdateLocationMissionDialog getInstance(Bundle args)
	{
		UpdateLocationMissionDialog ulm = new UpdateLocationMissionDialog();
		ulm.setArguments(args);
		return ulm;
	}
	
	@Override
	public String getTitleResource() {
		return "Update Mission";
	}

	@Override
	public void extractArgs() {
		super.extractArgs();
		Bundle args = getArguments();
		title = args.getString("title");
		description = args.getString("description");
		withDateAndTime = args.getBoolean("withDateAndTime");
		if (withDateAndTime) {
			Long d = args.getLong("dateAndTime");
			dateAndTime = new Date(d);
			String freq = args.getString("reminderFreq");
			reminderFreq = ReminderFrequency.valueOf(freq);
		}
		 locationName=args.getString("locationName");
		 lat=args.getDouble("lat");
		 lng=args.getDouble("lng");
		 rad=args.getInt("radius");
		 locNot=args.getBoolean("notifyLocation");
	}

	@Override
	public void processView() {
		super.processView();
		EditText eventName = (EditText) view
				.findViewById(R.id.event_name_edit_text);
		eventName.setText(title);
		EditText eventDesc = (EditText) view
				.findViewById(R.id.event_description_edit_text);
		eventDesc.setText(description);
		CheckBox cb = (CheckBox) view
				.findViewById(R.id.add_mission_with_time_cb);
		final LinearLayout timeLayout = (LinearLayout) view
				.findViewById(R.id.add_mission_time_layout);
		LinearLayout reminderLayou = (LinearLayout) view
				.findViewById(R.id.mission_reminder_layout);
		if (withDateAndTime) {
			Calendar cal=Calendar.getInstance();
			cal.setTime(dateAndTime);
			this.cal=cal;
			Button dateButton = (Button) view
					.findViewById(R.id.event_pick_date_button);
			dateButton.setText(cal.get(Calendar.DAY_OF_MONTH)+"/"+cal.get(Calendar.MONTH)+"/"+cal.get(Calendar.YEAR));
			cb.setChecked(false);
			Button timeButton = (Button) view
					.findViewById(R.id.event_pick_time);
			int iHour=cal.get(Calendar.HOUR);
			int iMinute=cal.get(Calendar.MINUTE);
			String sHour = "";
			String sMinute = "";
			if(iHour>9) sHour+=iHour;
			else
				sHour="0"+iHour;
			if(iMinute>9) sMinute+=iMinute;
			else
				sMinute="0"+iMinute;
			timeButton.setText(sHour+":"+sMinute);
			timeLayout.setVisibility(View.VISIBLE);
			TextView rStatus=(TextView) reminderLayou.findViewById(R.id.mission_tv_reminder_status);
			rStatus.setText(this.choiceList[reminderFreq.ordinal()]);
			this.reminderSelect=reminderFreq.ordinal();
		} else {
			cb.setChecked(true);
			timeLayout.setVisibility(View.GONE);
		}
		TextView locName=(TextView) view.findViewById(R.id.event_Location_text_view);
		locName.setText(locationName);
		SeekBar sb=(SeekBar) view.findViewById(R.id.seekBar1);
		if(locNot)
			sb.setProgress(rad);
		sb.setEnabled(locNot);
		CheckBox locNotCb=(CheckBox) view.findViewById(R.id.cb_add_mission_notify_location);
		locNotCb.setChecked(locNot);
	}
	

}
