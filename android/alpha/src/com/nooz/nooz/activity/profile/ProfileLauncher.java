package com.nooz.nooz.activity.profile;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.nooz.nooz.R;

public class ProfileLauncher {

	@SuppressLint("NewApi")
	public static void openProfile(Context c, String userId) {
		Intent profileIntent = new Intent(c.getApplicationContext(), ProfileActivity.class);
		Bundle args = new Bundle();
		args.putString("user_id", userId);
		profileIntent.putExtra("bundle", args);
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
			Bundle bndlanimation = ActivityOptions.makeCustomAnimation(c.getApplicationContext(), R.anim.slide_in_left,
					R.anim.fade_out).toBundle();
			c.startActivity(profileIntent, bndlanimation);
		} else {
			c.startActivity(profileIntent);
		}
	}
}
