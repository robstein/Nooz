package com.nooz.nooz.activity.map;

import com.nooz.nooz.R;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class FilterSettingsToggler implements OnClickListener {
	
	private MapActivity mC;
	
	FilterSettingsToggler(MapActivity mapActivity) {
		mC = mapActivity;
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.button_filter_mic:
			toggle(R.id.button_filter_mic, mC.mFilterSettings.Audio);
			break;
		case R.id.button_filter_camera:
			toggle(R.id.button_filter_camera, mC.mFilterSettings.Picture);
			break;
		case R.id.button_filter_video:
			toggle(R.id.button_filter_video, mC.mFilterSettings.Video);
			break;
		case R.id.button_filter_people:
			toggle(R.id.button_filter_people, mC.mFilterSettings.People);
			break;
		case R.id.button_filter_community:
			toggle(R.id.button_filter_community, mC.mFilterSettings.Community);
			break;
		case R.id.button_filter_sports:
			toggle(R.id.button_filter_sports, mC.mFilterSettings.Sports);
			break;
		case R.id.button_filter_food:
			toggle(R.id.button_filter_food, mC.mFilterSettings.Food);
			break;
		case R.id.button_filter_public_safety:
			toggle(R.id.button_filter_public_safety, mC.mFilterSettings.PublicSafety);
			break;
		case R.id.button_filter_arts_and_life:
			toggle(R.id.button_filter_arts_and_life, mC.mFilterSettings.ArtsAndLife);
			break;
		}
	}

	private void toggle(int imageViewId, Boolean currentlyOn) {
		if (currentlyOn) {
			ImageView v = (ImageView) mC.findViewById(imageViewId);
			v.setImageResource(getFilterOffImageByViewId(imageViewId));
			mC.mFilterSettings.toggle(imageViewId);
		} else {
			ImageView v = (ImageView) mC.findViewById(imageViewId);
			v.setImageResource(getFilterOnImageByViewId(imageViewId));
			mC.mFilterSettings.toggle(imageViewId);
		}
	}

	private int getFilterOffImageByViewId(int imageViewId) {
		switch (imageViewId) {
		case R.id.button_filter_mic:
			return R.drawable.selector_button_filter_mic;
		case R.id.button_filter_camera:
			return R.drawable.selector_button_filter_camera;
		case R.id.button_filter_video:
			return R.drawable.selector_button_filter_video;
		case R.id.button_filter_people:
			return R.drawable.selector_button_filter_people;
		case R.id.button_filter_community:
			return R.drawable.selector_button_filter_community;
		case R.id.button_filter_sports:
			return R.drawable.selector_button_filter_sports;
		case R.id.button_filter_food:
			return R.drawable.selector_button_filter_food;
		case R.id.button_filter_public_safety:
			return R.drawable.selector_button_filter_public_safety;
		case R.id.button_filter_arts_and_life:
			return R.drawable.selector_button_filter_arts_and_life;
		}
		return -1;
	}

	private int getFilterOnImageByViewId(int imageViewId) {
		switch (imageViewId) {
		case R.id.button_filter_mic:
			return R.drawable.selector_button_filter_mic_active;
		case R.id.button_filter_camera:
			return R.drawable.selector_button_filter_camera_active;
		case R.id.button_filter_video:
			return R.drawable.selector_button_filter_video_active;
		case R.id.button_filter_people:
			return R.drawable.selector_button_filter_people_active;
		case R.id.button_filter_community:
			return R.drawable.selector_button_filter_community_active;
		case R.id.button_filter_sports:
			return R.drawable.selector_button_filter_sports_active;
		case R.id.button_filter_food:
			return R.drawable.selector_button_filter_food_active;
		case R.id.button_filter_public_safety:
			return R.drawable.selector_button_filter_public_safety_active;
		case R.id.button_filter_arts_and_life:
			return R.drawable.selector_button_filter_arts_and_life_active;
		}
		return -1;
	}
}
