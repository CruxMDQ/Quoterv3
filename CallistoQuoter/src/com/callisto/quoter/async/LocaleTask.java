package com.callisto.quoter.async;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import com.google.android.gms.maps.model.LatLng;

import android.app.ProgressDialog;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;

public class LocaleTask extends AsyncTask <LatLng, Void, Address>
{
	Context context;
	
	private ProgressDialog mProgressDialog;
	
	public LocaleTask(Context context)
	{
		this.context = context;
	}
	
	@Override
	protected Address doInBackground(LatLng... params)
	{
		double mCurrentLat = params[0].latitude;
		double mCurrentLong = params[0].longitude;
		
        Geocoder gcd = new Geocoder(this.context, Locale.getDefault());

        List<Address> addresses;

        try 
        {
            addresses = gcd.getFromLocation(mCurrentLat, mCurrentLong, 1);

            if (addresses.size() > 0)
            {
                System.out.println(addresses.get(0).getLocality());
                
                return addresses.get(0);
            }
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
        }
		return null;
	}

	@Override
	protected void onPreExecute()
	{
		mProgressDialog = new ProgressDialog(context);
		
		mProgressDialog.setMessage("Recuperando información de ciudad/país...");
		
		mProgressDialog.show();
	}
	
	@Override
	protected void onPostExecute(Address result)
	{
		super.onPostExecute(result);
		
		mProgressDialog.dismiss();
	}
}
