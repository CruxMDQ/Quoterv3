package com.callisto.quoter.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import android.app.Dialog;
import android.app.SearchManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Contacts;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.callisto.quoter.R;
import com.callisto.quoter.async.GeolocationTask;
import com.callisto.quoter.async.LocaleTask;
import com.callisto.quoter.db.DBAdapter;
import com.callisto.quoter.db.PropDBAdapter;
import com.callisto.quoter.mapfragment.CustomSupportMapFragment;
import com.callisto.quoter.utils.ImageUtils;
import com.callisto.quoter.wizard.PropertyWizardActivity;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;

/*
 * "Boss, here be dem 'sources' for da fingz we'z got 'ere:"
 * 
 * http://stackoverflow.com/questions/13884105/android-google-maps-v2-add-object-to-marker
 * http://bon-app-etit.blogspot.be/2012/12/add-informationobject-to-marker-in.html
 * http://stackoverflow.com/questions/14310280/how-to-link-google-maps-android-api-v2-marker-to-an-object
 * https://developer.android.com/training/search/setup.html
 * https://developer.android.com/guide/topics/manifest/activity-element.html
 * http://stackoverflow.com/questions/14029941/how-to-get-complete-details-of-current-location-using-gps-location-manager
 * 
 * "We'z havin' to chekk out dem, too:"
 * http://stackoverflow.com/questions/14877878/drawing-a-route-between-2-locations-google-maps-api-android-v2 
 * http://stackoverflow.com/questions/18507043/android-on-activity-result-always-return-0-and-null-intent
 */

/*
 * TODO list:
 * - Implement user-driven zone creation (as in, define a set of points that mark its boundaries, save them, give the new area a name and color)
 * - Find a given point's COT zone (requires the above-mentioned feature)
 */

