package com.callisto.quoter.db;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

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

	public RoomsDBAdapter(Context context) 
	{
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

//	public Cursor getRoomsForProperty(long mPropId)
//	{
//		return db.rawQuery("select * from " + C_TABLE_ROOMS + 
//				" inner join " + PropsRoomsDBAdapter.C_TABLE_PROPS_ROOMS + 
//				" on " + RoomsDBAdapter.C_COLUMN_ID + " = " + PropsRoomsDBAdapter.C_COLUMN_ROOM_ID + 
//				" where " + PropsRoomsDBAdapter.C_COLUMN_PROP_ID + " = " + mPropId, null);
//	}
	
	public Cursor getRoomsForProperty(long mPropId)
	{
//		Cursor c = db.rawQuery("select PR." + PropsRoomsDBAdapter.C_COLUMN_PROP_ID 									// Property ID
//				+ ", RT." + RoomTypesDBAdapter.C_COLUMN_ROOM_TYPES_NAME 										// Room type name
//				+ ", R.* " 																						// All columns from rooms table
//				+ "FROM " + RoomsDBAdapter.C_TABLE_ROOMS + " AS R " 										
//				+ " INNER JOIN " + RoomTypesDBAdapter.C_TABLE_ROOM_TYPES + " AS RT " 
//				+ " ON R." + RoomsDBAdapter.C_COLUMN_ROOM_TYPE_ID + " = RT." + RoomTypesDBAdapter.C_COLUMN_ID
//				+ " INNER JOIN " + PropsRoomsDBAdapter.C_TABLE_PROPS_ROOMS + " AS PR "
//				+ " ON R." + RoomsDBAdapter.C_COLUMN_ID + " = PR." + PropsRoomsDBAdapter.C_COLUMN_ROOM_ID
//				+ " WHERE PR." + PropsRoomsDBAdapter.C_COLUMN_ROOM_ID + " = " + mPropId
//				, null);

		// "Um, boss... dis 'ere, um, 'query' fing'z un-Orky as a 'umie, but works."
		Cursor c = db.rawQuery("select R.*, RT.re_room_type FROM ROOMS as R, " +
				"ROOM_TYPES as RT, " +
				"PROPS_ROOMS as PR " +
				"WHERE R.re_room_type_id = RT._id " +
				"AND PR.re_room_id = R._id " +
				"AND PR.re_prop_id = " + mPropId, null);
		
		Log.i(this.getClass().toString() + ".getRoomsForProperty", "Rows returned: " + c.getCount());

		return c;
	}
}
