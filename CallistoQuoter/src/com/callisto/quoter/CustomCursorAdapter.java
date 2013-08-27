package com.callisto.quoter;

import com.callisto.quoter.DB.PropDBAdapter;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

public class CustomCursorAdapter extends CursorAdapter 
{
	private PropDBAdapter dbAdapter = null;
	
	@SuppressWarnings("deprecation")
	public CustomCursorAdapter(Context context, Cursor c)
	{
		super(context, c);
		dbAdapter = new PropDBAdapter(context);
		dbAdapter.open();
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor)
	{
		TextView tv = (TextView) view;
		
		tv.setText(cursor.getString(cursor.getColumnIndex(PropDBAdapter.C_PROP_ADDRESS)));
		
		/*
		 * FIELD COLORING IMPLEMENTED ON DATABASE UPGRADE STEP
		 */
		if (cursor.getInt(cursor.getColumnIndex(PropDBAdapter.C_PROP_CONFIRMED)) == 0)
		{
			tv.setTextColor(Color.GRAY);
		}
		else
		{
			tv.setTextColor(Color.BLACK);
		}
	}
	
	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent)
	{
		final LayoutInflater inflater = LayoutInflater.from(context);
		final View view = inflater.inflate(android.R.layout.simple_dropdown_item_1line, parent, false);
		
		return view;
	}
}
