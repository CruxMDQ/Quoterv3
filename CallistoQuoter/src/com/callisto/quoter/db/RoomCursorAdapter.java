package com.callisto.quoter.db;


import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;

public class RoomCursorAdapter extends CursorAdapter {

	private DBAdapter dbAdapter;

	@SuppressWarnings("deprecation")
	public RoomCursorAdapter(Context context, Cursor c)
	{
		super(context, c);
		dbAdapter = new RoomsDBAdapter(context);
		dbAdapter.open();
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		// TODO Auto-generated method stub

	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent)
	{
		final LayoutInflater inflater = LayoutInflater.from(context);
		final View view = inflater.inflate(android.R.layout.simple_dropdown_item_1line, parent, false);
		
		return view;
	}

}
