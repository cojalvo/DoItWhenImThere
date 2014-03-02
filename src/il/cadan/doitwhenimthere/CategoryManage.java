package il.cadan.doitwhenimthere;

import android.content.Context;
import android.content.SharedPreferences;

public class CategoryManage 
{
	public  static String getCategoryValue(Context ctx, Category category)
	{
		SharedPreferences sharedPref  = ctx.getSharedPreferences(DoITConstance.SHARED_PREFERENCES,Context.MODE_PRIVATE);
		return sharedPref.getString(category.toString(), category.toString());
	}
	
	public  static void setCategoryValue(Context ctx, Category category,String value)
	{
		SharedPreferences sharedPref  = ctx.getSharedPreferences(DoITConstance.SHARED_PREFERENCES,Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPref.edit();
		editor.putString(category.toString(), value);
		editor.commit();
	}

}
