package com.callisto.quoter.db;

import android.content.Context;

public class OpTypesDBAdapter extends DBAdapter
{
	static public final String T_OPERATIONS = "OPERATIONS_TYPES",
			LABEL_OP_TYPES = "Tipo de operaci—n";

	static public final String C_ID = "_id",
			C_OP_TYPE_NAME = "re_op_name";

	public OpTypesDBAdapter(Context context)
	{
		super(context);
		this.setManagedTable(T_OPERATIONS);
		this.setKeyColumn(C_OP_TYPE_NAME);
		this.setColumns(new String[] { C_ID,
				C_OP_TYPE_NAME });
	}
}
