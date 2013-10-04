package com.callisto.quoter.utils;

import android.view.View;
import android.widget.EditText;

import com.callisto.quoter.R;

public class AddTypeDialogWrapper
{
	EditText nameField = null;
	View base = null;
	
	public AddTypeDialogWrapper(View base)
	{
		this.base = base;
		nameField = (EditText) base.findViewById(R.id.txtName);
	}
	
	public String getName()
	{
		return (getNameField().getText().toString());
	}
	
	public EditText getNameField()
	{
		if (nameField == null)
		{
			nameField = (EditText) base.findViewById(R.id.txtName);
		}
		
		return (nameField);
	}
}
