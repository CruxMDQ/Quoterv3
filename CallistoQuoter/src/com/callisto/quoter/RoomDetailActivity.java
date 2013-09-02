package com.callisto.quoter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.TabActivity;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Media;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;

import com.callisto.quoter.DB.PropsRoomsDBAdapter;
import com.callisto.quoter.DB.RoomTypesDBAdapter;
import com.callisto.quoter.DB.RoomsDBAdapter;
import com.callisto.quoter.utils.ImageUtils;

/***
 * PSEUDOCODE: RETRIEVAL OF EXISTING DETAILS FROM DATABASE, IF THERE ARE ANY
 * 
 * 	Declare cursor object to store room details;
 * 
 * 	if roomId is not -1
 * 	{
 * 		cursor = query database for room details using roomId;
 * 
 * 		populate text fields;
 * 
 * 		if path to picture retrieved from database is not null
 * 		{
 * 			picture pic = get picture using path;
 * 
 * 			put picture on ImageView component;
 *  	}	
 * 	}
 */

public class RoomDetailActivity extends Activity // implements LoaderManager.LoaderCallbacks<Cursor>
{
//	private static final int 
//		TABLE_PROP_ROOMS = 10,
//		TABLE_ROOMS = 15,
//		TABLE_ROOMTYPES = 16;
//	
	private EditText daTxtWidthX, daTxtWidthY, daTxtFloors;
	
//	/*** "Dis 'ere gubbinz are fer da kamera to do work propa."
//	 */
	private ImageView mImageView;
	private Uri mCameraURI;

	private String mPhotoPath, 
		initialRoomType;

	private SimpleCursorAdapter mAdapter;

	private static final int
		ADD_ROOM_TYPE = Menu.FIRST + 12,
		DELETE_ID = Menu.FIRST + 3,
		ADD_TAB = Menu.FIRST + 11,
		CHANGE_ROOM_TYPE = Menu.FIRST + 13,
		C_PICK_IMAGE = 70002;

	private long 
		mPropId, 
		mRoomTypeId = -1, 
		mRoomId = -1;

	// Clean up the spinners, both here and on the UI files
	private Spinner spinnerDialogRoomType;
	
	OnItemSelectedListener spinnerListener, spinnerDialogListener;

	private RoomTypesDBAdapter mRoomTypes;
	private RoomsDBAdapter mRooms;
	private PropsRoomsDBAdapter mPropRooms;

	private Cursor mCursorRoomTypes;

	private Bitmap mBitmap;
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if (resultCode == RESULT_OK)
		{
			switch(requestCode)
			{
			case C_PICK_IMAGE:
			{
				try
				{
					Cursor cursor = getContentResolver().query(
							Media.EXTERNAL_CONTENT_URI, new String[]
									{
										Media.DATA, 
										Media.DATE_ADDED, 
										MediaStore.Images.ImageColumns.ORIENTATION
									}, 
									Media.DATE_ADDED, 
									null, 
									"date_added ASC"
					);
					
					if (cursor != null && cursor.moveToFirst())
					{
						do
						{
							mCameraURI = Uri.parse(cursor.getString(cursor.getColumnIndex(Media.DATA)));
							mPhotoPath = mCameraURI.toString();
						}
						while (cursor.moveToNext());
						cursor.close();
					}
					
					if (data != null)
					{
						/*
						 * VERY IMPORTANT DISCOVERY! 
						 * source: http://stackoverflow.com/questions/6982184/camera-activity-returning-null-android
						 */
						if (data.hasExtra("data"))
						{
							Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
							
				            mImageView.setImageBitmap(thumbnail);
						}
						else //This WILL fire up if the default photo taking activity is passed the MEDIA_OUTPUT extra. (Commented out.)
						{
							Log.i(this.getClass().toString() + ".onActivityResult", "Intent returned by picture taking activity does not have the 'data' Extra");
							
							int width = mImageView.getWidth();
							int height = mImageView.getHeight();
							
							BitmapFactory.Options factoryOptions = new BitmapFactory.Options();
							
							factoryOptions.inJustDecodeBounds = true;
							
							BitmapFactory.decodeFile(mPhotoPath, factoryOptions);
							
							int imageWidth = factoryOptions.outWidth;
							int imageHeight = factoryOptions.outHeight;
							
							// Determine how much to scale down the image
							int scaleFactor = Math.min(
									imageWidth/width,
									imageHeight/height
									);
							
							// Decode the image file into a Bitmap sized to fill view
							
							factoryOptions.inJustDecodeBounds = false;
							factoryOptions.inSampleSize = scaleFactor;
							factoryOptions.inPurgeable = true;
							
							Bitmap bitmap = BitmapFactory.decodeFile(mPhotoPath, factoryOptions);

							/*
							 * MOAR PROTOTYPE CODE: partly working, image size is too small [FIXED: XML layout issue]
							 * source: http://stackoverflow.com/questions/9015372/how-to-rotate-a-bitmap-90-degrees
							 */
					        Matrix matrix = new Matrix();
					        matrix.postRotate(90);

					        Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap , 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

					        mBitmap = Bitmap.createScaledBitmap(rotatedBitmap, 80, 80, true);
				            mImageView.setImageBitmap(mBitmap);
						}
					}
					if (!cursor.isClosed())
					{
						cursor.close();
					}
				}
				catch(Exception e)
				{
					Log.i(this.getClass().toString() + ".onActivityResult -> " + C_PICK_IMAGE, e.getMessage());
				}
			}
			default:
				break;			
			}
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_room_detail_tab);
		
