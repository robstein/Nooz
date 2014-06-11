package com.nooz.nooz.activity.map;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.LatLngBounds;
import com.nooz.nooz.activity.article.ArticleActivity;
import com.nooz.nooz.model.Story;

public class StoryDataController {

	private MapActivity mC;

	StoryDataController(MapActivity mapActivity) {
		this.mC = mapActivity;
	}

	void clearAndPopulateStories() {
		LatLngBounds bounds = mC.mMap.getProjection().getVisibleRegion().latLngBounds;
		mC.getNoozService().getAllStories(bounds, mC.mFilterSettings, mC.mMenuController.mCurrentSearchType, null);
	}

	void getStoriesCallBack() {
		List<Story> newStories = mC.getNoozService().getLoadedStories();
		if (!newStories.equals(mC.mStories)) {
			clearStories();
			mC.mStories = newStories;
			// Reset footer
			mC.mFooterAdapter.notifyDataSetChanged();
			mC.drawCirlesOnMap();
			mC.mPager.setCurrentItem(mC.mResumeStory);
		}
	}

	/**
	 * Clears mStories, mCircles, and mGroundOverlays.
	 * <p>
	 * First clears mStories, then calls clearMap(), then notifies the footer
	 * adapter that its data has changed, and then invokes clearMap().
	 * 
	 * @see #clearMap()
	 */
	void clearStories() {
		// Reset index
		mC.mResumeStory = 0;
		mC.mCurrentStory = 0;
		// Clear Stories list
		mC.mStories.clear();
		// Notify adapter that data has changed.
		mC.mFooterAdapter.notifyDataSetChanged();
		// Clear map
		clearMap();
	}

	/**
	 * Removes each of the Circle and GroundOverlay objects from the Google Map,
	 * and then clears all elements in the Circles and GroundOverlay Lists.
	 **/
	private void clearMap() {
		for (Circle c : mC.mCircles) {
			c.remove();
		}
		for (GroundOverlay g : mC.mGroundOverlays) {
			g.remove();
		}
		mC.mCircles.clear();
		mC.mGroundOverlays.clear();
	}

}
