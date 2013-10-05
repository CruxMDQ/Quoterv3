package com.callisto.quoter.ui;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.callisto.quoter.Config;
import com.callisto.quoter.R;
import com.callisto.quoter.db.PropCursorAdapter;
import com.callisto.quoter.db.PropDBAdapter;

public class PropListActivity extends ListActivity 
{
	static public final String C_MODE = "mode";
	static public final int C_VIEW = 551,
			C_CREATE = 552,
			C_EDIT = 553,
			C_DELETE = 554,
			C_CONFIG = 555,
			C_PROP_TYPE_ADD = 556;
	
	private PropDBAdapter dbAdapter;
//	private PropTypesDBAdapter propTypesDBAdapter; 
	private Cursor mCursorProperties;
//	private Cursor mCursorPropTypes;
	private PropCursorAdapter cursorAdapter;
	private ListView list;
	
	private String filter;
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		/*
		 * Ensure we're answering the petition previously made
		 */
		switch(requestCode)
		{
			case C_CREATE:
			{
				if (resultCode == RESULT_OK)
				{
					Log.i(this.getClass().toString(), "Notice: house created successfully");
					query();
				}
				else
				{
					Log.i(this.getClass().toString(), "WARNING: Failed to create new house!");
				}
				
				break;
			}
			case C_VIEW:
			{
				Log.i(this.getClass().toString(), "Detail view returned " + resultCode);

				if (resultCode == RESULT_OK)
				{
					query();
				}
				break;
			}
			case C_EDIT:
			{	
				Log.i(this.getClass().toString(), "Edition returned " + resultCode);

				if (resultCode == RESULT_OK)
				{
					query();
				}

				break;
			}
			case C_CONFIG:
			{
				/*
				 * No results have been defined on the PreferenceActivity,
				 * so we always reload them (?!?)
				 */
				getPreferences();
				query();
				
				break;
			}
			default:
			{
				query();
				super.onActivityResult(requestCode, resultCode, data);
			}
		}
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item)
	{
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		Intent i;
		
		switch(item.getItemId())
		{
			case C_DELETE:
			{
				delete(info.id);
				return true;
			}			
			case C_VIEW:
			{
				view(info.id);
				return true;
			}		
			case C_EDIT:
			{
				i = new Intent(PropListActivity.this, PropDetailActivity.class);
				i.putExtra(C_MODE, C_EDIT);
				i.putExtra(PropDBAdapter.C_COLUMN_ID, info.id);
				
				startActivityForResult(i, C_EDIT);
				return true;
			}						
		}
		
		return super.onContextItemSelected(item);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_house_list);
		
		getPreferences();
		
		list = (ListView) findViewById(android.R.id.list);
		
		dbAdapter = new PropDBAdapter(this);
		dbAdapter.open();
		
		query();
		
		registerForContextMenu(this.getListView());
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo)
	{
		menu.setHeaderTitle(mCursorProperties.getString(mCursorProperties.getColumnIndex(PropDBAdapter.C_PROP_ADDRESS)));
		menu.add(Menu.NONE, C_VIEW, Menu.NONE, R.string.menu_view);
		menu.add(Menu.NONE, C_EDIT, Menu.NONE, R.string.menu_edit);
		menu.add(Menu.NONE, C_DELETE, Menu.NONE, R.string.menu_delete);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		getMenuInflater().inflate(R.menu.menu_house_list, menu);
		return true;
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int pos, long id)
	{
		super.onListItemClick(l, v, pos, id);
		
		view(id);
	}
	
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item)
	{
		Intent i;
		
		switch(item.getItemId())
		{
			case R.id.menu_create:
			{
				i = new Intent(PropListActivity.this, PropDetailActivity.class);
				i.putExtra(C_MODE, C_CREATE);
				startActivityForResult(i, C_CREATE);
				return true;
			}
			case R.id.menu_preferences:
			{
				i = new Intent(PropListActivity.this, Config.class);
				startActivityForResult(i, C_CONFIG);
				return true;
			}
		}
		
		return super.onMenuItemSelected(featureId, item);
	}
	
