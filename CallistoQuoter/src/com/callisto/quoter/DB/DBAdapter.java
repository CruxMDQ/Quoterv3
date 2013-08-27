package com.callisto.quoter.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public abstract class DBAdapter 
{
	static public final String C_COLUMN_ID = "_id";
	
	protected Context context;
	protected DBHelper dbHelper;
	protected SQLiteDatabase db;
	protected String managedTable;
	protected String[] columns;
	
	public String getTableManaged() 
	{
		return managedTable;
	}

	public void setTableManaged(String managedTable) 
	{
		this.managedTable = managedTable;
	}

	public void setColumns(String[] columns)
	{
		this.columns = columns;
	}
	
	public DBAdapter(Context context)
	{
		this.context = context;
	}

	public void close()
	{
		dbHelper.close();
	}

	public DBAdapter open() throws SQLException
	{
		dbHelper = new DBHelper(context);
		db = dbHelper.getWritableDatabase();
		return this;
	}
	
	public Cursor getList()
	{
		Cursor c = db.query(true, managedTable, columns, null, null, null, null, null, null);
		
		return c;		
	}
	
	public long insert(ContentValues reg)
	{
		return 0;
	}
}
