package com.callisto.quoter.db;

import java.util.ArrayList;
import java.util.Locale;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.location.Address;
import android.os.Bundle;
import android.util.Log;

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
//		C_BEDROOMS = "re_bedrooms",
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
		C_PROP_TYPE_ID = "re_prop_type_id",
//		C_PRICE = "re_price",
		C_SURFACE_BUILT = "re_surface_built",
		C_SURFACE_PARCEL = "re_surface_parcel";
		
	/*
	 * Constructor
	 */
	public PropDBAdapter(Context context)
	{
		super(context);
		this.setManagedTable(T_PROPERTIES);
		this.setColumns(new String[] 
		{ 
			C_ID, 
			C_ADDRESS, 
//			C_BEDROOMS,
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
			C_PROP_TYPE_ID,
//			C_PRICE,
			C_SURFACE_BUILT,
			C_SURFACE_PARCEL
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
	
	/***
	 * Retrieves all addresses and geolocation data from database, paired with property ID.
	 * @return Populated arraylist.
	 * @throws SQLException
	 */
	public ArrayList<Address> getAllAddresses() throws SQLException
	{
		Cursor c = getCursor();
		
		ArrayList<Address> addresses = new ArrayList<Address>();
		
		while (c.moveToNext())
		{
			// TODO Is it worth fixing locales to be defined dynamically?
			Address t = new Address(new Locale("Spanish", "Argentina"));

			t.setLatitude(Double.parseDouble
					(String.valueOf
						(c.getFloat
							(c.getColumnIndexOrThrow(C_LATITUDE))
						)
					)
				);
		
			t.setLongitude(Double.parseDouble
					(String.valueOf
						(c.getFloat
							(c.getColumnIndexOrThrow(C_LONGITUDE))
						)
					)
				);
			
			t.setAddressLine(0,
					c.getString(
							c.getColumnIndexOrThrow(C_ADDRESS)
						)
					);

			Bundle extras = new Bundle();
			
			Long id = c.getLong(c.getColumnIndexOrThrow(C_ID));
			Log.i(this.getClass().toString() + ".getAllAddresses", "Property id stored on address: " + id);
			
			extras.putLong(C_ID, id);
			
			t.setExtras(extras);
			
			addresses.add(t);
		}
		
		return addresses;
	}
}
