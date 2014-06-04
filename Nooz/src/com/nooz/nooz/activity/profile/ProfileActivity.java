package com.nooz.nooz.activity.profile;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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

	private static final String TAG = "ProfileActivity";
	static final int RESULT_LOAD_IMAGE = 1111;

	// Profile Views
	private ImageView mButtonBack;
	TextView mProfileName;
	TextView mProfileLocation;
	private ImageView mProfilePictureFull;
	private ImageView mButtonProfileCup;
	TextView mButtonProfileNumbers;
	private ImageView mButtonProfilePersons;
	private ImageView mButtonProfileSettings;

	// Crop Image Views
	ImageView mButtonBackFromCrop;
	ImageView mButtonCropOk;
	ImageView mButtonPictureUncropped;

	/**
	 * The user id of the user whose profile is being viewed.
	 */
	String mUserId;
	private ProfileBroadcastReceiver mReceiver;
	UserDataController mUserDataController;
	ProfileInfo mProfileInfo;
	ProfilePictureController mProfilePictureController;
	public RelativeLayout mProfileLayout;
	public RelativeLayout mCropPictureLayout;

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
		mProfilePictureController = new ProfilePictureController(this);
	}

	private void initViews() {
		setContentView(R.layout.activity_profile);

		// Profile Views
		mProfileLayout = (RelativeLayout) findViewById(R.id.profile);
		mButtonBack = (ImageView) findViewById(R.id.button_back_from_profile);
		mProfileName = (TextView) findViewById(R.id.profile_name);
		mProfileLocation = (TextView) findViewById(R.id.profile_location);
		mProfileLocation.setOnClickListener(this);
		mProfilePictureFull = (ImageView) findViewById(R.id.profile_picture_full);
		mButtonProfileCup = (ImageView) findViewById(R.id.button_profile_cup);
		mButtonProfileNumbers = (TextView) findViewById(R.id.button_profile_numbers);
		mButtonProfilePersons = (ImageView) findViewById(R.id.button_profile_persons);
		mButtonProfileSettings = (ImageView) findViewById(R.id.button_profile_settings);

		// Crop image views
		mCropPictureLayout = (RelativeLayout) findViewById(R.id.profile_crop_picture);
		mButtonBackFromCrop = (ImageView) findViewById(R.id.button_back_from_crop);
		mButtonCropOk = (ImageView) findViewById(R.id.button_crop_ok);
		mButtonPictureUncropped = (ImageView) findViewById(R.id.profile_picture_uncropped);
	}

	private void initViewListeners() {
		// Profile listeners
		mButtonBack.setOnClickListener(this);
		mProfileName.setOnClickListener(this);
		mProfilePictureFull.setOnClickListener(this);
		mButtonProfileCup.setOnClickListener(this);
		mButtonProfileNumbers.setOnClickListener(this);
		mButtonProfilePersons.setOnClickListener(this);
		mButtonProfileSettings.setOnClickListener(this);
		
		// Crop image listeners
		mButtonBackFromCrop.setOnClickListener(this);
		mButtonCropOk.setOnClickListener(this);
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

	/**
	 * Result handler for any intents started with startActivityForResult,
	 * namely the profile picture selection from the gallery.
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		mProfilePictureController.handleResultFromGallery(requestCode, resultCode, data);
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
		case R.id.profile_picture_full:
			mProfilePictureController.selectImage();
			break;
		case R.id.button_back_from_crop:
			mProfilePictureController.cancel();
			break;
		case R.id.button_crop_ok:
			mProfilePictureController.confirm();
			break;
		}
	}

	private void finishWithAnimation() {
		finish();
		this.overridePendingTransition(R.anim.fade_in, R.anim.slide_out_left);
	}
}
