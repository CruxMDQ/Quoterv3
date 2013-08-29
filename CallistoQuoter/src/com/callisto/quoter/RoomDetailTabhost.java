package com.callisto.quoter;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TabHost;

@SuppressWarnings("deprecation")
public class RoomDetailTabhost extends TabActivity 
{
	private TabHost tabHost;

	private static final int
		ADD_TAB = Menu.FIRST + 11,
		DELETE_TAB = Menu.FIRST + 12,
	// TODO TRANSLATE THIS INTO DBHELPER!
		TABLE_ROOMTYPES = 16;

	private int z = 0;
	
	private long
		daPropId, 
		daRoomTypeIdTemporary,	// "Zoggin' bad setup, boss, I know, but I can think a' no better right now."
		daRoomTypeId;
		
	private String daRoomType;
	
	private Spinner daSpinnerRoomTypes;
	
	private SimpleCursorAdapter daRoomTypesAdapter;
	
	Object daSpinnerSelekshun;

	OnItemSelectedListener spinnerListener;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_room_detail_tabhost);
		
		this.tabHost = getTabHost();
		
		Bundle extras = getIntent().getExtras();
		
		if (extras == null)
		{
			daPropId = 0;
		}
		else
		{
			daPropId = extras.getLong("mPropId");

			// TODO code this on HouseDetailActivity
	        daRoomTypeId = extras.getLong("mRoomTypeId");
	        
	        daRoomType = extras.getString("mRoomType");
		}
		
		Intent newTab = new Intent();

		newTab.putExtra("mPropId", this.daPropId);
		
		// "We'z needin' dis 'ere fing to add up da new room into yer databaze, boss: da type indikator cannot be, um, 'null'."
		newTab.putExtra("mRoomTypeId", this.daRoomTypeId);
		
		tabHost.addTab(
				tabHost.newTabSpec("Main")
				.setIndicator(daRoomType)
				.setContent(newTab)
				);
		
		spinnerListener = new OnItemSelectedListener()
		{
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int pos, long id)
			{
				daRoomTypeId = id;
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent)
			{
				
			}
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
	
	public long getDaRoomTypeId()
	{
		return daRoomTypeId;
	}

	public void setDaRoomTypeId(long daRoomTypeId)
	{
		this.daRoomTypeId = daRoomTypeId;
	}
}
