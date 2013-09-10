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
	
	public SQLiteDatabase getDb()
	{
		return db;
	}

	public String getManagedTable() 
	{
		return managedTable;
	}

	public void setManagedTable(String managedTable) 
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

	public Cursor getList()
	{
		Cursor c = db.query(true, managedTable, columns, null, null, null, null, null, null);
		
		return c;		
	}
	
	/***
	 * Fetch a specific record from the database.
	 * @param id Row identifier.
	 * @return Cursor containing the requested row.
	 * @throws SQLException
	 */
	public Cursor getRecord(long id) throws SQLException
	{
		Cursor c = db.query(true, this.getManagedTable(), columns, C_COLUMN_ID + "=" + id, null, null, null, null, null);
		
		if (c != null)
		{
			c.moveToFirst();
		}
		
		return c;
	}
	
	/***
	 * Inserts values into a new table record.
	 * @param reg The set of values to insert.
	 * @return 
	 */
	public long insert(ContentValues reg)
	{
		if (db == null)
		{
			open();
		}
		
		return db.insert(this.getManagedTable(), null, reg);
	}

	public DBAdapter open() throws SQLException
	{
		dbHelper = new DBHelper(context);
		db = dbHelper.getWritableDatabase();
		
		return this;
	}
	
	public long update(ContentValues reg)
	{
		long result = 0;
		
		if (db == null)
		{
			open();
		}
		
		if (reg.containsKey(C_COLUMN_ID))
		{
			long id = reg.getAsLong(C_COLUMN_ID);
			
			reg.remove(C_COLUMN_ID);
			
			result = db.update(this.getManagedTable(), reg, "_id=" + id, null);
		}
		
		return result;
	}
}
