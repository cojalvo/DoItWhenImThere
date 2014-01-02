package il.cadan.doitwhenimthere;

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.w3c.dom.Text;

import il.cadan.doitwhenimthere.bl.ApplicationCallaback;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

public class AddNewMissionDialog extends CustomAlertDialogBase {
	protected Mission toReturn;
	ApplicationCallaback<Mission> callBack;
	protected Calendar cal;

	public AddNewMissionDialog() {
		super();
	}

	public static AddNewMissionDialog getInstance(Bundle args) {
		AddNewMissionDialog amd = new AddNewMissionDialog();
		amd.setArguments(args);
		return amd;
	}

	@Override
	public void setView() {
		view = View.inflate(getActivity(), R.layout.add_mission_layout, null);
	}

	@Override
	public void processView() {
		if (view == null)
			return;
		Button dateButton = (Button) view
				.findViewById(R.id.event_pick_date_button);
		dateButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// show the Date Picker
				DialogFragment newFragment = new DatePickerFragment();
				newFragment.setTargetFragment(AddNewMissionDialog.this, 0);
				newFragment.show(getFragmentManager(), "datePicker");
			}
		});

		Button timeButton = (Button) view.findViewById(R.id.event_pick_time);
		timeButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				DialogFragment newDialogFragment = new TimePickerFragment();
				newDialogFragment
						.setTargetFragment(AddNewMissionDialog.this, 0);
				newDialogFragment.show(getFragmentManager(), "TimePicker");
			}
		});
		final LinearLayout timeLayout = (LinearLayout) view
				.findViewById(R.id.add_mission_time_layout);
		CheckBox cb = (CheckBox) view
				.findViewById(R.id.add_mission_with_time_cb);
		cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked)
					// GuiHalper.collapse(timeLayout);
					timeLayout.setVisibility(View.GONE);
				else
					 //GuiHalper.expand(timeLayout);
					timeLayout.setVisibility(View.VISIBLE);

			}
		});
		
		LinearLayout reminderLayou=(LinearLayout) view.findViewById(R.id.mission_reminder_layout);
		reminderLayou.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showDialogButtonClick();
				
			}
		});

	}

	@Override
	public String getTitleResource() {
		// TODO Auto-generated method stub
		return "Add new mission";
	}

	@Override
	public int getIconResource() {
		// TODO Auto-generated method stub
		return R.drawable.ic_add_mission_black;
	}

	@Override
	public Boolean validateAndCreateOutput() {
		toReturn = new Mission();
		Calendar rightNow = Calendar.getInstance();
		EditText eventName = (EditText) view
				.findViewById(R.id.event_name_edit_text);
		EditText eventDesc = (EditText) view
				.findViewById(R.id.event_description_edit_text);
		CheckBox withDateTime = (CheckBox) view
				.findViewById(R.id.add_mission_with_time_cb);
		Boolean withoutTime = withDateTime.isChecked();
		String eventNameString = eventName.getText().toString();

		if (!withoutTime) {
			// cherck the time and date input
			if (cal == null) {
				Toast.makeText(getActivity(), "no Date choosen for the event ",
						3000).show();
				return false;
			}
			if (cal.before(rightNow)) {
				Toast.makeText(getActivity(),
						"Date is not valid cannot add past envents", 3000)
						.show();
				return false;
			} else
				toReturn.setStartTime(cal.getTime());
			// check the event name input
			switch (reminderSelect) {
			case 0:
				toReturn.setReminderFrequency(ReminderFrequency.Off);
				break;
			case 1:
				toReturn.setReminderFrequency(ReminderFrequency.Once);
				break;
			case 2:
				toReturn.setReminderFrequency(ReminderFrequency.OnceDay);
				break;
			case 3:
				toReturn.setReminderFrequency(ReminderFrequency.OnceWeek);
				break;
			case 4:
				toReturn.setReminderFrequency(ReminderFrequency.OnceMonth);
				break;
			case 5:
				toReturn.setReminderFrequency(ReminderFrequency.OnceYear);
				break;
			default:
				toReturn.setReminderFrequency(ReminderFrequency.Off);
				break;
			}
		} else {
			toReturn.setStartTime(null);
		}
		if (eventNameString == null || eventNameString.isEmpty()) {
			Toast.makeText(getActivity(), "Please set a name to the event",
					3000).show();
			eventName.setHintTextColor(Color.RED);
			return false;
		} else
			toReturn.setTitle(eventNameString);
		toReturn.setDscription(eventDesc.getText().toString());
		return true;
	}

	@Override
	public void returnCallback() {
		if (callBack != null)
			callBack.done(toReturn, null);

	}

	public void setDate(int year, int month, int day) {
		if (cal == null)
			cal = new GregorianCalendar();
		cal.set(year, month, day, cal.get(cal.HOUR_OF_DAY), cal.get(cal.MINUTE));
		Log.i(getTag(), "date is set to " + cal.toString());
	}

	public void setTime(int hourOfDay, int minute) {
		if (cal == null)
			cal = new GregorianCalendar();
		cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),
				cal.get(Calendar.DAY_OF_MONTH), hourOfDay, minute);
		Log.i(getTag(), "date is set to " + cal.toString());
	}

	// *********************************************************************************************
	// DATE PICKER
	// *********************************************************************************************
	public static class DatePickerFragment extends DialogFragment implements
			DatePickerDialog.OnDateSetListener {

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {

			// Use the current date as the default date in the picker
			final Calendar c = Calendar.getInstance();
			int year = c.get(Calendar.YEAR);
			int month = c.get(Calendar.MONTH);
			int day = c.get(Calendar.DAY_OF_MONTH);

			// Create a new instance of DatePickerDialog and return it
			return new DatePickerDialog(getActivity(), this, year, month, day);
		}

		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			AddNewMissionDialog targetFragment = (AddNewMissionDialog) getTargetFragment();
			Button b = (Button) targetFragment.view
					.findViewById(R.id.event_pick_date_button);
			b.setText(dayOfMonth + "/" + (1 + monthOfYear) + "/" + year);
			targetFragment.setDate(year, monthOfYear, dayOfMonth);
		}

	}

	// *********************************************************************************************
	// TIME PICKER
	// *********************************************************************************************

	public static class TimePickerFragment extends DialogFragment implements
			TimePickerDialog.OnTimeSetListener {

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// Use the current time as the default values for the picker
			final Calendar c = Calendar.getInstance();
			int hour = c.get(Calendar.HOUR_OF_DAY);
			int minute = c.get(Calendar.MINUTE);

			// Create a new instance of TimePickerDialog and return it
			return new TimePickerDialog(getActivity(), this, hour, minute,
					DateFormat.is24HourFormat(getActivity()));
		}

		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			AddNewMissionDialog targetFragment = (AddNewMissionDialog) getTargetFragment();
			Button b = (Button) targetFragment.view
					.findViewById(R.id.event_pick_time);
			String hour = "";
			String sminute = "";
			if (hourOfDay > 9)
				hour += hourOfDay;
			else {
				hour += 0;
				hour += hourOfDay;
			}
			if (minute > 9)
				sminute += minute;
			else {
				sminute += 0;
				sminute += minute;
			}

			b.setText(hour + ":" + sminute);
			targetFragment.setTime(hourOfDay, minute);

		}

	}

	@Override
	public void extractArgs() {
		Bundle args = getArguments();
		callBack = args.getParcelable("callBack");

	}

	@Override
	public String getNegButtonText() {
		return "Cancel";
	}

	@Override
	public String getPosButtonText() {
		// TODO Auto-generated method stub
		return "Ok";
	}

	protected int reminderSelect=0;
	private int tempReminderSelect=0;
	final CharSequence[] choiceList = { "Off", "Only once", "Once a Day",
	"Once a week","Once a month","Once a year"};
	private  void showDialogButtonClick() {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle("Reminder Settings");
		builder.setSingleChoiceItems(choiceList, reminderSelect,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						Toast.makeText(getActivity(),
								"Select " + choiceList[which],
								Toast.LENGTH_SHORT).show();
						tempReminderSelect=which;
					}
				}).setCancelable(false).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
					TextView reminderValue=(TextView) view.findViewById(R.id.mission_tv_reminder_status);
					reminderValue.setText(choiceList[tempReminderSelect]);
					reminderSelect=tempReminderSelect;
						
					}
				}).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						//do nothing
					}
				});
		AlertDialog alert = builder.create();
		alert.show();
	}

}
