package com.callisto.quoter.db;

import android.content.ContentValues;
import android.content.Context;

public class PropTypesDBAdapter extends DBAdapter
{
	/*
	 * Define constant with table name
	 */
	static public final String C_TABLE_PROP_TYPES = "PROP_TYPES";
	
	/*
	 * Define constants with column names
	 */
	static public final String C_COLUMN_ID = "_id",
		C_COLUMN_PROP_TYPES_NAME = "re_prop_type";
	
	public PropTypesDBAdapter(Context context)
	{
		super(context);
		this.setManagedTable(C_TABLE_PROP_TYPES);
		this.setColumns(new String[] { C_COLUMN_ID,
			C_COLUMN_PROP_TYPES_NAME });
	}
	
	/***
	 * Inserts values into a new table record.
	 * @param reg The set of values to insert.
	 * @return 
	 */
	public long insert(ContentValues reg)
	{
		if (db == null)
		{
			open();
		}
		
		return db.insert(C_TABLE_PROP_TYPES, null, reg);
	}
	
//	public PropTypesDBAdapter open() throws SQLException
//	{
//		return (PropTypesDBAdapter) super.open();
//	}
}
