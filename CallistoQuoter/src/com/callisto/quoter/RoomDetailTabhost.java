package com.callisto.quoter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

import com.callisto.quoter.DB.RoomTypesDBAdapter;
import com.callisto.quoter.DB.RoomsDBAdapter;
import com.callisto.quoter.interfaces.Observable;
import com.callisto.quoter.interfaces.Observer;

import android.app.AlertDialog;
import android.app.TabActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;

// DONE Figure how to get text from a spinner linked to a database via an Adapter (note link to solution when done)
// http://stackoverflow.com/questions/5787809/get-spinner-selected-items-text

/***
 *  ****** DEPRECATED: getting rooms from DB here is USELESS, actual room info must be grabbed by RoomDetailActivity ******
 * PSEUDOCODE FOR DATABASE RETRIEVAL LOGIC
 * 
 * 
 * void getRoomsFromDB
 * {
 *     Cursor rooms = get rooms from database
 *     {
 *     		cursor getRooms(parameter1: property id, integer)
 *     		{
 *   			Run query: 
				SELECT 
					A._id_room AS _id, 
				    A._id_room_type,
				    A.width_x,
				    A.width_y,
				    A.pisos,
				    A.detalles,
				    A.imagen 
				FROM Ambientes AS A, Propiedades_ambientes AS P
				WHERE P._id_prop = parameter1;
 *     		}
 *     }
 *     
 *     while there are items in rooms
 *     {
 *     		item = get current item on rooms;
 *     
 *     		create new tab using item;
 *     }
 * }
 */

/***
 * PSEUDOCODE FOR DATABASE RETRIEVAL LOGIC, TRY TWO (Purpose: repopulate a previous quote, or create a new one)
 * 
 * 	integer quantity = get room count on database for a given property ID;
 * 
 * 	if (quantity greater than zero (meaning case: previously existing quote))
 * 	{
 * 		cursor rooms = get rooms from database;
 * 
 * 		for (int i = 0; i < quantity; i++)
 * 		{
 * 			integer roomId = get room identifier from cursor rooms for room matching i;
 *  
 *  		create intent for new tab / new room;
 *  
 *  		put extras in intent, containing propId and roomId;
 *  
 *  		call method creating new tab;
 * 		}	
 *  }
 *  else (meaning case: new quote)
 *  {
 *  	create intent for new tab / new room;
 *  
 *  	put extra in intent, containing propId;
 *  
 *  	call method creating new tab;
 *  }
 */

@SuppressWarnings("deprecation")
public class RoomDetailTabhost extends TabActivity //implements Observable
{
	private TabHost tabHost;
	
	private static final int
		ADD_TAB = Menu.FIRST + 11,
		DELETE_TAB = Menu.FIRST + 12,
		SAVE_ALL = Menu.FIRST + 13,
		ACT_SAVE = 10001;

	private int z = 0;

	private long 
		mPropId, 
		mRoomTypeIdTemporary,	// "Zoggin' bad setup, boss, I know, but I can think a' no better right now."
		mRoomTypeId;
	
	private String mRoomType;
	
	private Spinner daSpinnerRoomTypes;
	
//	private SimpleCursorAdapter daRoomTypesAdapter;
	
	private OnItemSelectedListener spinnerListener;

	private RoomTypesDBAdapter mRoomTypes;
	private RoomsDBAdapter mRooms;

	private Cursor mCursorRoomTypes,
		mCursorRooms;

//	private ArrayList<Observer> observers;
	
	public ArrayList<Object> children;
	
	/**
	 * ACTIVITY LIFECYCLE OVERRIDES
	 */
	
