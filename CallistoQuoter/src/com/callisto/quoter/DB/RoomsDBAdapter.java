package com.callisto.quoter.DB;

import android.content.Context;
import android.database.Cursor;

public class RoomsDBAdapter extends DBAdapter 
{
	/*
	 * Define constant with table name
	 */
	static public final String C_TABLE_ROOMS = "ROOMS";

	/*
	 * Define constants with column names
	 */
	static public final String C_COLUMN_ID = "_id",
			C_COLUMN_ROOM_TYPE_ID = "re_room_type_id",
			C_COLUMN_ROOM_X = "re_room_x",
			C_COLUMN_ROOM_Y = "re_room_y",
			C_COLUMN_ROOM_FLOORS = "re_floors",
			C_COLUMN_ROOM_DETAILS = "re_details",
			C_COLUMN_IMAGE = "re_image";

	public RoomsDBAdapter(Context context) {
		super(context);
		this.setManagedTable(C_TABLE_ROOMS);
		this.setColumns(new String[] { C_COLUMN_ID,
				C_COLUMN_ROOM_TYPE_ID,
				C_COLUMN_ROOM_X,
				C_COLUMN_ROOM_Y,
				C_COLUMN_ROOM_FLOORS,
				C_COLUMN_ROOM_DETAILS,
				C_COLUMN_IMAGE });
	}

	public Cursor getRoomsForProperty(long mPropId)
	{
		return db.rawQuery("select * from " + C_TABLE_ROOMS + 
				" inner join " + PropsRoomsDBAdapter.C_TABLE_PROPS_ROOMS + 
				" on " + RoomsDBAdapter.C_COLUMN_ID + " = " + PropsRoomsDBAdapter.C_COLUMN_ROOM_ID + 
				" where " + PropsRoomsDBAdapter.C_COLUMN_PROP_ID + " = " + mPropId, null);
	}
}
