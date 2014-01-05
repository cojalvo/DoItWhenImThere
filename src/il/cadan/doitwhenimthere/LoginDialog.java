package il.cadan.doitwhenimthere;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import il.cadan.doitwhenimthere.bl.ApplicationCallaback;
import android.annotation.SuppressLint;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginDialog extends CustomAlertDialogBase
{
	private EditText userName = null;
	private EditText password = null;
	ProgressDialog pb=null;
	Toast t=null;
	ParseUser logedUser=null;
	ApplicationCallaback<ParseUser> callBack;
	
	

	@Override
	public void setbuttons(Builder builder) {
		//do nothing
	}

	@Override
	public void setHeadLine(Builder builder) {
		//do nothing
	}

	@Override
	public void extractArgs() {
		Bundle args = getArguments();
		callBack = args.getParcelable("callBack");
	}
	
	public static LoginDialog getInstance(Bundle args) {
		LoginDialog amd = new LoginDialog();
		amd.setArguments(args);
		return amd;
	}

	@Override
	public void setView() {
	
		view = View.inflate(getActivity(), R.layout.login, null);
	}

	@Override
	public void processView() {
		TextView signup = (TextView) view.findViewById(R.id.signup_text_view);
		signup.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				LoginDialog.this.dismiss();
				android.app.FragmentTransaction ft = getFragmentManager()
						.beginTransaction();
				ft.addToBackStack(null);

				// Create and show the dialog.
				Bundle args = new Bundle();
				// add the callBck to the argument bundle
				args.putParcelable("callBack",callBack);
				SignUpDialog d = SignUpDialog
						.getInstance(args);
				d.show(ft, "");

			}

		});
		Button loginButton = (Button) view.findViewById(R.id.login_button);
		userName = (EditText) view.findViewById(R.id.user_text);
		password = (EditText)view. findViewById(R.id.password_text);
		loginButton.setOnClickListener(new OnClickListener() {

			@SuppressLint("NewApi")
			@Override
			public void onClick(View v) {
				String user = userName.getText().toString();
				String pass = password.getText().toString();
				if (user.isEmpty() || user == null) {
					 t = Toast.makeText(getActivity(),
							"Please enter your User name", Toast.LENGTH_SHORT);
					t.setGravity(Gravity.BOTTOM, 0, 70);
					t.show();
					return;
				}
				if (pass == null || pass.isEmpty()) {
					Toast t = Toast.makeText(getActivity(),
							"Please enter your Password", Toast.LENGTH_SHORT);
					t.setGravity(Gravity.BOTTOM, 0, 70);
					t.show();
					return;

				}
				MessageHalper.showProgressDialog("Login...",getActivity());
				ParseUser.logInInBackground(user, pass, new LogInCallback() {
					public void done(ParseUser user, ParseException e) {
						if (user != null) {
							MessageHalper.closeProggresDialog();
							logedUser=user;
							LoginDialog.this.dismiss();
							returnCallback();
						} else {
							 t = Toast.makeText(getActivity(),
									"User name or password is incorrect",
									Toast.LENGTH_SHORT);
							t.setGravity(Gravity.BOTTOM, 0, 70);
							t.show();
							MessageHalper.closeProggresDialog();
						}
					}
				});
			}

		});
		
	}

	@Override
	public String getTitleResource() {
		return "Login";
	}

	@Override
	public int getIconResource() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Boolean validateAndCreateOutput() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void returnCallback() {
		if(callBack!=null)
			callBack.done(logedUser, null);
	}

	@Override
	public String getNegButtonText() {
		return "Cancel";
	}

	@Override
	public String getPosButtonText() {
		return "Login";
	}

	@Override
	public Button gePositiveButton() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Button getNegativeButton() {
		// TODO Auto-generated method stub
		return null;
	}

}
