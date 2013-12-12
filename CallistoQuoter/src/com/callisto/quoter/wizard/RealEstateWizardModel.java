package com.callisto.quoter.wizard;

import java.util.ArrayList;

import android.content.Context;
import android.os.Bundle;

import com.callisto.quoter.wizard.model.AbstractWizardModel;
import com.callisto.quoter.wizard.model.MultipleFixedChoicePage;
import com.callisto.quoter.wizard.model.PageList;
import com.callisto.quoter.wizard.model.SingleFixedChoicePage;

// Original template: WizardPagerSource/SandwichWizardModel
public class RealEstateWizardModel extends AbstractWizardModel
{
	Bundle extras;

	ArrayList<String> propTypesList, opTypesList, servicesList;
	
	SingleFixedChoicePage propTypeChoices;
	MultipleFixedChoicePage opTypeChoices, servChoices;
	
	public RealEstateWizardModel(Context context)
	{
		super(context);

//		propTypes = new PropTypesDBAdapter(context);	
//		Log.d(this.getClass().toString() + "::Constructor", (propTypes != null ? "y" : "n"));
	}
	
	public RealEstateWizardModel(Context context, Bundle extras)
	{
		super(context);
		
		propTypesList = extras.getStringArrayList("PropTypes");
		opTypesList = extras.getStringArrayList("opTypes");
		servicesList = extras.getStringArrayList("servTypes");
		
		propTypeChoices = new SingleFixedChoicePage(this, extras.getString("LabelPropType")).setChoices(
				propTypesList);
	}
	
	@Override
	protected PageList onNewRootPageList()
	{
		return new PageList(
				new SingleFixedChoicePage(this, "Tipo de propiedad").setChoices(
						"Local", "Galp—n", "Chalet", "Dœplex", "Departamento",
						"PH"),

				new MultipleFixedChoicePage(this, "Tipo de operaci—n")
						.setChoices("Venta", "Alquiler residencial",
								"Alquiler comercial", "Alquiler vacacional"));

//		extras.putString("LabelPropType", PropTypesDBAdapter.LABEL_PROP_TYPES);
//		extras.putString("LabelOpTypes", OpTypesDBAdapter.LABEL_OP_TYPES);
//		extras.putString("LabelServices", ServicesDBAdapter.LABEL_SERVICES);
//
//		extras.putStringArrayList("PropTypes", propTypes);
//		extras.putStringArrayList("OpTypes", opTypes);
//		extras.putStringArrayList("ServTypes", servTypes);

//		ArrayList<String> stock_list = new ArrayList<String>();
//	    stock_list.add("stock1");
//	    stock_list.add("stock2");
//	    String[] stockArr = new String[stock_list.size()];
//	    stockArr = stock_list.toArray(stockArr);

//		String[] propTypes = new String[propTypesList.size()];
//		propTypes = propTypesList.toArray(propTypes);
//		
//	    return new PageList(propTypeChoices);
	}
	
}