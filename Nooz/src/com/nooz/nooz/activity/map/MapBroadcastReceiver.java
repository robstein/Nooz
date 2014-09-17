package com.nooz.nooz.activity.map;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.nooz.nooz.util.GlobalConstant;

public class MapBroadcastReceiver extends BroadcastReceiver {

	private static final String TAG = "MapBroadcastReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		String intentAction = intent.getAction();
		if (GlobalConstant.QUADTREE_AND_TOP_STORIES_LOADED_ACTION.equals(intentAction)) {
			try {
				((MapActivity) context).mStoryReservoir.getInitialStoriesCallback();
			} catch (Exception e) {
				Log.e(TAG, "There was a problem in getStoriesCallBack: " + e.getMessage());
			}
		}
		if (GlobalConstant.RELEVANCE_UPDATE_ACTION.equals(intentAction)) {
			((MapActivity) context).handleUpdateRelevance(intent.getStringExtra("id"), intent.getIntExtra("input", 0));
		}
	}

}
