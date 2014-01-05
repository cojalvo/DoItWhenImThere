package il.cadan.doitwhenimthere;



import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class SignUp extends Activity {
	private TextView user = null;
	private TextView pass = null;
	private TextView email = null;
	private TextView conpass = null;
	private Button signUp = null;
	private ProgressDialog pb;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.signup);
		user = (TextView) findViewById(R.id.signup_user_text);
		pass = (TextView) findViewById(R.id.signup_password_text);
		email = (TextView) findViewById(R.id.signup_email);
		conpass = (TextView) findViewById(R.id.signup_confirmpass_text);
		signUp = (Button) findViewById(R.id.signup_button);
		signUp.setOnClickListener(new OnClickListener() {

			@SuppressLint("NewApi")
			@Override
			public void onClick(View v) {
				String username = user.getText().toString();
				String emailAddres = email.getText().toString();
				String password = pass.getText().toString();
				String connfirmPass = conpass.getText().toString();
				if (!password.equals(connfirmPass)) {
					Toast t = Toast.makeText(SignUp.this,
							"Please confirm the password again",
							Toast.LENGTH_SHORT);
					t.setGravity(Gravity.CENTER, 0, 70);
					t.show();
					return;
				}
				if (username.isEmpty() || emailAddres.isEmpty()
						|| password.isEmpty() || connfirmPass.isEmpty()) {
					Toast t = Toast.makeText(SignUp.this,
							"All fields are requiered", Toast.LENGTH_SHORT);
					t.setGravity(Gravity.CENTER, 0, 70);
					t.show();
					return;
				}
				ParseUser newUser = new ParseUser();
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
				pb = new ProgressDialog(SignUp.this);
				pb.setProgressStyle(ProgressDialog.STYLE_SPINNER);
				pb.setMessage("Send request to the server...");
				pb.show();
				newUser.signUpInBackground(new SignUpCallback() {

					@Override
					public void done(ParseException e) {
						if (e == null) {
							Intent intent = new Intent(SignUp.this, MainActivity.class);
							startActivity(intent);
							pb.cancel();
							finish();
						} else {
							pb.cancel();
							Toast t = Toast.makeText(SignUp.this, e.getMessage(),
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

}
