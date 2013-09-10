package com.callisto.quoter.DB;

import android.content.Context;

public class RoomTypesDBAdapter extends DBAdapter 
{
	/*
	 * Define constant with table name
	 */
	static public final String C_TABLE_ROOM_TYPES = "ROOM_TYPES";

	/*
	 * Define constants with column names
	 */
	static public final String C_COLUMN_ID = "_id",
		C_COLUMN_ROOM_TYPES_NAME = "re_room_type";

	public RoomTypesDBAdapter(Context context) {
		super(context);
		this.setManagedTable(C_TABLE_ROOM_TYPES);
		this.setColumns(new String[] { C_COLUMN_ID,
				C_COLUMN_ROOM_TYPES_NAME });
	}
}