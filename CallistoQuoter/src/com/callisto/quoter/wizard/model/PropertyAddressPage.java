package com.callisto.quoter.wizard.model;

import java.util.ArrayList;

import com.callisto.quoter.db.PropDBAdapter;
import com.callisto.quoter.db.PropTypesDBAdapter;
import com.callisto.quoter.wizard.ui.PropertyAddressFragment;

import android.support.v4.app.Fragment;
import android.text.TextUtils;

public class PropertyAddressPage extends Page
{

	public static final String ADDRESS_DATA_KEY = "address";
//			PRICE_DATA_KEY = "price";
	
	public PropertyAddressPage(ModelCallbacks callbacks, String title)
	{
		super(callbacks, title);
	}

	public PropertyAddressPage(ModelCallbacks callbacks, String title,
			String dbTable)
	{
		super(callbacks, title, dbTable);
	}


	@Override
	public Fragment createFragment()
	{
		return PropertyAddressFragment.create(getKey());
	}

	@Override
	public void getReviewItems(ArrayList<ReviewItem> dest)
	{
//		dest.add(new ReviewItem("Direcci—n", mData.getString(ADDRESS_DATA_KEY),
//				getKey(), -1));
//		dest.add(new ReviewItem("Precio", mData.getString(PRICE_DATA_KEY),
//				getKey(), -1));

		dest.add(new ReviewItem("Direcci—n", mData.getString(ADDRESS_DATA_KEY),
				getKey(), -1, this.getDBTable()));
//		dest.add(new ReviewItem("Precio", mData.getString(PRICE_DATA_KEY),
//				getKey(), -1, this.getDBTable()));
	}

	@Override
	public void getReviewItems(ArrayList<ReviewItem> dest, String dbField)
	{
		dest.add(new ReviewItem("Direcci—n", mData.getString(ADDRESS_DATA_KEY),
				getKey(), -1, dbField));
//		dest.add(new ReviewItem("Precio", mData.getString(PRICE_DATA_KEY),
//				getKey(), -1, dbField));
	}

	@Override
	public boolean isCompleted()
	{
		return !TextUtils.isEmpty(mData.getString(ADDRESS_DATA_KEY));
	}
}
