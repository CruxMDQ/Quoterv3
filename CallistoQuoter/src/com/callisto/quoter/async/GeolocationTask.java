package com.callisto.quoter.async;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

/***
 * Source for this class: http://stackoverflow.com/questions/3574644/how-can-i-find-the-latitude-and-longitude-from-address
 * @author Nirav Dangi (JSON stuff)
 * @author Octavian Damiean & ud_an (geocoder stuff, not implemented here, but worth taking a look)
 * 
 * Source for AsyncTask stuff: 
 * http://stackoverflow.com/questions/6343166/android-os-networkonmainthreadexception
 */
public class GeolocationTask extends AsyncTask<String, Void, LatLng>
{
	Context context;
	ProgressDialog mProgressDialog;
	
	public GeolocationTask(Context context)
	{
		this.context = context;
	}
	
	@Override
	protected LatLng doInBackground(String... params)
	{
		String address = params[0];
		
        StringBuilder stringBuilder = new StringBuilder();
        try 
        {
	        address = address.replaceAll(" ","%20");    
	
	        HttpPost httppost = new HttpPost("http://maps.google.com/maps/api/geocode/json?address=" + address + "&sensor=false");
	        HttpClient client = new DefaultHttpClient();
	        HttpResponse response;
	        stringBuilder = new StringBuilder();

            response = client.execute(httppost);
            HttpEntity entity = response.getEntity();
            InputStream stream = entity.getContent();
            
            int b;
            while ((b = stream.read()) != -1) 
            {
                stringBuilder.append((char) b);
            }
        } 
        catch (ClientProtocolException e) 
        {
        	Log.i("class GPSUtils.getLocationInfo", e.getMessage());
        } 
        catch (IOException e) 
        {
        	Log.i("class GPSUtils.getLocationInfo", e.getMessage());
        }

        JSONObject jsonObject = new JSONObject();

        try 
        {
            jsonObject = new JSONObject(stringBuilder.toString());
        } 
        catch (JSONException e) 
        {
            e.printStackTrace();
        }

		LatLng result;
		
		double latitude = 0, longitude = 0;
	
        try 
        {
        	longitude = ((JSONArray)jsonObject.get("results")).getJSONObject(0)
                .getJSONObject("geometry").getJSONObject("location")
                .getDouble("lng");

            latitude = ((JSONArray)jsonObject.get("results")).getJSONObject(0)
                .getJSONObject("geometry").getJSONObject("location")
                .getDouble("lat");
        } 
        catch (JSONException e) 
        {
        	Log.i("class GPSUtils.getLatLng", e.getMessage());
        }
        
        result = new LatLng(latitude, longitude);

        return result;
	}
	
	@Override
	protected void onPreExecute()
	{
		mProgressDialog = new ProgressDialog(context);
		
		mProgressDialog.setMessage("Recuperando coordenadas geogr‡ficas...");
		
		mProgressDialog.show();
	}
	
	@Override
	protected void onPostExecute(LatLng result)
	{
		super.onPostExecute(result);
		
		mProgressDialog.dismiss();
	}
}
