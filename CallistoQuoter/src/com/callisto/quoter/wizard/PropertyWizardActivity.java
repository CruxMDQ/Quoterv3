package com.callisto.quoter.wizard;

import java.util.List;

import com.callisto.quoter.R;
import com.example.android.wizardpager.wizard.model.AbstractWizardModel;
import com.example.android.wizardpager.wizard.model.ModelCallbacks;
import com.example.android.wizardpager.wizard.model.Page;
import com.example.android.wizardpager.wizard.ui.PageFragmentCallbacks;
import com.example.android.wizardpager.wizard.ui.ReviewFragment;
import com.example.android.wizardpager.wizard.ui.StepPagerStrip;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

// Original template: WizardPagerSource/MainActivity
public class PropertyWizardActivity extends FragmentActivity implements 
	PageFragmentCallbacks, ReviewFragment.Callbacks, ModelCallbacks
{
	private ViewPager mPager;
	private MyPagerAdapter mPagerAdapter;
	
	private boolean mEditingAfterReview;
	
	private AbstractWizardModel mWizardModel = new RealEstateWizardModel(this);
	
	private boolean mConsumePageSelectedEvent;
	
	private Button btnNext;
	private Button btnPrevious;
	
	private List<Page> mCurrentPageSequence;
	private StepPagerStrip mStepPagerStrip;
	
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_wizard_property);

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
									.setPositiveButton(R.string.submit_confirm_button, null)
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
	public void onPageTreeChanged()
	{
		mCurrentPageSequence = mWizardModel.getCurrentPageSequence();
		
		recalculateCutOffPage();
		
		// +1 = review step
		mStepPagerStrip.setPageCount(mCurrentPageSequence.size() + 1);
		
		mPagerAdapter.notifyDataSetChanged();
		
		updateBottomBar();
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
	
	@Override
	public AbstractWizardModel onGetModel()
	{
		return mWizardModel;
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

