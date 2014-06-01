package com.nooz.nooz.activity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.nooz.nooz.R;

/**
 * 
 * @author Rob Stein
 * 
 */
public class ProfileActivity extends BaseFragmentActivity implements OnClickListener {

	private ImageView mButtonBack;
	private TextView mProfileName;
	private TextView mProfileLocation;
	private ImageView mProfilePictureFull;
	private ImageView mButtonProfileCup;
	private TextView mButtonProfileNumbers;
	private ImageView mButtonProfilePersons;
	private ImageView mButtonProfileSettings;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile);

		mButtonBack = (ImageView) findViewById(R.id.button_back_from_profile);
		mButtonBack.setOnClickListener(this);
		mProfileName = (TextView) findViewById(R.id.profile_name);
		mProfileName.setOnClickListener(this);
		mProfileLocation = (TextView) findViewById(R.id.profile_location);
		mProfileLocation.setOnClickListener(this);
		mProfilePictureFull = (ImageView) findViewById(R.id.profile_picture_full);
		mProfilePictureFull.setOnClickListener(this);
		mButtonProfileCup = (ImageView) findViewById(R.id.button_profile_cup);
		mButtonProfileCup.setOnClickListener(this);
		mButtonProfileNumbers = (TextView) findViewById(R.id.button_profile_numbers);
		mButtonProfileNumbers.setOnClickListener(this);
		mButtonProfilePersons = (ImageView) findViewById(R.id.button_profile_persons);
		mButtonProfilePersons.setOnClickListener(this);
		mButtonProfileSettings = (ImageView) findViewById(R.id.button_profile_settings);
		mButtonProfileSettings.setOnClickListener(this);

	}

	@Override
	public void onBackPressed() {
		finishWithAnimation();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.button_back_from_profile:
			finishWithAnimation();
			break;
		case R.id.button_profile_settings:
			mNoozService.logout();
			break;
		}
	}

	private void finishWithAnimation() {
		finish();
		this.overridePendingTransition(R.anim.fade_in, R.anim.slide_out_left);
	}
}
