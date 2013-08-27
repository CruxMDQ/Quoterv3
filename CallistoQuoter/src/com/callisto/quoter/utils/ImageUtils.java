package com.callisto.quoter.utils;

import java.io.*;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

public class ImageUtils 
{
	static public byte[] bitmapToByteArray(Bitmap bitmap)
	{
		if (bitmap!= null)
		{
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
			
			byte[] data = baos.toByteArray();
			
			return data;
		}
		else
		{
			return null;
		}
	}
	
	static public byte[] drawableToByteArray(Drawable d)
	{
		if (d!= null)
		{
			Bitmap imageBitmap = ((BitmapDrawable) d).getBitmap();
			
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			
			imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
			
			byte[] data = baos.toByteArray();
			
			return data;
		}
		else
		{
			return null;
		}
	}
	
	static public Bitmap byteToBitmap(byte[] data)
	{
		if (data == null)
		{
			return null;
		}
		else
		{
			return BitmapFactory.decodeByteArray(data, 0, data.length);
		}
	}
	
	static public Drawable byteToDrawable(byte[] data)
	{
		if (data == null)
		{
			return null;
		}
		else
		{
			return new BitmapDrawable(BitmapFactory.decodeByteArray(data, 0, data.length));
		}
	}
}