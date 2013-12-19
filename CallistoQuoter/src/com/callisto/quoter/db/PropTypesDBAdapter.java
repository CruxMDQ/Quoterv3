package com.callisto.quoter.db;

import android.content.ContentValues;
import android.content.Context;

public class PropTypesDBAdapter extends DBAdapter
{
	/*
	 * Define constant with table name
	 */
	static public final String T_PROP_TYPES = "PROP_TYPES",
			LABEL_PROP_TYPES = "Tipo de propiedad";
	
	/*
	 * Define constants with column names
	 */
	static public final String C_ID = "_id",
		C_PROP_TYPES_NAME = "re_prop_type";
	
	public PropTypesDBAdapter(Context context)
	{
		super(context);
		this.setManagedTable(T_PROP_TYPES);
		this.setColumns(new String[] { C_ID,
			C_PROP_TYPES_NAME });
	}
	
	public long getId(String filter)
	{
		return super.getId(filter, C_PROP_TYPES_NAME);
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
		
		return db.insert(T_PROP_TYPES, null, reg);
	}
	
//	public PropTypesDBAdapter open() throws SQLException
//	{
//		return (PropTypesDBAdapter) super.open();
//	}
}
