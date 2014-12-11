package com.nooz.nooz.activity.settings;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.Preference;

import com.nooz.nooz.R;
import com.nooz.nooz.activity.BaseFragmentActivity;

public class SettingsActivity extends BaseFragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Display the fragment as the main content.
		getFragmentManager().beginTransaction().replace(android.R.id.content, new SettingsFragment()).commit();
	}

	void logoutFromPreference() {
		mNoozService.logoutFromActivityOnTopOfMap();
	}

	void openTextActivityFromPreference(String preferenceKey) {
		Intent textActivityIntent = new Intent(mContext, TextViewActivity.class);
		textActivityIntent.putExtra("preferenceKey", preferenceKey);
		mContext.startActivity(textActivityIntent);
	}

}
