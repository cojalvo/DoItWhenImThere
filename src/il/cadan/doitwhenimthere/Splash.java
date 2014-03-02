package il.cadan.doitwhenimthere;

import com.parse.Parse;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ProgressBar;

public class Splash extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        	
        int secondsDelayed = 2;
        new Handler().postDelayed(new Runnable() {
                public void run() {
                        startActivity(new Intent(Splash.this, MainActivity.class));
                        finish();
                }
        }, secondsDelayed * 1000);
    }
}