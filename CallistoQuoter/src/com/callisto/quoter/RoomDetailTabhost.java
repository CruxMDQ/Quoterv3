package com.callisto.quoter;

import com.callisto.quoter.DB.RoomTypesDBAdapter;

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

// TODO Figure how to get text from a spinner linked to a database via an Adapter (note link to solution when done)
// COMPLETED: http://stackoverflow.com/questions/5787809/get-spinner-selected-items-text

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
public class RoomDetailTabhost extends TabActivity
{
	private TabHost tabHost;
	
	private static final int
		ADD_TAB = Menu.FIRST + 11,
		DELETE_TAB = Menu.FIRST + 12,
		CHANGE_ROOM_TYPE = Menu.FIRST + 13;

	private int z = 0;

	private long 
		mPropId, 
		daRoomTypeIdTemporary,	// "Zoggin' bad setup, boss, I know, but I can think a' no better right now."
		mRoomTypeId;
	
	private String mRoomType;
	
	private Spinner daSpinnerRoomTypes;
	
	private SimpleCursorAdapter daRoomTypesAdapter;
	
	Object daSpinnerSelekshun;

	OnItemSelectedListener spinnerListener;

	private RoomTypesDBAdapter mRoomTypes;

	private Cursor mCursorRoomTypes;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_room_detail_tabhost);
		
		this.tabHost = getTabHost();

		Bundle extras = getIntent().getExtras();
	    
	    if(extras == null) 
	    {
	        mPropId = 0;
	    } 
	    else 
	    {
	        mPropId = extras.getLong("mPropId");
	        
	        // DONE Code this on PropDetailActivity
	        mRoomTypeId = extras.getLong("mRoomTypeId");
	        
	        mRoomType = extras.getString("mRoomType");
	    }
		
		Intent newTab = new Intent();

		newTab.putExtra("mPropId", this.mPropId);
		
		// "We'z needin' dis 'ere fing to add up da new room into yer databaze, boss: da type indikator cannot be, um, 'null'."
		newTab.putExtra("mRoomTypeId", this.mRoomTypeId);
		
		newTab.setClass(this, RoomDetailActivity.class);
		
		tabHost.addTab(
				tabHost.newTabSpec("Main")
				.setIndicator(mRoomType)
				.setContent(newTab)
				);
		
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
		