		Bundle extras = getIntent().getExtras();
	    
	    if(extras == null) 
	    {
	        mPropId = 0;
	    } 
	    else 
	    {
	        mPropId = extras.getLong("mPropId");
	        
	        initialRoomType = extras.getString("mRoomType");
	    }
		
//		spinnerRoomType = (Spinner) findViewById(R.id.spnPropType);
		
		daTxtWidthX = (EditText) findViewById(R.id.txtWidthX);
		daTxtWidthY = (EditText) findViewById(R.id.txtWidthY);
		daTxtFloors = (EditText) findViewById(R.id.txtFloors);

//		populateRoomTypes(spinnerRoomType);
		
		mImageView = (ImageView) findViewById(R.id.imgDisplayImage);
		
		mImageView.setOnClickListener(new View.OnClickListener()
		{			
			@Override
			public void onClick(View v)
			{
				Intent camera = new Intent();
				
				camera.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
				
				camera.putExtra("crop", "true");
				
				//File f = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
				
//				mCameraURI = Uri.fromFile(new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "myFile.jpg"));
				
//				camera.putExtra(MediaStore.EXTRA_OUTPUT, mCameraURI);
				
				startActivityForResult(camera, C_PICK_IMAGE);
			}
		});

		/* Log this: source for retrieval of row id:
		 * http://stackoverflow.com/questions/11037256/get-the-row-id-of-an-spinner-item-populated-from-database
		 */ 
		
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
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		menu.add(Menu.NONE, ADD_ROOM_TYPE, Menu.NONE, "New room type")
		.setAlphabeticShortcut('t');

		menu.add(Menu.NONE, CHANGE_ROOM_TYPE, Menu.NONE, "Change room type")
		.setAlphabeticShortcut('c');
	
