package il.cadan.doitwhenimthere;

import il.cadan.doitwhenimthere.bl.ApplicationCallaback;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.res.Resources.Theme;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;

public abstract class CustomAlertDialogBase extends DialogFragment {
	protected View view;

	public abstract void extractArgs();
	
	public abstract void setView();

	public abstract void processView();

	public abstract String getTitleResource();

	public abstract int getIconResource();

	public abstract Boolean validateAndCreateOutput();

	public abstract void returnCallback();
	
	public abstract String getNegButtonText();
	
	public abstract String getPosButtonText();
	 
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		super.onCreateDialog(savedInstanceState);
		setView();
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity()).
				setTitle(getTitleResource()).setIcon(getIconResource())
				.setPositiveButton(getPosButtonText(), new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// do nothing in here, i overidded the listener on
						// onStart method
						// this could validate the data in the fields before
						// closing the dialog
					}
				}).setNegativeButton(getNegButtonText(),
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
							}
						}).setView(view);
		processView();
		builder.setInverseBackgroundForced(true);
		AlertDialog d=builder.create();
		d.getWindow().getAttributes().windowAnimations = R.style.SlideInOutLeft;
		return d;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		extractArgs();
	}

	@Override
	public void onStart() {
		super.onStart(); // super.onStart() is where dialog.show() is actually
		// called on the underlying dialog, so we have to do it
		// after this point
		AlertDialog d = (AlertDialog) getDialog();
		this.getDialog().setCanceledOnTouchOutside(false);
		if (d != null) {
			Button positiveButton = (Button) d
					.getButton(Dialog.BUTTON_POSITIVE);
			positiveButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if(!validateAndCreateOutput())
						return;
					Log.i(getTag(), "new Event was created");
					returnCallback();
					dismiss();
				}
			});
		}
		
		
	}

}
