package com.nooz.nooz.activity.settings;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

import com.nooz.nooz.R;

public class SettingsFragment extends PreferenceFragment implements OnSharedPreferenceChangeListener {

	Preference mOpenSourceLicenses;
	Preference mLogout;
	Preference mPrivacyPolicy;
	Preference mTermsOfUse;
	ListPreference mStoryTextFontSize;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Load the preferences from an XML resource
		addPreferencesFromResource(R.xml.preferences);

		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
		preferences.registerOnSharedPreferenceChangeListener(this);

		initPreferences();
		initPreferencesSummaries();
		initPreferenceClickListeners();
		initPreferenceChangeListeners();
	}

	@Override
	public void onResume() {
		super.onResume();
		getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
	}

	@Override
	public void onPause() {
		super.onPause();
		getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
	}

	private void initPreferences() {
		mStoryTextFontSize = (ListPreference) findPreference(getString(R.string.story_text_size));
		mLogout = (Preference) findPreference(getString(R.string.logout));
		mOpenSourceLicenses = (Preference) findPreference(getString(R.string.open_source_licenses));
		mPrivacyPolicy = (Preference) findPreference(getString(R.string.privacy_policy));
		mTermsOfUse = (Preference) findPreference(getString(R.string.terms_of_use));
	}

	private void initPreferencesSummaries() {
		mStoryTextFontSize.setSummary(mStoryTextFontSize.getValue());
	}

	private void initPreferenceClickListeners() {
		mLogout.setOnPreferenceClickListener(logoutListener);
		mOpenSourceLicenses.setOnPreferenceClickListener(openTextActivityListener);
		mPrivacyPolicy.setOnPreferenceClickListener(openTextActivityListener);
		mTermsOfUse.setOnPreferenceClickListener(openTextActivityListener);
	}

	private void initPreferenceChangeListeners() {
		mStoryTextFontSize.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {	
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				mStoryTextFontSize.setSummary((CharSequence) newValue);				
				return true;
			}
		});
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		if (key.equals(R.string.story_text_size)) {
			Preference pref = findPreference(key);
			// Set summary to be the user-description for the selected value
			pref.setSummary(sharedPreferences.getString(key, ""));
		}
	}

	OnPreferenceClickListener logoutListener = new OnPreferenceClickListener() {
		@Override
		public boolean onPreferenceClick(Preference preference) {
			((SettingsActivity) getActivity()).logoutFromPreference();
			return true;
		}
	};

	OnPreferenceClickListener openTextActivityListener = new OnPreferenceClickListener() {
		@Override
		public boolean onPreferenceClick(Preference preference) {
			((SettingsActivity) getActivity()).openTextActivityFromPreference(preference.getKey());
			return true;
		}
	};

}
