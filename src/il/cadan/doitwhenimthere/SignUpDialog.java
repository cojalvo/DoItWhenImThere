package il.cadan.doitwhenimthere;

import il.cadan.doitwhenimthere.bl.ApplicationCallaback;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class SignUpDialog extends CustomAlertDialogBase {
	
	private TextView user = null;
	private TextView pass = null;
	private TextView email = null;
	private TextView conpass = null;
	private Button signUp = null;
	private ProgressDialog pb;
	ParseUser logedUser=null;
	ApplicationCallaback<ParseUser> callBack; 

	public static SignUpDialog getInstance(Bundle args) {
		SignUpDialog amd = new SignUpDialog();
		amd.setArguments(args);
		return amd;
	}
	
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
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setView() {
		view = View.inflate(getActivity(), R.layout.signup, null);
		
	}

	@Override
	public void processView() {
		user = (TextView) view.findViewById(R.id.signup_user_text);
		pass = (TextView) view.findViewById(R.id.signup_password_text);
		email = (TextView) view.findViewById(R.id.signup_email);
		conpass = (TextView) view.findViewById(R.id.signup_confirmpass_text);
		signUp = (Button) view.findViewById(R.id.signup_button);
		signUp.setOnClickListener(new OnClickListener() {

			@SuppressLint("NewApi")
			@Override
			public void onClick(View v) {
				String username = user.getText().toString();
				String emailAddres = email.getText().toString();
				String password = pass.getText().toString();
				String connfirmPass = conpass.getText().toString();
				if (!password.equals(connfirmPass)) {
					Toast t = Toast.makeText(getActivity(),
							"Please confirm the password again",
							Toast.LENGTH_SHORT);
					t.setGravity(Gravity.CENTER, 0, 70);
					t.show();
					return;
				}
				if (username.isEmpty() || emailAddres.isEmpty()
						|| password.isEmpty() || connfirmPass.isEmpty()) {
					Toast t = Toast.makeText(getActivity(),
							"All fields are requiered", Toast.LENGTH_SHORT);
					t.setGravity(Gravity.CENTER, 0, 70);
					t.show();
					return;
				}
				final ParseUser newUser = new ParseUser();
				newUser.setPassword(password);
				newUser.setEmail(emailAddres);
				newUser.setUsername(username);
				// if(newUser.)
				// {
				// Toast t = Toast.makeText(SignUp.this,
				// "This user name is allready exist", Toast.LENGTH_SHORT);
				// t.setGravity(Gravity.CENTER, 0, 70);
				// t.show();
				// return;
				// }
				
				MessageHalper.showProgressDialog("Send request to the server...", getActivity());
				newUser.signUpInBackground(new SignUpCallback() {

					@Override
					public void done(ParseException e) {
						if (e == null) {
							MessageHalper.closeProggresDialog();
							logedUser=newUser;
							SignUpDialog.this.dismiss();
						} else {
							MessageHalper.closeProggresDialog();
							Toast t = Toast.makeText(getActivity(), e.getMessage(),
									Toast.LENGTH_SHORT);
							t.setGravity(Gravity.CENTER, 0, 70);
							t.show();
						}
						return;

					}
				});

			}
		});

		
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getPosButtonText() {
		// TODO Auto-generated method stub
		return null;
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
