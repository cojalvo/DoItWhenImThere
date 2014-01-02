package il.cadan.doitwhenimthere;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.google.android.gms.plus.model.people.Person.Collection;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.WrapperListAdapter;

public class MissionListBaseAdapter extends BaseAdapter implements Filterable {

	private Activity context;
	private List<AdapterMissionWrapper> firstState;
	private List<AdapterMissionWrapper> missionList;
	private List<AdapterMissionWrapper> filteredMissionList;
	private LayoutInflater l_Inflater;
	private Typeface tf;
	private Typeface tfBold;
	int expand = -1;

	MissionListBaseAdapter(Activity context, ArrayList<Mission> arrayList) {
		this.missionList = createWrappedMissionList(arrayList);
		this.filteredMissionList = this.missionList;
		this.context = context;
		this.l_Inflater = context.getLayoutInflater();
		// Font path
		String fontPath = "fonts/Tw Cen MT.ttf";
		String fontPathBold = "fonts/Tw Cen MT Bold.ttf";

		// Loading Font Face
		tf = Typeface.createFromAsset(context.getAssets(), fontPath);
		tfBold = Typeface.createFromAsset(context.getAssets(), fontPathBold);
	}

	private List<AdapterMissionWrapper> createWrappedMissionList(
			List<Mission> mList) {
		List<AdapterMissionWrapper> wrapedList = new ArrayList<AdapterMissionWrapper>();
		List<Mission> pastMission = new ArrayList<Mission>();
		Calendar today = Calendar.getInstance();
		today.setTime(new Date());
		// today;
		today.set(today.get(Calendar.YEAR), today.get(Calendar.MONTH),
				today.get(Calendar.DAY_OF_MONTH), 0, 0, 0);

		Calendar tomorow = Calendar.getInstance();
		tomorow.setTime(new Date(today.getTime().getTime()
				+ DateUtils.DAY_IN_MILLIS));

		Calendar afterTomorow = Calendar.getInstance();
		afterTomorow.setTime(new Date(tomorow.getTime().getTime()
				+ DateUtils.DAY_IN_MILLIS));

		AdapterMissionWrapper todayWrapper = new AdapterMissionWrapper();
		todayWrapper.setIsSection(true);
		todayWrapper.setSectionText("Today");
		AdapterMissionWrapper tomorowWrapper = new AdapterMissionWrapper();
		tomorowWrapper.setIsSection(true);
		tomorowWrapper.setSectionText("Tomorow");
		AdapterMissionWrapper somtimesWrapper = new AdapterMissionWrapper();
		somtimesWrapper.setIsSection(true);
		somtimesWrapper.setSectionText("Sometime");
		AdapterMissionWrapper past = new AdapterMissionWrapper();
		past.setIsSection(true);
		past.setSectionText("Past");
		Collections.sort(mList);
		Boolean todayEntered = false;
		Boolean tomorowEntered = false;
		int index = 0;
		for (int i = index; i < mList.size(); i++) {
			if (mList.get(i).getStartTime() == null)
				break;
			if (mList.get(i).getStartTime().before(today.getTime())) {
				pastMission.add(mList.get(i));
				index++;
			} else
				break;
		}
		for (int i = index; i < mList.size(); i++) {
			if (mList.get(i).getStartTime() == null)
				break;
			if (mList.get(i).getStartTime().before(tomorow.getTime())) {
				if (!todayEntered) {
					wrapedList.add(todayWrapper);
					todayEntered = true;
				}
				AdapterMissionWrapper toAdd = new AdapterMissionWrapper();
				toAdd.setMission(mList.get(i));
				toAdd.setIsSection(false);
				wrapedList.add(toAdd);
				index++;
			} else
				break;
		}
		for (int i = index; i < mList.size(); i++) {
			if (mList.get(i).getStartTime() == null) {
				break;
			}
			if (mList.get(i).getStartTime().before(afterTomorow.getTime())) {
				if (!tomorowEntered) {
					tomorowEntered = true;
					wrapedList.add(tomorowWrapper);
				}
				AdapterMissionWrapper toAdd = new AdapterMissionWrapper();
				toAdd.setMission(mList.get(i));
				toAdd.setIsSection(false);
				wrapedList.add(toAdd);
				index++;
			} else
				break;
		}
	
		if (index < mList.size())
			wrapedList.add(somtimesWrapper);
		for (int i = index; i < mList.size(); i++) {
			AdapterMissionWrapper toAdd = new AdapterMissionWrapper();
			toAdd.setMission(mList.get(i));
			toAdd.setIsSection(false);
			wrapedList.add(toAdd);
		}
		if (pastMission.size() > 0)
			wrapedList.add(past);
		for (Mission mission : pastMission) {

			AdapterMissionWrapper toAdd = new AdapterMissionWrapper();
			toAdd.setMission(mission);
			toAdd.setIsSection(false);
			wrapedList.add(toAdd);
		}
		return wrapedList;
	}

