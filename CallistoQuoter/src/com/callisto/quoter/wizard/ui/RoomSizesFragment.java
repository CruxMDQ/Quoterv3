package com.callisto.quoter.wizard.ui;

import com.callisto.quoter.R;
import com.callisto.quoter.wizard.model.RoomSizesPage;
import com.example.android.wizardpager.wizard.ui.PageFragmentCallbacks;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

public class RoomSizesFragment extends Fragment
{
	static private final String C_KEY = "key";

	private PageFragmentCallbacks mCallbacks;
	private String mKey;
	private RoomSizesPage mPage;

	private TextView txtWidthX;
	private TextView txtWidthY;

	static public RoomSizesFragment create(String key)
	{
		Bundle args = new Bundle();
		args.putString(C_KEY, key);

		RoomSizesFragment fragment = new RoomSizesFragment();
		fragment.setArguments(args);
		return fragment;
	}

	public RoomSizesFragment()
	{

	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		Bundle args = getArguments();
		mKey = args.getString(C_KEY);
		mPage = (RoomSizesPage) mCallbacks.onGetPage(mKey);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		View rootView = inflater.inflate(R.layout.fragment_page_room_size,
				container, false);

		((TextView) rootView.findViewById(android.R.id.title)).setText(mPage
				.getTitle());

		txtWidthX = ((TextView) rootView.findViewById(R.id.txtWidthX));

		txtWidthY = ((TextView) rootView.findViewById(R.id.txtWidthY));

		return rootView;
	}

	@Override
	public void onAttach(Activity activity)
	{
		super.onAttach(activity);

		if (!(activity instanceof PageFragmentCallbacks))
		{
			throw new ClassCastException(
					"Activity MUST implement PageFragmentCallbacks");
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
		
		txtWidthX.addTextChangedListener(new TextWatcher()
		{

			@Override
			public void afterTextChanged(Editable editable)
			{
				// this retrieves a bundle from Page.java
				mPage.getData().putString(RoomSizesPage.ROOM_SIZE_X_KEY, 
						(editable != null) ? editable.toString() : null);
				
				// this has been commented as causing nothing to happen...
				mPage.notifyDataChanged();
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after)
			{
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count)
			{
			}
		});
		
		txtWidthY.addTextChangedListener(new TextWatcher()
		{

			@Override
			public void afterTextChanged(Editable editable)
			{
				// this retrieves a bundle from Page.java
				mPage.getData().putString(RoomSizesPage.ROOM_SIZE_Y_KEY, 
						(editable != null) ? editable.toString() : null);
				
				// this has been commented as causing nothing to happen...
				mPage.notifyDataChanged();
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after)
			{
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count)
			{
			}
		});
	}
	
	@Override
	public void setMenuVisibility(boolean menuVisible)
	{
		super.setMenuVisibility(menuVisible);
		
		if (txtWidthX != null)
		{
			InputMethodManager inm = (InputMethodManager) getActivity()
					.getSystemService(Context.INPUT_METHOD_SERVICE);
			if (!menuVisible)
			{
				inm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
			}
		}

		if (txtWidthY != null)
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
