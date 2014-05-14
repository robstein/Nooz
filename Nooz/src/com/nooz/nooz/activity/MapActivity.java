package com.nooz.nooz.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nooz.nooz.R;
import com.nooz.nooz.widget.PagerContainer;

public class MapActivity extends Activity implements OnClickListener{


	private PagerContainer mContainer;
	private ViewPager mPager;
	private TextView mButtonRelevant;
	private TextView mButtonBreaking;
	private ImageView mButtonSettingsAndFilters;
	private ImageView mButtonRefresh;
	private ImageView mButtonNewStory;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);

		mContainer = (PagerContainer) findViewById(R.id.pager_container);
		mPager = mContainer.getViewPager();
		PagerAdapter adapter = new MyPagerAdapter(this);
		mPager.setAdapter(adapter);
		// Necessary or the pager will only have one extra page to show
		// make this at least however many pages you can see
		mPager.setOffscreenPageLimit(adapter.getCount());
		// A little space between pages
		mPager.setPageMargin(pixelsToDips(4));
		// If hardware acceleration is enabled, you should also remove
		// clipping on the pager for its children.
		mPager.setClipChildren(false);
		
		mButtonNewStory = (ImageView) findViewById(R.id.button_new_story);
		mButtonNewStory.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.button_new_story:
			Intent newStoryIntent = new Intent(getApplicationContext(), NewArticleActivity.class);
			startActivity(newStoryIntent);
		}
	}

	private int pixelsToDips(int i) {
		Resources r = getResources();
		float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, i, r.getDisplayMetrics());
		return (int) Math.floor(px);
	}

	// Nothing special about this adapter, just throwing up colored views for
	// demo
	private class MyPagerAdapter extends PagerAdapter {

		private Context mContext;

		public MyPagerAdapter(Context context) {
			this.mContext = context;
		}

		@Override
		public void setPrimaryItem(ViewGroup container, int position, Object object) {
			LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(mContext.LAYOUT_INFLATER_SERVICE);
			View layout = inflater.inflate(R.layout.story_item, null);
			View storyItemShader = (View) layout.findViewById(R.id.story_item_shader);
			storyItemShader.setBackgroundColor(0x80000000);
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {

			LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(mContext.LAYOUT_INFLATER_SERVICE);
			View layout = inflater.inflate(R.layout.story_item, null);

			TextView title = (TextView) layout.findViewById(R.id.story_item_title);
			TextView author = (TextView) layout.findViewById(R.id.story_item_author);
			View categoryRuler = (View) layout.findViewById(R.id.categoryRuler);

			if (position != 0) {
				title.setText("Illinois Men's Wrestling Wins 1st");
				author.setText("Drew Smith");
				categoryRuler.setBackgroundColor(0xFFE84C3D);
			} else {
				title.setText("Spring Engineering Career Fair");
				author.setText("Matt Birkel");
				categoryRuler.setBackgroundColor(0xFF377DEC);
			}

			((ViewPager) container).addView(layout);

			return layout;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}

		@Override
		public int getCount() {
			return 5;
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return (view == object);
		}
	}


}
