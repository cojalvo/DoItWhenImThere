package il.cadan.doitwhenimthere;

import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.view.ViewParent;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.view.animation.Transformation;
import android.view.animation.TranslateAnimation;

public class GuiHalper {
	public static void expand(final View v) {
		v.setVisibility(View.VISIBLE);
		final ScaleAnimation anim = new ScaleAnimation(1, 1, 0, 1);
		anim.setDuration(400);
		Handler hn=new Handler();
		hn.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				v.startAnimation(anim);
			}
		}, 5);
	}

	public static void collapse(final View v,Boolean hide) {
		final ScaleAnimation anim = new ScaleAnimation(1, 1, 1, 0);
		if(hide)
			v.setVisibility(View.GONE);
		anim.setDuration(350);
		Handler hn=new Handler();
		hn.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				v.startAnimation(anim);
			}
		}, 200);
	}

	public static void slideIn(Context context,final View v) {
		
//		int h=v.getHeight();
//		//v.bringToFront();
//		TranslateAnimation anim = new TranslateAnimation(1,1,0,h);
//		anim.setDuration(500);
//		v.startAnimation(anim);
//		Handler hn=new Handler();
//		hn.postDelayed(new Runnable() {
//			
//			@Override
//			public void run() {
//				v.setVisibility(View.VISIBLE);
//			}
//		}, 500);

		Animation a=AnimationUtils.loadAnimation(context,R.anim.slide_down);
		a.setDuration(300);
		//v.setAnimation(a);
		v.startAnimation(a);
		v.setVisibility(View.VISIBLE);
	}
	public static void slideOut(Context context,final View v) {
		Animation a=AnimationUtils.loadAnimation(context,R.anim.slide_up);
		a.setDuration(300);
		//v.setAnimation(a);
		v.startAnimation(a);
		v.setVisibility(View.GONE);
	}
	

}
