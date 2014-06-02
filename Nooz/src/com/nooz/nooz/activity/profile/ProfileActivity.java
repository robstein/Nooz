package com.nooz.nooz.activity.profile;

import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.nooz.nooz.R;
import com.nooz.nooz.activity.BaseFragmentActivity;
import com.nooz.nooz.model.ProfileInfo;
import com.nooz.nooz.util.GlobalConstant;

/**
 * 
 * @author Rob Stein
 * 
 */
public class ProfileActivity extends BaseFragmentActivity implements OnClickListener {

	// Views
	private ImageView mButtonBack;
	TextView mProfileName;
	TextView mProfileLocation;
	private ImageView mProfilePictureFull;
	private ImageView mButtonProfileCup;
	TextView mButtonProfileNumbers;
	private ImageView mButtonProfilePersons;
	private ImageView mButtonProfileSettings;

	/**
	 * The user id of the user whose profile is being viewed.
	 */
	String mUserId;
	private ProfileBroadcastReceiver mReceiver;
	UserDataController mUserDataController;
	ProfileInfo mProfileInfo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initFields();
		initViews();
		initViewListeners();
		initBundleParameters();
	}

	private void initFields() {
		mReceiver = new ProfileBroadcastReceiver();
		mUserDataController = new UserDataController(this);
	}

	private void initViews() {
		setContentView(R.layout.activity_profile);
		mButtonBack = (ImageView) findViewById(R.id.button_back_from_profile);
		mProfileName = (TextView) findViewById(R.id.profile_name);
		mProfileLocation = (TextView) findViewById(R.id.profile_location);
		mProfileLocation.setOnClickListener(this);
		mProfilePictureFull = (ImageView) findViewById(R.id.profile_picture_full);
		mButtonProfileCup = (ImageView) findViewById(R.id.button_profile_cup);
		mButtonProfileNumbers = (TextView) findViewById(R.id.button_profile_numbers);
		mButtonProfilePersons = (ImageView) findViewById(R.id.button_profile_persons);
		mButtonProfileSettings = (ImageView) findViewById(R.id.button_profile_settings);
	}

	private void initViewListeners() {
		mButtonBack.setOnClickListener(this);
		mProfileName.setOnClickListener(this);
		mProfilePictureFull.setOnClickListener(this);
		mButtonProfileCup.setOnClickListener(this);
		mButtonProfileNumbers.setOnClickListener(this);
		mButtonProfilePersons.setOnClickListener(this);
		mButtonProfileSettings.setOnClickListener(this);
	}

	private void initBundleParameters() {
		Bundle bundle = getIntent().getParcelableExtra("bundle");
		mUserId = bundle.getString("user_id", "");
	}

	@Override
	protected void onResume() {
		super.onResume();

		registerReceivers();
		mUserDataController.populateProfile();
	}

	private void registerReceivers() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(GlobalConstant.PROFILE_INFO_LOADED_ACTION);
		registerReceiver(mReceiver, filter);
	}

	@Override
	protected void onPause() {
		unRegisterReceivers();
		super.onPause();
	}

	private void unRegisterReceivers() {
		unregisterReceiver(mReceiver);
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
