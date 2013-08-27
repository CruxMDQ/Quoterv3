package com.callisto.quoter.model;

import android.graphics.Bitmap;
import android.net.Uri;

public class Property 
{
	// This is as they appear on the database	
	int mId;
	String mAddress;
	int mBedrooms;
	Boolean mConfirmed;
	int mRatingId;
	Uri mOwnerUri;
	double mLat, mLong;
	Bitmap mImage;
	int mPropTypeId;
	
	public int getId() {
		return mId;
	}
	
	public void setId(int mId) {
		this.mId = mId;
	}
	
	public String getAddress() {
		return mAddress;
	}
	
	public void setAddress(String mAddress) {
		this.mAddress = mAddress;
	}
	
	public int getBedrooms() {
		return mBedrooms;
	}
	
	public void setBedrooms(int mBedrooms) {
		this.mBedrooms = mBedrooms;
	}
	
	public Boolean getConfirmed() {
		return mConfirmed;
	}
	
	public void setConfirmed(Boolean mConfirmed) {
		this.mConfirmed = mConfirmed;
	}
	
	public int getRatingId() {
		return mRatingId;
	}
	
	public void setRatingId(int mRatingId) {
		this.mRatingId = mRatingId;
	}

	public Uri getOwnerUri() {
		return mOwnerUri;
	}

	public void setOwnerUri(Uri mOwnerUri) {
		this.mOwnerUri = mOwnerUri;
	}
	
	public double getLat() {
		return mLat;
	}
	
	public void setLat(double mLat) {
		this.mLat = mLat;
	}

	public double getLong() {
		return mLong;
	}
	
	public void setLong(double mLong) {
		this.mLong = mLong;
	}
	
	public Bitmap getImage() {
		return mImage;
	}
	
	public void setImage(Bitmap mImage) {
		this.mImage = mImage;
	}
	
	public int getPropTypeId() {
		return mPropTypeId;
	}
	
	public void setPropTypeId(int mPropTypeId) {
		this.mPropTypeId = mPropTypeId;
	}	
}