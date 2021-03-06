package com.callisto.quoter.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.graphics.Bitmap;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Contacts;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Media;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.callisto.quoter.R;
import com.callisto.quoter.db.DBAdapter;
import com.callisto.quoter.db.PropDBAdapter;
import com.callisto.quoter.db.PropTypesDBAdapter;
import com.callisto.quoter.db.PropsRoomsDBAdapter;
import com.callisto.quoter.db.RatingsDBAdapter;
import com.callisto.quoter.db.RoomTypesDBAdapter;
import com.callisto.quoter.db.RoomsDBAdapter;
import com.callisto.quoter.utils.AddRoomDialogWrapper;
import com.callisto.quoter.utils.AddTypeDialogWrapper;
import com.callisto.quoter.utils.ImageUtils;

public class PropDetailActivity extends Activity implements LocationListener
{
	/*
	 * Constants: used for onActivityResult
	 */
	protected static final int C_PICK_CONTACT = 70001,
			C_PICK_IMAGE = 70002;
	
	private PropDBAdapter mHouses;
	private PropsRoomsDBAdapter mPropRooms;
	private PropTypesDBAdapter mPropTypes;
	private RatingsDBAdapter mRatings;
	private RoomsDBAdapter mRooms;	
	private RoomTypesDBAdapter mRoomTypes;
	private Cursor mCursorHouses, 
		mCursorRatings,
		mCursorPropTypes,
		mCursorRoomTypes;
//		mCursorRooms;
	
	/*
	 * Form mode
	 */
	private int mFormMode;
	
	/*
	 * Property and initial room types
	 */
	long mPropTypeId, mRoomTypeId, mRatingId;
	
	/*
	 * Record identifier (used when form mode is Modify)
	 */
	private long mPropId;
	
	/*
	 * Form widgets
	 */
	private EditText txtAddress;
	private EditText txtBedrooms;

	private TextView txtOwner;
	
	private Button btnCancel;
	private Button btnSave;
	private Button btnPickOwner;
	private Button btnAddPropType;
	private Button btnRooms;
	
	/*
	 * CHECKBOX IMPLEMENTED ON LESSON 9
	 */
	private CheckBox chkConfirmed;
	
	/*
	 * SPINNER IMPLEMENTED ON LESSON 10
	 */
	private Spinner spinnerRating, 
		spinnerPropType, 
		spinnerRoomType;

	AdapterView.OnItemSelectedListener spnLstPropType, 
		spnLstRoomType, 
		spnLstRating;
	
	private Uri mContactUri;

	/*
	 * GPS STUFF
	 */
	
	private LocationManager mLocationManager;
	private String mProvider;
	private double mCurrentLat = 0;
	private double mCurrentLong = 0;
	
	/*
	 * CAMERA STUFF, STEP 1: declare required fields
	 */
	private ImageView mImageView;
	private Uri mCameraUri;
	private String mPhotoPath;
	private Bitmap mBitmap;

	// **** VISIBLE METHODS AND OVERRIDES ****
	
	/***
	 * (Futile) Sources for contact retrieval: 
	 * http://www.stackoverflow.com/questions/10977887/how-to-get-contact-name-number-and-emai-id-from-a-list-of-contacts
	 * http://www.stackoverflow.com/questions/4993063/how-to-call-android-contacts-list-and-select-one-phone-number-from-its-details-s
	 * http://www.stackoverflow.com/questions/866769/how-to-call-android-contacts-list
	 * http://www.stackoverflow.com/questions/1721279/how-to-read-contacts-on-android-2.0
	 * 
	 * Solutions:
	 * http://stackoverflow.com/questions/11781618/trying-to-insert-contact-into-edittext-using-contact-picker
	 * http://stackoverflow.com/questions/6404275/android-save-android-net-uri-object-to-database
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if (resultCode == RESULT_OK)
		{
			switch(requestCode)
			{
			case C_PICK_CONTACT:
			{
				try
				{
		            if (data != null) 
		            {
		            	Uri contactData = data.getData();

		                try 
		                {		                         
		                	String id = contactData.getLastPathSegment();
		                	
		                	String[] columns = { 
		                			Phone.CONTACT_ID,
		                			Phone.DATA, 
		                			Phone.DISPLAY_NAME
		                			};
		                	
		                	Cursor phoneCur = getContentResolver()
	                                 .query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                     columns,
                                     ContactsContract.CommonDataKinds.Phone.CONTACT_ID
                                         + " = ?", new String[] { id },
                                     null);
		                    
	                        String Name = null;

	                        if (phoneCur.moveToFirst())
	                        {
	                        	//mContactId = phoneCur.getString(phoneCur.getColumnIndex(Phone.CONTACT_ID));
	                        	mContactUri = contactData;
	                            Name = phoneCur.getString(phoneCur.getColumnIndex(Phone.DISPLAY_NAME));
	                        }

	                        phoneCur.close();

                        	txtOwner.setText(Name);
                        	Log.i(this.getClass().toString(), Name);
	                     } 
		                 catch (Exception e) 
		                 {
	                         Log.e("FILES", "Failed to get phone data", e);
	                     }
	                 }				
				}
				catch(Exception e)
				{
					System.out.println("Cannot retrieve contact info");
				}
				break;
			}
			/*
			 * CAMERA STUFF, STEP 3: Get pic off results from camera activity  
			 */
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
							mCameraUri = Uri.parse(cursor.getString(cursor.getColumnIndex(Media.DATA)));
							mPhotoPath = mCameraUri.toString();
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
							mBitmap = (Bitmap) data.getExtras().get("data");
							