	// TODO Implement logic change: create a new tab for every room on mCursorRooms.
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_room_detail_tabhost);
		
		//observers = new ArrayList<Observer>();
		children = new ArrayList<Object>();
		
		this.tabHost = getTabHost();

		Bundle extras = getIntent().getExtras();
	    
	    mRooms = new RoomsDBAdapter(this);
	    mRooms.open();
	    
		mRoomTypes = new RoomTypesDBAdapter(this);
		mRoomTypes.open();
		
		mCursorRoomTypes = mRoomTypes.getList();
		
	    mCursorRooms = mRooms.getRoomsForProperty(mPropId);

	    if(extras == null) 
	    {
	        mPropId = 0;
	    } 
	    else 
	    {
	        mPropId = extras.getLong("mPropId");
	        
	        mRoomTypeId = extras.getLong("mRoomTypeId");
	        
	        mRoomType = extras.getString("mRoomType");
	    }
		
	    if (mCursorRooms.getCount() == 0)
	    {
	    	createSingleTab(mPropId, mRoomTypeId);
	    }
	    else
	    {
    		createMultipleTabs(mPropId, mCursorRooms);
	    }
		
		spinnerListener = new OnItemSelectedListener()
		{
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int pos, long id)
			{
				mRoomTypeId = id;
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) { }
		};
	}

	@Override
	protected void onDestroy()
	{
		Log.i(this.getClass().toString(), "LIFECYCLE: onDestroy() called");
		
		super.onDestroy();
	}
	
	@Override
	protected void onPause()
	{
		Log.i(this.getClass().toString(), "LIFECYCLE: onPause() called");
		
		super.onPause();
	}
	
	@Override
	protected void onResume()
	{
		Log.i(this.getClass().toString(), "LIFECYCLE: onResume() called");
		
		super.onResume();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		menu.add(Menu.NONE, ADD_TAB, Menu.NONE, "New room")
//			.setIcon(R.drawable.add)
			.setAlphabeticShortcut('a');
		
		menu.add(Menu.NONE, SAVE_ALL, Menu.NONE, "Save property");
//		menu.add(Menu.NONE, CHANGE_ROOM_TYPE, Menu.NONE, "Change room type")
//			.setAlphabeticShortcut('c');
		
		return (super.onCreateOptionsMenu(menu));
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
		case ADD_TAB:
			addTab();
			
			return (true);
	
		case DELETE_TAB:
			deleteTab();
			
			return (true);
			
		case SAVE_ALL:
//			notifyObservers();
			
			Iterator<Object> i = children.iterator();
			
			while (i.hasNext())
			{
				finishActivity(i.next().hashCode());
			}
			finish();

			return(true);
		}
		
		return (super.onOptionsItemSelected(item));
	}

	@Override
	public void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);
		
		outState.putLong("mPropId", mPropId);
        
        outState.putLong("mRoomTypeId", mRoomTypeId);
        
        outState.putString("mRoomType", mRoomType);

