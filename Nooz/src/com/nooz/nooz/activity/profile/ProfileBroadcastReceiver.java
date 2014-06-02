package com.nooz.nooz.activity.profile;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.nooz.nooz.activity.map.MapActivity;
import com.nooz.nooz.util.GlobalConstant;

public class ProfileBroadcastReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		String intentAction = intent.getAction();
		if (GlobalConstant.PROFILE_INFO_LOADED_ACTION.equals(intentAction)) {
			((ProfileActivity) context).mUserDataController.getUserData();
		}
	}

}