//	private void addPropType()
//	{
//		LayoutInflater inflater = LayoutInflater.from(this);
//		
//		View addView = inflater.inflate(R.layout.dialog_prop_type_add, null);
//		
//		final AddPropTypeDialogWrapper wrapper = new AddPropTypeDialogWrapper(addView);
//		
//		new AlertDialog.Builder(this)
//			.setTitle("New property type")
//			.setView(addView)
//			.setPositiveButton(R.string.ok,
//					new DialogInterface.OnClickListener() {
//						
//						@Override
//						public void onClick(DialogInterface dialog, int which) 
//						{
//							processAddPropType(wrapper);
//							
//						}
//					})
//			.setNegativeButton(R.string.cancel, 
//					new DialogInterface.OnClickListener() 
//					{
//						@Override
//						public void onClick(DialogInterface dialog, int which) 
//						{
//							
//						}
//					})
//			.show();
//	}
	
	private void delete(final long id)
	{
		AlertDialog.Builder dialogDelete = new AlertDialog.Builder(this);
		
		dialogDelete.setIcon(android.R.drawable.ic_dialog_alert)
			.setTitle(getResources().getString(R.string.house_delete_title))
			.setMessage(getResources().getString(R.string.house_delete_confirm))
			.setCancelable(false)
			.setPositiveButton(getResources().getString(android.R.string.ok), new DialogInterface.OnClickListener() 
			{
				@Override
				public void onClick(DialogInterface dialog, int which) 
				{
					dbAdapter.delete(id);
					Toast.makeText(PropListActivity.this, R.string.house_delete_notice, Toast.LENGTH_LONG)
						.show();
					
					setResult(RESULT_OK);
//					finish();
				}
			})
			.setNegativeButton(android.R.string.no, null)
			.show();
		
	}
	
	private void getPreferences()
	{
		/*
		 * Retrieve preferences
		 */
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		
		if (prefs.getBoolean("hide_unconfirmed_houses", false))
		{
			this.filter = PropDBAdapter.C_PROP_CONFIRMED + " = 1";
		}
		else
		{
			this.filter = null;
		}			
	}
	
//	private void populatePropTypes()
//	{
//		String[] from = new String[] { RatingsDBAdapter.C_COLUMN_RATING_NAME };
//		
//		int[] to = new int[] { android.R.id.text1 };
//
//		propTypesDBAdapter = new PropTypesDBAdapter(this);
//		propTypesDBAdapter.open();
//		
//		mCursorPropTypes = propTypesDBAdapter.getList();
//		
//		@SuppressWarnings("deprecation")
//		SimpleCursorAdapter adapterRatings = new SimpleCursorAdapter(this, 
//				android.R.layout.simple_spinner_item, 
//				mCursorPropTypes, 
//				from,		/*new String[] { RatingsDBAdapter.C_COLUMN_RATING_NAME }, */
//				to);		/*new int[] { android.R.id.text1 } */
//		
//		adapterRatings.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//		
//		// TO DO ASSIGN THIS ADAPTER TO A SPINNER!
//		//spinnerRating.setAdapter(adapterRatings);
//		
//	}

//	private void processAddPropType(AddPropTypeDialogWrapper wrapper) 
//	{
//	    ContentValues reg = new ContentValues();
//	    
//	    reg.put(PropTypesDBAdapter.C_COLUMN_PROP_TYPES_NAME, wrapper.getName());
//	    
//	    propTypesDBAdapter.insert(reg);
//	    
//		populatePropTypes();
//	}

	@SuppressWarnings("deprecation")
	private void query() 
	{
		mCursorProperties = dbAdapter.getCursor(filter);
		
		startManagingCursor(mCursorProperties);
		
		cursorAdapter = new PropCursorAdapter(this, mCursorProperties);
		
		list.setAdapter(cursorAdapter);
	}
	
	private void view(long id)
	{
		Intent i = new Intent(PropListActivity.this, PropDetailActivity.class);
		i.putExtra(C_MODE, C_VIEW);
		i.putExtra(PropDBAdapter.C_COLUMN_ID, id);
		
		startActivityForResult(i, C_VIEW);
	}
	
	class AddPropTypeDialogWrapper
	{
		EditText nameField = null;
		View base = null;
		
		AddPropTypeDialogWrapper(View base)
		{
			this.base = base;
			nameField = (EditText) base.findViewById(R.id.txtName);
		}
		
		String getName()
		{
			return (getNameField().getText().toString());
		}
		
		private EditText getNameField()
		{
			if (nameField == null)
			{
				nameField = (EditText) base.findViewById(R.id.txtName);
			}
			
			return (nameField);
		}
	}
}