//		/*** TODO Log this: source for retrieval of row id:
//		 * http://stackoverflow.com/questions/11037256/get-the-row-id-of-an-spinner-item-populated-from-database
//		 */ 
//		daSpinnerRoomTypes.setOnItemSelectedListener(new OnItemSelectedListener()
//		{
//			@Override
//			public void onItemSelected(AdapterView<?> parent, View view,
//					int pos, long id)
//			{
//				daRoomTypeIdTemporary = id;
//			}
//
//			@Override
//			public void onNothingSelected(AdapterView<?> parent)
//			{
//				
//			}
//		});
		
	}
	
	@Override
	protected void onPause()
	{
		super.onPause();
		
		/***
		 * PSEUDOCODE
		 * 
		 * - Get writable database
		 * - Prepare content values
		 * - Write stuff to database (uses daPropId)
		 * - Assign row id to daRoomId field
		 * - Close database
		 */
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		menu.add(Menu.NONE, ADD_TAB, Menu.NONE, "New room")
//			.setIcon(R.drawable.add)
			.setAlphabeticShortcut('a');
		
//		menu.add(Menu.NONE, CHANGE_ROOM_TYPE, Menu.NONE, "Change room type")
//			.setAlphabeticShortcut('c');
		
		return (super.onCreateOptionsMenu(menu));
	}

	public long getDaRoomTypeId()
	{
		return mRoomTypeId;
	}

	public void setDaRoomTypeId(long daRoomTypeId)
	{
		this.mRoomTypeId = daRoomTypeId;
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
			
		case CHANGE_ROOM_TYPE:
//			TextView title = (TextView) tabHost.getTabWidget().getChildAt(tabId).findViewById(android.R.id.title);
//			title.setText("xyz");
			
			return (true);
		}
		
		return (super.onOptionsItemSelected(item));
	}

	private void addTab()
	{
		LayoutInflater inflater = LayoutInflater.from(this);
		
		View addView = inflater.inflate(R.layout.dialog_room_add, null);
		
		final AddRoomDialogWrapper wrapper = new AddRoomDialogWrapper(addView);
		
		daSpinnerRoomTypes = wrapper.getSpinner();
		
		daSpinnerRoomTypes.setOnItemSelectedListener(spinnerListener);
		
//		daSpinnerRoomTypes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
//		{
//			@Override
//			public void onItemSelected(AdapterView<?> parent, View view,
//					int pos, long id)
//			{
//				daSpinnerSelekshun = parent.getItemAtPosition(pos);
//				
//				System.out.println(daSpinnerSelekshun.toString());
//			}
//
//			@Override
//			public void onNothingSelected(AdapterView<?> arg0)
//			{
//				// "Nothing 'appens 'ere, boss."
//			}
//		});
		
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

//	private void changeTabType()
//	{
//		LayoutInflater inflater = LayoutInflater.from(this);
//		
//		View addView = inflater.inflate(R.layout.dialog_room_type_change, null);
//		
//		final AddRoomDialogWrapper wrapper = new AddRoomDialogWrapper(addView);
//		
//		daSpinnerRoomTypes = wrapper.getSpinner();
//		
//		daSpinnerRoomTypes.setOnItemSelectedListener(spinnerListener);
//		
//		populateRoomTypes();
//		
//		new AlertDialog.Builder(this)
//			.setTitle("Select room type")
//			.setView(addView)
//			.setPositiveButton(R.string.ok,
//				new DialogInterface.OnClickListener()
//				{
//					@Override
//					public void onClick(DialogInterface dialog, int which) 
//					{
//						TextView t = (TextView) daSpinnerRoomTypes.getSelectedView();
//						
//						mRoomType = t.getText().toString();
//						
//						doTabGubbinz();
//						//startRoomsActivity(daPropId, t.getText().toString());
//					}
//				})
//			.setNegativeButton(R.string.cancel,
//				new DialogInterface.OnClickListener()
//				{
//					@Override
//					public void onClick(DialogInterface dialog, int which) {
//	
//					}
//				}
//			).show();			
//	}

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
		
		Intent newTab = new Intent();
		
		newTab.putExtra("propId", this.mPropId);
	
		/*** "YA GIT, DIS AIN'T GONNA DO DA JOB WITH NEW TAB GUBBINZ! Ya'z havin' to cook up sumfin' elze, or me Squiggof's eatin' tonight!"
		 * (KUFF!) "OWWWW! I hearz ya, boss... methinks somehow tellin' it da new value a' dat room type fing could work..."
		 * Follow-up:
		 * "Boss, we'z tryin' dis up: I've set up one a' dem 'AlertDialog' fings to ask up for da room type, as I did wiv da PropDetailActivity fing."
		 */
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
	
		mRoomTypes = new RoomTypesDBAdapter(this);
		mRoomTypes.open();
		
		mCursorRoomTypes = mRoomTypes.getList();
		
		SimpleCursorAdapter adapterRoomTypes = new SimpleCursorAdapter(this, 
				android.R.layout.simple_spinner_item, 
				mCursorRoomTypes, 
				from,		/*new String[] { RatingsDBAdapter.C_COLUMN_RATING_NAME }, */
				to);		/*new int[] { android.R.id.text1 } */
		
		adapterRoomTypes.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		daSpinnerRoomTypes.setAdapter(adapterRoomTypes);
	}
	
	private void getRoomsOnProperty(int propId)
	{
		
		/***
		 * PSEUDOCODE
		 * 
		 * - Declare an array to store room identifiers
		 * - Run query on DB
		 * - 
		 */
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
						// TODO Figure how to get text from a spinner linked to a database via an Adapter (note link to solution when done)
						// COMPLETED: http://stackoverflow.com/questions/5787809/get-spinner-selected-items-text
						
						TextView t = (TextView) daSpinnerRoomTypes.getSelectedView();
						
						mRoomType = t.getText().toString();
						
						mRoomTypeId = daRoomTypeIdTemporary;
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
