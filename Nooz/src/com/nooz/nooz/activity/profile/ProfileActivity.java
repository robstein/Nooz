package com.nooz.nooz.activity.profile;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.nooz.nooz.R;
import com.nooz.nooz.activity.ActivityGestureDetector;
import com.nooz.nooz.activity.BaseFragmentActivity;
import com.nooz.nooz.activity.article.ArticleLauncher;
import com.nooz.nooz.model.ProfileInfo;
import com.nooz.nooz.util.GlobalConstant;
import com.soundcloud.android.crop.Crop;

/**
 * 
 * @author Rob Stein
 * 
 */
public class ProfileActivity extends BaseFragmentActivity implements OnClickListener {

	private static final String TAG = "ProfileActivity";
	static final int RESULT_LOAD_IMAGE = 1111;
	static final boolean HIGHLIGHT = true;

	// Profile Views
	private ImageView mButtonBack;
	TextView mProfileName;
	TextView mProfileLocation;
	private NetworkImageView mProfilePictureFull;
	private ImageView mButtonProfileCup;
	TextView mButtonProfileNumbers;
	private ImageView mButtonProfilePersons;
	private ImageView mButtonProfileSettingsOrPm;
	GridView mUserStoryGridView;

	// Crop Image Views
	ImageView mButtonBackFromCrop;
	ImageView mButtonCropOk;
	ImageView mResultView;

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
	Boolean mIsMyProfile;

	private GestureDetector mGestureDetector;
	private OnTouchListener mGestureListener;

	ProfileStoriesController mProfileStoriesController;
	private ProfileAwardsController mProfileAwardsController;
	private ProfilePeopleController mProfilePeopleController;
	ProfileStoryAdapter mProfileStoriesAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initBundleParameters();
		initFields();
		initViews();
		initGridAdapter();
		initViewListeners();
	}

	private void initBundleParameters() {
		Bundle bundle = getIntent().getParcelableExtra("bundle");
		mUserId = bundle.getString("user_id", "");
	}

	private void initFields() {
		mIsMyProfile = mUserId.equals(mNoozService.getUserId());
		mReceiver = new ProfileBroadcastReceiver();
		mUserDataController = new UserDataController(this);
		mProfilePictureController = new ProfilePictureController(this);
		mProfileStoriesController = new ProfileStoriesController(this);
		mProfileAwardsController = new ProfileAwardsController(this);
		mProfilePeopleController = new ProfilePeopleController(this);
	}

	private void initViews() {
		setContentView(R.layout.activity_profile);

		// Profile Views
		mProfileLayout = (RelativeLayout) findViewById(R.id.profile);
		mButtonBack = (ImageView) findViewById(R.id.button_back_from_profile);
		mProfileName = (TextView) findViewById(R.id.profile_name);
		mProfileLocation = (TextView) findViewById(R.id.profile_location);
		mProfileLocation.setOnClickListener(this);
		mProfilePictureFull = (NetworkImageView) findViewById(R.id.profile_picture_full);
		mButtonProfileCup = (ImageView) findViewById(R.id.button_profile_cup);
		mButtonProfileNumbers = (TextView) findViewById(R.id.button_profile_numbers);
		mButtonProfilePersons = (ImageView) findViewById(R.id.button_profile_persons);
		mButtonProfileSettingsOrPm = (ImageView) findViewById(R.id.button_profile_settings);
		if (!mIsMyProfile) {
			mButtonProfileSettingsOrPm.setImageDrawable(getResources().getDrawable(R.drawable.profile_pm));
		}
		mUserStoryGridView = (GridView) findViewById(R.id.gridview);

		// Crop image views
		mCropPictureLayout = (RelativeLayout) findViewById(R.id.profile_crop_picture);
	}

	private void initGridAdapter() {
		mProfileStoriesAdapter = new ProfileStoryAdapter(this);
		mUserStoryGridView.setAdapter(mProfileStoriesAdapter);
	}

	private void initViewListeners() {
		// Gesture detection
		initGestureDetectionListeners();
		mProfileLayout.setOnTouchListener(mGestureListener);

		// Profile listeners
		mButtonBack.setOnClickListener(this);
		mProfileName.setOnClickListener(this);
		if (mIsMyProfile) {
			mProfilePictureFull.setOnClickListener(this);
		}
		mButtonProfileCup.setOnClickListener(this);
		mButtonProfileNumbers.setOnClickListener(this);
		mButtonProfilePersons.setOnClickListener(this);
		mButtonProfileSettingsOrPm.setOnClickListener(this);
	}

	private void initGestureDetectionListeners() {
		mGestureDetector = new GestureDetector(this, new ActivityGestureDetector() {
			@Override
			public void onSwipeLeft() {
				finishWithAnimation();
			}
		});
		mGestureListener = new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				return mGestureDetector.onTouchEvent(event);
			}
		};
	}

	@Override
	protected void onResume() {
		super.onResume();
		registerReceivers();
		mUserDataController.populateProfile();
		mProfilePictureFull.setImageUrl(GlobalConstant.PROFILE_URL + mUserId, mImageLoader);
		mProfileStoriesController.populateProfileStories();
	}

	private void registerReceivers() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(GlobalConstant.PROFILE_INFO_LOADED_ACTION);
		filter.addAction(GlobalConstant.BLOB_CREATED_ACTION);
		filter.addAction(GlobalConstant.STORIES_LOADED_ACTION);
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
		if (requestCode == Crop.REQUEST_PICK && resultCode == RESULT_OK) {
			mProfilePictureController.beginCrop(data.getData());
		} else if (requestCode == Crop.REQUEST_CROP) {
			mProfilePictureController.handleCrop(resultCode, data);
		}
	}

	@Override
	public void onBackPressed() {
		finishWithAnimation();
	}

	private void finishWithAnimation() {
		finish();
		this.overridePendingTransition(R.anim.fade_in, R.anim.slide_out_left);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.button_back_from_profile:
			finishWithAnimation();
			break;
		case R.id.button_profile_settings:
			if (mIsMyProfile) {
				new AlertDialog.Builder(this).setTitle("Logout").setMessage("Are you sure you want to logout?")
						.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								mNoozService.logout();
							}
						}).setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								// do nothing
							}
						}).setIcon(android.R.drawable.ic_dialog_alert).show();
			} else {

			}
			break;
		case R.id.profile_picture_full:
			mProfilePictureController.selectImage();
			break;
		}
	}

}
