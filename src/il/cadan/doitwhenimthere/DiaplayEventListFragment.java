package il.cadan.doitwhenimthere;

import il.cadan.doitwhenimthere.bl.*;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle.Control;

import com.haarman.listviewanimations.itemmanipulation.OnDismissCallback;
import com.haarman.listviewanimations.itemmanipulation.SwipeDismissAdapter;
import com.haarman.listviewanimations.itemmanipulation.contextualundo.ContextualUndoAdapter;
import com.haarman.listviewanimations.itemmanipulation.contextualundo.ContextualUndoAdapter.CountDownFormatter;
import com.haarman.listviewanimations.itemmanipulation.contextualundo.ContextualUndoAdapter.DeleteItemCallback;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.ListFragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcel;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnDragListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TextView;

public class DiaplayEventListFragment extends Fragment implements
		DeleteItemCallback, OnDismissCallback, OnItemLongClickListener,
		OnItemClickListener {

	private Handler handler = new Handler();
	private Category curentCategory=Category.Personal;
	IController controller;
	MissionListBaseAdapter mAdapter;
	List<Integer> itemsToDelete = new ArrayList<Integer>();
	List<Mission> deletedMissionItems = new ArrayList<Mission>();
	int numberOfDeletedItems = 0;
	LinearLayout undoLayout;
	SwipeDismissAdapter adapter;
	Button undoDeleteBtn;
	TextView itemDeleted;
	FrameLayout empetyListLayoutMessage=null;
	int lastDeleted = 0;
	ListView eventsList;
	TextView personalTab;
	TextView workTab;
	TextView otherTab;
	TextView currentTabTextView;
	private Boolean searchclose = true;

	public static DiaplayEventListFragment getInstace(Bundle args) {
		DiaplayEventListFragment dlf = new DiaplayEventListFragment();
		dlf.setArguments(args);
		return dlf;
	}

	@SuppressLint("NewApi")
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		undoLayout = (LinearLayout) view.findViewById(R.id.undo_layout);
		undoLayout.setVisibility(View.GONE);
		undoDeleteBtn = (Button) undoLayout
				.findViewById(R.id.undo_row_undobutton);
		empetyListLayoutMessage=(FrameLayout) view.findViewById(R.id.empety_list_message_layout);
		undoDeleteBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				revertDelete();

			}
		});
		empetyListLayoutMessage.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				invokeAddNewMission();
				
			}
		});
		itemDeleted = (TextView) undoLayout.findViewById(R.id.undo_row_texttv);
		// Somewhere in your adapter creation code
		SwipeDismissAdapter adapter = new SwipeDismissAdapter(mAdapter, this);
		adapter.setAbsListView(eventsList);
		eventsList.setAdapter(adapter);
		// adapter.setDeleteItemCallback(this);
		eventsList.setOnItemLongClickListener(this);
		eventsList.setOnItemClickListener(this);
		registerViewModelReciever();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		/** Creating an array adapter to store the list of countries **/
		// set the view
		final View view = getActivity().getLayoutInflater().inflate(
				R.layout.events_list_layout, null, false);
		initializeTabs(view);

		// set the adapter to the list
		eventsList = (ListView) view.findViewById(R.id.missionsList);
		// attache a listener to the text box in order to filter the events
		final LinearLayout searchLayot = (LinearLayout) view
				.findViewById(R.id.serach_layout_id);
		EditText searchBox = (EditText) searchLayot
				.findViewById(R.id.event_search_edit_text);
		searchBox.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence cs, int arg1, int arg2,
					int arg3) {
				// When user changed the Text
				ListView eventList = (ListView) view
						.findViewById(R.id.missionsList);
				MissionListBaseAdapter adapter = (MissionListBaseAdapter) ((SwipeDismissAdapter) eventList
						.getAdapter()).getDecoratedBaseAdapter();
				adapter.getFilter().filter(cs);
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {

			}

			@Override
			public void afterTextChanged(Editable arg0) {

			}

		});

		final TextView openSerachTab = (TextView) view
				.findViewById(R.id.img_search_tap_open);

		openSerachTab.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				if (searchclose) {
					searchclose = false;
					searchLayot.setVisibility(View.VISIBLE);

				}

				else {
					searchclose = true;
					searchLayot.setVisibility(View.GONE);
					Handler hn = new Handler();

				}

				return false;
			}
		});
