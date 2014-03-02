package il.cadan.doitwhenimthere;

import il.cadan.doitwhenimthere.bl.ApplicationCallaback;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class ChangeCategoryNameDialog extends CustomAlertDialogBase {

	String category;
	String toReturn="";
	EditText et_category;
	Button btn_change;
	ApplicationCallaback<String> callBack;
	@Override
	public void extractArgs() {
		Bundle args=getArguments();
		callBack = args.getParcelable("callBack");
		category=args.getString("category");
	}
	
	public static ChangeCategoryNameDialog getInstance(Bundle args) {
		ChangeCategoryNameDialog amd = new ChangeCategoryNameDialog();
		amd.setArguments(args);
		return amd;
	}

	@Override
	public void setView() {
		view = View.inflate(getActivity(), R.layout.change_category_name_layout, null);
		
	}

	@Override
	public void processView() {
		if (view == null)
			return;
		String val=category;
		et_category=(EditText) view.findViewById(R.id.category_name_edit_text);
		et_category.setText(val);
	}

	@Override
	public String getTitleResource() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getIconResource() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Boolean validateAndCreateOutput() {
		toReturn=et_category.getText().toString();
		SharedPreferences sharedPref  = getActivity().getSharedPreferences(DoITConstance.SHARED_PREFERENCES,Context.MODE_PRIVATE);
		sharedPref.edit().putString(category.toString(), toReturn);
		String val=sharedPref.getString(category.toString(), category.toString());
		return true;
	}

	@Override
	public void returnCallback() {
		if (callBack != null)
			callBack.done(toReturn, null);
		
	}

	@Override
	public Button gePositiveButton() {
		return (Button) view.findViewById(R.id.btn_change_category);
	}

	@Override
	public Button getNegativeButton() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getNegButtonText() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getPosButtonText() {
		// TODO Auto-generated method stub
		return null;
	}

}
