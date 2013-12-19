package com.callisto.quoter.wizard;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.IBinder;

import com.callisto.quoter.R;
import com.callisto.quoter.db.DBAdapter;
import com.callisto.quoter.db.OpTypesDBAdapter;
import com.callisto.quoter.db.PropDBAdapter;
import com.callisto.quoter.db.PropTypesDBAdapter;
import com.callisto.quoter.db.ServicesDBAdapter;
import com.callisto.quoter.wizard.model.AbstractWizardModel;
import com.callisto.quoter.wizard.model.MultipleFixedChoicePage;
import com.callisto.quoter.wizard.model.PageList;
import com.callisto.quoter.wizard.model.PropertyAddressPage;
import com.callisto.quoter.wizard.model.SingleFixedChoicePage;

// Original template: WizardPagerSource/SandwichWizardModel
public class RealEstateWizardModel extends AbstractWizardModel
{
	ArrayList<String> propTypesList, opTypesList, servicesList;
	
	public RealEstateWizardModel(Context context)
	{
		super(context);
	}

	// "Dis 'ere not gonna cut it, boss. Da, um, supa-konstruktor fing calls the 'onNewRootPageList' mezod 
	// first, un' only then does da rest."
//	public RealEstateWizardModel(Context context, Bundle extras)
//	{
//		super(context);
//		
//		propTypesList = extras.getStringArrayList("PropTypes");
//		opTypesList = extras.getStringArrayList("opTypes");
//		servicesList = extras.getStringArrayList("servTypes");
//		
//		propTypeChoices = new SingleFixedChoicePage(this, extras.getString("LabelPropType")).setChoices(
//				propTypesList);
//	}
	
	@Override
	protected PageList onNewRootPageList() { return null; };
	
	/* Research sources:
	 * http://stackoverflow.com/questions/8713045/null-pointer-in-getapplicationcontext
	 */
	@Override
	protected PageList onNewRootPageList(Context context)
	{
		propTypesList = pullChoicesFromDB(new PropTypesDBAdapter(context), PropTypesDBAdapter.C_PROP_TYPES_NAME);
		opTypesList = pullChoicesFromDB(new OpTypesDBAdapter(context), OpTypesDBAdapter.C_OP_TYPE_NAME);
		servicesList = pullChoicesFromDB(new ServicesDBAdapter(context), ServicesDBAdapter.C_SERVICE_NAME);
		
		String[] propTypesArray = new String[propTypesList.size()];
		propTypesArray = propTypesList.toArray(propTypesArray);
		
		String[] opTypesArray = new String[opTypesList.size()];
		opTypesArray = opTypesList.toArray(opTypesArray);
		
		String[] servicesArray = new String[servicesList.size()];
		servicesArray = servicesList.toArray(servicesArray);
		
	    return new PageList(
	    		new PropertyAddressPage(
	    				this, "Datos b‡sicos", PropDBAdapter.T_PROPERTIES),
	    		
	    		new SingleFixedChoicePage(
	    				this, 
	    				PropTypesDBAdapter.LABEL_PROP_TYPES, 
	    				PropTypesDBAdapter.T_PROP_TYPES).setChoices(		// "Tipo de propiedad" // getResources().getString(R.string.label_property_type)
	    						propTypesArray),

	    		new MultipleFixedChoicePage(
	    				this, 
	    				OpTypesDBAdapter.LABEL_OP_TYPES,
	    				OpTypesDBAdapter.T_OPERATIONS).setChoices(		// "Tipo de operaciones" // getResources().getString(R.string.label_property_operation)
	    						opTypesArray),
	    						
	    		new MultipleFixedChoicePage(
	    				this, 
	    				ServicesDBAdapter.LABEL_SERVICES,
	    				ServicesDBAdapter.T_SERVICES).setChoices(		// "Servicios disponibles"
	    						servicesArray)
	    		);
	}

	@Override
	public IBinder onBind(Intent intent) { return null; }
	
	private ArrayList<String> pullChoicesFromDB(DBAdapter dbAdapter, String columnName)
	{
		ArrayList<String> result = new ArrayList<String>();
		
		if (dbAdapter != null)
		{
			dbAdapter.open();
			
			Cursor c = dbAdapter.getList();
			
			while (c.moveToNext())
			{
				String value = c.getString(c.getColumnIndex(columnName));
				
				result.add(value);
			}
			
			c.close();
			
			dbAdapter.close();

			return result;
		}
		else
		{
			throw new NullPointerException("Database adapter NOT initialized!");
		}
		
	}
}