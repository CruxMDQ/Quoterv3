package com.callisto.quoter;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class PropLocationActivity extends FragmentActivity
{
	GoogleMap mMap;
	
	double mLat, mLong;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);  
		setContentView(R.layout.activity_house_location);
		
		Bundle data = getIntent().getExtras();
		
		if (data.containsKey("LATITUDE"))
		{
			mLat = data.getDouble("LATITUDE");
			mLong = data.getDouble("LONGITUDE");
		}		
		
		mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
		
		LatLng target = new LatLng(mLat, mLong);
		CameraPosition camPos = new CameraPosition.Builder()
		        .target(target)   // Center map on target location
		        .zoom(19)         // Set zoom to 19
		        .bearing(45)      // Set orientation with north-east upwards 
		        .tilt(70)         // Lower camera POV 70 degrees 
		        .build();
		 
		CameraUpdate camUpd =
		    CameraUpdateFactory.newCameraPosition(camPos);
		 
		mMap.animateCamera(camUpd);
		
		showMarker(mLat, mLong);
	}
	
	private void showMarker(double lat, double lng)
	{
	    mMap.addMarker(new MarkerOptions()
	        .position(new LatLng(lat, lng))
	        .title("House location"));
	}	

}
