package com.nooz.nooz.activity.map;

import java.io.InputStream;
import java.net.URL;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.gson.JsonObject;
import com.nooz.nooz.R;
import com.nooz.nooz.activity.article.ArticleActivity;
import com.nooz.nooz.model.Story;
import com.nooz.nooz.util.GlobalConstant;
import com.nooz.nooz.util.NoozService;

public class StoryDataController {

	private MapActivity mC;

	StoryDataController(MapActivity mapActivity) {
		this.mC = mapActivity;
	}

	void clearAndPopulateStories() {
		clearStories();
		LatLngBounds bounds = mC.mMap.getProjection().getVisibleRegion().latLngBounds;
		mC.getNoozService().getAllStories(bounds, mC.mFilterSettings, mC.mMenuController.mCurrentSearchType);
	}

	void getStoriesCallBack() {
		mC.mStories = mC.getNoozService().getLoadedStories();
		// Reset footer
		mC.mFooterAdapter.notifyDataSetChanged();
		mC.drawCirlesOnMap();
		mC.mPager.setCurrentItem(mC.mResumeStory);
	}

	/**
	 * Starts an activity to view the provided story
	 * 
	 * @param s
	 *            Story to open
	 */
	void openStory(Story s) {
		Bundle args = new Bundle();
		args.putParcelable("story", s);
		Intent readStoryIntent = new Intent(mC.getApplicationContext(), ArticleActivity.class);
		readStoryIntent.putExtra("bundle", args);
		mC.startActivity(readStoryIntent);
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
