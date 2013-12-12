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
	private static int version = 11;	// 9	
	
	private static String name = "REDB";
	private static CursorFactory factory = null;

	private String T_PROPERTIES = "PROPERTIES",
			C_ID = "_id",
			C_IMAGE = "re_image",
			C_ADDRESS = "re_address",
			C_BEDROOMS = "re_bedrooms",
			C_CONFIRMED = "re_confirmed",
			C_RATING_ID = "re_rating_id",
			C_OWNER_URI = "re_owner_uri",
			C_LATITUDE = "re_latitude",
			C_LONGITUDE = "re_longitude",
			C_TYPE_ID = "re_prop_type_id",
			C_PRICE = "re_price",
			C_SURFACE_BUILT = "re_surface_built",
			C_SURFACE_PARCEL = "re_surface_parcel",
			
			C_PROP_ID = "re_prop_id",
			C_ROOM_ID = "re_room_id",

			C_ROOM_TYPE_ID = "re_room_type_id";

	private String D_PROPERTIES = "CREATE TABLE " + T_PROPERTIES + "(" 
			+ C_ID + " INTEGER PRIMARY KEY, "
			+ C_ADDRESS + " TEXT NOT NULL, "
			+ C_BEDROOMS + " INTEGER NOT NULL)";
	
	private String D_PROP_INDEX = "CREATE UNIQUE INDEX " + C_ADDRESS 
			+ " ON " + T_PROPERTIES 
			+ "(" + C_ADDRESS + " ASC)"; 
	
	private String T_RATING = "RATINGS",
			C_RATING_NAME = "re_rating";
	
	private String D_RATING = "CREATE TABLE " + T_RATING + "("
			+ C_ID + " INTEGER PRIMARY KEY, "
			+ C_RATING_NAME + " TEXT NOT NULL)";
	
	private String D_RATING_INDEX = "CREATE UNIQUE INDEX " + C_RATING_NAME
			+ " ON " + T_RATING
			+ "(" + C_RATING_NAME + " ASC)";
	
	private String T_PROP_TYPES = "PROP_TYPES",
			C_PROP_TYPE_NAME = "re_prop_type";
			
	private String D_PROP_TYPES = "CREATE TABLE " + T_PROP_TYPES + "("
			+ C_ID + " INTEGER PRIMARY KEY, "
			+ C_PROP_TYPE_NAME + " TEXT NOT NULL"
			+ ")";
	
	private String D_PROP_TYPE_INDEX = "CREATE UNIQUE INDEX " + C_PROP_TYPE_NAME
			+ " ON " + T_PROP_TYPES
			+ "(" + C_PROP_TYPE_NAME + " ASC)";

	private String T_ROOM_TYPES = "ROOM_TYPES",
			C_ROOM_TYPE_NAME = "re_room_type";
	
	private String D_ROOM_TYPES = "create table if not exists " 
			+ T_ROOM_TYPES + "(" 
			+ C_ID + " integer primary key, " 
			+ C_ROOM_TYPE_NAME + " text not null"
			+ ");";
	
	private String D_ROOM_TYPE_INDEX = "CREATE UNIQUE INDEX " + C_ROOM_TYPE_NAME
			+ " ON " + T_ROOM_TYPES
			+ "(" + C_ROOM_TYPE_NAME + " ASC)";

	private String T_ROOMS = "ROOMS",
			//id
			T_ROOM_X = "re_room_x",
			T_ROOM_Y = "re_room_y",
			T_ROOM_FLOORS = "re_floors",
			T_ROOM_DETAILS = "re_details";
			//image
	
	private String D_ROOMS = "create table if not exists "
			+ T_ROOMS + "("
			+ C_ID + " integer primary key, "
			+ C_ROOM_TYPE_ID + " integer not null default 1, "
			+ T_ROOM_X + " real not null, "
			+ T_ROOM_Y + " real not null, "
			+ T_ROOM_FLOORS + " text, "
			+ T_ROOM_DETAILS + " text, "
			+ C_IMAGE + " text"
			+ ");";
	
	private String T_PROPS_ROOMS = "PROPS_ROOMS";			
	
	private String D_PROPS_ROOMS = "create table if not exists "
			+ T_PROPS_ROOMS + "("
			+ C_PROP_ID + " integer not null, "
			+ C_ROOM_ID + " integer not null, "
			+ " PRIMARY KEY (" + C_PROP_ID + ", " + C_ROOM_ID + "), "
			+ " FOREIGN KEY" + "(" + C_PROP_ID + ")" + " REFERENCES " + T_PROPERTIES + "(" + C_ID + ") ON DELETE CASCADE,"
			+ " FOREIGN KEY" + "(" + C_ROOM_ID + ")" + " REFERENCES " + T_ROOMS + "(" + C_ID + ") ON DELETE CASCADE"
			+ ");";
	
	private String D_ROOMS_INDEX = "CREATE UNIQUE INDEX " + C_ID
			+ " ON " + T_ROOMS
			+ "(" + C_ID + " ASC)";
	
	private String T_SERVICES = "SERVICES",
			C_SERVICE_NAME = "re_serv_name",
			C_SERVICE_DETAILS = "re_serv_details";
			;
	
	private String D_SERVICES = "create table if not exists "
			+ T_SERVICES + "("
			+ C_ID + " integer primary key, "
			+ C_SERVICE_NAME + " text not null, "
			+ C_SERVICE_DETAILS + " text" 
			+ ");";
	
	private String D_SERVICES_INDEX = "CREATE UNIQUE INDEX " + C_SERVICE_NAME
			+ " ON " + T_SERVICES
			+ "(" + C_SERVICE_NAME + " ASC)";
	
	private String T_PROPS_SERVICES = "PROPS_SERVICES",
			C_SERV_ID = "re_serv_id";
	
	private String D_PROPS_SERVICES = "create table if not exists "
			+ T_PROPS_SERVICES + "("
			+ C_PROP_ID + " integer not null, "
			+ C_SERV_ID + " integer not null, "
			+ " PRIMARY KEY (" + C_PROP_ID + ", " + C_SERV_ID + "), "
			+ " FOREIGN KEY" + "(" + C_PROP_ID + ")" + " REFERENCES " + T_PROPERTIES + "(" + C_ID + ") ON DELETE CASCADE,"
			+ " FOREIGN KEY" + "(" + C_SERV_ID + ")" + " REFERENCES " + T_SERVICES + "(" + C_ID + ") ON DELETE CASCADE"
			+ ");";
	
	private String T_OPERATIONS_TYPES = "OPERATIONS_TYPES",
			C_OP_TYPE_NAME = "re_op_name";
	
	private String D_OPERATIONS = "create table if not exists "
			+ T_OPERATIONS_TYPES + "("
			+ C_ID + " integer primary key, "
			+ C_OP_TYPE_NAME + " text not null"
			+ ");";
	
	private String D_OPERATIONS_INDEX = "CREATE UNIQUE INDEX " + C_OP_TYPE_NAME
			+ " ON " + T_OPERATIONS_TYPES 
			+ "(" + C_OP_TYPE_NAME + " ASC)";
	
	private String T_PROPS_OPS= "PROPS_OPS",
			C_OP_TYPE_ID = "re_op_type_id";
	
	private String D_PROPS_OPS = "create table if not exists "
			+ T_PROPS_OPS + "("
			+ C_PROP_ID + " integer not null, "
			+ C_OP_TYPE_ID + " integer not null, "
			+ " PRIMARY KEY (" + C_PROP_ID + ", " + C_OP_TYPE_ID + "), "
			+ " FOREIGN KEY" + "(" + C_PROP_ID + ")" + " REFERENCES " + T_PROPERTIES + "(" + C_ID + ") ON DELETE CASCADE,"
			+ " FOREIGN KEY" + "(" + C_OP_TYPE_ID + ")" + " REFERENCES " + T_OPERATIONS_TYPES + "(" + C_ID + ") ON DELETE CASCADE"
			+ ");";
	
	public DBHelper(Context context)
	{
		super(context, name, factory, version);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) 
	{
		Log.i(this.getClass().toString(), "Building DB");
		
		db.execSQL(D_PROPERTIES);
		
		db.execSQL(D_PROP_INDEX);
		
		db.execSQL("INSERT INTO " + T_PROPERTIES + "(" + C_ADDRESS + "," + C_BEDROOMS + ") VALUES ('Puan 1917', 1)");
		db.execSQL("INSERT INTO " + T_PROPERTIES + "(" + C_ADDRESS + "," + C_BEDROOMS + ") VALUES ('Crocce 1853', 2)");
		db.execSQL("INSERT INTO " + T_PROPERTIES + "(" + C_ADDRESS + "," + C_BEDROOMS + ") VALUES ('Formosa 4910', 2)");

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
		upgradeToVersion10(db);
		upgradeToVersion11(db);
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
		if (oldVersion < 10)
		{
			try
			{
				upgradeToVersion10(db);
			}
			catch(SQLException e)
			{
				Log.i(this.getClass().toString(), e.getMessage());
			}
		}
		if (oldVersion < 11)
		{
			try
			{
				upgradeToVersion11(db);
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
		db.execSQL("ALTER TABLE " + T_PROPERTIES + " ADD " + C_CONFIRMED + " INTEGER NOT NULL DEFAULT 0");
		
		Log.i(this.getClass().toString(), "Update to version 2 complete");
	}
	
	private void upgradeToVersion3(SQLiteDatabase db)
	{
		db.execSQL(D_RATING);
		
		db.execSQL(D_RATING_INDEX);
		
		db.execSQL("INSERT INTO RATINGS(_id, re_rating) VALUES(1, 'Pobre')");
		db.execSQL("INSERT INTO RATINGS(_id, re_rating) VALUES(2, 'Regular')");
		db.execSQL("INSERT INTO RATINGS(_id, re_rating) VALUES(3, 'Bueno')");
		db.execSQL("INSERT INTO RATINGS(_id, re_rating) VALUES(4, 'Muy bueno')");
		db.execSQL("INSERT INTO RATINGS(_id, re_rating) VALUES(5, 'Excelente')");
		
		db.execSQL("ALTER TABLE " + T_PROPERTIES + " ADD " + C_RATING_ID + " INTEGER NOT NULL DEFAULT 2");

		Log.i(this.getClass().toString(), "Update to version 3 complete");
	}
	
	private void upgradeToVersion4(SQLiteDatabase db)
	{
		db.execSQL("ALTER TABLE " + T_PROPERTIES + " ADD " + C_OWNER_URI + " TEXT");
		
		Log.i(this.getClass().toString(), "Update to version 4 complete");
	}
	
	private void upgradeToVersion5(SQLiteDatabase db)
	{
		db.execSQL("ALTER TABLE " + T_PROPERTIES + " ADD " + C_LATITUDE + " REAL");
		db.execSQL("ALTER TABLE " + T_PROPERTIES + " ADD " + C_LONGITUDE + " REAL");
		
		Log.i(this.getClass().toString(), "Update to version 5 complete");
	}
	
	private void upgradeToVersion6(SQLiteDatabase db)
	{
		db.execSQL("ALTER TABLE " + T_PROPERTIES + " ADD " + C_IMAGE + " TEXT");
		
		Log.i(this.getClass().toString(), "Update to version 6 complete");
	}
	
	private void upgradeToVersion7(SQLiteDatabase db)
	{
		db.execSQL(D_PROP_TYPES);
		
		db.execSQL(D_PROP_TYPE_INDEX);
		
		db.execSQL("INSERT INTO " + T_PROP_TYPES 
				+ "(" + C_ID + ", " + C_PROP_TYPE_NAME + ")"
				+ " VALUES (1, 'Chalet')");
		db.execSQL("INSERT INTO " + T_PROP_TYPES 
				+ "(" + C_ID + ", " + C_PROP_TYPE_NAME + ")"
				+ " VALUES (2, 'Departamento')");
		db.execSQL("INSERT INTO " + T_PROP_TYPES 
				+ "(" + C_ID + ", " + C_PROP_TYPE_NAME + ")"
				+ " VALUES (3, 'Local')");
		
		db.execSQL("ALTER TABLE " + T_PROPERTIES + " ADD " + C_TYPE_ID + " INTEGER NOT NULL DEFAULT 1");
		
		Log.i(this.getClass().toString(), "Update to version 7 complete");
	}
	
	private void upgradeToVersion8(SQLiteDatabase db)
	{
		db.execSQL(D_ROOM_TYPES);
		
		db.execSQL(D_ROOM_TYPE_INDEX);
		
		db.execSQL("INSERT INTO " + T_ROOM_TYPES 
				+ "(" + C_ID + ", " + C_ROOM_TYPE_NAME + ")"
				+ " VALUES (1, 'Sala de estar')");
		db.execSQL("INSERT INTO " + T_ROOM_TYPES 
				+ "(" + C_ID + ", " + C_ROOM_TYPE_NAME + ")"
				+ " VALUES (2, 'Cocina')");
		db.execSQL("INSERT INTO " + T_ROOM_TYPES 
				+ "(" + C_ID + ", " + C_ROOM_TYPE_NAME + ")"
				+ " VALUES (3, 'Dormitorio')");

//		db.execSQL("ALTER TABLE " + TABLE_PROPERTIES + " ADD re_room_type_id INTEGER NOT NULL DEFAULT 1");
		
		Log.i(this.getClass().toString(), "Update to version 8 complete");
	}
	
	private void upgradeToVersion9(SQLiteDatabase db)
	{
		db.execSQL(D_ROOMS);
		
		db.execSQL(D_ROOMS_INDEX);
		
		db.execSQL(D_PROPS_ROOMS);

		db.execSQL("INSERT INTO " + T_ROOMS 
				+ "(" + C_ID + ", " + C_ROOM_TYPE_ID + ", " + T_ROOM_X + ", " + T_ROOM_Y + " )"
				+ " VALUES (1, 1, 4, 4)");
		db.execSQL("INSERT INTO " + T_ROOMS 
				+ "(" + C_ID + ", " + C_ROOM_TYPE_ID + ", " + T_ROOM_X + ", " + T_ROOM_Y + " )"
				+ " VALUES (2, 2, 3, 2)");
		db.execSQL("INSERT INTO " + T_ROOMS 
				+ "(" + C_ID + ", " + C_ROOM_TYPE_ID + ", " + T_ROOM_X + ", " + T_ROOM_Y + " )"
				+ " VALUES (3, 3, 3, 4)");
		
		db.execSQL("INSERT INTO " + T_PROPS_ROOMS
				+ "(" + C_PROP_ID + ", " + C_ROOM_ID + ")"
				+ "VALUES (1, 1)");
		db.execSQL("INSERT INTO " + T_PROPS_ROOMS
				+ "(" + C_PROP_ID + ", " + C_ROOM_ID + ")"
				+ "VALUES (1, 2)");
		db.execSQL("INSERT INTO " + T_PROPS_ROOMS
				+ "(" + C_PROP_ID + ", " + C_ROOM_ID + ")"
				+ "VALUES (1, 3)");
		
		db.execSQL("INSERT INTO " + T_ROOMS 
				+ "(" + C_ID + ", " + C_ROOM_TYPE_ID + ", " + T_ROOM_X + ", " + T_ROOM_Y + " )"
				+ " VALUES (4, 1, 5, 3)");
		db.execSQL("INSERT INTO " + T_ROOMS 
				+ "(" + C_ID + ", " + C_ROOM_TYPE_ID + ", " + T_ROOM_X + ", " + T_ROOM_Y + " )"
				+ " VALUES (5, 2, 2, 2)");
		db.execSQL("INSERT INTO " + T_ROOMS 
				+ "(" + C_ID + ", " + C_ROOM_TYPE_ID + ", " + T_ROOM_X + ", " + T_ROOM_Y + " )"
				+ " VALUES (6, 3, 4, 4)");

		db.execSQL("INSERT INTO " + T_PROPS_ROOMS
				+ "(" + C_PROP_ID + ", " + C_ROOM_ID + ")"
				+ "VALUES (2, 4)");
		db.execSQL("INSERT INTO " + T_PROPS_ROOMS
				+ "(" + C_PROP_ID + ", " + C_ROOM_ID + ")"
				+ "VALUES (2, 5)");
		db.execSQL("INSERT INTO " + T_PROPS_ROOMS
				+ "(" + C_PROP_ID + ", " + C_ROOM_ID + ")"
				+ "VALUES (2, 6)");
		
		Log.i(this.getClass().toString(), "Update to version 9 complete");
	}
	
	private void upgradeToVersion10(SQLiteDatabase db)
	{
		db.execSQL("ALTER TABLE " + T_PROPERTIES + " ADD " + C_PRICE + " INTEGER");
		db.execSQL("ALTER TABLE " + T_PROPERTIES + " ADD " + C_SURFACE_BUILT + " REAL");
		db.execSQL("ALTER TABLE " + T_PROPERTIES + " ADD " + C_SURFACE_PARCEL + " REAL");
		
		db.execSQL(D_SERVICES);
		
		db.execSQL(D_SERVICES_INDEX);
		
		db.execSQL(D_PROPS_SERVICES);
		
		db.execSQL("INSERT INTO " + T_SERVICES
				+ "(" + C_ID + ", " + C_SERVICE_NAME + ")"
				+ "VALUES(1, 'TV cable')");
		db.execSQL("INSERT INTO " + T_SERVICES
				+ "(" + C_ID + ", " + C_SERVICE_NAME + ")"
				+ "VALUES(2, 'TV satelital')");
		db.execSQL("INSERT INTO " + T_SERVICES
				+ "(" + C_ID + ", " + C_SERVICE_NAME + ")"
				+ "VALUES(3, 'Internet')");
		db.execSQL("INSERT INTO " + T_SERVICES
				+ "(" + C_ID + ", " + C_SERVICE_NAME + ")"
				+ "VALUES(4, 'TelŽfono')");
		db.execSQL("INSERT INTO " + T_SERVICES
				+ "(" + C_ID + ", " + C_SERVICE_NAME + ")"
				+ "VALUES(5, 'Monitoreo')");
		db.execSQL("INSERT INTO " + T_SERVICES
				+ "(" + C_ID + ", " + C_SERVICE_NAME + ")"
				+ "VALUES(6, 'Seguridad privada')");
	}
	
	private void upgradeToVersion11(SQLiteDatabase db)
	{
		db.execSQL(D_OPERATIONS);
		db.execSQL(D_OPERATIONS_INDEX);
		db.execSQL(D_PROPS_OPS);
		
		db.execSQL("INSERT INTO " + T_OPERATIONS_TYPES 
				+ "(" + C_ID + ", " + C_OP_TYPE_NAME + ")" 
				+ "VALUES(1, 'Alquiler comercial')" );
		
		db.execSQL("INSERT INTO " + T_OPERATIONS_TYPES 
				+ "(" + C_ID + ", " + C_OP_TYPE_NAME + ")" 
				+ "VALUES(2, 'Alquiler residencial')" );
		
		db.execSQL("INSERT INTO " + T_OPERATIONS_TYPES 
				+ "(" + C_ID + ", " + C_OP_TYPE_NAME + ")" 
				+ "VALUES(3, 'Alquiler vacacional')" );
		
		db.execSQL("INSERT INTO " + T_OPERATIONS_TYPES 
				+ "(" + C_ID + ", " + C_OP_TYPE_NAME + ")" 
				+ "VALUES(4, 'Venta')" );
		
		db.execSQL("INSERT INTO " + T_OPERATIONS_TYPES 
				+ "(" + C_ID + ", " + C_OP_TYPE_NAME + ")" 
				+ "VALUES(5, 'Permuta')" );
	}
}
