package com.callisto.quoter.wizard.model;

import java.util.ArrayList;

import com.callisto.quoter.db.PropDBAdapter;
import com.callisto.quoter.db.PropTypesDBAdapter;
import com.callisto.quoter.wizard.ui.PropertyAddressNPriceFragment;

import android.support.v4.app.Fragment;
import android.text.TextUtils;

public class PropertyAddressNPricePage extends Page
{

	public static final String ADDRESS_DATA_KEY = "address",
			PRICE_DATA_KEY = "price";
	
	public PropertyAddressNPricePage(ModelCallbacks callbacks, String title)
	{
		super(callbacks, title);
	}

	@Override
	public Fragment createFragment()
	{
		return PropertyAddressNPriceFragment.create(getKey());
	}

	@Override
	public void getReviewItems(ArrayList<ReviewItem> dest)
	{
		dest.add(new ReviewItem("Direcci—n", mData.getString(ADDRESS_DATA_KEY),
				getKey(), -1));
		dest.add(new ReviewItem("Precio", mData.getString(PRICE_DATA_KEY),
				getKey(), -1));
	}

	@Override
	public boolean isCompleted()
	{
		return !TextUtils.isEmpty(mData.getString(ADDRESS_DATA_KEY));
	}
}
