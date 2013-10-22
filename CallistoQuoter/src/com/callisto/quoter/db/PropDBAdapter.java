package com.callisto.quoter.db;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;

public class PropDBAdapter extends DBAdapter 
{
	/*
	 * Define constant with table name
	 */
	static public final String T_PROPERTIES = "PROPERTIES",
	/*
	 * Define constants with column names
	 */
		C_ADDRESS = "re_address",
		C_BEDROOMS = "re_bedrooms",
	/*
	 * NEW FIELD IMPLEMENTED ON DATABASE UPGRADE STEP (Lesson 9)
	 */
		C_CONFIRMED = "re_confirmed",
	/*
	 * NEW FIELD IMPLEMENTED ON DATABASE UPGRADE STEP (Lesson 10)
	 */
		C_RATING_ID = "re_rating_id",
		C_OWNER_URI = "re_owner_uri",
		C_LATITUDE = "re_latitude",
		C_LONGITUDE = "re_longitude",
		C_IMAGE = "re_image",
		C_PROP_TYPE_ID = "re_prop_type_id";
	
//	private String[] columns = new String[] 
//	{ 
//		C_COLUMN_ID, 
//		C_PROP_ADDRESS, 
//		C_PROP_BEDROOMS,
//		/*
//		 * NEW FIELD IMPLEMENTED ON DATABASE UPGRADE STEP (Lesson 9)
//		 */
//		C_PROP_CONFIRMED,
//		/*
//		 * NEW FIELD IMPLEMENTED ON DATABASE UPGRADE STEP (Lesson 10)
//		 */
//		C_PROP_RATING_ID,
//		/*
//		 * DATABASE VERSION 4
//		 */
//		C_PROP_OWNER_URI,
//		/*
//		 * DATABASE VERSION 5
//		 */
//		C_PROP_LATITUDE,
//		C_PROP_LONGITUDE,
//		/*
//		 * DATABASE VERSION 6
//		 */
//		C_PROP_IMAGE,
//		/*
//		 * DATABASE VERSION 7
//		 */
//		C_PROP_TYPE_ID
//	};

	/*
	 * Constructor
	 */
	public PropDBAdapter(Context context)
	{
		super(context);
		this.setManagedTable(T_PROPERTIES);
		this.setColumns(new String[] 
		{ 
			C_COLUMN_ID, 
			C_ADDRESS, 
			C_BEDROOMS,
			/*
			 * NEW FIELD IMPLEMENTED ON DATABASE UPGRADE STEP (Lesson 9)
			 */
			C_CONFIRMED,
			/*
			 * NEW FIELD IMPLEMENTED ON DATABASE UPGRADE STEP (Lesson 10)
			 */
			C_RATING_ID,
			/*
			 * DATABASE VERSION 4
			 */
			C_OWNER_URI,
			/*
			 * DATABASE VERSION 5
			 */
			C_LATITUDE,
			C_LONGITUDE,
			/*
			 * DATABASE VERSION 6
			 */
			C_IMAGE,
			/*
			 * DATABASE VERSION 7
			 */
			C_PROP_TYPE_ID
		});
	}
	
	public long delete(long id) 
	{
		if (db == null)
		{
			open();
		}
		return db.delete(T_PROPERTIES, "_id=" + id, null);		
	}

	/***
	 * 	
	 * @return Cursor containing all table rows and columns.
	 * @throws SQLException
	 */
	public Cursor getCursor() throws SQLException
	{
		Cursor c = db.query(true, T_PROPERTIES, columns, null, null, null, null, null, null);
		
		return c;
	}
	
	/***
	 * 
	 * @param filter String value used to filter results.
	 * @return Cursor containing filtered table rows and columns.
	 * @throws SQLException
	 */
	public Cursor getCursor(String filter) throws SQLException
	{
		Cursor c = db.query(true, T_PROPERTIES, columns, filter, null, null, null, null, null);
		
		return c;
	}
	
//	/***
//	 * Fetch a specific record from the database.
//	 * @param id Row identifier.
//	 * @return Cursor containing the requested row.
//	 * @throws SQLException
//	 */
//	public Cursor getRecord(long id) throws SQLException
//	{
//		Cursor c = db.query(true, C_TABLE_PROPERTIES, columns, C_COLUMN_ID + "=" + id, null, null, null, null, null);
//		
//		if (c != null)
//		{
//			c.moveToFirst();
//		}
//		
//		return c;
//	}
//	
//	/***
//	 * Inserts values into a new table record.
//	 * @param reg The set of values to insert.
//	 * @return 
//	 */
//	public long insert(ContentValues reg)
//	{
//		if (db == null)
//		{
//			open();
//		}
//		
//		return db.insert(C_TABLE_PROPERTIES, null, reg);
//	}
//	
//	/***
//	 * Modifies an existing record on the database.
//	 * @param reg The set of values to be used for the update.
//	 * @return Code for database operation result.  
//	 */
//	public long update(ContentValues reg)
//	{
//		long result = 0;
//		
//		if (db == null)
//		{
//			open();
//		}
//		
//		if (reg.containsKey(C_COLUMN_ID))
//		{
//			long id = reg.getAsLong(C_COLUMN_ID);
//			
//			reg.remove(C_COLUMN_ID);
//			
//			result = db.update(this.getTableManaged(), reg, "_id=" + id, null);
//		}
//		
//		return result;
//	}
}
