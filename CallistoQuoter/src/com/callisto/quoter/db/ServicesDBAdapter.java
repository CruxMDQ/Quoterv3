package com.callisto.quoter.db;

import android.content.Context;

public class ServicesDBAdapter extends DBAdapter
{
	static public final String T_SERVICES = "SERVICES",
			LABEL_SERVICES = "Servicios";

	static public final String C_ID = "_id",
			C_SERVICE_NAME = "re_serv_name",
			C_SERVICE_DETAILS = "re_serv_details";

	public ServicesDBAdapter(Context context)
	{
		super(context);
		this.setManagedTable(T_SERVICES);
		this.setKeyColumn(C_SERVICE_NAME);
		this.setColumns(new String[] { C_ID,
				C_SERVICE_NAME,
				C_SERVICE_DETAILS });
	}
}
