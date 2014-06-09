package com.nooz.nooz.activity.map;

import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.nooz.nooz.R;
import com.nooz.nooz.util.CategoryResourceHelper;

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
		mC.mCircles.get(mC.mCurrentStory).setFillColor(
				CategoryResourceHelper.getColorByCategory(mC.mStories.get(mC.mCurrentStory).category, mC.SHADE));
		mC.mCircles.get(mC.mCurrentStory).setStrokeColor(
				CategoryResourceHelper.getStrokeColorByCategory(mC.mStories.get(mC.mCurrentStory).category, mC.SHADE));
		mC.mGroundOverlays.get(mC.mCurrentStory).setImage(
				BitmapDescriptorFactory.fromResource(CategoryResourceHelper.getGroundOverlayByCategory(mC.mStories
						.get(mC.mCurrentStory).category)));

		// Change current selected view
		mC.mCurrentStory = position;

		// Brighten current view
		layout = mC.mPager.findViewWithTag(mC.mCurrentStory);
		storyItemShader = (View) layout.findViewById(R.id.story_item_shader);
		storyItemShader.setBackgroundDrawable((mC.getResources()
				.getDrawable(R.drawable.selector_footer_story_item_highlighted)));
		mC.mCircles.get(mC.mCurrentStory).setFillColor(
				CategoryResourceHelper.getColorByCategory(mC.mStories.get(mC.mCurrentStory).category, mC.HIGHLIGHT));
		mC.mCircles.get(mC.mCurrentStory).setStrokeColor(
				CategoryResourceHelper.getStrokeColorByCategory(mC.mStories.get(mC.mCurrentStory).category,
						mC.HIGHLIGHT));
		mC.mGroundOverlays.get(mC.mCurrentStory).setImage(
				BitmapDescriptorFactory.fromResource(CategoryResourceHelper
						.getActiveGroundOverlayByCategory(mC.mStories.get(mC.mCurrentStory).category)));

	}
}
