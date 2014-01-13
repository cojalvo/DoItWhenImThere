package il.cadan.doitwhenimthere;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;

public class MessageHalper
{
    private static ProgressDialog pb = null;

    public static void showProgressDialog(String message, Context context)
    {
	Activity ac;
	try
	{
	    ac = (Activity) context;
	}
	catch (Exception e)
	{
	    return;
	}
	if (ac != null && !ac.isFinishing())
	{
	    pb = ProgressDialog.show(context, "Loading...", message);
	    pb.setCanceledOnTouchOutside(false);
	}
    }

    public static void closeProggresDialog()
    {
	if (pb != null)
	    pb.dismiss();
    }

    public static void showAlertDialog(String title, String message, Context context)
    {
	AlertDialog alertDialog = new AlertDialog.Builder(context).create();
	alertDialog.setTitle(title);
	alertDialog.setMessage(message);
	alertDialog.setButton(alertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener()
	{
	    public void onClick(DialogInterface dialog, int which)
	    {
		// TODO Add your code for the button here.
	    }
	});
	// Set the Icon for the Dialog
	alertDialog.setIcon(R.drawable.ic_launcher);
	alertDialog.show();
    }
    public static void showYesNoDialog(String title, String message, Context context,DialogInterface.OnClickListener yesListener,DialogInterface.OnClickListener noListener)
    {
    	AlertDialog alertDialog = new AlertDialog.Builder(context).create();
    	alertDialog.setTitle(title);
    	alertDialog.setMessage(message);
    	alertDialog.setButton(alertDialog.BUTTON_POSITIVE, "Ok", yesListener);
    	alertDialog.setButton(alertDialog.BUTTON_NEGATIVE, "Cancel", noListener);

    	// Set the Icon for the Dialog
    	alertDialog.setIcon(R.drawable.ic_launcher);
    	alertDialog.show();
    }

}
