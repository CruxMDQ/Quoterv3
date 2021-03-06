package com.callisto.quoter.wizard.model;

import java.util.ArrayList;

import android.support.v4.app.Fragment;
import android.text.TextUtils;

import com.callisto.quoter.wizard.ui.RoomSizesFragment;
import com.callisto.quoter.wizard.model.ModelCallbacks;
import com.callisto.quoter.wizard.model.Page;
import com.callisto.quoter.wizard.model.ReviewItem;

public class RoomSizesPage extends Page
{
	static public final String ROOM_SIZE_X_KEY = "size_x";
	static public final String ROOM_SIZE_Y_KEY = "size_y";

	public RoomSizesPage(ModelCallbacks callbacks, String title)
	{
		super(callbacks, title);
	}

	@Override
	public Fragment createFragment()
	{
		return RoomSizesFragment.create(getKey());
	}

	@Override
	public void getReviewItems(ArrayList<ReviewItem> dest)
	{
//		dest.add(new ReviewItem("Dimensión 1",
//				mData.getString(ROOM_SIZE_X_KEY), getKey(), -1));
//
//		dest.add(new ReviewItem("Dimensión 2",
//				mData.getString(ROOM_SIZE_Y_KEY), getKey(), -1));
		
		dest.add(new ReviewItem("Dimensión 1",
				mData.getString(ROOM_SIZE_X_KEY), getKey(), -1, this.getDBTable()));

		dest.add(new ReviewItem("Dimensión 2",
				mData.getString(ROOM_SIZE_Y_KEY), getKey(), -1, this.getDBTable()));
		
	}

	@Override
	public void getReviewItems(ArrayList<ReviewItem> dest, String dbField)
	{
		dest.add(new ReviewItem("Dimensión 1",
				mData.getString(ROOM_SIZE_X_KEY), getKey(), -1, dbField));

		dest.add(new ReviewItem("Dimensión 2",
				mData.getString(ROOM_SIZE_Y_KEY), getKey(), -1, dbField));
	}

	@Override
	public boolean isCompleted()
	{
		return (!TextUtils.isEmpty(mData.getString(ROOM_SIZE_X_KEY)) && 
				!TextUtils.isEmpty(mData.getString(ROOM_SIZE_Y_KEY))) 
				? false : true;

		// return !TextUtils.isEmpty(mData.getString(ROOM_SIZE_X_KEY));
	}

}
