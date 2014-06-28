package com.nooz.nooz.activity.map;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;

import com.nooz.nooz.R;
import com.nooz.nooz.activity.article.ArticleLauncher;
import com.nooz.nooz.activity.profile.ProfileLauncher;
import com.nooz.nooz.mediarecorder.MediaRecorderActivity;
import com.nooz.nooz.util.Alert;

public class MapActivityOnClickListener implements OnClickListener {

	private MapActivity mC;

	MapActivityOnClickListener(MapActivity c) {
		mC = c;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.region:
			mC.mRegion.setText("");
		case R.id.button_relevant:
			mC.mMenuController.switchSearchTypes(R.id.button_relevant);
			break;
		case R.id.story_item_shader:
			ArticleLauncher.openStory(mC, mC.mStories.get(mC.mCurrentStory));
			break;
		case R.id.button_back_from_filter:
			mC.mMenuController.hideFiltersLayout();
			break;
		}
	}

}
