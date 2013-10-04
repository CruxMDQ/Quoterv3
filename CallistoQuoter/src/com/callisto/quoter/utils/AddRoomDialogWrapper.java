package com.callisto.quoter.utils;

import android.view.View;
import android.widget.Spinner;

import com.callisto.quoter.R;

public class AddRoomDialogWrapper
{
	Spinner spinnerType = null;
	View base = null;
	Object item;
	
	public AddRoomDialogWrapper(View base)
	{
		this.base = base;
		spinnerType = (Spinner) base.findViewById(R.id.spnRoomType);
	}

	public Spinner getSpinner()
	{
		if (spinnerType == null)
		{
			spinnerType = (Spinner) base.findViewById(R.id.spnRoomType);
		}
		
		return (spinnerType);
	}
	
	public Object getSelectedItem()
	{
		return item;
	}
}