		return (super.onCreateOptionsMenu(menu));
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case ADD_ROOM_TYPE:
			{	
				addRoomType();
			}
			case CHANGE_ROOM_TYPE:
			{
				changeTabTitle();
			}
		}
		return (super.onOptionsItemSelected(item));
	}
	
	private void addRoomType()
	{
		LayoutInflater inflater = LayoutInflater.from(this);
		
		View addView = inflater.inflate(R.layout.dialog_room_type_add, null);
		
		final AddTypeDialogWrapper wrapper = new AddTypeDialogWrapper(addView);
		
		new AlertDialog.Builder(this)
			.setTitle(R.string.add_type_title)
			.setView(addView)
			.setPositiveButton(R.string.ok,
				new DialogInterface.OnClickListener() 
				{
					@Override
					public void onClick(DialogInterface dialog, int which) {
						processAddRoomType(wrapper); 
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
	
	private void changeTabTitle()
	{
		@SuppressWarnings("deprecation")
		TabActivity parent = (TabActivity) getParent();
		@SuppressWarnings("deprecation")
		final TabHost parentTabHost = parent.getTabHost();
		final TabWidget vTabs = parentTabHost.getTabWidget();

		LayoutInflater inflater = LayoutInflater.from(this);
		
		View addView = inflater.inflate(R.layout.dialog_room_type_change, null);
		
		final AddRoomDialogWrapper wrapper = new AddRoomDialogWrapper(addView);
		
		final Spinner daSpinnerRoomTypes = wrapper.getSpinner();
		
		daSpinnerRoomTypes.setOnItemSelectedListener(spinnerListener);
		
		populateRoomTypes(daSpinnerRoomTypes);
		
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
					
					View indicatorView = vTabs.getChildAt(parentTabHost.getCurrentTab());
					((TextView) indicatorView.findViewById(android.R.id.title)).setText(t.getText().toString());			
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
	
	private void populateRoomTypes(Spinner spinner) 
	{
		String[] from = new String[] { RoomTypesDBAdapter.C_COLUMN_ROOM_TYPES_NAME };
		
		int[] to = new int[] { android.R.id.text1 };

		mRoomTypes = new RoomTypesDBAdapter(this);
		mRoomTypes.open();
		
		mCursorRoomTypes = mRoomTypes.getList();
		
		@SuppressWarnings("deprecation")
		SimpleCursorAdapter adapterRoomTypes = new SimpleCursorAdapter(this, 
				android.R.layout.simple_spinner_item, 
				mCursorRoomTypes, 
				from,		/*new String[] { RatingsDBAdapter.C_COLUMN_RATING_NAME }, */
				to);		/*new int[] { android.R.id.text1 } */
		
		adapterRoomTypes.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		spinner.setAdapter(adapterRoomTypes);
	}
	
	private void processAddRoomType(AddTypeDialogWrapper wrapper) 
	{
	    ContentValues reg = new ContentValues();

	    reg.put(RoomTypesDBAdapter.C_COLUMN_ROOM_TYPES_NAME, wrapper.getName());
	    
	    mRoomTypes.insert(reg);
	    
//		populateRoomTypes(spinnerRoomType);
	}
	
	@Override
	protected void onPause()
	{
		super.onPause();

//		saveStuff();
	}
	
	// This is supposed to handle passage of room type id back to the parent RoomDetailTabhost class. Find out how.
	private void addRoom()
	{
		LayoutInflater inflater = LayoutInflater.from(this);
		
		View addView = inflater.inflate(R.layout.dialog_room_add, null);
		
		final AddRoomDialogWrapper wrapper = new AddRoomDialogWrapper(addView);
		
		spinnerDialogRoomType = wrapper.getSpinner();
		
		spinnerDialogRoomType.setOnItemSelectedListener(spinnerListener);
		
		populateRoomTypes(spinnerDialogRoomType);
		
		new AlertDialog.Builder(this)
			.setTitle("Select initial room")
			.setView(addView)
			.setPositiveButton(R.string.ok,
				new DialogInterface.OnClickListener() 
				{
					@Override
					public void onClick(DialogInterface dialog, int which) 
					{
						/*** "Urrr... 'ow ta get dis 'ere klass 'daRoomTypeId' fing back to dat uvver TabHost klass?"
						 * "Speculation: you need to find a way to send a message from a running activity to another running activity without ending it, meatbag."
						 * "I HEARZ DAT!"
						 */
						
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

	private void save()
	{
		ContentValues reg = new ContentValues();
		
		/* Log research about casting from string to float found at: 
		 * stackoverflow.com/questions/4229710/string-from-edittext-to-float
		 */
		String s1 = daTxtWidthX.getText().toString();
		String s2 = daTxtWidthY.getText().toString();
		
		if (/*daTxtWidthX.getText().toString()*/ !s1.equals(""))
		{
			reg.put(RoomsDBAdapter.C_COLUMN_ROOM_X, Float.valueOf(daTxtWidthX.getText().toString()));
		}
		
		if (/*daTxtWidthY.getText().toString()*/ !s2.equals(""))
		{
			reg.put(RoomsDBAdapter.C_COLUMN_ROOM_Y, Float.valueOf(daTxtWidthX.getText().toString()));
		}
		
		reg.put(RoomsDBAdapter.C_COLUMN_ROOM_FLOORS, daTxtFloors.getText().toString());
		reg.put(RoomsDBAdapter.C_COLUMN_ROOM_DETAILS, "TEST");
		reg.put(RoomsDBAdapter.C_COLUMN_IMAGE, ImageUtils.bitmapToByteArray(mBitmap));
		reg.put(RoomsDBAdapter.C_COLUMN_ROOM_TYPE_ID, mRoomTypeId);
	
		Log.i(this.getClass().toString(), "Room ID: " + mRoomId);
		
		if (mRoomId == -1)
		{
			try
			{
				Log.i(this.getClass().toString(), "Performing room insertion");
				mRooms.insert(reg);
			}
			catch(SQLException S)
			{
				Log.i(this.getClass().toString(), S.getMessage());
			}
		}
		else
		{
			try
			{
				Log.i(this.getClass().toString(), "Updating existing room");
				reg.put(RoomsDBAdapter.C_COLUMN_ID, mRoomId);
				
				mRooms.update(reg);
			}
			catch(SQLException S)
			{
				Log.i(this.getClass().toString(), S.getMessage());
			}
		}
			
		ContentValues propRooms = new ContentValues();
		
		propRooms.put(PropsRoomsDBAdapter.C_COLUMN_PROP_ID, mPropId);
		propRooms.put(PropsRoomsDBAdapter.C_COLUMN_ROOM_ID, mRoomId);
		
		try
		{
			mPropRooms.insert(propRooms);
		}
		catch(SQLException S)
		{
			Log.i(this.getClass().toString(), S.getMessage());
		}
	}

	class AddTypeDialogWrapper
	{
		EditText nameField = null;
		View base = null;
		
		AddTypeDialogWrapper(View base)
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