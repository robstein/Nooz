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
import com.nooz.nooz.activity.ArticleActivity;
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
		// Get pictures
		mC.getNoozService().getBlobSases(GlobalConstant.CONTAINER_NAME, mC.mStories);
	}

	void getStoryImageCallBack(int i) {
		// Load the image using the SAS URL
		JsonObject blob = mC.getNoozService().getLoadedStoryImage(i);
		String sasUrl = blob.getAsJsonPrimitive("sasUrl").toString();
		sasUrl = sasUrl.replace("\"", "");
		if ("PICTURE".equals(mC.mStories.get(i).medium)) {
			(new ImageFetcherTask(sasUrl, i)).execute();
		}
		if ("VIDEO".equals(mC.mStories.get(i).medium)) {

		}
		if ("AUDIO".equals(mC.mStories.get(i).medium)) {
			View v = mC.mPager.findViewWithTag(i);
			ProgressBar loading = (ProgressBar) v.findViewById(R.id.loading);
			loading.setVisibility(View.GONE);

			ImageView mic = (ImageView) v.findViewById(R.id.story_medium_icon);
			mic.setImageDrawable(mC.getResources().getDrawable(R.drawable.mic_small));
		}
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
	
	/**
	 * This class specifically handles fetching an image from a URL and setting
	 * the image view source on the screen
	 */
	private class ImageFetcherTask extends AsyncTask<Void, Void, Boolean> {
		private static final String TAG = "ImageFetcherTask";
		private String mUrl;
		private Bitmap mBitmap;
		private Integer mIndex;

		public ImageFetcherTask(String url, int index) {
			mUrl = url;
			mIndex = index;
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			try {
				mBitmap = BitmapFactory.decodeStream((InputStream) new URL(mUrl).getContent());
			} catch (Exception e) {
				Log.e(TAG, "There was a problem decoding the stream to a bitmap: " + e.getMessage());
				return false;
			}
			return true;
		}

		/***
		 * If the image was loaded successfully, set the image view
		 */
		@Override
		protected void onPostExecute(Boolean loaded) {
			if (loaded) {
				if (mIndex < mC.mStories.size()) {
					mC.mStories.get(mIndex).setBitmap(mBitmap);

					// update image
					View v = mC.mPager.findViewWithTag(mIndex);
					if (v != null) {
						ImageView image = (ImageView) v.findViewById(R.id.story_item_article_image);
						if ("PICTURE".equals(mC.mStories.get(mIndex).medium)) {
							if (mC.mStories.get(mIndex).bitmap != null) {
								image.setImageBitmap(mC.mStories.get(mIndex).bitmap);

								ProgressBar loading = (ProgressBar) v.findViewById(R.id.loading);
								loading.setVisibility(View.GONE);
								image.setVisibility(View.VISIBLE);
							}
						}
					}
				}
			}
		}
	}
}
