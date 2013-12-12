package com.callisto.quoter.wizard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.database.SQLException;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.callisto.quoter.R;
import com.callisto.quoter.db.PropDBAdapter;
import com.callisto.quoter.ui.PropDetailActivity;
import com.callisto.quoter.ui.PropListActivity;
import com.callisto.quoter.utils.ImageUtils;
import com.callisto.quoter.wizard.model.AbstractWizardModel;
import com.callisto.quoter.wizard.model.ModelCallbacks;
import com.callisto.quoter.wizard.model.Page;
import com.callisto.quoter.wizard.model.ReviewItem;
import com.callisto.quoter.wizard.ui.PageFragmentCallbacks;
import com.callisto.quoter.wizard.ui.ReviewFragment;
import com.callisto.quoter.wizard.ui.StepPagerStrip;

// Original template: WizardPagerSource/MainActivity
public class PropertyWizardActivity extends FragmentActivity implements 
	PageFragmentCallbacks, ReviewFragment.Callbacks, ModelCallbacks
{
	private ViewPager mPager;
	private MyPagerAdapter mPagerAdapter;
	
	private boolean mEditingAfterReview;
	
	private AbstractWizardModel mWizardModel; // = new RealEstateWizardModel(this);	// this
	
	private boolean mConsumePageSelectedEvent;
	
	private Button btnNext;
	private Button btnPrevious;
	
	private List<Page> mCurrentPageSequence;
	private StepPagerStrip mStepPagerStrip;
	
	private Uri mContactUri;
	
	private PropDBAdapter mProperties;
	private long mPropId;
	
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_wizard_property);

		Bundle extras = getIntent().getExtras();
		
		mWizardModel = new RealEstateWizardModel(this);
//		mWizardModel = new RealEstateWizardModel(this, extras);		
	
		if (savedInstanceState != null)
		{
			mWizardModel.load(savedInstanceState.getBundle("model"));
		}
		
		mWizardModel.registerListener(this);
		
		mPagerAdapter = new MyPagerAdapter(getSupportFragmentManager());
		
		mPager = (ViewPager) findViewById(R.id.pager);
		
		mPager.setAdapter(mPagerAdapter);
		
		mStepPagerStrip = (StepPagerStrip) findViewById(R.id.strip);
		
		mStepPagerStrip.setOnPageSelectedListener(new StepPagerStrip.OnPageSelectedListener()
		{	
			@Override
			public void onPageStripSelected(int position)
			{
				position = Math.min(mPagerAdapter.getCount() - 1, position);
				
				if (mPager.getCurrentItem() != position)
				{	
					mPager.setCurrentItem(position);
				}
			}
		});
		
		mContactUri = Uri.parse(getIntent().getStringExtra("mContactUri"));
		
		/*
		 * Creating houses adapter
		 */
		mProperties = new PropDBAdapter(this);
		mProperties.open();
					
		btnNext = (Button) findViewById(R.id.next_button);
		btnPrevious = (Button) findViewById(R.id.prev_button);
