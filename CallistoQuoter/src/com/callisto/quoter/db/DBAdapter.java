package com.callisto.quoter.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public abstract class DBAdapter
{
	static public final String C_ID = "_id";

	protected Context context;
	protected DBHelper dbHelper;
	protected SQLiteDatabase db;
	protected String keyColumn, managedTable;
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
	
	public String getKeyColumn()
	{
		return keyColumn;
	}
	
	public void setKeyColumn(String columnName)
	{
		this.keyColumn = columnName;
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

	public long delete(long id)
	{
		long result;

		if (db == null)
		{
			open();
		}
		result = db.delete(this.getManagedTable(), "_id=" + id, null);

		// close();

		return result;
	}

	/***
	 * 
	 * @return Cursor containing all table rows and columns.
	 * @throws SQLException
	 */
	public Cursor getCursor() throws SQLException
	{
		Cursor c = db.query(true, this.getManagedTable(), columns, null, null,
				null, null, null, null);

		return c;
	}

	/***
	 * 
	 * @param filter
	 *            String value used to filter results.
	 * @return Cursor containing filtered table rows and columns.
	 * @throws SQLException
	 */
	public Cursor getCursor(String filter) throws SQLException
	{
		Cursor c = db.query(true, this.getManagedTable(), columns, filter,
				null, null, null, null, null);

		return c;
	}

	public long getId(String filter)
	{
		return this.getId(filter, getKeyColumn());
	}
	
	public Cursor getList()
	{
		Cursor c = db.query(true, managedTable, columns, null, null, null,
				null, null, null);

		return c;
	}

	/***
	 * Fetch a specific record from the database.
	 * 
	 * @param id
	 *            Row identifier.
	 * @return Cursor containing the requested row.
	 * @throws SQLException
	 */
	public Cursor getRecord(long id) throws SQLException
	{
		Cursor c = db.query(true, this.getManagedTable(), columns, C_ID + "="
				+ id, null, null, null, null, null);

		if (c != null)
		{
			c.moveToFirst();
		}

		return c;
	}

	/***
	 * Inserts values into a new table record.
	 * 
	 * @param reg
	 *            The set of values to insert.
	 * @return
	 */
	public long insert(ContentValues reg)
	{
		long result;

		if (db == null)
		{
			open();
		}

		result = db.insert(this.getManagedTable(), null, reg);

		// close();

		return result;
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

		if (reg.containsKey(C_ID))
		{
			long id = reg.getAsLong(C_ID);

			// reg.remove(C_COLUMN_ID);

			result = db.update(this.getManagedTable(), reg, "_id=" + id, null);
		}

		// close();

		return result;
	}

	/***
	 * 
	 * @param filter
	 *            Value to match for result.
	 * @param columnName
	 *            Column to search.
	 * @return Row identifier.
	 * @throws SQLException
	 */
	private long getId(String filter, String columnName) throws SQLException
	{
		long result = 0;

//		String query = "SELECT " + C_ID + " FROM " + this.getManagedTable()
//				+ " WHERE " + columnName + " = " + filter;
		
		Cursor c = this.getCursor();

//		Cursor c = db.rawQuery(
//				"SELECT " + C_ID + " FROM " + this.getManagedTable()
//						+ " WHERE " + columnName + " = ?", new String[] { filter });
		
		while (c.moveToNext())
		{
			String t = c.getString(c.getColumnIndexOrThrow(columnName));
			
			if (t.compareTo(filter) == 0)
			{
				result = c.getLong(c.getColumnIndexOrThrow(C_ID));
			}
		}
		return result;
	}
}