	@Override
	public int getCount() {
		return filteredMissionList.size();
	}

	@Override
	public Object getItem(int pos) {
		return filteredMissionList.get(pos).getMission();
	}

	@Override
	public long getItemId(int pos) {
		return pos;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = context.getLayoutInflater();
		ViewHolder holder;
		final Mission currentMission = filteredMissionList.get(position)
				.getMission();
		if (filteredMissionList.get(position).getIsSection()) {
			View secstion = inflater.inflate(R.layout.category_list_item, null);
			TextView sec = (TextView) secstion
					.findViewById(R.id.category_title);
			sec.setTypeface(tfBold);
			sec.setText(filteredMissionList.get(position).getSectionText());
			return secstion;
		}
		if (convertView == null || (ViewHolder) convertView.getTag() == null) {
			convertView = inflater.inflate(R.layout.mission_layout, null);
			holder = new ViewHolder();
			holder.txt_itemDescription = (TextView) convertView
					.findViewById(R.id.mission_description);
			holder.txt_itemTitle = (TextView) convertView
					.findViewById(R.id.mission_title);
			holder.txt_date = (TextView) convertView
					.findViewById(R.id.mission_date);
			holder.txt_date.setVisibility(View.GONE);
			holder.btn_navigate = (ImageView) convertView
					.findViewById(R.id.event_navigation_button);
			holder.btn_navigate.setVisibility(View.GONE);
			convertView.setTag(holder);
			holder.btn_edit = (ImageView) convertView
					.findViewById(R.id.event_edit_mission);
			holder.txt_locationName=(TextView) convertView.findViewById(R.id.tv_location_name);
			holder.txt_locationName.setVisibility(View.GONE);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		if (currentMission.getStartTime() != null)
			holder.txt_date.setText(ParsingHelper.fromDateToString(
					currentMission.getStartTime(), "dd/MM/yyyy HH:mm"));
		holder.txt_itemDescription.setText(currentMission.getDscription());
		// mark as done
		holder.txt_itemTitle.setText(currentMission.getTitle());
		holder.txt_itemTitle.setTypeface(tfBold);
		holder.txt_itemDescription.setTypeface(tf);
		if (position == expand) {
			if (currentMission.getStartTime() != null)
				holder.txt_date.setVisibility(View.VISIBLE);
			else
				holder.txt_date.setVisibility(View.INVISIBLE);
			holder.txt_itemDescription.setVisibility(View.VISIBLE);
			holder.btn_edit.setVisibility(View.VISIBLE);
			if(currentMission instanceof LocationMission)
			{
				holder.btn_navigate.setVisibility(View.VISIBLE);
				holder.txt_locationName.setVisibility(View.VISIBLE);
			}
				
		} else {
			holder.btn_edit.setVisibility(View.GONE);
			holder.btn_navigate.setVisibility(View.GONE);
			holder.txt_date.setVisibility(View.GONE);
			holder.txt_locationName.setVisibility(View.GONE);
			holder.txt_itemDescription.setVisibility(View.GONE);
		}
		if (currentMission.getDone()) {
			holder.txt_itemTitle.setPaintFlags(holder.txt_itemTitle
					.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
			holder.txt_itemDescription.setPaintFlags(holder.txt_itemDescription
					.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

		} else {
			holder.txt_itemTitle.setPaintFlags(0);
			holder.txt_itemDescription.setPaintFlags(0);
		}
		holder.btn_edit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				invokeUpdateMission(currentMission.getId());
				Toast.makeText(context, "Edit Btn was clicked", 300).show();

			}
		});
		if ((currentMission instanceof LocationMission)) {
			LocationMission navigateTo = (LocationMission) currentMission;
			holder.btn_navigate.setTag(position);
			holder.txt_locationName.setText(" "+navigateTo.getLocationName());
			holder.btn_navigate.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// navigate to the the event;
					Integer position = (Integer) v.getTag();
					LocationMission navigateTo = (LocationMission) filteredMissionList
							.get(position).getMission();
					Toast.makeText(
							context,
							"navigate Btn was clicked at item in posigion "
									+ position, 300).show();
					invokeNavigate(currentMission.getId());
					
					// Controller controller = Controller.getInstance(null);
					// controller.closePreferanceView();
					// controller.navigateTo(navigateTo.getParseId());
				}
			});

		}
		return convertView;
	}

	private void invokeUpdateMission(long id) {
		Intent inti = new Intent();
		inti.putExtra("id", id);
		inti.setAction(DoITConstance.MISSIONL_UPDATED);
		if (context != null)
			context.sendBroadcast(inti);
	}
	
	private void invokeNavigate(long id) {
		Intent inti = new Intent();
		inti.putExtra("id", id);
		inti.setAction(DoITConstance.ACTION_NAVIGATE_CLICKED);
		if (context != null)
			context.sendBroadcast(inti);
	}

	@Override
	public Filter getFilter() {
		return new Filter() {

			@Override
			protected void publishResults(CharSequence constraint,
					FilterResults results) {

				filteredMissionList = (ArrayList<AdapterMissionWrapper>) results.values;
				notifyDataSetChanged();
			}

			@Override
			protected FilterResults performFiltering(CharSequence constraint) {
				FilterResults results = new FilterResults();
				// If there's nothing to filter on, return the original data for
				// your list
				if (constraint == null || constraint.length() == 0) {
					results.values = missionList;
					results.count = missionList.size();
				} else {
					/* filtering the list */
					ArrayList<AdapterMissionWrapper> filteredData = new ArrayList<AdapterMissionWrapper>();
					String[] titleWords;
					String[] descWords;
					for (AdapterMissionWrapper curr : missionList) {
						// compare the Char Sequence i received to the event
						// title
						if (curr.getIsSection()) {
							filteredData.add(curr);
							continue;
						}
						titleWords = curr.getMission().getTitle().split(" ");
						for (int i = 0; i < titleWords.length; i++) {
							if (titleWords[i].startsWith((String) constraint)) {
								filteredData.add(curr);
							}
						}
						if (!filteredData.contains(curr)) {
							// compare the description words
							String desc = curr.getMission().getDscription();
							if (desc != null) {
								descWords = desc.split(" ");
								for (int i = 0; i < descWords.length; i++) {
									if (descWords[i]
											.startsWith((String) constraint)) {
										filteredData.add(curr);
									}
								}
							}
						}
					}
					results.values = filteredData;
					results.count = filteredData.size();
				}
				return results;
			}
		};
	}

	private String getDistanceString(long eventId) {
		float dist = 150;// controller.getMyDistanceFrom(eventId);
		if (dist > 0) {
			String unit;
			String finalDist;
			if (dist > 1000) {
				unit = "ק'מ";

				finalDist = String.format("%.2f", dist / 1000);
			} else {
				unit = "מטרים";
				finalDist = String.format("%.0f", dist);
			}

			return ("האירוע נמצא: " + finalDist + " " + unit + " " + "ממני");
		}
		return "אתה נמצא באירוע";
	}

	public void markItemAsDone(int pos) {
		Mission m = filteredMissionList.get(pos).getMission();
		if (m != null)
			m.setDone(!m.getDone());
		this.notifyDataSetChanged();
	}

	static class ViewHolder {
		TextView txt_itemDescription;
		TextView txt_itemTitle;
		TextView txt_date;
		TextView txt_locationName;
		ImageView btn_navigate;
		ImageView btn_edit;
	}

	public void remove(int pos) {
		if (pos == expand)
			expand = -1;
		AdapterMissionWrapper toRemoveItem = filteredMissionList.get(pos);
		if (firstState == null)
			firstState = new ArrayList<AdapterMissionWrapper>(missionList);
		filteredMissionList.remove(toRemoveItem);
		missionList.remove(toRemoveItem);
		notifyDataSetChanged();
	}

	public void revertRemove() {
		if (firstState == null)
			return;
		missionList = firstState;
		filteredMissionList = firstState;
		firstState = null;
		notifyDataSetChanged();
	}

	public void removePermanently() {
		firstState = null;
		notifyDataSetChanged();
	}

	public List<Mission> getAll() {
		List<Mission> toReturn = new ArrayList<Mission>();
		for (AdapterMissionWrapper m : missionList) {
			if (!m.getIsSection())
				toReturn.add(m.getMission());
		}
		return toReturn;
	}

	public void itemClicked(int pos) {
		if (expand == pos)
			expand = -1;
		else {
			expand = pos;
		}
		notifyDataSetChanged();
	}
}
