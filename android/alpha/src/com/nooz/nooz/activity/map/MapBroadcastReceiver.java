package com.nooz.nooz.activity.map;

import com.nooz.nooz.util.GlobalConstant;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class MapBroadcastReceiver extends BroadcastReceiver {

	private static final String TAG = "MapBroadcastReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		String intentAction = intent.getAction();
		if (GlobalConstant.STORIES_LOADED_ACTION.equals(intentAction)) {
			try {
				((MapActivity) context).mStoryDataController.getStoriesCallBack();
			} catch (Exception e) {
				Log.e(TAG, "There was a problem in getStoriesCallBack: " + e.getMessage());
			}
		}
		if (GlobalConstant.RELEVANCE_UPDATE_ACTION.equals(intentAction)) {
			((MapActivity) context).handleUpdateRelevance(intent.getStringExtra("id"), intent.getIntExtra("input", 0));
		}
	}

}
