package com.callisto.quoter.db;

import android.content.Context;

public class PropsServicesDBAdapter extends DBAdapter
{

	static public final String T_PROPS_ROOMS = "PROPS_SERVICES",
			C_PROP_ID = "re_prop_id",
			C_SERV_ID = "re_serv_id";
	
	public PropsServicesDBAdapter(Context context)
	{
		super(context);
		this.setManagedTable(T_PROPS_ROOMS);
		this.setColumns(new String[] { C_PROP_ID,
			C_SERV_ID });
	}
}
