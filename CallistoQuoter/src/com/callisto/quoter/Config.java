package com.callisto.quoter;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class Config extends PreferenceActivity 
{
	@SuppressWarnings("deprecation")
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		addPreferencesFromResource(R.xml.config);
	}
}
