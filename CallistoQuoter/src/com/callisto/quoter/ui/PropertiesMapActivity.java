package com.callisto.quoter.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import android.app.Dialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.graphics.Bitmap;
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
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.callisto.quoter.R;
import com.callisto.quoter.async.GeolocationTask;
import com.callisto.quoter.async.LocaleTask;
import com.callisto.quoter.db.DBAdapter;
import com.callisto.quoter.db.PropDBAdapter;
import com.callisto.quoter.utils.ImageUtils;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/*
 * "Boss, here be dem 'sources' for da fingz we'z got 'ere:"
 * 
 * http://stackoverflow.com/questions/13884105/android-google-maps-v2-add-object-to-marker
 * http://bon-app-etit.blogspot.be/2012/12/add-informationobject-to-marker-in.html
 * http://stackoverflow.com/questions/14310280/how-to-link-google-maps-android-api-v2-marker-to-an-object
 * https://developer.android.com/training/search/setup.html
 * https://developer.android.com/guide/topics/manifest/activity-element.html
 * http://stackoverflow.com/questions/14029941/how-to-get-complete-details-of-current-location-using-gps-location-manager
 */

public class PropertiesMapActivity extends FragmentActivity implements LocationListener
{
	static public final String C_MODE = "mode";
	static public final int C_VIEW = 551,
			C_CREATE = 552,
			C_EDIT = 553,
			C_DELETE = 554,
			C_CONFIG = 555,
			C_PROP_TYPE_ADD = 556;
	
	static public final String C_CITY = "Mar del Plata";
	
	String cityName, countryName;
	
	GoogleMap mMap;

	PropDBAdapter mDBAdapter;
	
	ArrayList<Address> addresses;
	
	HashMap<Marker, Cursor> mProperties;
	
	private Uri mContactUri;

	private LocationManager mLocationManager;
	private String mProvider;
	private double mCurrentLat;
	private double mCurrentLong;

