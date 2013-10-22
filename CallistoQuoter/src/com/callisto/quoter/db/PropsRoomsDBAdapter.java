package com.callisto.quoter.db;

import android.content.Context;

public class PropsRoomsDBAdapter extends DBAdapter 
{
	static public final String T_PROPS_ROOMS = "PROPS_ROOMS",
			C_PROP_ID = "re_prop_id",
			C_ROOM_ID = "re_room_id";
	
	public PropsRoomsDBAdapter(Context context) {
		super(context);
		this.setManagedTable(T_PROPS_ROOMS);
		this.setColumns(new String[] { C_PROP_ID,
			C_ROOM_ID });
	}	
}