package com.callisto.quoter.db;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {

	/*
	 * VERSION CHANGE IMPLEMENTED ON DATABASE UPGRADE STEP
	 */
	private static int version = 9;	// 9	
	
	private static String name = "REDB";
	private static CursorFactory factory = null;

	private String TABLE_PROPERTIES = "PROPERTIES",
			TABLE_ID = "_id",
			TABLE_IMAGE = "re_image",
			TABLE_PROP_ADDRESS = "re_address",
			TABLE_PROP_BEDROOMS = "re_bedrooms",
			TABLE_PROP_CONFIRMED = "re_confirmed",
			TABLE_PROP_RATING_ID = "re_rating_id",
			TABLE_PROP_OWNER_URI = "re_owner_uri",
			TABLE_PROP_LATITUDE = "re_latitude",
			TABLE_PROP_LONGITUDE = "re_longitude",
			TABLE_PROP_TYPE_ID = "re_prop_type_id",
			
			TABLE_PROP_ID = "re_prop_id",
			TABLE_ROOM_ID = "re_room_id",

			TABLE_ROOM_TYPE_ID = "re_room_type_id";

	private String DEFINE_PROPERTIES = "CREATE TABLE " + TABLE_PROPERTIES + "(" 
			+ TABLE_ID + " INTEGER PRIMARY KEY, "
			+ TABLE_PROP_ADDRESS + " TEXT NOT NULL, "
			+ TABLE_PROP_BEDROOMS + " INTEGER NOT NULL)";
	
	private String DEFINE_PROP_INDEX = "CREATE UNIQUE INDEX " + TABLE_PROP_ADDRESS 
			+ " ON " + TABLE_PROPERTIES 
			+ "(" + TABLE_PROP_ADDRESS + " ASC)"; 
	
	private String TABLE_RATING = "RATINGS",
			TABLE_RATING_NAME = "re_rating";
	
	private String DEFINE_RATING = "CREATE TABLE " + TABLE_RATING + "("
			+ TABLE_ID + " INTEGER PRIMARY KEY, "
			+ TABLE_RATING_NAME + " TEXT NOT NULL)";
	
	private String DEFINE_RATING_INDEX = "CREATE UNIQUE INDEX " + TABLE_RATING_NAME
			+ " ON " + TABLE_RATING
			+ "(" + TABLE_RATING_NAME + " ASC)";
	
	private String TABLE_PROP_TYPES = "PROP_TYPES",
			TABLE_PROP_TYPE_NAME = "re_prop_type";
			
	private String DEFINE_PROP_TYPES = "CREATE TABLE " + TABLE_PROP_TYPES + "("
			+ TABLE_ID + " INTEGER PRIMARY KEY, "
			+ TABLE_PROP_TYPE_NAME + " TEXT NOT NULL"
			+ ")";
	
	private String DEFINE_PROP_TYPE_INDEX = "CREATE UNIQUE INDEX " + TABLE_PROP_TYPE_NAME
			+ " ON " + TABLE_PROP_TYPES
			+ "(" + TABLE_PROP_TYPE_NAME + " ASC)";

	private String TABLE_ROOM_TYPES = "ROOM_TYPES",
			TABLE_ROOM_TYPE_NAME = "re_room_type";
	
	private String DEFINE_ROOM_TYPES = "create table if not exists " 
			+ TABLE_ROOM_TYPES + "(" 
			+ TABLE_ID + " integer primary key, " 
			+ TABLE_ROOM_TYPE_NAME + " text not null"
			+ ");";
	
	private String DEFINE_ROOM_TYPE_INDEX = "CREATE UNIQUE INDEX " + TABLE_ROOM_TYPE_NAME
			+ " ON " + TABLE_ROOM_TYPES
			+ "(" + TABLE_ROOM_TYPE_NAME + " ASC)";

	private String TABLE_ROOMS = "ROOMS",
			//id
			TABLE_ROOM_X = "re_room_x",
			TABLE_ROOM_Y = "re_room_y",
			TABLE_ROOM_FLOORS = "re_floors",
			TABLE_ROOM_DETAILS = "re_details";
			//image
	
	private String DEFINE_ROOMS = "create table if not exists "
			+ TABLE_ROOMS + "("
			+ TABLE_ID + " integer primary key, "
			+ TABLE_ROOM_TYPE_ID + " integer not null default 1, "
			+ TABLE_ROOM_X + " real not null, "
			+ TABLE_ROOM_Y + " real not null, "
			+ TABLE_ROOM_FLOORS + " text, "
			+ TABLE_ROOM_DETAILS + " text, "
			+ TABLE_IMAGE + " text"
			+ ");";
	
	private String TABLE_PROPS_ROOMS = "PROPS_ROOMS";			
	
	private String DEFINE_PROPS_ROOMS = "create table if not exists "
			+ TABLE_PROPS_ROOMS + "("
			+ TABLE_PROP_ID + " integer not null, "
			+ TABLE_ROOM_ID + " integer not null, "
			+ " PRIMARY KEY (" + TABLE_PROP_ID + ", " + TABLE_ROOM_ID + "), "
			+ " FOREIGN KEY" + "(" + TABLE_PROP_ID + ")" + " REFERENCES " + TABLE_PROPERTIES + "(" + TABLE_ID + ") ON DELETE CASCADE,"
			+ " FOREIGN KEY" + "(" + TABLE_ROOM_ID + ")" + " REFERENCES " + TABLE_ROOMS + "(" + TABLE_ID + ") ON DELETE CASCADE"
			+ ");";
	
	private String DEFINE_ROOMS_INDEX = "CREATE UNIQUE INDEX " + TABLE_ID
			+ " ON " + TABLE_ROOMS
			+ "(" + TABLE_ID + " ASC)";
	
	public DBHelper(Context context)
	{
		super(context, name, factory, version);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) 
	{
		Log.i(this.getClass().toString(), "Building DB");
		
		db.execSQL(DEFINE_PROPERTIES);
		
		db.execSQL(DEFINE_PROP_INDEX);
		
		db.execSQL("INSERT INTO " + TABLE_PROPERTIES + "(" + TABLE_PROP_ADDRESS + "," + TABLE_PROP_BEDROOMS + ") VALUES ('Puan 1917', 1)");
		db.execSQL("INSERT INTO " + TABLE_PROPERTIES + "(" + TABLE_PROP_ADDRESS + "," + TABLE_PROP_BEDROOMS + ") VALUES ('Crocce 1853', 2)");
		db.execSQL("INSERT INTO " + TABLE_PROPERTIES + "(" + TABLE_PROP_ADDRESS + "," + TABLE_PROP_BEDROOMS + ") VALUES ('Formosa 4910', 2)");

		Log.i(this.getClass().toString(), "Initial data inserted");
		
		Log.i(this.getClass().toString(), "Database created");
		
		/*
		 * CALL FOR UPGRADING METHODS IMPLEMENTED ON DATABASE UPGRADE STEP 
		 */
		upgradeToVersion2(db);
		upgradeToVersion3(db);
		upgradeToVersion4(db);
		upgradeToVersion5(db);
		upgradeToVersion6(db);
		upgradeToVersion7(db);
		upgradeToVersion8(db);
		upgradeToVersion9(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) 
	{
		if (oldVersion < 2)
		{
			try
			{
				upgradeToVersion2(db);
			}
			catch(SQLException e)
			{
				Log.i(this.getClass().toString(), e.getMessage());
			}
		}
		if (oldVersion < 3)
		{
			try
			{
				upgradeToVersion3(db);
			}
			catch(SQLException e)
			{
				Log.i(this.getClass().toString(), e.getMessage());
			}
		}
		if (oldVersion < 4)
		{
			try
			{
				upgradeToVersion4(db);
			}
			catch(SQLException e)
			{
				Log.i(this.getClass().toString(), e.getMessage());
			}
		}
		if (oldVersion < 5)
		{
			try
			{
				upgradeToVersion5(db);
			}
			catch(SQLException e)
			{
				Log.i(this.getClass().toString(), e.getMessage());
			}
		}
		if (oldVersion < 6)
		{
			try
			{
				upgradeToVersion6(db);
			}
			catch(SQLException e)
			{
				Log.i(this.getClass().toString(), e.getMessage());
			}
		}
		if (oldVersion < 7)
		{
			try
			{
				upgradeToVersion6(db);
			}
			catch(SQLException e)
			{
				Log.i(this.getClass().toString(), e.getMessage());
			}
		}
		if (oldVersion < 8)
		{
			try
			{
				upgradeToVersion8(db);
			}
			catch(SQLException e)
			{
				Log.i(this.getClass().toString(), e.getMessage());
			}
		}
		if (oldVersion < 9)
		{
			try
			{
				upgradeToVersion9(db);
			}
			catch(SQLException e)
			{
				Log.i(this.getClass().toString(), e.getMessage());
			}
		}
	}

	@Override
	public void onOpen(SQLiteDatabase db) 
	{
	    super.onOpen(db);
	    
	    if (!db.isReadOnly()) 
	    {
	        db.execSQL("PRAGMA foreign_keys=ON;");
	    }
	}
	
	private void upgradeToVersion2(SQLiteDatabase db) 
	{
		db.execSQL("ALTER TABLE " + TABLE_PROPERTIES + " ADD " + TABLE_PROP_CONFIRMED + " INTEGER NOT NULL DEFAULT 0");
		
		Log.i(this.getClass().toString(), "Update to version 2 complete");
	}
	
	private void upgradeToVersion3(SQLiteDatabase db)
	{
		db.execSQL(DEFINE_RATING);
		
		db.execSQL(DEFINE_RATING_INDEX);
		
		db.execSQL("INSERT INTO RATINGS(_id, re_rating) VALUES(1, 'Pobre')");
		db.execSQL("INSERT INTO RATINGS(_id, re_rating) VALUES(2, 'Regular')");
		db.execSQL("INSERT INTO RATINGS(_id, re_rating) VALUES(3, 'Bueno')");
		db.execSQL("INSERT INTO RATINGS(_id, re_rating) VALUES(4, 'Muy bueno')");
		db.execSQL("INSERT INTO RATINGS(_id, re_rating) VALUES(5, 'Excelente')");
		
		db.execSQL("ALTER TABLE " + TABLE_PROPERTIES + " ADD re_rating_id INTEGER NOT NULL DEFAULT 2");

		Log.i(this.getClass().toString(), "Update to version 3 complete");
	}
	
	private void upgradeToVersion4(SQLiteDatabase db)
	{
		db.execSQL("ALTER TABLE " + TABLE_PROPERTIES + " ADD re_owner_uri TEXT");
		
		Log.i(this.getClass().toString(), "Update to version 4 complete");
	}
	
	private void upgradeToVersion5(SQLiteDatabase db)
	{
		db.execSQL("ALTER TABLE " + TABLE_PROPERTIES + " ADD re_latitude REAL");
		db.execSQL("ALTER TABLE " + TABLE_PROPERTIES + " ADD re_longitude REAL");
		
		Log.i(this.getClass().toString(), "Update to version 5 complete");
	}
	
	private void upgradeToVersion6(SQLiteDatabase db)
	{
		db.execSQL("ALTER TABLE " + TABLE_PROPERTIES + " ADD re_image TEXT");
		
		Log.i(this.getClass().toString(), "Update to version 6 complete");
	}
	
	private void upgradeToVersion7(SQLiteDatabase db)
	{
		db.execSQL(DEFINE_PROP_TYPES);
		
		db.execSQL(DEFINE_PROP_TYPE_INDEX);
		
		db.execSQL("INSERT INTO " + TABLE_PROP_TYPES 
				+ "(" + TABLE_ID + ", " + TABLE_PROP_TYPE_NAME + ")"
				+ " VALUES (1, 'Chalet')");
		db.execSQL("INSERT INTO " + TABLE_PROP_TYPES 
				+ "(" + TABLE_ID + ", " + TABLE_PROP_TYPE_NAME + ")"
				+ " VALUES (2, 'Departamento')");
		db.execSQL("INSERT INTO " + TABLE_PROP_TYPES 
				+ "(" + TABLE_ID + ", " + TABLE_PROP_TYPE_NAME + ")"
				+ " VALUES (3, 'Local')");
		
		db.execSQL("ALTER TABLE " + TABLE_PROPERTIES + " ADD re_prop_type_id INTEGER NOT NULL DEFAULT 1");
		
		Log.i(this.getClass().toString(), "Update to version 7 complete");
	}
	
	private void upgradeToVersion8(SQLiteDatabase db)
	{
		db.execSQL(DEFINE_ROOM_TYPES);
		
		db.execSQL(DEFINE_ROOM_TYPE_INDEX);
		
		db.execSQL("INSERT INTO " + TABLE_ROOM_TYPES 
				+ "(" + TABLE_ID + ", " + TABLE_ROOM_TYPE_NAME + ")"
				+ " VALUES (1, 'Sala de estar')");
		db.execSQL("INSERT INTO " + TABLE_ROOM_TYPES 
				+ "(" + TABLE_ID + ", " + TABLE_ROOM_TYPE_NAME + ")"
				+ " VALUES (2, 'Cocina')");
		db.execSQL("INSERT INTO " + TABLE_ROOM_TYPES 
				+ "(" + TABLE_ID + ", " + TABLE_ROOM_TYPE_NAME + ")"
				+ " VALUES (3, 'Dormitorio')");

//		db.execSQL("ALTER TABLE " + TABLE_PROPERTIES + " ADD re_room_type_id INTEGER NOT NULL DEFAULT 1");
		
		Log.i(this.getClass().toString(), "Update to version 8 complete");
	}
	
	private void upgradeToVersion9(SQLiteDatabase db)
	{
		db.execSQL(DEFINE_ROOMS);
		
		db.execSQL(DEFINE_ROOMS_INDEX);
		
		db.execSQL(DEFINE_PROPS_ROOMS);

		db.execSQL("INSERT INTO " + TABLE_ROOMS 
				+ "(" + TABLE_ID + ", " + TABLE_ROOM_TYPE_ID + ", " + TABLE_ROOM_X + ", " + TABLE_ROOM_Y + " )"
				+ " VALUES (1, 1, 4, 4)");
		db.execSQL("INSERT INTO " + TABLE_ROOMS 
				+ "(" + TABLE_ID + ", " + TABLE_ROOM_TYPE_ID + ", " + TABLE_ROOM_X + ", " + TABLE_ROOM_Y + " )"
				+ " VALUES (2, 2, 3, 2)");
		db.execSQL("INSERT INTO " + TABLE_ROOMS 
				+ "(" + TABLE_ID + ", " + TABLE_ROOM_TYPE_ID + ", " + TABLE_ROOM_X + ", " + TABLE_ROOM_Y + " )"
				+ " VALUES (3, 3, 3, 4)");
		
		db.execSQL("INSERT INTO " + TABLE_PROPS_ROOMS
				+ "(" + TABLE_PROP_ID + ", " + TABLE_ROOM_ID + ")"
				+ "VALUES (1, 1)");
		db.execSQL("INSERT INTO " + TABLE_PROPS_ROOMS
				+ "(" + TABLE_PROP_ID + ", " + TABLE_ROOM_ID + ")"
				+ "VALUES (1, 2)");
		db.execSQL("INSERT INTO " + TABLE_PROPS_ROOMS
				+ "(" + TABLE_PROP_ID + ", " + TABLE_ROOM_ID + ")"
				+ "VALUES (1, 3)");
		
		db.execSQL("INSERT INTO " + TABLE_ROOMS 
				+ "(" + TABLE_ID + ", " + TABLE_ROOM_TYPE_ID + ", " + TABLE_ROOM_X + ", " + TABLE_ROOM_Y + " )"
				+ " VALUES (4, 1, 5, 3)");
		db.execSQL("INSERT INTO " + TABLE_ROOMS 
				+ "(" + TABLE_ID + ", " + TABLE_ROOM_TYPE_ID + ", " + TABLE_ROOM_X + ", " + TABLE_ROOM_Y + " )"
				+ " VALUES (5, 2, 2, 2)");
		db.execSQL("INSERT INTO " + TABLE_ROOMS 
				+ "(" + TABLE_ID + ", " + TABLE_ROOM_TYPE_ID + ", " + TABLE_ROOM_X + ", " + TABLE_ROOM_Y + " )"
				+ " VALUES (6, 3, 4, 4)");

		db.execSQL("INSERT INTO " + TABLE_PROPS_ROOMS
				+ "(" + TABLE_PROP_ID + ", " + TABLE_ROOM_ID + ")"
				+ "VALUES (2, 4)");
		db.execSQL("INSERT INTO " + TABLE_PROPS_ROOMS
				+ "(" + TABLE_PROP_ID + ", " + TABLE_ROOM_ID + ")"
				+ "VALUES (2, 5)");
		db.execSQL("INSERT INTO " + TABLE_PROPS_ROOMS
				+ "(" + TABLE_PROP_ID + ", " + TABLE_ROOM_ID + ")"
				+ "VALUES (2, 6)");
		
		Log.i(this.getClass().toString(), "Update to version 9 complete");
	}
}
