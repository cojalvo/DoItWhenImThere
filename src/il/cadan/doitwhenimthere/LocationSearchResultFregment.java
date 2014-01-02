package il.cadan.doitwhenimthere;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.database.DataSetObserver;
import android.location.Address;
import android.os.Bundle;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;

public class LocationSearchResultFregment extends Fragment
{
	private ArrayList<String> searchResult;
	private ListView searchList;

	public static LocationSearchResultFregment getInstace(Bundle args) {
		LocationSearchResultFregment dlf = new LocationSearchResultFregment();
		dlf.setArguments(args);
		return dlf;
	}
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle args=getArguments();
		searchResult=args.getStringArrayList("searchResults");
	}


	@SuppressLint("NewApi")
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// set the view
		final View view = getActivity().getLayoutInflater().inflate(
				R.layout.search_result_layout, null, false);

		// set the adapter to the list
		searchList = (ListView) view.findViewById(R.id.lv_search_result);
		// attache a listener to the text box in order to filter the events
		  ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
			        android.R.layout.simple_list_item_1, searchResult);
		searchList.setAdapter(adapter);
		return super.onCreateView(inflater, container, savedInstanceState);
	}
}

