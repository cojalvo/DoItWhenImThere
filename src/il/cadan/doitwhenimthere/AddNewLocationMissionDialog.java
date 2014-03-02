package il.cadan.doitwhenimthere;


import com.google.android.gms.maps.model.LatLng;

import android.os.Bundle;
import android.view.DragEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class AddNewLocationMissionDialog extends AddNewMissionDialog
{
	private LatLng location; 
	private String locationName;
	SeekBar sb;
	CheckBox cb;
	
	public static AddNewLocationMissionDialog getLocationMissionDialogInsance(Bundle args)
	{
		AddNewLocationMissionDialog amd=new AddNewLocationMissionDialog();
		amd.setArguments(args);
		return amd;
	}

	@Override
	public void processView() {
		super.processView();
		LinearLayout locationLayout=(LinearLayout) view.findViewById(R.id.mission_layout_location_layout);
		locationLayout.setVisibility(View.VISIBLE);
		TextView eltv=(TextView) locationLayout.findViewById(R.id.event_Location_text_view);
		if(eltv!=null)
		{
			eltv.setText(locationName);
		}
		 sb=(SeekBar) locationLayout.findViewById(R.id.seekBar1);
		 cb=(CheckBox) locationLayout.findViewById(R.id.cb_add_mission_notify_location);
		cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean isCheck) {
				sb.setEnabled(isCheck);
			}
		});
		sb.setEnabled(false);
		sb.setOnSeekBarChangeListener(new OnSeekBarChangeListener()
		{

		    @Override
		    public void onStopTrackingTouch(SeekBar seekBar)
		    {
			// TODO Auto-generated method stub

		    }

		    @Override
		    public void onStartTrackingTouch(SeekBar seekBar)
		    {
			// TODO Auto-generated method stub

		    }

		    @Override
		    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
		    {
		    	cb.setText("Notify in " + progress + "m distance from here"); 
		    }
		});
	}
	

	@Override
	public Boolean validateAndCreateOutput() {
	
		if(super.validateAndCreateOutput())
		{
			LocationMission lm=new LocationMission(toReturn);
			if(!cb.isChecked()) lm.setRadiusNotification(-1);
			else
				lm.setRadiusNotification(sb.getProgress());
			lm.setLocationName(locationName);
			toReturn=lm;
			return true;
		}
		return false;
	}

	@Override
	public void extractArgs() {
		super.extractArgs();
		Bundle args=getArguments();
		locationName=args.getString("locationName");
	}	
	
}