public class PropertiesMapActivity extends FragmentActivity implements
		// Added as part of code being evaluated for zonification
		OnMapClickListener, OnMapLongClickListener,
		LocationListener
{
	static public final String C_MODE = "mode";
	static public final int C_VIEW = 551, C_CREATE = 552, C_EDIT = 553,
			C_DELETE = 554, C_CONFIG = 555, C_PROP_TYPE_ADD = 556;

	static public final String C_CITY = "Mar del Plata";

	static public final int C_PICK_CONTACT = 601, C_PICK_IMAGE = 602, C_PICK_CONTACT_FOR_NEW_PROPERTY = 603;

	String cityName, countryName;

	GoogleMap mMap;

	PropDBAdapter mDBAdapter;

	ArrayList<Address> addresses;

	HashMap<Marker, Cursor> mProperties;

	// ******* FIELDS ADDED FOR ZONIFICATION CODE *******
	private PolygonOptions polygonOptions;
	private Polygon polygon;

	public static boolean mMapIsTouched = false;
	// ******* FIELDS ADDED FOR ZONIFICATION CODE *******

	private Uri mContactUri;

	private LocationManager mLocationManager;
	private String mProvider;
	private double mCurrentLat;
	private double mCurrentLong;

	protected TextView txtOwnerName;
	protected TextView txtOwnerPhone;
	protected ImageView img;
	
	// protected TextView txtAddress;
	protected TextView txtPrice;
	protected TextView txtBuiltSurface;
	protected TextView txtParcelSurface;

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if (resultCode == RESULT_OK)
		{
			switch (requestCode)
			{
				case C_PICK_CONTACT_FOR_NEW_PROPERTY:
				{
					try
					{
						if (data != null)
						{
							Uri contactData = data.getData();
		
							try
							{
								String id = contactData.getLastPathSegment();
		
								String[] columns =
								{ Phone.CONTACT_ID, Phone.DATA, Phone.DISPLAY_NAME };
		
								Cursor phoneCur = getContentResolver()
										.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
												columns,
												ContactsContract.CommonDataKinds.Phone.CONTACT_ID
														+ " = ?", new String[]
												{ id }, null);
		
								// String Name = null;
		
								if (phoneCur.moveToFirst())
								{
									mContactUri = contactData;
								}
		
								phoneCur.close();

								startWizardActivity();
							}
							catch (Exception e)
							{
								Log.e("FILES", "Failed to get phone data", e);
							}
						}
					}
					catch (Exception e)
					{
						System.out.println("Cannot retrieve contact info");
					}
					break;
				}
				case C_CREATE:
				{
					query();
				}
			}
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_properties_map);

		mDBAdapter = new PropDBAdapter(this);
		mDBAdapter.open();

		// ******* CODE ADDED FOR ZONIFICATION *******
		polygonOptions = new PolygonOptions();
		// ******* CODE ADDED FOR ZONIFICATION *******

		initializeMap();

		prepareEyeInTheSky();

		getLocale();

		handleIntent(getIntent());

		query();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_properties_map, menu);

		SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		SearchView searchView = (SearchView) menu.findItem(R.id.search)
				.getActionView();
		searchView.setSearchableInfo(searchManager
				.getSearchableInfo(getComponentName()));

		return true;
	}

	@Override
	public void onLocationChanged(Location loc)
	{
		mCurrentLat = loc.getLatitude();
		mCurrentLong = loc.getLongitude();

		Toast.makeText(
				getBaseContext(),
				"Location changed : Lat: " + mCurrentLat + " Lng: "
						+ mCurrentLong, Toast.LENGTH_SHORT).show();

		Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());

		List<Address> addresses;

		try
		{
			addresses = gcd.getFromLocation(mCurrentLat, mCurrentLong, 1);

			if (addresses.size() > 0)
			{
				System.out.println(addresses.get(0).getLocality());
			}

			cityName = addresses.get(0).getLocality();
			countryName = addresses.get(0).getCountryName();

			Log.i(this.getClass().toString() + ".onLocationChanged", "City: "
					+ cityName + ", Country: " + countryName);

		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	// TODO "We'z disablin' dem two until we'z got the akshun bar fing working propa, boss."
	@Override
	public void onMapClick(LatLng point)
	{
		try
		{
			// Toast.makeText(getBaseContext(), "tapped, point=" + point,
			// Toast.LENGTH_SHORT).show();
			//
			// polygonOptions.add(point);
			// countPolygonPoints();
		}
		catch (Exception e)
		{
			Log.i(this.getClass().toString() + ".onMapClick",
					"" + e.getMessage());
		}
	}

	@Override
	public void onMapLongClick(LatLng point)
	{
		try
		{
			// Toast.makeText(getBaseContext(), "long pressed, point=" + point,
			// Toast.LENGTH_SHORT).show();
		}
		catch (Exception e)
		{
			Log.i(this.getClass().toString() + ".onMapLongClick",
					"" + e.getMessage());
		}
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item)
	{
		Intent i;

		switch (item.getItemId())
		{
		case R.id.menu_property_create:
		{
			i = new Intent(Intent.ACTION_PICK,
					Contacts.CONTENT_URI);
			
			startActivityForResult(i, C_PICK_CONTACT_FOR_NEW_PROPERTY);

			return true;
		}
		case R.id.menu_preferences:
		{
			// i = new Intent(PropListActivity.this, Config.class);
			// startActivityForResult(i, C_CONFIG);
			return true;
		}
		}

		return super.onMenuItemSelected(featureId, item);
	}

	@Override
	public void onProviderDisabled(String provider)
	{
	}

	@Override
	public void onProviderEnabled(String provider)
	{
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras)
	{
	}

	@Override
	protected void onNewIntent(Intent intent)
	{
		handleIntent(intent);
	}

	private void handleIntent(Intent intent)
	{
		if (Intent.ACTION_SEARCH.equals(intent.getAction()))
		{
			String query = null;

			if (cityName == null || countryName == null)
			{
				query = intent.getStringExtra(SearchManager.QUERY); // + ", " +
																	// C_CITY;
			}
			else
			{
				query = intent.getStringExtra(SearchManager.QUERY) + ", "
						+ cityName + ", " + countryName;
			}

			try
			{
				LatLng latlng = new GeolocationTask(this).execute(query).get();

				centerMapOnLocation(latlng);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
			catch (ExecutionException e)
			{
				e.printStackTrace();
			}
		}
	}

	private void centerMapOnLocation(LatLng latlng)
	{
		// Center map on target location
		CameraPosition camPos = new CameraPosition.Builder().target(latlng) 
				.zoom(12) // Set zoom to 12
				// .bearing(315) // Set orientation with north-east upwards
				// .tilt(70) // Lower camera POV 70 degrees
				.build();

		CameraUpdate camUpd = CameraUpdateFactory.newCameraPosition(camPos);

		mMap.animateCamera(camUpd);

		// mMap.addMarker(new MarkerOptions()
		// .position(latlng));
	}

	// TODO Re-enable this when I have worked out how to 'toggle' map
	// zonification mode on and off
	@SuppressWarnings("unused")
	private void countPolygonPoints()
	{
		if (polygonOptions.getPoints().size() > 3)
		{
			polygonOptions.strokeColor(Color.RED);
			polygonOptions.strokeWidth((float) 0.30);
			polygonOptions.fillColor(0x7F00FF00);

			polygon = mMap.addPolygon(polygonOptions);
		}
	}

	private void getLocale()
	{
		Location location = mLocationManager.getLastKnownLocation(mProvider);

		Address a;

		try
		{
			LatLng latlng = new LatLng(location.getLatitude(),
					location.getLongitude());

			centerMapOnLocation(latlng);

			a = new LocaleTask(this).execute(latlng).get();

			cityName = a.getLocality();
			countryName = a.getCountryName();
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		catch (ExecutionException e)
		{
			e.printStackTrace();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	private void initializeMap()
	{
		if (mMap == null)
		{
			mMap = ((CustomSupportMapFragment) getSupportFragmentManager()
					.findFragmentById(R.id.completeMap)).getMap();

			// ******* CODE ADDED FOR ZONIFICATION *******
			mMap.setOnMapClickListener(this);
			// ******* CODE ADDED FOR ZONIFICATION *******

			if (mMap == null)
			{
				Toast.makeText(getApplicationContext(),
						"WARNING: UNABLE TO CREATE MAPS!", Toast.LENGTH_SHORT)
						.show();
			}
		}
	}

	private void populateMap()
	{
		for (int i = 0; i < addresses.size(); i++)
		{
			Address a = addresses.get(i);

			LatLng latlng = new LatLng(a.getLatitude(), a.getLongitude());

			if (a.getLatitude() == 0 && a.getLongitude() == 0)
			{
				if (cityName == null || countryName == null)
				{
					prepareEyeInTheSky();
				}

				try
				{
					latlng = new GeolocationTask(this).execute(
							a.getAddressLine(0) + ", " + cityName + ", "
									+ countryName).get();
					
					Long recordId = a.getExtras().getLong(PropDBAdapter.C_ID);
					
					saveGeoLocData(latlng, recordId);
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
				catch (ExecutionException e)
				{
					e.printStackTrace();
				}
			}

			MarkerOptions m = new MarkerOptions().position(latlng).title(
					a.getAddressLine(0));

			Cursor c = mDBAdapter.getRecord(a.getExtras().getLong(
					DBAdapter.C_ID));
			Log.i(this.getClass().toString() + ".populateMap",
					"The record has the following id: "
							+ c.getLong(c.getColumnIndexOrThrow(DBAdapter.C_ID)));

			mProperties.put(mMap.addMarker(m), c);

//			c.close();
		}
	}

	/***
	 * Retrieve latitude and longitude coordinates of current location.
	 */
	private void prepareEyeInTheSky()
	{
		mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		Criteria criteria = new Criteria();

		mProvider = mLocationManager.getBestProvider(criteria, false);

		mLocationManager.requestLocationUpdates(mProvider, 400, 1, this);
	}

	private void query()
	{
//		mMap.clear();

		try
		{
			addresses = mDBAdapter.getAllAddresses();
		}
		catch (SQLException S)
		{
			Log.i(this.getClass().toString(), " " + S.getMessage());
		}
		catch (Exception e)
		{
			Log.i(this.getClass().toString(), " " + e.getMessage());
		}

		// TODO Code info retrieval for ***ALL*** of this. ALL OF THIS!
		mMap.setOnInfoWindowClickListener(new OnInfoWindowClickListener()
		{
			private Bitmap mBitmap;

			@Override
			public void onInfoWindowClick(Marker m)
			{
				Cursor c = mProperties.get(m);

				mContactUri = Uri.parse(c.getString(c
						.getColumnIndex(PropDBAdapter.C_OWNER_URI)));

				Dialog dialog = new Dialog(PropertiesMapActivity.this);
				dialog.setContentView(R.layout.dialog_map_property_details);
				
				String title = c.getString(c
						.getColumnIndex(PropDBAdapter.C_ADDRESS));
				
				dialog.setTitle(title);
				
				dialog.setCancelable(true);

//				Cursor contactDetails = retrieveContactDetails(c);
				Cursor contactDetails;
				
				/***
				 * [SOLVED] Problem when retrieving stored contact info from DB. Sources:
				 * - http://stackoverflow.com/questions/8064452/android-database-cursor-index-out-of-bound-of-exception
				 */
				try
				{
					contactDetails = retrieveContactDetails(mContactUri);

					txtOwnerName = (TextView) dialog
							.findViewById(R.id.txtOwnerName);
					
					txtOwnerPhone = (TextView) dialog
							.findViewById(R.id.txtOwnerPhone);

					while (contactDetails.moveToNext())
					{
						String owner = contactDetails
								.getString(contactDetails
										.getColumnIndex(Phone.DISPLAY_NAME));

						txtOwnerName.setText(owner);

						String phone = contactDetails
								.getString(contactDetails
										.getColumnIndex(Phone.NUMBER));
						
						txtOwnerPhone.setText(phone);
					}
				}
				catch (SQLException e)
				{
					Log.i(this.getClass().toString() + "." + "query",
							"Database retrieval failure", e);
				}
				catch (Exception e)
				{
					Log.i(this.getClass().toString() + ".query",
							"" + e.getMessage());
				}
				
//				Long recordId = c.getLong(c
//						.getColumnIndexOrThrow(DBAdapter.C_ID));
				
				// set up image view
				img = (ImageView) dialog.findViewById(R.id.imgOwnerThumbnail);
				try
				{
					byte[] rawImage = c.getBlob(c
							.getColumnIndex(PropDBAdapter.C_IMAGE));

					mBitmap = ImageUtils.byteToBitmap(rawImage);

					img.setImageBitmap(mBitmap);
				}
				catch (SQLException e)
				{
					Log.i(this.getClass().toString() + ".query",
							"Cannot retrieve image from database");
				}
				catch (Exception e)
				{
					Log.i(this.getClass().toString() + ".query",
							"Cannot process image");
				}

				// img.setImageResource(R.drawable.nista_logo);

				txtPrice = (TextView) dialog.findViewById(R.id.txtPrice);

				txtBuiltSurface = (TextView) dialog
						.findViewById(R.id.txtBuiltSurface);

				txtParcelSurface = (TextView) dialog
						.findViewById(R.id.txtParcelSurface);

				// c.close();
				
				// contactDetails.close();
				
				// now that the dialog is set up, it's time to show it
				dialog.show();

				// view(recordId);
			}
		});

//		if (mProperties != null)
//		{
//			mProperties.clear();
//		}

		mProperties = new HashMap<Marker, Cursor>();

		populateMap();
	}

	/***
	 * Internal method needed to retrieve contact info from phone book.
	 * @param uri
	 * @return
	 * @throws Exception
	 * @throws SQLException
	 */
	private Cursor retrieveContactDetails(Uri uri) throws Exception, SQLException
	{
		Cursor results;
		
		String contactId = mContactUri.getLastPathSegment();

		String[] columns =
		{ Phone.CONTACT_ID, Phone.DATA2, Phone.DISPLAY_NAME,	// Phone.DATA
				Phone.NUMBER };

		results = getContentResolver().query(
				ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
				columns,
				ContactsContract.CommonDataKinds.Phone.CONTACT_ID
						+ " = ?", new String[]
				{ contactId }, null);

		return results;
	}
	
//	private Cursor retrieveContactDetails(Cursor c)
//	{
//		Cursor results;
//
//		/*
//		 * REQUIRED FOR PICKING CONTACT FROM PHONE BOOK
//		 */
//		try
//		{
//			mContactUri = Uri.parse(c.getString(c
//					.getColumnIndex(PropDBAdapter.C_OWNER_URI)));
//
//			if (mContactUri != null)
//			{
//				String contactId = mContactUri.getLastPathSegment();
//
//				String[] columns =
//				{ Phone.CONTACT_ID, Phone.DATA, Phone.DISPLAY_NAME,
//						Phone.NUMBER };
//
//				results = getContentResolver().query(
//						ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
//						columns,
//						ContactsContract.CommonDataKinds.Phone.CONTACT_ID
//								+ " = ?", new String[]
//						{ contactId }, null);
//
//				return results;
//			}
//		}
//		catch (SQLException e)
//		{
//			Log.i(this.getClass().toString() + "." + "query",
//					"Database retrieval failure", e);
//		}
//		catch (Exception e)
//		{
//			Log.i(this.getClass().toString() + ".query",
//					"Cannot parse contact URI from database");
//		}
//
//		return null;
//	}
	
	private void saveGeoLocData(LatLng latlng, long recordId)
	{
		ContentValues reg = new ContentValues();
		
		reg.put(PropDBAdapter.C_ID, recordId);
		reg.put(PropDBAdapter.C_LATITUDE, latlng.latitude);
		reg.put(PropDBAdapter.C_LONGITUDE, latlng.longitude);
		
		try
		{
			PropDBAdapter properties = new PropDBAdapter(this);
			
			properties.open();
			properties.update(reg);
			properties.close();
		}
		catch(SQLException e)
		{
			Log.i(this.getClass().toString(), e.getMessage());
		}
	}

	private void startWizardActivity()
	{
		Intent intent = new Intent();

		Bundle extras = new Bundle();
		
		extras.putString("mContactUri", mContactUri.toString());
		
		intent.setClass(this, PropertyWizardActivity.class);
		
		intent.putExtras(extras);
		
		startActivityForResult(intent, C_CREATE);
	}
	
	private void view(long id)
	{
		Intent i = new Intent(PropertiesMapActivity.this,
				PropDetailActivity.class);
		i.putExtra(C_MODE, C_VIEW);
		i.putExtra(PropDBAdapter.C_ID, id);

		startActivityForResult(i, C_VIEW);
	}
}