//		openSerachTab.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				if (searchclose) {
//					searchclose = false;
//					searchLayot.setVisibility(View.VISIBLE);
//
//				}
//
//				else {
//					searchclose = true;
//					searchLayot.setVisibility(View.GONE);
//				}
//			}
//
//		});
		controller = MainController.getInstance(getActivity());
		mAdapter = new MissionListBaseAdapter(getActivity(),
				new ArrayList<Mission>(controller.getMissionsList()));
		return view;
	}

	@Override
	public void deleteItem(int position) {
		lastDeleted = position;
		mAdapter.remove(position);
		mAdapter.notifyDataSetChanged();

	}

	// private ArrayList<Mission> getDummyList() {
	// ArrayList<Mission> ret = new ArrayList<Mission>();
	// Mission m;
	// for (int i = 0; i < 30; i++) {
	//
	// m = new Mission();
	// m.setTitle("Mission number " + (i + 1));
	// m.setDscription("Description mission number " + (i + 1));
	// ret.add(m);
	// }
	// for (int i = 0; i < 30; i++) {
	// m = new LocationMission();
	// m.setTitle("Location mission number " + (i + 1));
	// ret.add(m);
	// }
	// return ret;
	// }

	@Override
	public void onDismiss(AbsListView arg0, int[] reverseSortedPositions) {
		for (int i : reverseSortedPositions) {
			deletedMissionItems.add((Mission) mAdapter.getItem(i));
			mAdapter.remove(i);
		}
		updateUndoRow(reverseSortedPositions);
	}

	private void updateUndoRow(int[] toDelete) {
		for (int i : toDelete) {
			itemsToDelete.add(i);
			lastDeleted = i;
		}
		if (undoLayout.getVisibility() != View.VISIBLE) {
			undoLayout.setVisibility(View.VISIBLE);
			Animation animation = AnimationUtils.loadAnimation(getActivity(),
					R.anim.slide_in_left);
			undoLayout.setAnimation(animation);
			undoLayout.startAnimation(animation);
		}
		itemDeleted.setText(itemsToDelete.size() + " items was deleted");
		if (handler == null) {
			handler = new Handler();
		} else {
			handler.removeCallbacks(deletePermanentlyRunner);

		}
		handler.postDelayed(deletePermanentlyRunner, 5000);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		getActivity().unregisterReceiver(viewModelUpdatedReciever);
	}

	private void revertDelete() {
		mAdapter.revertRemove();
		List<Mission> l = mAdapter.getAll();
		createAdapter(l);
		itemsToDelete.clear();
		deletedMissionItems.clear();
		if (handler != null) {
			handler.removeCallbacks(deletePermanentlyRunner);
			handler = null;
		}
		undoLayout.setVisibility(View.GONE);
		adapter.notifyDataSetChanged();
	}

	private void createAdapter(List<Mission> l) {
		mAdapter = new MissionListBaseAdapter(getActivity(),
				new ArrayList<Mission>(l));
		adapter = new SwipeDismissAdapter(mAdapter, this);
		adapter.setAbsListView(eventsList);
		eventsList.setAdapter(adapter);
		adapter.notifyDataSetChanged(true);

	}

	private void deletePermanently() {
		undoLayout.setVisibility(View.GONE);
		List<Long> toDeleteIds = new ArrayList<Long>();
		for (Mission toDelete : deletedMissionItems) {
			toDeleteIds.add(toDelete.getId());
		}
		controller.deleteMany(toDeleteIds);
		mAdapter.removePermanently();
		itemsToDelete.clear();
		deletedMissionItems.clear();
		if(mAdapter.getAll().size()==0)
			empetyListLayoutMessage.setVisibility(View.VISIBLE);
		handler = null;
	}

	private class MyFormatCountDownCallback implements CountDownFormatter {

		@Override
		public String getCountDownString(long millisUntilFinished) {
			int seconds = (int) Math.ceil((millisUntilFinished / 1000.0));

			if (seconds > 0) {
				return getResources().getQuantityString(
						R.plurals.countdown_seconds, seconds, seconds);
			}
			return getString(R.string.countdown_dismissing);
		}
	}

	// this runnable is running every interval time.
	private final Runnable deletePermanentlyRunner = new Runnable() {
		@Override
		public void run() {
			deletePermanently();

		}

	};
	private BroadcastReceiver viewModelUpdatedReciever;

	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int pos,
			long arg3) {

		mAdapter.markItemAsDone(pos);
		Mission toUpdate = (Mission) mAdapter.getItem(pos);
		controller.updateMission(toUpdate);
		return true;
	}

	static DiaplayEventListFragment newInstance(Bundle args) {
		DiaplayEventListFragment f = new DiaplayEventListFragment();
		f.setArguments(args);
		return f;
	}

	private void registerViewModelReciever() {
		IntentFilter filterSend = new IntentFilter();
		filterSend.addAction(DoITConstance.VIEW_MODEL_UPDATED);
		viewModelUpdatedReciever = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				if (intent.getAction().equals(DoITConstance.VIEW_MODEL_UPDATED)) {
					List<Mission> mList=controller.getMissionsList();
					if(mList.size()==0)
						empetyListLayoutMessage.setVisibility(View.VISIBLE);
					else
					{
						empetyListLayoutMessage.setVisibility(View.GONE);
						createAdapter(mList);
					}
				}
			}
		};
		getActivity().registerReceiver(viewModelUpdatedReciever, filterSend);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View clickedView, int pos,
			long arg3) {
		if (clickedView instanceof ImageView)
			return;
		mAdapter.itemClicked(pos);

	}
	
	private void invokeAddNewMission()
	{
		Intent inti = new Intent();
		inti.setAction(DoITConstance.ACTION_CREATE_NEW_MISSION);
		if (getActivity() != null)
			getActivity().sendBroadcast(inti);
	}
	
	private void initializeTabs(View v)
	{
		personalTab=(TextView) v.findViewById(R.id.tab_personal);
		personalTab.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				switchTab(Category.Personal);
				
			}
		});
		otherTab=(TextView) v.findViewById(R.id.tab_other);
		otherTab.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				switchTab(Category.Other);
				
			}
		});
		workTab=(TextView) v.findViewById(R.id.tab_work);
		workTab.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				switchTab(Category.Work);
				
			}
		});
		currentTabTextView=personalTab;
		workTab.setText(CategoryManage.getCategoryValue(getActivity(), Category.Work));
		personalTab.setText(CategoryManage.getCategoryValue(getActivity(),Category.Personal));
		otherTab.setText(CategoryManage.getCategoryValue(getActivity(),Category.Other));
		
		workTab.setOnLongClickListener(new OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
					Bundle args=new Bundle();
					args.putString("category", workTab.getText().toString());
					args.putParcelable("callBack",new ApplicationCallaback<String>(){

						@Override
						public int describeContents() {
							// TODO Auto-generated method stub
							return 0;
						}

						@Override
						public void writeToParcel(Parcel dest, int flags) {
							// TODO Auto-generated method stub
							
						}

						@Override
						public void done(String retObj, Exception e) {
							if(e==null)
							{
								workTab.setText(retObj);
								CategoryManage.setCategoryValue(getActivity(), Category.Work, retObj);
							}
							
						}
						
					});
					ChangeCategoryNameDialog cc=ChangeCategoryNameDialog.getInstance(args);
					android.app.FragmentTransaction ft = getFragmentManager()
							.beginTransaction();
					ft.addToBackStack(null);
					cc.show(ft, "");
				return true;
			}
		});
		personalTab.setOnLongClickListener(new OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				Bundle args=new Bundle();
				args.putString("category", personalTab.getText().toString());
				args.putParcelable("callBack",new ApplicationCallaback<String>(){

					@Override
					public int describeContents() {
						// TODO Auto-generated method stub
						return 0;
					}

					@Override
					public void writeToParcel(Parcel dest, int flags) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void done(String retObj, Exception e) {
						if(e==null)
						{
							personalTab.setText(retObj);
							CategoryManage.setCategoryValue(getActivity(), Category.Personal, retObj);
						}
						
					}
					
				});
				ChangeCategoryNameDialog cc=ChangeCategoryNameDialog.getInstance(args);
				android.app.FragmentTransaction ft = getFragmentManager()
						.beginTransaction();
				ft.addToBackStack(null);
				cc.show(ft, "");
			return true;
			}
		});
		
		otherTab.setOnLongClickListener(new OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				Bundle args=new Bundle();
				args.putString("category", otherTab.getText().toString());
				args.putParcelable("callBack",new ApplicationCallaback<String>(){

					@Override
					public int describeContents() {
						// TODO Auto-generated method stub
						return 0;
					}

					@Override
					public void writeToParcel(Parcel dest, int flags) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void done(String retObj, Exception e) {
						if(e==null)
						{
							otherTab.setText(retObj);
							CategoryManage.setCategoryValue(getActivity(), Category.Other, retObj);
						}
						
					}
					
				});
				ChangeCategoryNameDialog cc=ChangeCategoryNameDialog.getInstance(args);
				android.app.FragmentTransaction ft = getFragmentManager()
						.beginTransaction();
				ft.addToBackStack(null);
				cc.show(ft, "");
			return true;
			}
		});
	}
	
	
	public void switchTab(Category category)
	{
		final float scale = getActivity().getResources().getDisplayMetrics().density;
		int bigHeight=(int) (55 * scale + 0.5f);
		if(curentCategory==category) return;
		currentTabTextView.setLayoutParams(new TableLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1f));
		switch (category) {
		case Personal:
			currentTabTextView=personalTab;
			curentCategory=Category.Personal;
			break;
			
		case Other:
			currentTabTextView=otherTab;
			curentCategory=Category.Other;
			break;
			
		case Work:
			currentTabTextView=workTab;
			curentCategory=Category.Work;
			break;

		default:
			break;
		}
		controller.changeCategory(category);
		currentTabTextView.setLayoutParams(new TableLayout.LayoutParams(30, bigHeight, 1f));
		
		
	}
	
	

}