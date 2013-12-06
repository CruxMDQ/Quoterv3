package com.callisto.quoter.wizard;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.callisto.quoter.db.PropTypesDBAdapter;
import com.callisto.quoter.wizard.model.AbstractWizardModel;
import com.callisto.quoter.wizard.model.MultipleFixedChoicePage;
import com.callisto.quoter.wizard.model.PageList;
import com.callisto.quoter.wizard.model.SingleFixedChoicePage;

// Original template: WizardPagerSource/SandwichWizardModel
public class RealEstateWizardModel extends AbstractWizardModel
{
	PropTypesDBAdapter propTypes;
	
	public RealEstateWizardModel(Context context)
	{
		super(context);

//		propTypes = new PropTypesDBAdapter(context);	
//		Log.d(this.getClass().toString() + "::Constructor", (propTypes != null ? "y" : "n"));
	}

	@Override
	protected PageList onNewRootPageList()
	{
//		return new PageList(
//				new SingleFixedChoicePage(this, "Tipo de propiedad").setChoices(
//						pullChoicesFromDB(PropTypesDBAdapter.C_PROP_TYPES_NAME))
//						);
				
		return new PageList(
				new SingleFixedChoicePage(this, "Tipo de propiedad").setChoices(
						"Local", "Galp—n", "Chalet", "Dœplex", "Departamento",
						"PH"),

				new MultipleFixedChoicePage(this, "Tipo de operaci—n")
						.setChoices("Venta", "Alquiler residencial",
								"Alquiler comercial", "Alquiler vacacional"));
	}
	
	private ArrayList<String> pullChoicesFromDB(String columnName)
	{
		ArrayList<String> result = new ArrayList<String>();
		
		propTypes.open();
		
		Cursor c = propTypes.getList();
		
		while (c.moveToNext())
		{
			String value = c.getString(c.getColumnIndex(columnName));
			
			result.add(value);
		}
		
		c.close();
		
		propTypes.close();

		return result;
	}

//	private ArrayList<String> getPropTypes()
//	{
//		ArrayList<String> result = new ArrayList<String>();
//		
////		String[] from = new String[] { PropTypesDBAdapter.C_PROP_TYPES_NAME };
////		
////		int[] to = new int[] { android.R.id.text1 };
//
//		PropTypesDBAdapter propTypes = new PropTypesDBAdapter(mContext);
//		propTypes.open();
//		
//		Cursor c = propTypes.getList();
//		
//		while (c.moveToNext())
//		{
//			String value = c.getString(c.getColumnIndex(PropTypesDBAdapter.C_PROP_TYPES_NAME));
//			
//			result.add(value);
//		}
//		
//		c.close();
//		
//		propTypes.close();
//		
//		return result;
//	}
//
//	private void getRoomTypes() 
//	{
//		String[] from = new String[] { RoomTypesDBAdapter.C_ROOM_TYPES_NAME };
//		
//		int[] to = new int[] { android.R.id.text1 };
//
////		mRoomTypes = new RoomTypesDBAdapter(mContext);
////		mRoomTypes.open();
//		
//		Cursor c = new RoomTypesDBAdapter(mContext).open().getList();
//		
//		@SuppressWarnings("deprecation")
//		
//		SimpleCursorAdapter adapterRoomTypes = new SimpleCursorAdapter(mContext, 
//				android.R.layout.simple_spinner_item, 
//				c, 
//				from,		/*new String[] { RatingsDBAdapter.C_COLUMN_RATING_NAME }, */
//				to);		/*new int[] { android.R.id.text1 } */
//		
//		adapterRoomTypes.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//	}

}
