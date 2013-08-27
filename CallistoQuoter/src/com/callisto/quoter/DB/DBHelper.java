package com.callisto.quoter.DB;

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
	private static int version = 7;	//2	
	
	private static String name = "REDB";
	private static CursorFactory factory = null;

	private String TABLE_PROPERTIES = "PROPERTIES",
		TABLE_ID = "_id",
		TABLE_PROP_ADDRESS = "re_address",
		TABLE_PROP_BEDROOMS = "re_bedrooms";
	
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
	}

	private void upgradeToVersion2(SQLiteDatabase db) 
	{
		db.execSQL("ALTER TABLE " + TABLE_PROPERTIES + " ADD re_confirmed INTEGER NOT NULL DEFAULT 0");
		
		Log.i(this.getClass().toString(), "Update to version 2 complete");
	}
	
	private void upgradeToVersion3(SQLiteDatabase db)
	{
		db.execSQL(DEFINE_RATING);
		
		db.execSQL(DEFINE_RATING_INDEX);
		
		db.execSQL("INSERT INTO RATINGS(_id, re_rating) VALUES(1, 'Poor')");
		db.execSQL("INSERT INTO RATINGS(_id, re_rating) VALUES(2, 'Average')");
		db.execSQL("INSERT INTO RATINGS(_id, re_rating) VALUES(3, 'Good')");
		db.execSQL("INSERT INTO RATINGS(_id, re_rating) VALUES(4, 'Very good')");
		db.execSQL("INSERT INTO RATINGS(_id, re_rating) VALUES(5, 'Excellent')");
		
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

}