	protected TextView txtOwnerName;
	protected TextView txtOwnerPhone;
	protected ImageView img;
	protected TextView txtAddress;
	protected TextView txtPrice;
	protected TextView txtBuiltSurface;
	protected TextView txtParcelSurface;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_properties_map);
		
		mDBAdapter = new PropDBAdapter(this);
		mDBAdapter.open();
		
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

	    SearchManager searchManager =
	           (SearchManager) getSystemService(Context.SEARCH_SERVICE);
	    SearchView searchView =
	            (SearchView) menu.findItem(R.id.search).getActionView();
	    searchView.setSearchableInfo(
	            searchManager.getSearchableInfo(getComponentName()));

	    return true;
	}

	@Override
    public void onLocationChanged(Location loc) 
	{
		mCurrentLat = loc.getLatitude();
		mCurrentLong = loc.getLongitude();
		
        Toast.makeText(getBaseContext(),
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
            
            Log.i(this.getClass().toString() + ".onLocationChanged", "City: " + cityName + ", Country: " + countryName);
            
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
        }
    }
	
	@Override
	public void onProviderDisabled(String provider) { }

	@Override
	public void onProviderEnabled(String provider) { }

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) { }	

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
    			query = intent.getStringExtra(SearchManager.QUERY); // + ", " + C_CITY;
    		}
    		else
    		{
    			query = intent.getStringExtra(SearchManager.QUERY) + ", " + cityName + ", " + countryName;
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
		CameraPosition camPos = new CameraPosition.Builder()
		        .target(latlng)   // Center map on target location
		        .zoom(12)         // Set zoom to 12
//			    .bearing(315)      // Set orientation with north-east upwards 
//			        .tilt(70)         // Lower camera POV 70 degrees 
		        .build();
		 
		CameraUpdate camUpd =
			    CameraUpdateFactory.newCameraPosition(camPos);
			 
		mMap.animateCamera(camUpd);
			
//			mMap.addMarker(new MarkerOptions()
//            	.position(latlng));
    }
    
    private void getLocale()
    {
	    Location location = mLocationManager.getLastKnownLocation(mProvider);
	    
	    Address a;
	    
	    try
		{
	    	LatLng latlng = new LatLng(location.getLatitude(), location.getLongitude());
	    	
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
    }
    
    private void initializeMap()
    {
		if (mMap == null)
		{
			mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.completeMap)).getMap();
			
			if (mMap == null)
			{
				Toast.makeText(getApplicationContext(), "WARNING: UNABLE TO CREATE MAPS!", Toast.LENGTH_SHORT).show();
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
					latlng = new GeolocationTask(this).execute(a.getAddressLine(0) + ", " + cityName + ", " + countryName).get();
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

			MarkerOptions m = new MarkerOptions()
				.position(latlng)
				.title(a.getAddressLine(0));
			
			Cursor c = mDBAdapter.getRecord(a.getExtras().getLong(DBAdapter.C_ID));
			Log.i(this.getClass().toString() + ".populateMap", "The record has the following id: " + c.getLong(c.getColumnIndexOrThrow(DBAdapter.C_ID)));
			
			mProperties.put(mMap.addMarker(m), c);
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
		        
	//	    Location location = mLocationManager.getLastKnownLocation(mProvider);		
		    
	//	    if (location != null) 
	//	    {
	//	    	System.out.println("Provider " + mProvider + " has been selected.");
	//		    	onLocationChanged(location);
	//	    }
	//	    
	//	    try
	//	    {
	//	    	if (mCurrentLat == 0 && mCurrentLong == 0)
	//	    	{
	//				mCurrentLat = location.getLatitude();
	//				mCurrentLong = location.getLongitude();
	//	    	}
	//			Log.i(this.getClass().toString() + ".doEyeInTheSky", "Lat: " + mCurrentLat + ", long: " + mCurrentLong);
	//	    }
	//	    catch (Exception e)
	//	    {
	//	//	    	System.out.println(e.getMessage());
	//	    }
		}

	private void query()
	{
		try
		{
			addresses = mDBAdapter.getAllAddresses();
		}
		catch(SQLException S)
		{
			Log.i(this.getClass().toString(), " " + S.getMessage());
		}
		catch(Exception e)
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

				Cursor contactDetails = retrieveContactDetails(c);
				
				Long recordId = c.getLong(c
						.getColumnIndexOrThrow(DBAdapter.C_ID));
						
				Dialog dialog = new Dialog(PropertiesMapActivity.this);
				dialog.setContentView(R.layout.dialog_map_property_details);
				dialog.setTitle(c.getString(c.getColumnIndex(PropDBAdapter.C_ADDRESS)));
				dialog.setCancelable(true);

				if (contactDetails != null)
				{
					txtOwnerName = (TextView) dialog
							.findViewById(R.id.txtOwnerName);
					txtOwnerName.setText(contactDetails
							.getString(contactDetails
									.getColumnIndex(Phone.DISPLAY_NAME)));
					// text.setText(R.string.lots_of_text);

					txtOwnerPhone = (TextView) dialog
							.findViewById(R.id.txtOwnerPhone);
					txtOwnerPhone.setText(contactDetails
							.getString(contactDetails
									.getColumnIndex(Phone.NUMBER)));
					// text.setText(R.string.lots_of_text);
				}
				
				// set up image view
				img = (ImageView) dialog.findViewById(R.id.imgOwnerThumbnail);
				try
				{
					byte[] rawImage = c.getBlob(c.getColumnIndex(PropDBAdapter.C_IMAGE));
					
					mBitmap = ImageUtils.byteToBitmap(rawImage);

					img.setImageBitmap(mBitmap);
				}
				catch(SQLException e)
				{
					Log.i(this.getClass().toString() + ".query", "Cannot retrieve image from database");
				}
				catch(Exception e)
				{
					Log.i(this.getClass().toString() + ".query", "Cannot process image");
				}
				
//				img.setImageResource(R.drawable.nista_logo);

				txtAddress = (TextView) dialog.findViewById(R.id.txtAddress);
				txtAddress.setText(c.getString(c.getColumnIndex(PropDBAdapter.C_ADDRESS)));
//				text.setText(R.string.lots_of_text);

				txtPrice = (TextView) dialog.findViewById(R.id.txtPrice);
//				text.setText(R.string.lots_of_text);

				txtBuiltSurface = (TextView) dialog.findViewById(R.id.txtBuiltSurface);
//				text.setText(R.string.lots_of_text);

				txtParcelSurface = (TextView) dialog.findViewById(R.id.txtParcelSurface);
//				text.setText(R.string.lots_of_text);
				
//				// set up button
//				Button button = (Button) dialog.findViewById(R.id.Button01);
//				button.setOnClickListener(new OnClickListener()
//				{
//					@Override
//					public void onClick(View v)
//					{
//						finish();
//					}
//				});
				// now that the dialog is set up, it's time to show it
				dialog.show();

//				view(recordId);
			}
		});
		
		mProperties = new HashMap<Marker, Cursor>();
		
		populateMap();
	}
	
	private Cursor retrieveContactDetails(Cursor c)
	{
		Cursor results;
		
		/*
		 * REQUIRED FOR PICKING CONTACT FROM PHONE BOOK
		 */
		try
		{
			mContactUri = Uri.parse(c.getString(c.getColumnIndex(PropDBAdapter.C_OWNER_URI)));

			if (mContactUri != null)
			{
		        	String contactId = mContactUri.getLastPathSegment();
		        	
		        	String[] columns = {
		        			Phone.CONTACT_ID,
		        			Phone.DATA, 
		        			Phone.DISPLAY_NAME,
		        			Phone.NUMBER
		        			};
		        	
		        	results = getContentResolver()
		                     .query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
		                     columns,
		                     ContactsContract.CommonDataKinds.Phone.CONTACT_ID
		                         + " = ?", new String[] { contactId },
		                     null);
		            
		        	return results;
		        	
//		            String Name = null;
//		
//		            if (phoneCur.moveToFirst())
//		            {
//		                Name = phoneCur.getString(phoneCur.getColumnIndex(Phone.DISPLAY_NAME));
//		            }
//		
//		            phoneCur.close();
//		
//		        	txtOwnerName.setText(Name);
//		        	Log.i(this.getClass().toString(), Name);
			 }
		}
        catch (SQLException e) 
        {
            Log.i(this.getClass().toString() + "." + "query", "Database retrieval failure", e);
        }
		catch (Exception e)
		{
			Log.i(this.getClass().toString() + ".query", "Cannot parse contact URI from database");
		}
		
		return null;
	}
	
	private void view(long id)
	{
		Intent i = new Intent(PropertiesMapActivity.this, PropDetailActivity.class);
		i.putExtra(C_MODE, C_VIEW);
		i.putExtra(PropDBAdapter.C_ID, id);
		
		startActivityForResult(i, C_VIEW);
	}
}
