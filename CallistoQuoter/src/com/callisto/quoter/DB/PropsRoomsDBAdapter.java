package com.callisto.quoter.DB;

import android.content.Context;

public class PropsRoomsDBAdapter extends DBAdapter 
{
	private String C_TABLE_PROPS_ROOMS = "PROPS_ROOMS";
	
	static public final String C_COLUMN_PROP_ID = "re_prop_id",
			C_COLUMN_ROOM_ID = "re_room_id";
	
	public PropsRoomsDBAdapter(Context context) {
		super(context);
		this.setTableManaged(C_TABLE_PROPS_ROOMS);
		this.setColumns(new String[] { C_COLUMN_PROP_ID,
			C_COLUMN_ROOM_ID });
	}	
}