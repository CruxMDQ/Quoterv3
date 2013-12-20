package com.callisto.quoter.db;

import android.content.Context;

public class PropsOpsDBAdapter extends DBAdapter
{
	static public final String T_PROPS_ROOMS = "PROPS_OPS",
			C_PROP_ID = "re_prop_id",
			C_OP_TYPE_ID = "re_op_type_id",
			C_PRICE = "re_price";
	
	public PropsOpsDBAdapter(Context context)
	{
		super(context);
		this.setManagedTable(T_PROPS_ROOMS);
		this.setColumns(new String[] { C_PROP_ID,
			C_OP_TYPE_ID,
			C_PRICE });
	}
}
