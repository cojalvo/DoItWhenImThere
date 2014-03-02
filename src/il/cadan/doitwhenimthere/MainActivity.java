package il.cadan.doitwhenimthere;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import il.cadan.doitwhenimthere.R;
import il.cadan.doitwhenimthere.bl.ApplicationCallaback;
import il.cadan.doitwhenimthere.bl.IController;
import il.cadan.doitwhenimthere.bl.MainController;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.GoogleAnalytics;
import com.google.analytics.tracking.android.MapBuilder;
import com.google.analytics.tracking.android.Tracker;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.parse.Parse;
import com.parse.ParseUser;

import android.R.menu;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.DialogInterface.OnClickListener;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.DrawableContainer;
import android.location.Address;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Vibrator;
import android.speech.RecognizerIntent;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.SearchView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnMapClickListener,
		OnMapLongClickListener, OnMarkerClickListener {
	private MapManager mapManager = null;
	private IController controller;
	private DrawerLayout mDrawerLayout;
	private BroadcastReceiver missionUpdatedReciever;
	private BroadcastReceiver createNewMissionReciever;
	private ActionBarDrawerToggle mDrawerToggle;
	private MenuItem addMissionMenuItem;
	private MenuItem quickMissionMenuItem;
	private MenuItem searchMenuItem;
	protected boolean inExitProcess;
	private Tracker tracker;
	private GoogleAnalytics gaInstance;
	private boolean isDrawerOpen;
	private Vibrator vibrator = null;
	private BroadcastReceiver navigateReciever;
	private static final int REQUEST_CODE = 1234;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        Parse.initialize(this, "wusl9kWw4Uv1XiYzFnVqg5zwG2N9TPG8ViafwaWV",
				"66gq6FOSuDUGMbxWSuGro4uuV0qrlJvMYVJoaYJZ");
		setContentView(R.layout.activity_main);
		gaInstance = GoogleAnalytics.getInstance(this);
	    tracker = gaInstance.getTracker("UA-48558343-1");
		vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		handleIntent(getIntent());
		initializeDrawerLayout();
		initMapManager();
		controller = MainController.getInstance(this);
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		ft.replace(R.id.content_frame, new DiaplayEventListFragment());
		ft.commit();
		registerMissionUpdateReciever();
		registerNavigateReciever();
		registerCreateNewMissionReciever();
		minimizeKeyboard();
		controller.updateViewModelInBackground();
		controller.startLocationNofificationService();
		 // gets the activity's default ActionBar
	    ActionBar actionBar = getActionBar();
	    actionBar.show();
	    actionBar.setDisplayHomeAsUpEnabled(true);

	}

	private void minimizeKeyboard() {
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		controller.updateViewModelInBackground();
	}

	@SuppressLint("NewApi")
	private void initializeDrawerLayout() {
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
		// ActionBarDrawerToggle ties together the the proper interactions
		// between the sliding drawer and the action bar app icon
		mDrawerToggle = new ActionBarDrawerToggle(this, /* host Activity */
		mDrawerLayout, /* DrawerLayout object */
		R.drawable.ic_drawer, /* nav drawer image to replace 'Up' caret */
		R.string.drawer_open, /* "open drawer" description for accessibility */
		R.string.drawer_close /* "close drawer" description for accessibility */
		) {
			public void onDrawerClosed(View view) {
				invalidateOptionsMenu(); // creates call to
											// onPrepareOptionsMenu()
				// mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
				isDrawerOpen = false;
				getActionBar().setTitle(R.string.todo_list);
			}

			public void onDrawerOpened(View drawerView) {
				invalidateOptionsMenu(); // creates call to
											// onPrepareOptionsMenu()
				isDrawerOpen = true;
				getActionBar().setTitle(R.string.todo_on_the_map);
			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);
		// getActionBar().setBackgroundDrawable(Dra)
		getActionBar().setBackgroundDrawable(
				getResources().getDrawable(R.drawable.section_background));

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(missionUpdatedReciever);
		unregisterReceiver(navigateReciever);
		unregisterReceiver(createNewMissionReciever);
		mapManager.unRegisterRecivers();
	}

	private void initMapManager() {
		mapManager = new MapManager(this, ((MapFragment) getFragmentManager()
				.findFragmentById(R.id.map)).getMap(),
				GoogleMap.MAP_TYPE_NORMAL);
		mapManager.setOnMapLongClickListener(this);
		mapManager.setOnMarkerClickListener(this);
		mapManager.setOnMapClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu items for use in the action bar
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		addMissionMenuItem = menu.getItem(0);
		searchMenuItem = menu.getItem(1);
		quickMissionMenuItem = menu.getItem(2);
		// Associate searchable configuration with the SearchView
		SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		SearchView searchView = (SearchView) menu.findItem(R.id.search)
				.getActionView();
		searchView.setSearchableInfo(searchManager
				.getSearchableInfo(getComponentName()));
		if (isDrawerOpen) {
			addMissionMenuItem.setVisible(false);
			searchMenuItem.setVisible(true);
			quickMissionMenuItem.setVisible(false);
			searchView.setQueryHint("Search address");

		} else {
			addMissionMenuItem.setVisible(true);
			searchMenuItem.setVisible(false);
			quickMissionMenuItem.setVisible(true);
			minimizeKeyboard();
		}

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// if the user press the back button that doExit will invoke
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			if (isDrawerOpen) {
				mDrawerLayout.closeDrawers();
				return true;
			}
			// doExit();

		}
		return super.onKeyDown(keyCode, event);
	}

	//
	// Exit the application will ask the user if he sure.
	//
	private void doExit() {

		AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

		alertDialog.setPositiveButton("Yes", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				inExitProcess = true;
				finish();
			}
		});

		alertDialog.setNegativeButton("No", null);

		alertDialog.setMessage(R.string.are_you_sure_you_want_to_exit_);
		alertDialog.setTitle(" ");
		alertDialog.setIcon(R.drawable.ic_launcher);
		alertDialog.show();
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	@SuppressLint("NewApi")
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Pass the event to ActionBarDrawerToggle, if it returns
		// true, then it has handled the app icon touch event
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		// Handle your other action bar items...
		switch (item.getItemId()) {
		case R.id.bt_add_mission:
			addNewMissionClicked();
			break;
		case R.id.bt_voice_add_mission:
			startVoiceRecognitionActivity();
			break;
		case R.id.action_settings:
			if (ParseUser.getCurrentUser() != null) {
				ParseUser.logOut();
			} else
				loginProcces();
			break;
		case R.id.action_backup:
			backup();
			break;
		case R.id.action_restore:
			restore();
			break;
		default:
			break;
		}

		return super.onOptionsItemSelected(item);
	}

	/*
	 * Open the new mission dialog and get the result in the callBack
	 */
	private void addNewMissionClicked() {
		tracker.send(MapBuilder.createEvent("Main Activity", "New event was created", "MainActivity", null).build());
		android.app.FragmentTransaction ft = getFragmentManager()
				.beginTransaction();
		ft.addToBackStack(null);

		// Create and show the dialog.
		Bundle args = new Bundle();
		// add the callBck to the argument bundle
		args.putParcelable("callBack", new ApplicationCallaback<Mission>() {

			@Override
			public int describeContents() {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public void writeToParcel(Parcel arg0, int arg1) {
				// TODO Auto-generated method stub

			}

			@Override
			public void done(Mission retObj, Exception e) {
				if (retObj != null)
					Toast.makeText(
							getApplicationContext(),
							R.string.new_mission_was_created_
									+ retObj.getTitle(), 3000).show();
				controller.addMission(retObj);

			}
		});
		AddNewMissionDialog d = AddNewMissionDialog.getInstance(args);

		d.show(ft, "");
	}
	
	  @Override
	  public void onStart() {
	    super.onStart();
	    EasyTracker.getInstance(this).activityStart(this);  // Add this method.
	  }
	  
	  
	  @Override
	  public void onStop() {
	    super.onStop();
	    EasyTracker.getInstance(this).activityStop(this);  // Add this method.
	  }

	private void registerMissionUpdateReciever() {
		IntentFilter filterSend = new IntentFilter();
		filterSend.addAction(DoITConstance.MISSIONL_UPDATED);
		missionUpdatedReciever = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				/*
				 * Bundle args = getArguments(); title =
				 * args.getString("title"); description =
				 * args.getString("description"); withDateAndTime =
				 * args.getBoolean("withDateAndTime"); if (withDateAndTime) {
				 * Long d = args.getLong("dateAndTime"); dateAndTime = new
				 * Date(d); String freq = args.getString("reminderFreq");
				 * reminderFreq = ReminderFrequency.valueOf(freq); }
				 */
				if (intent.getAction().equals(DoITConstance.MISSIONL_UPDATED)) {
					tracker.send(MapBuilder.createEvent("Main Activity", "event was updated", "MainActivity", null).build());
					final Long id = intent.getLongExtra("id", -1);
					Bundle args = new Bundle();
					final Mission toUpdate = controller.getMission(id);
					args = new Bundle();
					args.putString("title", toUpdate.getTitle());
					args.putString("description", toUpdate.getDscription());
					if (toUpdate.getStartTime() == null)
						args.putBoolean("withDateAndTime", false);
					else {
						args.putBoolean("withDateAndTime", true);
						args.putLong("dateAndTime", toUpdate.getStartTime()
								.getTime());
						args.putString("reminderFreq", toUpdate
								.getReminderFrequency().toString());
					}
					if (toUpdate instanceof LocationMission) {
						LocationMission lm = (LocationMission) toUpdate;
						args.putInt("radius", lm.getRadiusNotification());
						args.putBoolean("notifyLocation",
								lm.getRadiusNotification() != -1);
						args.putString("locationName", lm.getLocationName());
						args.putDouble("lat", lm.getLocation().latitude);
						args.putDouble("lng", lm.getLocation().longitude);
					}
					args.putParcelable("callBack",
							new ApplicationCallaback<Mission>() {

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
								public void done(Mission retObj, Exception e) {
									if (e == null) {
										retObj.setId(id);
										// if the mission was done than the
										// state wil be as is.
										retObj.setDone(toUpdate.getDone());
										if (retObj instanceof LocationMission) {
											LocationMission lm = (LocationMission) toUpdate;
											LocationMission retLm = (LocationMission) retObj;
											retLm.setLocationName(lm
													.getLocationName());
											retLm.setLocation(lm.getLocation());
										}
										controller.updateMission(retObj);
									}
								}
							});
					if (toUpdate instanceof LocationMission) {
						UpdateLocationMissionDialog ulm = UpdateLocationMissionDialog
								.getInstance(args);
						ulm.show(getFragmentManager(), "");
					} else {
						UpdateMissionDialog umd = UpdateMissionDialog
								.getInstance(args);
						umd.show(getFragmentManager(), "");
					}
				}

			}
		};
		this.registerReceiver(missionUpdatedReciever, filterSend);
	}

	private void registerCreateNewMissionReciever() {
		IntentFilter filterSend = new IntentFilter();
		filterSend.addAction(DoITConstance.ACTION_CREATE_NEW_MISSION);
		createNewMissionReciever = new BroadcastReceiver() {

			@Override
			public void onReceive(Context arg0, Intent intent) {
				if (intent.getAction().equals(
						DoITConstance.ACTION_CREATE_NEW_MISSION)) {
					addNewMissionClicked();
				}
			}
		};
		this.registerReceiver(createNewMissionReciever, filterSend);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {

		if (ParseUser.getCurrentUser() == null) {
			menu.getItem(3).setTitle("Sign In");
			menu.getItem(3).setIcon(R.drawable.ic_open);
			menu.getItem(4).setVisible(false);
			menu.getItem(5).setVisible(false);
		} else {
			menu.getItem(3).setTitle("Sign Out");
			menu.getItem(3).setIcon(R.drawable.ic_add_mission_black);
			menu.getItem(4).setVisible(true);
			menu.getItem(5).setVisible(true);
		}
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onMarkerClick(Marker marker) {
		marker.showInfoWindow();
		return true;
	}

	@Override
	public void onMapLongClick(final LatLng point) {
		vibrator.vibrate(50);
		MessageHalper.showProgressDialog(getString(R.string.locating_your_addres_), this);
		mapManager.getAddresFromLatLng(point,
				new ApplicationCallaback<String>() {

					@Override
					public void writeToParcel(Parcel arg0, int arg1) {
						// TODO Auto-generated method stub

					}

					@Override
					public int describeContents() {
						return 0;
					}

					@Override
					public void done(String retObj, Exception e) {
						tracker.send(MapBuilder.createEvent("Main Activity", "New location event was created", "MainActivity", null).build());
						MessageHalper.closeProggresDialog();
						//Toast.makeText(MainActivity.this, retObj, 500).show();

						addNewMissionClicked(point, retObj);

					}
				});

	}

	/*
	 * Open the new mission dialog and get the result in the callBack
	 */
	private void addNewMissionClicked(final LatLng ll, String locationName) {
		android.app.FragmentTransaction ft = getFragmentManager()
				.beginTransaction();
		ft.addToBackStack(null);

		// Create and show the dialog.
		Bundle args = new Bundle();
		// add the callBck to the argument bundle
		args.putString("locationName", locationName);
		args.putParcelable("callBack", new ApplicationCallaback<Mission>() {

			@Override
			public int describeContents() {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public void writeToParcel(Parcel arg0, int arg1) {
				// TODO Auto-generated method stub

			}

			@Override
			public void done(Mission retObj, Exception e) {
				if (retObj != null)
					Toast.makeText(
							getApplicationContext(),
							"New mission was created in name: "
									+ retObj.getTitle(), 3000).show();
				LocationMission lm = (LocationMission) retObj;
				lm.setLocation(ll);
				controller.addMission(retObj);

			}
		});
		AddNewLocationMissionDialog d = AddNewLocationMissionDialog
				.getLocationMissionDialogInsance(args);
		d.show(ft, "");
	}

	@Override
	public void onMapClick(LatLng point) {
		minimizeKeyboard();

	}

	private void registerNavigateReciever() {
		IntentFilter filterSend = new IntentFilter();
		filterSend.addAction(DoITConstance.ACTION_NAVIGATE_CLICKED);
		navigateReciever = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				if (intent.getAction().equals(
						DoITConstance.ACTION_NAVIGATE_CLICKED)) {
					Long id = intent.getLongExtra("id", -1);
					if (id != -1) {
						mDrawerLayout.openDrawer(findViewById(R.id.menu_frame));
						mapManager.moveCameraToMission(id);
					}

				}
			}
		};
		this.registerReceiver(navigateReciever, filterSend);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		handleIntent(intent);
	}

	private void handleIntent(Intent intent) {

		if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			tracker.send(MapBuilder.createEvent("Main Activity", "user searched for addres", "MainActivity", null).build());
			String query = intent.getStringExtra(SearchManager.QUERY);
			// Toast.makeText(this, query, 300).show();
			MessageHalper.showProgressDialog("Searching for addres..", this);
			mapManager.getAddressFromString(query,
					new ApplicationCallaback<List<Address>>() {

						@Override
						public void writeToParcel(Parcel dest, int flags) {
							// TODO Auto-generated method stub

						}

						@Override
						public int describeContents() {
							// TODO Auto-generated method stub
							return 0;
						}

						@Override
						public void done(final List<Address> retObj, Exception e) {
							MessageHalper.closeProggresDialog();
							if (retObj != null && e == null) {
								Toast.makeText(
										MainActivity.this,
										"Map manager return: " + retObj.size()
												+ " results", 300).show();
								if (retObj.size() > 0) {
									ArrayList<String> sResult = new ArrayList<String>();
									for (Address address : retObj) {
										String a = fromAddresToString(address);
										sResult.add(a);
									}
									AlertDialog.Builder builder = new AlertDialog.Builder(
											MainActivity.this);
									builder.setTitle("Search Results").setIcon(
											R.drawable.ic_marker);
									builder.setCustomTitle(findViewById(R.layout.search_result_layout));
									final CharSequence[] cs = sResult
											.toArray(new CharSequence[sResult
													.size()]);
									builder.setItems(
											cs,
											new DialogInterface.OnClickListener() {
												public void onClick(
														DialogInterface dialog,
														int item) {
													Toast.makeText(
															getApplicationContext(),
															cs[item],
															Toast.LENGTH_SHORT)
															.show();
													Address toNavigate = retObj
															.get(item);
													LatLng des = new LatLng(
															toNavigate
																	.getLatitude(),
															toNavigate
																	.getLongitude());
													minimizeKeyboard();
													mapManager
															.moveCameraLocation(des);
												}
											}).show();
									// Address a =retObj.get(0);
									// LatLng target=new
									// LatLng(a.getLatitude(),a.getLongitude());
									// mapManager.moveCameraLocation(target);

								}
							}

						}
					});
		}
		// else if(intent.getAction()!=null &&
		// intent.getAction().equals("il.cadan.do"))
		// {
		// Toast.makeText(this, "done was clicked", 300).show();
		// }
	}

	private String fromAddresToString(Address address) {
		if (address == null)
			return "";
		String firstPart = "";
		String secondPart = "";
		String thirdPart = "";
		firstPart = address.getMaxAddressLineIndex() > 0 ? address
				.getAddressLine(0) : "";
		secondPart = address.getLocality();
		if (secondPart == null)
			secondPart = "";
		thirdPart = address.getCountryName();
		if (thirdPart == null)
			thirdPart = "";

		String addressText = String.format("%s, %s, %s",
		// If there's a street address, add it
				firstPart,
				// Locality is usually a city
				secondPart,
				// The country of the address
				thirdPart);
		return addressText;
	}

	private void startVoiceRecognitionActivity() {
		tracker.send(MapBuilder.createEvent("Main Activity", "New quick event was created", "MainActivity", null).build());
		Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
				RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
		intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Add Mission...");
		startActivityForResult(intent, REQUEST_CODE);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
			// Populate the wordsList with the String values the recognition
			// engine thought it heard
			ArrayList<String> matches = data
					.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
			Mission m = new Mission();
			m.setTitle(matches.get(0));
			controller.addMission(m);
			Toast.makeText(this, matches.get(0), 300).show();

		}
		super.onActivityResult(requestCode, resultCode, data);
		super.onActivityResult(requestCode, resultCode, data);
	}

	private void loginProcces() {
		tracker.send(MapBuilder.createEvent("Main Activity", "user was loged in", "", null).build());
		ParseUser currentUser = ParseUser.getCurrentUser();
		if (currentUser == null) {
			android.app.FragmentTransaction ft = getFragmentManager()
					.beginTransaction();
			ft.addToBackStack(null);

			// Create and show the dialog.
			Bundle args = new Bundle();
			// add the callBck to the argument bundle
			args.putParcelable("callBack",
					new ApplicationCallaback<ParseUser>() {

						@Override
						public int describeContents() {
							// TODO Auto-generated method stub
							return 0;
						}

						@Override
						public void writeToParcel(Parcel arg0, int arg1) {
							// TODO Auto-generated method stub

						}

						@Override
						public void done(ParseUser currentUser, Exception e) {
							if (currentUser != null) {

							}
						}
					});
			LoginDialog d = LoginDialog.getInstance(args);
			d.show(ft, "");

		}
	}

	private void backup() {
		MessageHalper.showProgressDialog(getString(R.string.backup_), this);
		tracker.send(MapBuilder.createEvent("Main Activity", "user backup his data", "MainActivity", null).build());
		controller.backup(new ApplicationCallaback<Integer>() {

			@Override
			public void writeToParcel(Parcel arg0, int arg1) {
				// TODO Auto-generated method stub

			}

			@Override
			public int describeContents() {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public void done(Integer retObj, Exception e) {
				MessageHalper.closeProggresDialog();

			}
		});
	}

	private void restore() {
		MessageHalper.showProgressDialog(getString(R.string.restoring_), this);
		tracker.send(MapBuilder.createEvent("Main Activity", "user restore his data", "MainActivity", null).build());
		controller.restore(new ApplicationCallaback<Integer>() {

			@Override
			public void writeToParcel(Parcel arg0, int arg1) {
				// TODO Auto-generated method stub

			}

			@Override
			public int describeContents() {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public void done(Integer retObj, Exception e) {
				MessageHalper.closeProggresDialog();

			}
		});
	}
}