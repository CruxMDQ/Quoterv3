package com.callisto.quoter.wizard.ui;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.callisto.quoter.R;
import com.callisto.quoter.wizard.model.PropertyAddressPage;

public class PropertyAddressFragment extends Fragment
{
	private static final String ARG_KEY = "key";

	private PageFragmentCallbacks mCallbacks;
	private String mKey;
	private PropertyAddressPage mPage;
	private TextView mAddressView;
//	private TextView mPriceView;

	public static PropertyAddressFragment create(String key)
	{
		Bundle args = new Bundle();
		args.putString(ARG_KEY, key);

		PropertyAddressFragment fragment = new PropertyAddressFragment();
		fragment.setArguments(args);
		return fragment;
	}

	public PropertyAddressFragment()
	{
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		Bundle args = getArguments();
		mKey = args.getString(ARG_KEY);
		mPage = (PropertyAddressPage) mCallbacks.onGetPage(mKey);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		View rootView = inflater
				.inflate(R.layout.fragment_page_property_address,
						container, false);
		((TextView) rootView.findViewById(android.R.id.title)).setText(mPage
				.getTitle());

		mAddressView = ((TextView) rootView.findViewById(R.id.txtAddress));
		mAddressView.setText(mPage.getData().getString(
				PropertyAddressPage.ADDRESS_DATA_KEY));

//		mPriceView = ((TextView) rootView.findViewById(R.id.txtPrice));
//		mPriceView.setText(mPage.getData().getString(
//				PropertyAddressNPricePage.PRICE_DATA_KEY));

		return rootView;
	}

	@Override
	public void onAttach(Activity activity)
	{
		super.onAttach(activity);

		if (!(activity instanceof PageFragmentCallbacks))
		{
			throw new ClassCastException(
					"Activity must implement PageFragmentCallback");
		}

		mCallbacks = (PageFragmentCallbacks) activity;
	}

	@Override
	public void onDetach()
	{
		super.onDetach();
		mCallbacks = null;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState)
	{
		super.onViewCreated(view, savedInstanceState);
		
		mAddressView.addTextChangedListener(new TextWatcher()
		{
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count)
			{
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after)
			{
			}
			
			@Override
			public void afterTextChanged(Editable editable)
			{
				mPage.getData().putString(PropertyAddressPage.ADDRESS_DATA_KEY,
						(editable != null) ? editable.toString() : null);
				
				mPage.notifyDataChanged();
			}
		});
		
//		mPriceView.addTextChangedListener(new TextWatcher()
//		{
//			
//			@Override
//			public void onTextChanged(CharSequence s, int start, int before, int count)
//			{
//			}
//			
//			@Override
//			public void beforeTextChanged(CharSequence s, int start, int count,
//					int after)
//			{
//			}
//			
//			@Override
//			public void afterTextChanged(Editable editable)
//			{
//				mPage.getData().putString(PropertyAddressNPricePage.PRICE_DATA_KEY,
//						(editable != null) ? editable.toString() : "0");
//				
//				mPage.notifyDataChanged();
//			}
//		});
	}
	
	@Override
	public void setMenuVisibility(boolean menuVisible)
	{
		super.setMenuVisibility(menuVisible);
		
		if (mAddressView != null)
		{
			InputMethodManager inm = (InputMethodManager) getActivity()
					.getSystemService(Context.INPUT_METHOD_SERVICE);
			
			if (!menuVisible)
			{
				inm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
			}
		}
	}
}
