package com.callisto.quoter.db;

import android.content.Context;

public class RoomTypesDBAdapter extends DBAdapter 
{
	/*
	 * Define constant with table name
	 */
	static public final String T_ROOM_TYPES = "ROOM_TYPES";

	/*
	 * Define constants with column names
	 */
	static public final String C_ID = "_id",
		C_ROOM_TYPES_NAME = "re_room_type";

	public RoomTypesDBAdapter(Context context) {
		super(context);
		this.setManagedTable(T_ROOM_TYPES);
		this.setColumns(new String[] { C_ID,
				C_ROOM_TYPES_NAME });
	}
}