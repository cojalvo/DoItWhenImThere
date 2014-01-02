package il.cadan.doitwhenimthere;

import java.util.List;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceManager;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class PrefsFragment extends PreferenceFragment {
	public static final String ACTION_INTENT = "il.ac.shenkar.CHANGE";
	OnPreferenceSelectedListener mCallback;
	private Context context;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
//		context = getActivity().getBaseContext();
//		// update the full name and the profile picture of current user
//		// Load the preferences from an XML resource
//		addPreferencesFromResource(R.xml.preference);
//
//		Preference testShow = findPreference("show_events");
//		testShow.setOnPreferenceClickListener(new OnPreferenceClickListener() {
//
//			@Override
//			public boolean onPreferenceClick(Preference preference) {
//				android.app.FragmentTransaction transaction = getFragmentManager()
//						.beginTransaction();
//				Fragment fragment = getFragmentManager().findFragmentByTag(
//						"dialog");
//				if (fragment != null) {
//					transaction.remove(fragment);
//				}
//				transaction.addToBackStack(null);
//				DiaplayEventListFragment newDiaplayEventListFragment = new DiaplayEventListFragment();
//				Bundle args = new Bundle();
//				args.putBoolean("tetsOnly", true);
//				newDiaplayEventListFragment.setArguments(args);
//				newDiaplayEventListFragment.show(transaction, "dialog");
//				return true;
//			}
//		});

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	public interface OnPreferenceSelectedListener {
		public void onPreferenceSelected(String selected);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		// This makes sure that the container activity has implemented
		// the callback interface. If not, it throws an exception
		try {
			mCallback = (OnPreferenceSelectedListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnHeadlineSelectedListener");
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}
}
