package com.callisto.quoter;

import java.io.File;

import com.callisto.quoter.DB.PropTypesDBAdapter;
import com.callisto.quoter.DB.RoomTypesDBAdapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.SQLException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.provider.ContactsContract.CommonDataKinds.Phone;
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
//
//import com.callisto.quoter.R;
//import com.callisto.quoter.contentprovider.QuoterContentProvider;
//import com.callisto.quoter.database.QuoterDBHelper;
//import com.callisto.quoter.database.TableProperties;
//import com.callisto.quoter.database.TableRoomTypes;
//import com.callisto.quoter.database.TableRooms;
//
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

public class RoomDetailActivity extends Activity
////	implements LoaderManager.LoaderCallbacks<Cursor>
{
//	private static final int 
//		TABLE_PROP_ROOMS = 10,
//		TABLE_ROOMS = 15,
//		TABLE_ROOMTYPES = 16;
//	
	// Clean up the spinners, both here and on the UI files
	private Spinner spinnerRoomType, spinnerDialogRoomType;
	
	private EditText daTxtWidthX, daTxtWidthY, daTxtFloors;
	
//	/*** "Dis 'ere gubbinz are fer da kamera to do work propa."
//	 */
	private ImageView mImageView;
	private Uri mCameraURI;
	private String mPhotoPath;

	private SimpleCursorAdapter mAdapter;

	private static final int
		ADD_ID = Menu.FIRST + 1,
		DELETE_ID = Menu.FIRST + 3,
		ADD_TAB = Menu.FIRST + 11,
		C_PICK_IMAGE = 70002;

	private long 
		mPropId, 
		mRoomTypeId = -1, 
		mRoomId = -1;

	private String initialRoomType;

	OnItemSelectedListener spinnerListener;

	private RoomTypesDBAdapter mRoomTypes;

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
							
//					        Matrix matrix = new Matrix();
//					        matrix.postRotate(90);
//
//					        Bitmap rotatedBitmap = Bitmap.createBitmap(thumbnail, 
//					        		0, 0, 
//					        		thumbnail.getWidth(), 
//					        		thumbnail.getHeight(), 
//					        		matrix, 
//					        		true);
//
//					        mBitmap = Bitmap.createScaledBitmap(rotatedBitmap, 80, 80, true);
//				            mImageView.setImageBitmap(mBitmap);

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
		
		spinnerRoomType = (Spinner) findViewById(R.id.spnPropType);
		
		daTxtWidthX = (EditText) findViewById(R.id.txtWidthX);
		daTxtWidthY = (EditText) findViewById(R.id.txtWidthY);
		daTxtFloors = (EditText) findViewById(R.id.txtFloors);

		populateRoomTypes();
		
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
		
//		dialogRoomType.setOnItemSelectedListener(new OnItemSelectedListener()
//		{
//			@Override
//			public void onItemSelected(AdapterView<?> parent, View view,
//					int pos, long id)
//			{
//				daRoomTypeId = id;
//			}
//
//			@Override
//			public void onNothingSelected(AdapterView<?> parent) { }
//		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		menu.add(Menu.NONE, ADD_ID, Menu.NONE, "New room type")
//		.setIcon(R.drawable.add)
		.setAlphabeticShortcut('t');

		return (super.onCreateOptionsMenu(menu));
	}
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
		case R.id.menu_room_add_type:
			addRoomType();
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

	private void populateRoomTypes() 
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
		
		spinnerRoomType.setAdapter(adapterRoomTypes);
	}
	
	private void processAddRoomType(AddTypeDialogWrapper wrapper) 
	{
	    ContentValues reg = new ContentValues();

	    reg.put(RoomTypesDBAdapter.C_COLUMN_ROOM_TYPES_NAME, wrapper.getName());
	    
	    mRoomTypes.insert(reg);
	    
		populateRoomTypes();
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

//	private void saveStuff()
//	{
//		QuoterDBHelper DAO = new QuoterDBHelper(getApplicationContext());
//
//		ContentValues roomDetails = new ContentValues();
//		
//		/* Log research about casting from string to float found at: 
//		 * stackoverflow.com/questions/4229710/string-from-edittext-to-float
//		 */
//		String s1 = daTxtWidthX.getText().toString();
//		String s2 = daTxtWidthY.getText().toString();
//		
//		if (/*daTxtWidthX.getText().toString()*/ !s1.equals(""))
//		{
//			roomDetails.put(TableRooms.COLUMN_ROOM_WIDTH_X, Float.valueOf(daTxtWidthX.getText().toString()));
//		}
//		
//		if (/*daTxtWidthY.getText().toString()*/ !s2.equals(""))
//		{
//			roomDetails.put(TableRooms.COLUMN_ROOM_WIDTH_Y, Float.valueOf(daTxtWidthY.getText().toString()));
//		}
//		
////			roomDetails.put("COLUMN_ROOM_WIDTH_X", Float.valueOf(daTxtWidthX.getText().toString()));
////			roomDetails.put("COLUMN_ROOM_WIDTH_Y", Float.valueOf(daTxtWidthY.getText().toString()));
//		roomDetails.put(TableRooms.COLUMN_FLOORS, daTxtFloors.getText().toString());
//		roomDetails.put(TableRooms.COLUMN_DETAILS, "TEST");
//		roomDetails.put(TableRooms.COLUMN_PICTURE, daPhotoPath);
////			roomDetails.put(TableRooms.COLUMN_ID_ROOM, daRoomId);
//		roomDetails.put(TableRoomTypes.COLUMN_ID_ROOM_TYPE, daRoomTypeId);
//	
//		System.out.println("Room ID: " + daRoomId);
//		
//		if (daRoomId == -1)
//		{
//			try
//			{
//				System.out.println("Performing room insertion");
//				daRoomId = DAO.insert(TABLE_ROOMS, roomDetails);
//			}
//			catch(SQLException S)
//			{
//				System.out.println("Exception on insert on TABLE_ROOMS step");
//				System.out.println(S.getMessage());
//			}
//		}
//		else
//		{
//			try
//			{
//				System.out.println("Updating existing room");
//				roomDetails.put(TableRooms.COLUMN_ID_ROOM, daRoomId);
//
//				DAO.update(TABLE_ROOMS, roomDetails);
//			}
//			catch(SQLException S)
//			{
//				System.out.println("Exception on update on TABLE_ROOMS step");
//				System.out.println(S.getMessage());
//			}
//		}
//			
//		ContentValues propRooms = new ContentValues();
//		
//		propRooms.put(TableProperties.COLUMN_ID_PROPERTY, daPropId);
//		propRooms.put(TableRooms.COLUMN_ID_ROOM, daRoomId);
//		
//		try
//		{
//			DAO.insert(TABLE_PROP_ROOMS, propRooms);
//		}
//		catch(SQLException S)
//		{
//			System.out.println("Exception on update on TABLE_PROP_ROOMS step");
//			System.out.println(S.getMessage());
//		}
//	}
//
////	@Override
////	public Loader<Cursor> onCreateLoader(int id, Bundle args)
////	{
////		String[] projection = { TableRoomTypes.COLUMN_ID_ROOM_TYPE, TableRoomTypes.COLUMN_NAME };
////		
////		CursorLoader cursorLoader = new CursorLoader (this,
////				QuoterContentProvider.CONTENT_URI_ROOM_TYPES, projection, null, null, null);
////		
////		return cursorLoader;
////	}
////	
////	@Override
////	public void onLoadFinished(Loader<Cursor> loader, Cursor data)
////	{
////		MaskingWrapper mask = new MaskingWrapper(data);
////		
////		daAdapter.swapCursor(mask);
////	}
////	
////	@Override
////	public void onLoaderReset(Loader<Cursor> loader)
////	{
////		// Data not available anymore -> delete reference
////		daAdapter.swapCursor(null);
////	}
//
//	
//	class AddRoomDialogWrapper
//	{
//		Spinner spinnerType = null;
//		View base = null;
//		Object item;
//		
//		AddRoomDialogWrapper(View base)
//		{
//			this.base = base;
//			spinnerType = (Spinner) base.findViewById(R.id.spnRoomType);
//		}
//
//		private Spinner getSpinner()
//		{
//			if (spinnerType == null)
//			{
//				spinnerType = (Spinner) base.findViewById(R.id.spnRoomType);
//			}
//			
//			return (spinnerType);
//		}
//		
//		public Object getSelectedItem()
//		{
//			return item;
//		}
//	}
//	
//	// CursorWrapper subclass built to dodge the '_id' requirement for SimpleCursorAdapter
//	// (ref.: http://stackoverflow.com/questions/7796345/column-id-does-not-exist-simplecursoradapter-revisited/7796404#7796404)
//	class MaskingWrapper extends CursorWrapper
//	{
//		Cursor maskedCursor;
//		
//		public MaskingWrapper(Cursor cursor)
//		{
//			super(cursor);
//			maskedCursor = cursor;
//		}
//		
//		@Override
//		public int getColumnCount(){
//			return super.getColumnCount() + 1;
//		}
//		
//		@Override
//		public int getColumnIndex(String columnName)
//		{
//			if (columnName == "_id")
//				return 0;
//			else
//				return super.getColumnIndex(columnName);
//		}
//
//		@Override
//		public int getColumnIndexOrThrow(String columnName)
//		{
//			if (columnName == "_id")
//				return 0;
//			else
//				return super.getColumnIndexOrThrow(columnName);
//		}
//
//		@Override
//		public double getDouble(int columnIndex)
//		{
//			if (columnIndex == 0)
//				return (double)super.getPosition();
//			else
//				return super.getDouble(columnIndex);
//		}
//		
//		@Override
//		public float getFloat(int columnIndex)
//		{
//			if (columnIndex == 0)
//				return (float)super.getPosition();
//			else
//				return super.getFloat(columnIndex);
//		}
//		
//		@Override
//		public int getType(int columnIndex)
//		{
//			if (columnIndex == 0)
//				return Cursor.FIELD_TYPE_INTEGER;
//			else
//				return super.getType(columnIndex);
//		}
//		
//		@Override
//		public boolean isNull(int columnIndex)
//		{
//			if (columnIndex == 0)
//				return super.isNull(1);
//			else
//				return super.isNull(columnIndex);
//		}
//		
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