package com.nooz.nooz.activity.map;

import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;

import com.nooz.nooz.R;

public class StoryAdapterPageChangeListener implements OnPageChangeListener {

	private MapActivity mC;

	StoryAdapterPageChangeListener(MapActivity mapActivity) {
		this.mC = mapActivity;
	}

	@Override
	public void onPageScrollStateChanged(int state) {
	}

	@Override
	public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
	}

	@Override
	public void onPageSelected(int position) {

		// Shade old view
		View layout = mC.mPager.findViewWithTag(mC.mCurrentStory);
		View storyItemShader = (View) layout.findViewById(R.id.story_item_shader);
		storyItemShader.setBackgroundDrawable((mC.getResources()
				.getDrawable(R.drawable.selector_footer_story_item_unhighlighted)));

		// Change current selected view
		mC.mCurrentStory = position;

		// Brighten current view
		layout = mC.mPager.findViewWithTag(mC.mCurrentStory);
		storyItemShader = (View) layout.findViewById(R.id.story_item_shader);
		storyItemShader.setBackgroundDrawable((mC.getResources()
				.getDrawable(R.drawable.selector_footer_story_item_highlighted)));

	}
}
