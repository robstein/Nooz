package com.nooz.nooz.activity.map;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.nooz.nooz.R;
import com.nooz.nooz.activity.ArticleActivity;
import com.nooz.nooz.activity.MediaRecorderActivity;
import com.nooz.nooz.activity.ProfileActivity;
import com.nooz.nooz.util.Alert;

public class MapActivityOnClickListener implements OnClickListener {

	private MapActivity mC;
	
	MapActivityOnClickListener(MapActivity c) {
		mC = c;
	}
	
	@SuppressLint("NewApi")
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.region:
			mC.mRegion.setText("");
		case R.id.button_relevant:
			mC.mMenuController.switchSearchTypes(R.id.button_relevant);
			break;
		case R.id.button_breaking:
			mC.mMenuController.switchSearchTypes(R.id.button_breaking);
			break;
		case R.id.button_settings:
			mC.mMenuController.hideOrShowSettingsMenu();
			break;
		case R.id.button_refresh:
			mC.mStoryDataController.clearAndPopulateStories();
			break;
		case R.id.button_new_story:
			if (mC.getCurrentLocation() == null) {
				Alert.createAndShowDialog("Please turn on Locations Services", "Location not found", mC);
			} else {
				Intent mediaRecorderIntent = new Intent(mC.getApplicationContext(), MediaRecorderActivity.class);
				mC.startActivity(mediaRecorderIntent);
			}
			break;
		case R.id.story_item_layout:
			mC.mStoryDataController.openStory(mC.mStories.get(mC.mCurrentStory));
			break;
		case R.id.button_map_filters:
			mC.mMenuController.showFiltersLayout();
			break;
		case R.id.button_back_from_filter:
			mC.mMenuController.hideFiltersLayout();
			break;
		case R.id.button_profile:
			if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
				Intent profileIntent = new Intent(mC.getApplicationContext(), ProfileActivity.class);
				Bundle bndlanimation = ActivityOptions.makeCustomAnimation(mC.getApplicationContext(),
						R.anim.slide_in_left, R.anim.fade_out).toBundle();
				mC.startActivity(profileIntent, bndlanimation);
			} else {
				Intent profileIntent = new Intent(mC.getApplicationContext(), ProfileActivity.class);
				mC.startActivity(profileIntent);
			}
		}
	}

}