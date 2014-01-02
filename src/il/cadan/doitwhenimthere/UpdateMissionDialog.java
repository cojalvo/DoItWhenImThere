package il.cadan.doitwhenimthere;

import il.cadan.doitwhenimthere.AddNewMissionDialog.DatePickerFragment;
import il.cadan.doitwhenimthere.AddNewMissionDialog.TimePickerFragment;

import java.util.Calendar;
import java.util.Date;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

public class UpdateMissionDialog extends AddNewMissionDialog {
	private String title;
	private String description;
	private Boolean withDateAndTime;
	private Date dateAndTime;
	private ReminderFrequency reminderFreq;
	private Boolean done;
	
	public static UpdateMissionDialog getInstance(Bundle args) {
		UpdateMissionDialog amd = new UpdateMissionDialog();
		amd.setArguments(args);
		return amd;
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
			dateButton.setText(ParsingHelper.fromDateToString(dateAndTime, "dd/MM/yyyy"));
			cb.setChecked(false);
			Button timeButton = (Button) view
					.findViewById(R.id.event_pick_time);
			timeButton.setText(ParsingHelper.fromDateToString(dateAndTime, "HH:mm"));
			timeLayout.setVisibility(View.VISIBLE);
			TextView rStatus=(TextView) reminderLayou.findViewById(R.id.mission_tv_reminder_status);
			rStatus.setText(this.choiceList[reminderFreq.ordinal()]);
			this.reminderSelect=reminderFreq.ordinal();
		} else {
			cb.setChecked(true);
			timeLayout.setVisibility(View.GONE);
		}
	}
}
