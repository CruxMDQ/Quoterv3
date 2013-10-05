package com.callisto.quoter.ui;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.callisto.quoter.R;
import com.callisto.quoter.db.RoomCursorAdapter;
import com.callisto.quoter.db.RoomTypesDBAdapter;
import com.callisto.quoter.db.RoomsDBAdapter;
import com.callisto.quoter.utils.AddRoomDialogWrapper;

public class RoomListActivity extends ListActivity 
{
	static public final String C_MODE = "mode";
	static public final int C_VIEW = 551,
			C_CREATE = 552,
			C_EDIT = 553,
			C_DELETE = 554,
			C_CONFIG = 555,
			C_PROP_TYPE_ADD = 556;
	
	private Long mPropId;
	
//	private String filter;
	private ListView list;

	private Cursor mCursorRooms;
	private Cursor mCursorRoomTypes; 

	private RoomCursorAdapter cursorAdapter;

	private RoomsDBAdapter roomsDBAdapter;
	private RoomTypesDBAdapter roomTypesDBAdapter;

	private Spinner spinnerRoomType;
	
	AdapterView.OnItemSelectedListener spnLstRoomType;

	protected long mRoomTypeId;

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
					Log.i(this.getClass().toString(), "Notice: room created successfully");
					query();
				}
				else
				{
					Log.i(this.getClass().toString(), "WARNING: Failed to create new room!");
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
				//getPreferences();
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
//		Intent i;
		
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
				return editRoom(item, C_EDIT);
			}						
		}
		
		return super.onContextItemSelected(item);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_room_list);
		
		//getPreferences();
	
		Bundle extras = getIntent().getExtras();
		
		mPropId = extras.getLong("mPropId");
		
	    list = (ListView) findViewById(android.R.id.list);
		
		roomsDBAdapter = new RoomsDBAdapter(this);
		roomsDBAdapter.open();
		
		spnLstRoomType = new AdapterView.OnItemSelectedListener() 
		{
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int pos, long id) 
			{
				mRoomTypeId = id;
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) { }
			
		};

	    query();
		
		registerForContextMenu(this.getListView());
	}