//		btnDone = (Button) findViewById(R.id.done_button);
		
		mPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener()
		{
			@Override
			public void onPageSelected(int position)
			{
				mStepPagerStrip.setCurrentPage(position);
				{
					if (mConsumePageSelectedEvent)
					{
						mConsumePageSelectedEvent = false;
						return;
					}
					
					mEditingAfterReview = false;
					updateBottomBar();
				}
			}
		});
		
		btnNext.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				
				if (mPager.getCurrentItem() == mCurrentPageSequence.size())
				{
					DialogFragment dg = new DialogFragment()
					{
						@Override
						public Dialog onCreateDialog(Bundle savedInstanceState)
						{
							return new AlertDialog.Builder(getActivity())
									.setMessage(R.string.submit_confirm_message)
//									.setPositiveButton(R.string.submit_confirm_button, null)									
									// TODO Develop code to directly write the new property to database 
									.setPositiveButton(R.string.submit_confirm_button, new OnClickListener()
									{	
										@Override
										public void onClick(DialogInterface dialog, int which)
										{
											Intent result = new Intent();
											int pageIndex = 0;
	
//											for (Page page : mWizardModel.getCurrentPageSequence())
//											{
//												String t = page.getData().getString(Page.SIMPLE_DATA_KEY);
//												
//												// t == null => page contains multiple choices?
//												if (t != null)
//												{
//													// result.putExtra(page.getTitle(), t);
//													// result.putExtra("choiceCount", 1);
//
//													result.putExtra(page.getTitle(), 1);
//													result.putExtra(page.getTitle() + ", answer " + 1, t);
//												}
//												else
//												{
//													// mPage.getData().putStringArrayList(Page.SIMPLE_DATA_KEY, selections);
//													java.util.ArrayList<String> selections = page.getData().getStringArrayList(Page.SIMPLE_DATA_KEY);
//													
//													int choices = selections.size();
//													String t2;
//													result.putExtra(page.getTitle(), choices);
//													
//													for (int i = 0; i < choices; i++)
//													{
//														t2 = selections.get(i);
//														// result.putExtra(page.getTitle(), t2);
//														result.putExtra(page.getTitle() + ", answer " + i, t2);
//													}
//												}
//												result.putExtra("Page" + pageIndex, page.getTitle());
//												pageIndex++;
//											}
//											
//											result.putExtra("Pages", pageIndex);
											
											/* "Dis 'ere chunk o' code pulls reviw'd items from... uh... dat 'wizardModel' fing." */
											ArrayList<ReviewItem> reviewItems = new ArrayList<ReviewItem>();
											
											for (Page page : mWizardModel.getCurrentPageSequence())
											{
												page.getReviewItems(reviewItems);
											}
											
											Collections.sort(reviewItems, new Comparator<ReviewItem>()
											{
												@Override
												public int compare(ReviewItem a, ReviewItem b)
												{
													return a.getWeight() > b.getWeight() ? +1 : a.getWeight() < b
															.getWeight() ? -1 : 0;
												}
											});
											/* "End o' dat code chunk. */
											
											for (int i = 0; i < reviewItems.size(); i++)
											{
												ReviewItem t = reviewItems.get(i);
												
												
											}
											
											setResult(RESULT_OK, result);
											finish();
										}
									})
									.setNegativeButton(android.R.string.cancel, null)
									.create();
						}
					};
					
					dg.show(getSupportFragmentManager(), "TODO_define_this_tag");
				}
				else
				{
					if (mEditingAfterReview)
					{
						mPager.setCurrentItem(mPagerAdapter.getCount() - 1);
					}
					else 
					{
						mPager.setCurrentItem(mPager.getCurrentItem() + 1);
					}
				}
			}
		});
		
		btnPrevious.setOnClickListener(new View.OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				mPager.setCurrentItem(mPager.getCurrentItem() - 1);
			}
		});
		
		onPageTreeChanged();
		
		updateBottomBar();
	}
	
	@Override
	public void onEditScreenAfterReview(String key)
	{
		for (int i = mCurrentPageSequence.size() - 1; i >= 0; i--)
		{
			if (mCurrentPageSequence.get(i).getKey().equals(key))
			{
				mConsumePageSelectedEvent = true;
				mEditingAfterReview = true;
				mPager.setCurrentItem(i);
				updateBottomBar();
				break;
			}
		}
	}

	@Override
	public AbstractWizardModel onGetModel()
	{
		return mWizardModel;
	}

	@Override
	public void onPageTreeChanged()
	{
		mCurrentPageSequence = mWizardModel.getCurrentPageSequence();
		
		recalculateCutOffPage();
		
		// +1 = review step
		mStepPagerStrip.setPageCount(mCurrentPageSequence.size() + 1);
		
		mPagerAdapter.notifyDataSetChanged();
		
		updateBottomBar();
	}

	@Override
	public void onPageDataChanged(Page page)
	{
		if (page.isRequired())
		{
			if (recalculateCutOffPage())
			{
				mPagerAdapter.notifyDataSetChanged();
				updateBottomBar();
			}
		}
	}

	@Override
	public Page onGetPage(String key)
	{
		return mWizardModel.findByKey(key);
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		
		mWizardModel.unregisterListener(this);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);
		outState.putBundle("model", mWizardModel.save());
	}

	private boolean recalculateCutOffPage()
	{
		int cutOffPage = mCurrentPageSequence.size() + 1;
		for (int i = 0; i < mCurrentPageSequence.size(); i++)
		{
			Page page = mCurrentPageSequence.get(i);
			if (page.isRequired() && !page.isCompleted())
			{
				cutOffPage = i;
				break;
			}
		}
		
		if (mPagerAdapter.getCutOffPage() != cutOffPage)
		{
			mPagerAdapter.setCutOffPage(cutOffPage);
			return true;
		}
		
		return false;
	}

	/***
	 * Saves form content into database.
	 */
	private void save() 
	{
		ContentValues reg = new ContentValues();
//		
//		reg.put(PropDBAdapter.C_ADDRESS, txtAddress.getText().toString());
//		reg.put(PropDBAdapter.C_BEDROOMS, Integer.parseInt(txtBedrooms.getText().toString()));
//
		/*
		 * DATABASE VERSION 4
		 */
		try
		{
			reg.put(PropDBAdapter.C_OWNER_URI, mContactUri.toString());
		}
		catch(Exception e)
		{
			Log.i(this.getClass().toString() + ".save", "Owner URI missing or cannot be retrieved");
		}
//		
//		/*
//		 * DATABASE VERSION 5
//		 */
//		reg.put(PropDBAdapter.C_LATITUDE, mCurrentLat);
//		reg.put(PropDBAdapter.C_LONGITUDE, mCurrentLong);
//		
//		/*
//		 * DATABASE VERSION 6
//		 */
//		try
//		{
//			byte[] storedPic = ImageUtils.bitmapToByteArray(mBitmap);
//			
//			reg.put(PropDBAdapter.C_IMAGE, storedPic);
//		}
//		catch(Exception e)
//		{
//			Log.i(this.getClass().toString() + ".save", "Cannot put image into ContentValues object");
//		}
//		
//		/*
//		 * CHECKBOX IMPLEMENTED ON LESSON 9
//		 */		
//		if (chkConfirmed.isChecked())
//		{
//			reg.put(PropDBAdapter.C_CONFIRMED, 1);
//		}
//		else
//		{
//			reg.put(PropDBAdapter.C_CONFIRMED, 0);
//		}
//		
//		/*
//		 * SPINNER IMPLEMENTED ON LESSON 10
//		 */
////		reg.put(PropDBAdapter.C_PROP_RATING_ID, spinnerRating.getSelectedItemId());
//		reg.put(PropDBAdapter.C_RATING_ID, mRatingId);
//
//		/*
//		 * MUST use the definition at this DB adapter because of differing column names
//		 * 
//		 * FOLLOW-UP: after setting up the onSelectedItemListener on the spinner, is this really necessary? 
//		 */
////		mPropTypeId = Long.parseLong(spinnerType.getSelectedItemId());
//		Log.i(this.getClass().toString(), "mPropTypeId: " + mPropTypeId);
//		
//		reg.put(PropDBAdapter.C_PROP_TYPE_ID, mPropTypeId);
////		reg.put(PropDBAdapter.C_PROP_TYPE_ID, spinnerType.getSelectedItemId());
//
		/*
		 * Save content into database
		 */
		try
		{
			mPropId = mProperties.insert(reg);
			Toast.makeText(PropertyWizardActivity.this, R.string.house_create_notice + " with ID = " + mPropId, Toast.LENGTH_LONG).show();
		}
		catch(SQLException e)
		{
			Log.i(this.getClass().toString(), e.getMessage());
		}
	}
	
	private void updateBottomBar()
	{
		int position = mPager.getCurrentItem();
		
		if (position == mCurrentPageSequence.size())
		{
			btnNext.setText(R.string.finish);
			
			btnNext.setBackgroundResource(R.drawable.finish_background);
			
			btnNext.setTextAppearance(this, R.style.TextAppearanceFinish);
		}
		else
		{
			btnNext.setText(mEditingAfterReview ? R.string.review 
					: R.string.next);
			
			btnNext.setBackgroundResource(R.drawable.selectable_item_background);
			
			TypedValue v = new TypedValue();
			
			btnNext.setTextAppearance(this, v.resourceId);
			
			btnNext.setEnabled(position != mPagerAdapter.getCutOffPage());
		}
		
		btnPrevious.setVisibility(position <= 0 ? View.INVISIBLE : View.VISIBLE);
	}

	public class MyPagerAdapter extends FragmentStatePagerAdapter
	{
		private int mCutOffPage;
		private Fragment mPrimaryItem;

		public MyPagerAdapter(FragmentManager fm)
		{
			super(fm);
		}

		@Override
		public Fragment getItem(int i)
		{
			if (i >= mCurrentPageSequence.size())
			{
				return new ReviewFragment();
			}

			return mCurrentPageSequence.get(i).createFragment();
		}

		@Override
		public int getItemPosition(Object object)
		{
			// TODO: be smarter about this
			if (object == mPrimaryItem)
			{
				// Re-use the current fragment (its position never changes)
				return POSITION_UNCHANGED;
			}

			return POSITION_NONE;
		}

		@Override
		public void setPrimaryItem(ViewGroup container, int position,
				Object object)
		{
			super.setPrimaryItem(container, position, object);
			mPrimaryItem = (Fragment) object;
		}

		@Override
		public int getCount()
		{
			if (mCurrentPageSequence == null)
			{
				return 0;
			}
			return Math.min(mCutOffPage + 1, mCurrentPageSequence.size() + 1);
		}

		public void setCutOffPage(int cutOffPage)
		{
			if (cutOffPage < 0)
			{
				cutOffPage = Integer.MAX_VALUE;
			}
			mCutOffPage = cutOffPage;
		}

		public int getCutOffPage()
		{
			return mCutOffPage;
		}
	}
}

