package il.cadan.doitwhenimthere;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.text.format.DateFormat;

public class ParsingHelper
{

    public static Date fromStringToDate(String theDate, String dFormat)
    {
	Date date = null;
	SimpleDateFormat format = new SimpleDateFormat(dFormat);
	try
	{
	    date = format.parse(theDate);
	}
	catch (ParseException e)
	{
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	return date;
    }

    public static String fromDateToString(Date d, String format)
    {
	SimpleDateFormat df = new SimpleDateFormat(format);
	return df.format(d);

    }

}