//	 Proper implementation of this requires linking rooms to room types on a single cursor...
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo)
	{
		menu.setHeaderTitle(mCursorRooms.getString(7));		// TODO FIX THIS REFERENCE! IT'S DANGEROUS!

		
		menu.add(Menu.NONE, C_VIEW, Menu.NONE, R.string.menu_view);
		menu.add(Menu.NONE, C_EDIT, Menu.NONE, R.string.menu_edit);
		menu.add(Menu.NONE, C_DELETE, Menu.NONE, R.string.menu_delete);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		getMenuInflater().inflate(R.menu.menu_room_list, menu);
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
		switch(item.getItemId())
		{
			case R.id.menu_create:
			{
				return createNewRoom(item, C_CREATE);
			}
		}
		
		return super.onMenuItemSelected(featureId, item);
	}
	
	@Override
	public void onResume()
	{
		super.onResume();
		
		query();
	}

	private boolean createNewRoom(MenuItem item, final int mode)
	{
//		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		Intent i;

		i = new Intent(RoomListActivity.this, RoomDetailActivity.class);

		LayoutInflater inflater = LayoutInflater.from(this);

		View addView = inflater.inflate(R.layout.dialog_room_add, null);

		final AddRoomDialogWrapper wrapper = new AddRoomDialogWrapper(addView);

		spinnerRoomType = wrapper.getSpinner();
		
		spinnerRoomType.setOnItemSelectedListener(spnLstRoomType);

		populateRoomTypes();

		new AlertDialog.Builder(this)
		.setTitle("Select initial room")
		.setView(addView)
		.setPositiveButton(R.string.ok,
			new DialogInterface.OnClickListener() 
			{
				@Override
				public void onClick(DialogInterface dialog, int which) 
				{
					// TO DO Figure how to get text from a spinner linked to a database via an Adapter (note link to solution when done)
					// COMPLETED: http://stackoverflow.com/questions/5787809/get-spinner-selected-items-text
					
					// TO DO How to get the ID of a table row based on the text displayed on a spinner
					
					TextView t = (TextView) spinnerRoomType.getSelectedView();
					
					startRoomsActivity(mPropId, mRoomTypeId, mode, t.getText().toString());
				}
			})
		.setNegativeButton(R.string.cancel,
			new DialogInterface.OnClickListener()
			{
				@Override
				public void onClick(DialogInterface dialog, int which) {

				}
			}
		).show();			
		
		i.putExtra(C_MODE, mode);
		i.putExtra("mPropId", mPropId);
//		i.putExtra("mRoomId", info.id);
//		i.putExtra(RoomsDBAdapter.C_COLUMN_ID, info.id);

//		startActivityForResult(i, mode);
		return true;
	}
	
	private boolean editRoom(MenuItem item, int mode)
	{
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		Intent i;

		i = new Intent(RoomListActivity.this, RoomDetailActivity.class);
		
		i.putExtra(C_MODE, mode);
		i.putExtra("mPropId", mPropId);
		i.putExtra("mRoomId", info.id);
		i.putExtra(RoomsDBAdapter.C_COLUMN_ID, info.id);

		startActivityForResult(i, mode);
		return true;
	}
		
	private void delete(final long id)
	{
		AlertDialog.Builder dialogDelete = new AlertDialog.Builder(this);
		
		dialogDelete.setIcon(android.R.drawable.ic_dialog_alert)
			.setTitle(getResources().getString(R.string.room_delete_title))
			.setMessage(getResources().getString(R.string.room_delete_confirm))
			.setCancelable(false)
			.setPositiveButton(getResources().getString(android.R.string.ok), new DialogInterface.OnClickListener() 
			{
				@Override
				public void onClick(DialogInterface dialog, int which) 
				{
					roomsDBAdapter.delete(id);
					Toast.makeText(RoomListActivity.this, R.string.room_delete_notice, Toast.LENGTH_LONG)
						.show();
					
					setResult(RESULT_OK);
					finish();
				}
			})
			.setNegativeButton(android.R.string.no, null)
			.show();
		
	}
	
	private void populateRoomTypes() 
	{
		String[] from = new String[] { RoomTypesDBAdapter.C_COLUMN_ROOM_TYPES_NAME };
		
		int[] to = new int[] { android.R.id.text1 };

		roomTypesDBAdapter = new RoomTypesDBAdapter(this);
		roomTypesDBAdapter.open();
		
		mCursorRoomTypes = roomTypesDBAdapter.getList();
		
		@SuppressWarnings("deprecation")
		SimpleCursorAdapter adapterRoomTypes = new SimpleCursorAdapter(this, 
				android.R.layout.simple_spinner_item, 
				mCursorRoomTypes, 
				from,		
				to);		
		
		adapterRoomTypes.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		spinnerRoomType.setAdapter(adapterRoomTypes);
	}

	@SuppressWarnings("deprecation")
	private void query() 
	{
		mCursorRooms = roomsDBAdapter.getRoomsForProperty(mPropId);

		startManagingCursor(mCursorRooms);

		cursorAdapter = new RoomCursorAdapter(this, mCursorRooms);

		list.setAdapter(cursorAdapter);
	}

	private void view(long id)
	{
		Intent i = new Intent(RoomListActivity.this, RoomDetailActivity.class);

		i.putExtra(C_MODE, C_VIEW);
		i.putExtra("mPropId", mPropId);
		i.putExtra(RoomsDBAdapter.C_COLUMN_ID, id);
		
		startActivityForResult(i, C_VIEW);
	}
	
	/***
	 * Starts room tab host activity. All parameters are bundled as extras with the same name.
	 * @param propId ID of the property being quoted
	 * @param roomTypeId First room type ID
	 * @param roomType First room type name, used for titling the tab
	 */
	public void startRoomsActivity(long propId, long roomTypeId, int mode, String roomType)
	{
		Intent intent = new Intent();
		
		intent.setClass(this, RoomDetailActivity.class);

		intent.putExtra(C_MODE, mode);
		
		intent.putExtra("mPropId", propId);
		
		intent.putExtra("mRoomTypeId", roomTypeId);
		
		intent.putExtra("mRoomType", roomType);

		startActivity(intent);
	}
}
