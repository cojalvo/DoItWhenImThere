package il.cadan.doitwhenimthere.bl;

import android.os.Parcelable;

public interface ApplicationCallaback<T> extends Parcelable {
	void done(T retObj,Exception e);
}