//        try
//        {
//	        for (int i = 0; i < observers.size(); i++)
//			{
//				outState.putSerializable("observer" + i, (Serializable) observers.get(i));
//			}
//	
//			outState.putInt("qObservers", observers.size());
//        }
//        catch(Exception E)
//        {
//			Log.i(this.getClass().toString(), "onSaveInstanceState: cannot save content - " + E.getMessage());
//        }
	}
	
	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState)
	{
		super.onRestoreInstanceState(savedInstanceState);
		
		mPropId = savedInstanceState.getLong("mPropId");

		mRoomTypeId = savedInstanceState.getLong("mRoomTypeId");

		mRoomType = savedInstanceState.getString("mRoomType");
		
//		int q = savedInstanceState.getInt("qObservers");
//		
//		try
//		{
//			for (int i = 0; i < q; i++)
//			{
//				observers.add((Observer) savedInstanceState.getSerializable("observer" + i));
//			}
//		}
//		catch(Exception E)
//		{
//			Log.i(this.getClass().toString(), "onRestoreInstanceState: cannot restore observers - " + E.getMessage());
//		}
	}

	private void addTab()
	{
		LayoutInflater inflater = LayoutInflater.from(this);
		
		View addView = inflater.inflate(R.layout.dialog_room_add, null);
		
		final AddRoomDialogWrapper wrapper = new AddRoomDialogWrapper(addView);
		
		daSpinnerRoomTypes = wrapper.getSpinner();
		
		daSpinnerRoomTypes.setOnItemSelectedListener(spinnerListener);
		
		populateRoomTypes();
		
		new AlertDialog.Builder(this)
			.setTitle("Select room type")
			.setView(addView)
			.setPositiveButton(R.string.ok,
				new DialogInterface.OnClickListener()
				{
					@Override
					public void onClick(DialogInterface dialog, int which) 
					{
						TextView t = (TextView) daSpinnerRoomTypes.getSelectedView();
						
						mRoomType = t.getText().toString();
						
						doTabGubbinz();
						//startRoomsActivity(daPropId, t.getText().toString());
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
	}

	private void createMultipleTabs(long propId, Cursor cursorRooms) 
	{
		while(cursorRooms.moveToNext())
		{
			Intent newTab = new Intent();
			
			newTab.putExtra("mPropId", propId);
			
			newTab.putExtra("mRoomId", cursorRooms.getLong(cursorRooms.getColumnIndex(RoomsDBAdapter.C_COLUMN_ID)));

//			newTab.putExtra("roomX", cursorRooms.getLong(cursorRooms.getColumnIndex(RoomsDBAdapter.C_COLUMN_ROOM_X)));
//			newTab.putExtra("roomY", cursorRooms.getLong(cursorRooms.getColumnIndex(RoomsDBAdapter.C_COLUMN_ROOM_Y)));
//			newTab.putExtra("roomFloors", cursorRooms.getString(cursorRooms.getColumnIndex(RoomsDBAdapter.C_COLUMN_ROOM_FLOORS)));
//			newTab.putExtra("roomDetails", cursorRooms.getString(cursorRooms.getColumnIndex(RoomsDBAdapter.C_COLUMN_ROOM_DETAILS)));
//			newTab.putExtra("roomImage", cursorRooms.getString(cursorRooms.getColumnIndex(RoomsDBAdapter.C_COLUMN_IMAGE)));
			
			newTab.setClass(this, RoomDetailActivity.class);

			tabHost.addTab(
					tabHost.newTabSpec("Main")
					.setIndicator(mCursorRoomTypes.getString(mCursorRoomTypes.getColumnIndex(RoomTypesDBAdapter.C_COLUMN_ROOM_TYPES_NAME)))
					.setContent(newTab)
					);
		}
	}

	private void createSingleTab(long propId, long roomTypeId)
	{
		Intent newTab = new Intent();
	
		newTab.putExtra("mPropId", propId);
		
		newTab.putExtra("mRoomTypeId", roomTypeId);
	
		newTab.setClass(this, RoomDetailActivity.class);
		
		tabHost.addTab(
				tabHost.newTabSpec("Main")
				.setIndicator(mRoomType)
				.setContent(newTab)
				);
	}

	// "Boss, we really cannot delete one a'dem tab fingz, so we hides 'em."
	private void deleteTab() 
	{
		int position = tabHost.getCurrentTab();
		Log.d("Position", Integer.toString(position));
		
		Log.d("Z val in delete()", Integer.toString(z));
		
		tabHost.getCurrentTabView().setVisibility(View.GONE);

		if (position > 0)
		{
			tabHost.setCurrentTab(position + 1);
			
			z -= 1;
			
			if (z < 0)
			{
				z = 0;
			}
		}
		else if (position == 0)
		{
			tabHost.setCurrentTab(position + 1);
			
			z = 0;
		}
		else if (position == z)
		{
			tabHost.setCurrentTab(z - 1);

			Log.d("Z value in final", "lol");
			Log.d("Pos", Integer.toString(position));
			Log.d("Z pos", Integer.toString(z));
		}		
	}
	
	private void doTabGubbinz()
	{
		spawnTypeRequestDialog();
		
		/*** "YA GIT, DIS AIN'T GONNA DO DA JOB WITH NEW TAB GUBBINZ! Ya'z havin' to cook up sumfin' elze, or me Squiggof's eatin' tonight!"
		 * (KUFF!) "OWWWW! I hearz ya, boss... methinks somehow tellin' it da new value a' dat room type fing could work..."
		 * Follow-up:
		 * "Boss, we'z tryin' dis up: I've set up one a' dem 'AlertDialog' fings to ask up for da room type, as I did wiv da PropDetailActivity fing."
		 */
		Intent newTab = new Intent();
		
		newTab.putExtra("propId", this.mPropId);
	
		newTab.putExtra("roomTypeId", this.mRoomTypeId);
		
		newTab.setClass(this, RoomDetailActivity.class);
		
		tabHost.addTab(
				tabHost.newTabSpec("NewRoomTab")
						.setIndicator(mRoomType)
						.setContent(newTab)
				);
	
		Log.d("z", Integer.toString(z));
		
		++z;
	}

	private void populateRoomTypes() 
	{
		String[] from = new String[] { RoomTypesDBAdapter.C_COLUMN_ROOM_TYPES_NAME };
		
		int[] to = new int[] { android.R.id.text1 };
	
		SimpleCursorAdapter adapterRoomTypes = new SimpleCursorAdapter(this, 
				android.R.layout.simple_spinner_item, 
				mCursorRoomTypes, 
				from,		/*new String[] { RatingsDBAdapter.C_COLUMN_RATING_NAME }, */
				to);		/*new int[] { android.R.id.text1 } */
		
		adapterRoomTypes.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		daSpinnerRoomTypes.setAdapter(adapterRoomTypes);
	}
	
	private void spawnTypeRequestDialog()
	{
		LayoutInflater inflater = LayoutInflater.from(this);
		
		View addView = inflater.inflate(R.layout.dialog_room_add, null);
		
		final AddRoomDialogWrapper wrapper = new AddRoomDialogWrapper(addView);
		
		daSpinnerRoomTypes = wrapper.getSpinner();
		
		populateRoomTypes();
		
		new AlertDialog.Builder(this)
			.setTitle("Select room type")
			.setView(addView)
			.setPositiveButton(R.string.ok,
				new DialogInterface.OnClickListener() 
				{
					@Override
					public void onClick(DialogInterface dialog, int which) 
					{
						// DONE Figure how to get text from a spinner linked to a database via an Adapter (note link to solution when done)
						// COMPLETED: http://stackoverflow.com/questions/5787809/get-spinner-selected-items-text
						
						TextView t = (TextView) daSpinnerRoomTypes.getSelectedView();
						
						mRoomType = t.getText().toString();
						
						mRoomTypeId = mRoomTypeIdTemporary;
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
	}

	/**
	 * OBSERVER/OBSERVABLE STUFF 
	 */
	
//	@Override
//	public void notifyObservers() 
//	{
//		for (int i = 0; i < observers.size(); i++)
//		{
//			Observer observer = (Observer) observers.get(i);
//			observer.update(ACT_SAVE, "Save");
//		}
//		
//	}	
//
//	@Override
//	public void registerObserver(Observer o) 
//	{
//		observers.add(o);		
//	}
//
//	@Override
//	public void removeObserver(Observer o) 
//	{
//		int i = observers.indexOf(o);
//		if (i >= 0)
//		{
//			observers.remove(i);
//		}
//	}

	public class AddRoomDialogWrapper
	{
		Spinner spinnerType = null;
		View base = null;
		Object item;
		
		AddRoomDialogWrapper(View base)
		{
			this.base = base;
			spinnerType = (Spinner) base.findViewById(R.id.spnRoomType);
		}

		public Spinner getSpinner()
		{
			if (spinnerType == null)
			{
				spinnerType = (Spinner) base.findViewById(R.id.spnRoomType);
			}
			
			return (spinnerType);
		}
		
		public Object getSelectedItem()
		{
			return item;
		}
	}
}
