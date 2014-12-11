package com.nooz.nooz.activity.profile;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.gson.JsonObject;
import com.nooz.nooz.activity.map.MapActivity;
import com.nooz.nooz.util.GlobalConstant;

public class ProfileBroadcastReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		String intentAction = intent.getAction();
		if (GlobalConstant.PROFILE_INFO_LOADED_ACTION.equals(intentAction)) {
			((ProfileActivity) context).mUserDataController.getUserData();
		}
		if (GlobalConstant.BLOB_CREATED_ACTION.equals(intentAction)) {
			// If a blob has been created, upload the profile picture image
			JsonObject blob = ((ProfileActivity) context).getNoozService().getLoadedBlob();
			String sasUrl = blob.getAsJsonPrimitive("sasUrl").toString();
			((ProfileActivity) context).mProfilePictureController.uploadProfilePicture(sasUrl);
		}
		if (GlobalConstant.STORIES_LOADED_ACTION.equals(intentAction)) {
			((ProfileActivity) context).mProfileStoriesController.getStoriesCallBack();
		}

	}
}
