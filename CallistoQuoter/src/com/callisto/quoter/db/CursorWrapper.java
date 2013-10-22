package com.callisto.quoter.db;

import android.database.Cursor;
import android.util.Log;

public class CursorWrapper
{
	static public void closeCursor(Cursor c)
	{
		try
		{
			if (c != null)
			{
				if (!c.isClosed())
				{
					c.close();
				}
			}
		}
		catch(Exception e)
		{
			Log.i("com.callisto.quoter.db.CursorWrapper", "");
			e.printStackTrace();
		}

	}
}
