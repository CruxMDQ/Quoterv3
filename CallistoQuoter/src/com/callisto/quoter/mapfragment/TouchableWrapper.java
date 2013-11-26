package com.callisto.quoter.mapfragment;

import com.callisto.quoter.ui.PropertiesMapActivity;

import android.content.Context;
import android.view.MotionEvent;
import android.widget.FrameLayout;

public class TouchableWrapper extends FrameLayout
{

	public TouchableWrapper(Context context)
	{
		super(context);
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent event)
	{
		switch (event.getAction())
		{
		case MotionEvent.ACTION_DOWN:
			PropertiesMapActivity.mMapIsTouched = true;
			break;
		case MotionEvent.ACTION_UP:
			PropertiesMapActivity.mMapIsTouched = false;
			break;
		}
		return super.dispatchTouchEvent(event);
	}
}