				            mImageView.setImageBitmap(mBitmap);
						}

//						else //This WILL fire up if the default photo taking activity is passed the MEDIA_OUTPUT extra. (That's why it was commented out.)
//						{
//							Log.i(this.getClass().toString() + ".onActivityResult", "Intent returned by picture taking activity does not have the 'data' Extra");
//							
//							int width = mImageView.getWidth();
//							int height = mImageView.getHeight();
//							
//							BitmapFactory.Options factoryOptions = new BitmapFactory.Options();
//							
//							factoryOptions.inJustDecodeBounds = true;
//							
//							BitmapFactory.decodeFile(mPhotoPath, factoryOptions);
//							
//							int imageWidth = factoryOptions.outWidth;
//							int imageHeight = factoryOptions.outHeight;
//							
//							// Determine how much to scale down the image
//							int scaleFactor = Math.min(
//									imageWidth/width,
//									imageHeight/height
//									);
//							
//							// Decode the image file into a Bitmap sized to fill view
//							
//							factoryOptions.inJustDecodeBounds = false;
//							factoryOptions.inSampleSize = scaleFactor;
//							factoryOptions.inPurgeable = true;
//							
//							Bitmap bitmap = BitmapFactory.decodeFile(mPhotoPath, factoryOptions);
//
//							/*
//							 * MOAR PROTOTYPE CODE: partly working, image size is too small [FIXED: XML layout issue]
//							 * source: http://stackoverflow.com/questions/9015372/how-to-rotate-a-bitmap-90-degrees
//							 */
//					        Matrix matrix = new Matrix();
//					        matrix.postRotate(90);
//
//					        Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap , 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
//
//					        mBitmap = Bitmap.createScaledBitmap(rotatedBitmap, 80, 80, true);
//				            mImageView.setImageBitmap(mBitmap);
//						}
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
				break;
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
		setContentView(R.layout.activity_house_detail);
		
		Intent intent = getIntent();
		Bundle extra = intent.getExtras();
		
		if (extra == null)
		{
			return;
		}
		
		/*
		 * Retrieve view widgets 
		 */
		txtAddress = (EditText) findViewById(R.id.txtAddress);
		txtBedrooms = (EditText) findViewById(R.id.txtBedrooms);
		
		txtOwner = (TextView) findViewById(R.id.txtPickOwner);				
		
		/***
		 * Source:
		 * http://stackoverflow.com/questions/14674199/how-to-open-native-contact-card-by-uri
		 */
		txtOwner.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				if (mContactUri != null)
				{
					Intent intent = new Intent(Intent.ACTION_VIEW);
					intent.setData(mContactUri);
					startActivity(intent);
				}
			}
		});
		
		/*
		 * CHECKBOX IMPLEMENTED ON LESSON 9
		 */
		chkConfirmed = (CheckBox) findViewById(R.id.chkConfirmed);
		
		/*
		 * SPINNER IMPLEMENTED ON LESSON 10
		 */
		spinnerRating = (Spinner) findViewById(R.id.spinnerRating);
		spinnerPropType = (Spinner) findViewById(R.id.spinnerType);
		
		/*
		 * Define buttons and their actions
		 */
		btnCancel = (Button) findViewById(R.id.btnCancel);
		btnCancel.setOnClickListener(new View.OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				cancel();
			}
		});
		
		btnSave = (Button) findViewById(R.id.btnSave);
		btnSave.setOnClickListener(new View.OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				save();
				
				/*
				 * Return application flow to main activity
				 */
				setResult(RESULT_OK);
				finish();
			}
		});
		
		btnPickOwner = (Button) findViewById(R.id.btnPickOwner);
		btnPickOwner.setOnClickListener(new View.OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				pickContact(v);
				
			}
		});
		
		btnAddPropType = (Button) findViewById(R.id.btnAddPropType);
		btnAddPropType.setOnClickListener(new View.OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				addPropType();
			}
		});
		
		btnRooms = (Button) findViewById(R.id.btnAddRoom);
		btnRooms.setOnClickListener(new View.OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				viewRooms();				
			}
		});
		
		/*
		 * CAMERA STUFF, STEP 2: Retrieve widget from XML, assign event listener and create call for pic taking activity
		 */
		mImageView = (ImageView) findViewById(R.id.imgDisplayImage);
		mImageView.setOnClickListener(new View.OnClickListener()
		{			
			@Override
			public void onClick(View v)
			{
				if (mFormMode != PropListActivity.C_VIEW)
				{
					Intent camera = new Intent();
					
					camera.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
					
					camera.putExtra("crop", "true");
					
//					mCameraUri = Uri.fromFile(new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "myFile.jpg"));
//					
//					camera.putExtra(MediaStore.EXTRA_OUTPUT, mCameraUri);
					
					startActivityForResult(camera, C_PICK_IMAGE);
				}
			}
		});
		
		/*
		 * Creating houses adapter
		 */
		mHouses = new PropDBAdapter(this);
		mHouses.open();
			
		/*
		 * Creating adapter for table that joins properties with rooms
		 */
		mPropRooms = new PropsRoomsDBAdapter(this);
		mPropRooms.open();
		
		/*
		 * Creating rooms adapter: only opened on viewRooms
		 */
		mRooms = new RoomsDBAdapter(this);
		
		populatePropTypes();
		populateRatings();
		
		int pos = mCursorRatings.getInt(mCursorRatings.getColumnIndex(RatingsDBAdapter.C_ID));
		Log.i(this.getClass().toString(), "Spinner selected value " + pos);
		
		spinnerRating.setSelection(pos);
		
		long t = spinnerRating.getSelectedItemId();
		Log.i(this.getClass().toString(), "Spinner selected item ID: " + t);

		spnLstPropType = new AdapterView.OnItemSelectedListener()
		{
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int pos, long id) 
			{
				mPropTypeId = id; 	// 1
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) { }
		};
		spinnerPropType.setOnItemSelectedListener(spnLstPropType);

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
		
		spnLstRating = new AdapterView.OnItemSelectedListener() 
		{
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int pos, long id) 
			{
				mRatingId = id;
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) { }
		};
		spinnerRating.setOnItemSelectedListener(spnLstRating);
		
		/*
		 * Set form mode
		 */
		setFormMode(extra.getInt(PropListActivity.C_MODE));
		
		/*
		 * Get record ID if provided
		 */
		if (extra.containsKey(PropDBAdapter.C_ID))
		{
			mPropId = extra.getLong(PropDBAdapter.C_ID);
			query(mPropId);
		}
		
		doEyeInTheSky();
		
		/*
		 * Possible fix for keyboard always popping up?
		 * source: http://stackoverflow.com/questions/4149415/onscreen-keyboard-opens-automatically-when-activity-starts
		 */
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		menu.clear();
		
		if (mFormMode == PropListActivity.C_VIEW)
		{
			getMenuInflater().inflate(R.menu.menu_house_view, menu);
		}
		else
		{
			getMenuInflater().inflate(R.menu.menu_house_edit, menu);
		}
		
		return true;
	}

	@Override
	public void onLocationChanged(Location location)
	{
		mCurrentLat = location.getLatitude();
		mCurrentLong = location.getLongitude();
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item)
	{
		switch(item.getItemId())
		{
			case R.id.menu_delete:
			{
				delete(mPropId);
				return true;
			}
			case R.id.menu_cancel:
			{
				cancel();
				return true;
			}
			case R.id.menu_save:
			{
				save();
				return true;
			}
			case R.id.menu_edit:
			{
				setFormMode(PropListActivity.C_EDIT);
				return true;
			}
			case R.id.menu_location:
			{
				openMap();
				return true;
			}
		}
		
		return super.onMenuItemSelected(featureId, item);
	}

	@Override
	public void onProviderDisabled(String provider)
	{
		Toast.makeText(this, "Disabled provider " + provider,
				Toast.LENGTH_SHORT).show();
	}


	@Override
	public void onProviderEnabled(String provider)
	{
		Toast.makeText(this, "Enabled new provider " + provider,
				Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);
		
		outState.putInt("mFormMode", mFormMode);
		
		if (mContactUri != null)
		{		
			outState.putString("mContactUri", mContactUri.toString()); 
		}
		
		outState.putString("txtAddress", txtAddress.getText().toString());
		
		outState.putString("txtBedrooms", txtBedrooms.getText().toString());		
		
		outState.putLong("mPropId", mPropId);
	}
	
	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState)
	{
		super.onRestoreInstanceState(savedInstanceState);
		mFormMode = savedInstanceState.getInt("mFormMode");

		try
		{
			mContactUri = Uri.parse(savedInstanceState.getString("mContactUri"));
		}
		catch(Exception E)
		{
			Log.i(this.getClass().toString(), "onRestoreInstanceState: cannot parse URI");
		}
		
		txtAddress.setText(savedInstanceState.getString("txtAddress"));
		txtBedrooms.setText(savedInstanceState.getString("txtBedrooms"));
		
		mPropId = savedInstanceState.getLong("mPropId");
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras)
	{
	
	}

	// **** INTERNAL METHODS ****
	
	private void addPropType()
	{
		LayoutInflater inflater = LayoutInflater.from(this);
		
		View addView = inflater.inflate(R.layout.dialog_prop_type_add, null);
		
		final AddTypeDialogWrapper wrapper = new AddTypeDialogWrapper(addView);
		
		new AlertDialog.Builder(this)
			.setTitle("New property type")
			.setView(addView)
			.setPositiveButton(R.string.ok,
				new DialogInterface.OnClickListener() 
				{
					@Override
					public void onClick(DialogInterface dialog, int which) 
					{
						processAddPropType(wrapper); 
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

	/*
	 * COMPLETED Change logic here: if this house has no rooms, bring up the popup with the spinner already coded, but if not, load the existing ones as new tabs
	 * ---------
	 * BEHAVIOR DEPRECATED! Now the room list will open up, regardless of how many rooms the house has
	 */
	private void viewRooms()
	{
		LayoutInflater inflater = LayoutInflater.from(this);
		
		View addView = inflater.inflate(R.layout.dialog_room_add, null);
		
		final AddRoomDialogWrapper wrapper = new AddRoomDialogWrapper(addView);
		
		spinnerRoomType = wrapper.getSpinner();
		
		spinnerRoomType.setOnItemSelectedListener(spnLstRoomType);

		populateRoomTypes();

		// Open rooms adapter
		mRooms.open();
		
		// Retrieve this property's rooms
//		mCursorRooms = mRooms.getRoomsForProperty(mPropId);
		
		// If this property has no rooms, ask for the type of the first one to be created
		// BEHAVIOR DEPRECATED!
//		if (mCursorRooms.getCount() == 0)
//		{
//			new AlertDialog.Builder(this)
//				.setTitle("Select initial room")
//				.setView(addView)
//				.setPositiveButton(R.string.ok,
//					new DialogInterface.OnClickListener() 
//					{
//						@Override
//						public void onClick(DialogInterface dialog, int which) 
//						{
//							// TO DO Figure how to get text from a spinner linked to a database via an Adapter (note link to solution when done)
//							// COMPLETED: http://stackoverflow.com/questions/5787809/get-spinner-selected-items-text
//							
//							// TO DO How to get the ID of a table row based on the text displayed on a spinner
//							
//							TextView t = (TextView) spinnerRoomType.getSelectedView();
//							
//							startRoomsActivity(mPropId, mRoomTypeId, t.getText().toString());
//							
//	//						startRoomsActivity(daPropId, t.getText().toString());
//						}
//					})
//				.setNegativeButton(R.string.cancel,
//					new DialogInterface.OnClickListener()
//					{
//						@Override
//						public void onClick(DialogInterface dialog, int which) {
//		
//						}
//					}
//				).show();			
//		}
//		else 

		if (mPropId == 0)
		{
			save();
		}	

		startRoomsActivity(mPropId);
	}

	private void cancel() { }
	
//	private long createRoom()
//	{
//		long roomId = 0;
//		
//		ContentValues reg = new ContentValues();
//		
//		reg.put(RoomsDBAdapter.C_COLUMN_ROOM_X, 0);
//		
//		reg.put(RoomsDBAdapter.C_COLUMN_ROOM_Y, 0);
//		
//		reg.put(RoomsDBAdapter.C_COLUMN_ROOM_TYPE_ID, mRoomTypeId);
//	
//		try
//		{
//			Log.i(this.getClass().toString(), "Performing room insertion");
//			roomId = mRooms.insert(reg);
//		}
//		catch(SQLException S)
//		{
//			Log.i(this.getClass().toString(), S.getMessage());
//		}
//
//		ContentValues propRooms = new ContentValues();
//		
//		propRooms.put(PropsRoomsDBAdapter.C_COLUMN_PROP_ID, mPropId);
//		propRooms.put(PropsRoomsDBAdapter.C_COLUMN_ROOM_ID, roomId);
//		
//		try
//		{
//			mPropRooms.insert(propRooms);
//		}
//		catch(SQLException S)
//		{
//			Log.i(this.getClass().toString(), S.getMessage());
//		}
//		
//		return roomId;
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
					mHouses.delete(id);
					Toast.makeText(PropDetailActivity.this, R.string.house_delete_notice, Toast.LENGTH_LONG)
						.show();
					
					setResult(RESULT_OK);
					finish();
				}
			})
			.setNegativeButton(android.R.string.no, null)
			.show();
	}

	/***
	 * Retrieve latitude and longitude coordinates of current location.
	 */
	private void doEyeInTheSky()
	{
	    mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		   
	    Criteria criteria = new Criteria();
		    
	    mProvider = mLocationManager.getBestProvider(criteria, false);
		    
	    mLocationManager.requestLocationUpdates(mProvider, 400, 1, this);
	        
	    Location location = mLocationManager.getLastKnownLocation(mProvider);		
		    
	    if (location != null) 
	    {
	    	System.out.println("Provider " + mProvider + " has been selected.");
		    	onLocationChanged(location);
	    }
	    
	    try
	    {
	    	if (mCurrentLat == 0 && mCurrentLong == 0)
	    	{
				mCurrentLat = location.getLatitude();
				mCurrentLong = location.getLongitude();
	    	}
			Log.i(this.getClass().toString() + ".doEyeInTheSky", "Lat: " + mCurrentLat + ", long: " + mCurrentLong);
	    }
	    catch (Exception e)
	    {
	//	    	System.out.println(e.getMessage());
	    }
	}

	@SuppressWarnings("unused")
	private int getItemPositionById(Cursor c, long id) 
	{
		for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext())
		{
			if (c.getLong(c.getColumnIndex(RatingsDBAdapter.C_ID)) == id)
			{
				return c.getPosition();
			}
		}
		
		return 0;
	}
	
	private int getItemPositionById(Cursor c, long id, DBAdapter adapter)
	{
		for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext())
		{
			long t = c.getLong(c.getColumnIndex(DBAdapter.C_ID));
			
			if (t == id)
			{
				Log.i(this.getClass().toString(), "id = " + t + ", c.getPosition() = " + c.getPosition());
				
				return c.getPosition();
			}
		}
		
		return 0;
	} 

	/***
	 * SOLUTION FOR GOOGLE MAPS IMPLEMENTATION: http://www.sgoliver.net/blog/?p=3244. 
	 * (Points to mr. Benoffi for this one.)
	 */
	private void openMap()
	{
		Intent mapIntent = new Intent(PropDetailActivity.this, PropLocationActivity.class);
		
		mapIntent.putExtra("LATITUDE", mCurrentLat);
		mapIntent.putExtra("LONGITUDE", mCurrentLong);
		
		startActivity(mapIntent);
	}

	private void pickContact(View v)
	{
		Intent i = new Intent(Intent.ACTION_PICK,
				Contacts.CONTENT_URI);
		
		startActivityForResult(i, C_PICK_CONTACT);
	}

	private void populateRatings()
	{
		String[] from = new String[] { RatingsDBAdapter.C_RATING_NAME };
		
		int[] to = new int[] { android.R.id.text1 };

		mRatings = new RatingsDBAdapter(this);
		mRatings.open();
		
		mCursorRatings = mRatings.getList();
		
		@SuppressWarnings("deprecation")
		SimpleCursorAdapter adapterRatings = new SimpleCursorAdapter(this, 
				android.R.layout.simple_spinner_item, 
				mCursorRatings, 
				from,		/*new String[] { RatingsDBAdapter.C_COLUMN_RATING_NAME }, */
				to);		/*new int[] { android.R.id.text1 } */
		
		adapterRatings.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		spinnerRating.setAdapter(adapterRatings);
	}
	
	private void populatePropTypes()
	{
		String[] from = new String[] { PropTypesDBAdapter.C_PROP_TYPES_NAME };
		
		int[] to = new int[] { android.R.id.text1 };

		mPropTypes = new PropTypesDBAdapter(this);
		mPropTypes.open();
		
		mCursorPropTypes = mPropTypes.getList();
		
		@SuppressWarnings("deprecation")
		SimpleCursorAdapter adapterPropTypes = new SimpleCursorAdapter(this, 
				android.R.layout.simple_spinner_item, 
				mCursorPropTypes, 
				from,		/*new String[] { RatingsDBAdapter.C_COLUMN_RATING_NAME }, */
				to);		/*new int[] { android.R.id.text1 } */
		
		adapterPropTypes.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		spinnerPropType.setAdapter(adapterPropTypes);
	}

	private void populateRoomTypes() 
	{
		String[] from = new String[] { RoomTypesDBAdapter.C_ROOM_TYPES_NAME };
		
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

	private void processAddPropType(AddTypeDialogWrapper wrapper) 
	{
	    ContentValues reg = new ContentValues();
	    
	    reg.put(PropTypesDBAdapter.C_PROP_TYPES_NAME, wrapper.getName());
	    
	    mPropTypes.insert(reg);
	    
		populatePropTypes();
	}
	
//	private void processAddRoomType(AddRoomDialogWrapper wrapper)
//	{
//	    ContentValues reg = new ContentValues();
//	    
//	    reg.put(RoomTypesDBAdapter.C_COLUMN_ROOM_TYPES_NAME, wrapper.getName());
//	    
//	    mRoomTypes.insert(reg);
//	    
//		populateRoomTypes();
//	}
//
	/***
	 * Queries database for details on the row matching the provided ID and fills up form text boxes.
	 * @param id identifier of the row to retrieve data from.
	 */
	private void query(long id) 
	{
		mCursorHouses = mHouses.getRecord(id);
		
		txtAddress.setText(mCursorHouses.getString(mCursorHouses.getColumnIndex(PropDBAdapter.C_ADDRESS)));
//		txtBedrooms.setText(mCursorHouses.getString(mCursorHouses.getColumnIndex(PropDBAdapter.C_BEDROOMS)));

		try
		{
			mContactUri = Uri.parse(mCursorHouses.getString(mCursorHouses.getColumnIndex(PropDBAdapter.C_OWNER_URI)));
		}
		catch(Exception e)
		{
			Log.i(this.getClass().toString() + ".query", "Cannot parse contact URI from database");
		}
		
		/*
		 * DATABASE VERSION 5
		 */
		mCurrentLat = mCursorHouses.getDouble(mCursorHouses.getColumnIndex(PropDBAdapter.C_LATITUDE));
		mCurrentLong = mCursorHouses.getDouble(mCursorHouses.getColumnIndex(PropDBAdapter.C_LONGITUDE)); 
		
		/*
		 * DATABASE VERSION 6
		 */
		try
		{
			byte[] rawImage = mCursorHouses.getBlob(mCursorHouses.getColumnIndex(PropDBAdapter.C_IMAGE));
			
			mBitmap = ImageUtils.byteToBitmap(rawImage);

			mImageView.setImageBitmap(mBitmap);
		}
		catch(SQLException e)
		{
			Log.i(this.getClass().toString() + ".query", "Cannot retrieve image from database");
		}
		catch(Exception e)
		{
			Log.i(this.getClass().toString() + ".query", "Cannot process image");
		}
		
		/*
		 * CHECKBOX IMPLEMENTED ON LESSON 9
		 */
		if (mCursorHouses.getInt(mCursorHouses.getColumnIndex(PropDBAdapter.C_CONFIRMED)) == 1)
		{
			chkConfirmed.setChecked(true);
		}
		else
		{
			chkConfirmed.setChecked(false);
		}
		
		/*
		 * SPINNER IMPLEMENTED ON LESSON 10
		 */

		mRatingId = getItemPositionById(
				mCursorRatings,
				mCursorHouses.getColumnIndex(PropDBAdapter.C_RATING_ID),
				mRatings
			);
		
		Log.i(this.getClass().toString(), "mRatingId = " + mRatingId);

		spinnerRating.setSelection(
			    getItemPositionById(
			        mCursorRatings, 
			        mCursorHouses.getInt(mCursorHouses.getColumnIndex(PropDBAdapter.C_RATING_ID)),
			        mRatings
			    )
			);
		
		mPropTypeId = getItemPositionById(
				mCursorPropTypes, 
				mCursorHouses.getColumnIndex(PropDBAdapter.C_PROP_TYPE_ID),
				mPropTypes
			);

		Log.i(this.getClass().toString(), "mPropTypeId = " + mPropTypeId);

		try
		{
			spinnerPropType.setSelection(
				    getItemPositionById(
				        mCursorPropTypes, 
				        mCursorHouses.getInt
				        (
				        	mCursorHouses.getColumnIndex(PropDBAdapter.C_PROP_TYPE_ID)
    					),
				        mPropTypes
				    )
				);
		}
		catch(Exception e)
		{
			Log.i(this.getClass().toString(), "EXCEPTION: " + e.getMessage());
		}
		/*
		 * REQUIRED FOR PICKING CONTACT FROM PHONE BOOK
		 */
		if (mContactUri != null)
		{
	        try 
	        {		                         
	        	String contactId = mContactUri.getLastPathSegment();
	        	
	        	String[] columns = {
	        			Phone.CONTACT_ID,
	        			Phone.DATA, 
	        			Phone.DISPLAY_NAME
	        			};
	        	
	        	Cursor phoneCur = getContentResolver()
	                     .query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
	                     columns,
	                     ContactsContract.CommonDataKinds.Phone.CONTACT_ID
	                         + " = ?", new String[] { contactId },
	                     null);
	            
	            String Name = null;
	
	            if (phoneCur.moveToFirst())
	            {
	                Name = phoneCur.getString(phoneCur.getColumnIndex(Phone.DISPLAY_NAME));
	            }
	
	            phoneCur.close();
	
	        	txtOwner.setText(Name);
	        	Log.i(this.getClass().toString(), Name);
	         } 
	         catch (Exception e) 
	         {
	             Log.i(this.getClass().toString() + "." + "query", "Database retrieval failure", e);
	         }
		 }
	}

	/***
	 * Saves form content into database.
	 */
	private void save() 
	{
		/*
		 * Retrieve content from form
		 */
		ContentValues reg = new ContentValues();
		
		reg.put(PropDBAdapter.C_ADDRESS, txtAddress.getText().toString());
//		reg.put(PropDBAdapter.C_BEDROOMS, Integer.parseInt(txtBedrooms.getText().toString()));

		/*
		 * DATABASE VERSION 4
		 */
		try
		{
			reg.put(PropDBAdapter.C_OWNER_URI, mContactUri.toString());
		}
		catch(Exception e)
		{
			Log.i(this.getClass().toString() + ".save", "Owner URI missing or cannot be retrieved");
		}
		
		/*
		 * DATABASE VERSION 5
		 */
		reg.put(PropDBAdapter.C_LATITUDE, mCurrentLat);
		reg.put(PropDBAdapter.C_LONGITUDE, mCurrentLong);
		
		/*
		 * DATABASE VERSION 6
		 */
		try
		{
			byte[] storedPic = ImageUtils.bitmapToByteArray(mBitmap);
			
			reg.put(PropDBAdapter.C_IMAGE, storedPic);
		}
		catch(Exception e)
		{
			Log.i(this.getClass().toString() + ".save", "Cannot put image into ContentValues object");
		}
		
		/*
		 * CHECKBOX IMPLEMENTED ON LESSON 9
		 */		
		if (chkConfirmed.isChecked())
		{
			reg.put(PropDBAdapter.C_CONFIRMED, 1);
		}
		else
		{
			reg.put(PropDBAdapter.C_CONFIRMED, 0);
		}
		
		/*
		 * SPINNER IMPLEMENTED ON LESSON 10
		 */
//		reg.put(PropDBAdapter.C_PROP_RATING_ID, spinnerRating.getSelectedItemId());
		reg.put(PropDBAdapter.C_RATING_ID, mRatingId);

		/*
		 * MUST use the definition at this DB adapter because of differing column names
		 * 
		 * FOLLOW-UP: after setting up the onSelectedItemListener on the spinner, is this really necessary? 
		 */
//		mPropTypeId = Long.parseLong(spinnerType.getSelectedItemId());
		Log.i(this.getClass().toString(), "mPropTypeId: " + mPropTypeId);
		
		reg.put(PropDBAdapter.C_PROP_TYPE_ID, mPropTypeId);
//		reg.put(PropDBAdapter.C_PROP_TYPE_ID, spinnerType.getSelectedItemId());

		/*
		 * Save content into database
		 */
		try
		{
			if (mFormMode == PropListActivity.C_CREATE && mPropId == 0)
			{
				mPropId = mHouses.insert(reg);
				Toast.makeText(PropDetailActivity.this, R.string.house_create_notice + " with ID = " + mPropId, Toast.LENGTH_LONG).show();
			}
			else if (mFormMode == PropListActivity.C_EDIT)
			{
				Toast.makeText(PropDetailActivity.this, R.string.house_edit_notice, Toast.LENGTH_LONG).show();
		
				reg.put(PropDBAdapter.C_ID, mPropId);
				
				long resultCode = mHouses.update(reg);
				Log.i(this.getClass().toString(), "Database operation result code: " + resultCode);			
			}
		}
		catch(SQLException e)
		{
			Log.i(this.getClass().toString(), e.getMessage());
		}
	}
	
	/***
	 * Toggles whether the text fields on the form are enabled for editing.
	 * @param option 
	 */
	private void setEditable(boolean option) 
	{
		btnPickOwner.setEnabled(option);
		btnAddPropType.setEnabled(option);
		btnRooms.setEnabled(option);
		
		txtAddress.setEnabled(option);
		txtBedrooms.setEnabled(option);

		/*
		 * CHECKBOX IMPLEMENTED ON LESSON 9
		 */
		chkConfirmed.setEnabled(option);
		
		/*
		 * SPINNER IMPLEMENTED ON LESSON 10
		 */
		spinnerRating.setEnabled(option);
		spinnerPropType.setEnabled(option);
		
		//mImageView.setEnabled(option);
		
		LinearLayout v = (LinearLayout) findViewById(R.id.buttonPad);
		
		if (option)
		{
			v.setVisibility(View.VISIBLE);
		}
		else
		{
			v.setVisibility(View.INVISIBLE);
		}
	}

	/***
	 * Sets form usage mode.
	 * @param mode identifier of the mode to enable.
	 */
	private void setFormMode(int mode) 
	{
		this.mFormMode = mode;
		
		if (mode == PropListActivity.C_VIEW)
		{
			this.setTitle(txtAddress.getText().toString());
			this.setEditable(false);
		}
		else if (mode == PropListActivity.C_CREATE)
		{
			this.setTitle
			(R.string.house_create_title);
			this.setEditable(true);
		}
		else if (mode == PropListActivity.C_EDIT)
		{
			this.setTitle(R.string.house_edit_title);
			this.setEditable(true);
		}
	}

	private void startRoomsActivity(long mPropId) 
	{
		Intent intent = new Intent();
		
		intent.setClass(this, RoomListActivity.class);
		
		intent.putExtra("mPropId", mPropId);
		
		startActivity(intent);
	}
	
//	/***
//	 * Starts room tab host activity. All parameters are bundled as extras with the same name.
//	 * @param propId ID of the property being quoted
//	 * @param roomTypeId First room type ID
//	 * @param roomType First room type name, used for titling the tab
//	 */
//	public void startRoomsActivity(long propId, long roomTypeId, String roomType)
//	{
//		Intent intent = new Intent();
//		
//		intent.setClass(this, RoomListActivity.class);	//	intent.setClass(this, RoomDetailTabhost.class);
//		
//		intent.putExtra("mPropId", propId);
//		
//		intent.putExtra("mRoomTypeId", roomTypeId);
//		
//		intent.putExtra("mRoomType", roomType);
//
//		startActivity(intent);
//	}
}
