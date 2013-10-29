package com.callisto.quoter.db;

import android.content.Context;

public class RatingsDBAdapter extends DBAdapter 
{
//	private Context context;
//	private DBHelper dbHelper;
//	private SQLiteDatabase db;
//
	static public final String T_RATINGS = "RATINGS";

	static public final String C_RATING_NAME = "re_rating";
	
//	private String[] columns = new String[] { C_COLUMN_ID,
//			C_COLUMN_RATING_NAME };
	
	public RatingsDBAdapter(Context context)
	{
		super(context);
		this.setManagedTable(T_RATINGS);
		this.setColumns(new String[] { C_ID,
			C_RATING_NAME });
//		this.context = context;
	}

//	public void close()
//	{
//		super.close();
////		dbHelper.close();
//	}

	/***
	 * Returns a list containing all records for this table.
	 * @return Cursor containing records.
	 * @throws SQLException
	 */
//	public Cursor getList() throws SQLException
//	{
//		Cursor c = db.query(true, C_TABLE_RATINGS, columns, null, null, null, null, null, null);
//		
//		return c;
//	}
	
//	public RatingsDBAdapter open() throws SQLException
//	{
//		return (RatingsDBAdapter) open();
//		dbHelper = new DBHelper(context);
//		db = dbHelper.getWritableDatabase();
//		return this;
//	}

}
