package com.nooz.nooz.activity.map;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.android.volley.toolbox.NetworkImageView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.nooz.nooz.R;
import com.nooz.nooz.activity.BaseLocationFragmentActivity;
import com.nooz.nooz.activity.LoginActivity;
import com.nooz.nooz.model.Story;
import com.nooz.nooz.util.CategoryResourceHelper;
import com.nooz.nooz.util.GlobalConstant;
import com.nooz.nooz.util.Tools;
import com.nooz.nooz.widget.PagerContainer;

/**
 * 
 * @author Rob Stein
 * 
 */
public class MapActivity extends BaseLocationFragmentActivity implements OnMapClickListener, OnCameraChangeListener {

	// Constants
	private static final String TAG = "MapActivity";
	private static final float FOOTER_WEIGHT = 0.29f;
	static final boolean HIGHLIGHT = true;
	static final boolean SHADE = false;
	private static final LatLng USA = new LatLng(37.09024, -95.712891);
	private static final int ZOOM_USA = 3;

	// Main map views
	RelativeLayout mMapContainer;
	GoogleMap mMap;
	TextView mRegion;
	LinearLayout mMiddlebar;
	TextView mButtonRelevant;
	ImageView mButtonSettingsAndFilters;
	TextView mButtonBreaking;
	RelativeLayout mStoryFooter;
	private PagerContainer mContainer;
	PagerAdapter mFooterAdapter;
	ViewPager mPager;
	private ImageView mButtonRefresh;
	private ImageView mButtonNewStory;

	// Settings menu views
	RelativeLayout mMenuSettings;
	private NetworkImageView mIconProfile;
	private TextView mButtonProfile;
	private TextView mButtonMapFilters;
	private TextView mButtonTopNooz;

	// Filter menu views
	LinearLayout mLayoutFilters;
	private ImageView mButtonMapFiltersBack;
	private ImageView mTogglerFilterAudio;
	private ImageView mTogglerFilterPicture;
	private ImageView mTogglerFilterVideo;
	private ImageView mTogglerFilterPeople;
	private ImageView mTogglerFilterCommunity;
	private ImageView mTogglerFilterSports;
	private ImageView mTogglerFilterFood;
	private ImageView mTogglerFilterPublicSafety;
	private ImageView mTogglerFilterArtsAndLife;

	// Story Lists
	List<Story> mStories;
	List<Circle> mCircles;
	List<GroundOverlay> mGroundOverlays;
	Integer mCurrentStory;
	Integer mResumeStory;

	/* Other fields */

	/**
	 * We hold onto the user's Id because when the user access the profile page,
	 * we pass it along to that activity.
	 */
	String mUserId;

	/**
	 * Used to store zoom level on camera updates. Is compared with current zoom
	 * level to determine if we should resize bubbles. Initialized to a
	 * non-valid zoom value.
	 */
	private float mPreviousZoomLevel;

	/**
	 * Screen width in pixels measured in onCreate via
	 * getWindowManager().getDefaultDisplay().getSize(Point). Used to compute
	 * the footer layout parameters to make the pager the correct height across
	 * various devices. Also used to compute the map width in meters.
	 */
	private int mScreenHeightInPixels;

	/**
	 * Screen width in pixels measured in onCreate via
	 * getWindowManager().getDefaultDisplay().getSize(Point). Used to compute
	 * the footer layout parameters to make the pager the correct height across
	 * various devices. Also used to compute the map width in meters.
	 */
	private int mScreenWidthInPixels;

	/**
	 * Updated on zoom changes. Is used to determine size of bubbles.
	 */
	private double mMapWidthInMeters;

	/**
	 * A FilterSettings instance representing the user's current search
	 * settings.
	 */
	FilterSettings mFilterSettings;

	/* Helper Objects */
	/***
	 * BroadcastReceiver handles blobs for stories when loaded
	 */
	MapBroadcastReceiver mReceiver;

	/**
	 * Initialize OnClickListener for this MapActivity
	 */
	MapActivityOnClickListener mActivityOnClickListener;

	/**
	 * Initialize OnClickListener for this MapActivity
	 */
	OnClickListener mFilterSettingsToggler;

	/**
	 * StoryAdapter onSwiper
	 */
	OnPageChangeListener mOnStorySwipe;

	/**
	 * Controls getting story data, clearing it, and opening new stories.
	 */
	StoryDataController mStoryDataController;

	/**
	 * Controls displaying menus. This variable cannot be initialized until
	 * MapActivity's onCreate because its constructor initializes Animations.
	 */
	MapMenusController mMenuController;

	/* ***** ACTIVITY LIFECYCLE BEGIN ***** */

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (handleLogoutIntent()) {
			return;
		}

		initFields();
		initViews();
		initViewListeners();
		initScreenMeasurements();
		initPager();
	}

	private void initFields() {
		initStoryLists();
		mCurrentStory = 0;
		mResumeStory = 0;
		mPreviousZoomLevel = -1.0f;
		mFilterSettings = new FilterSettings();
		mReceiver = new MapBroadcastReceiver();
		mActivityOnClickListener = new MapActivityOnClickListener(this);
		mFilterSettingsToggler = new FilterSettingsToggler(this);
		mOnStorySwipe = new StoryAdapterPageChangeListener(this);
		mStoryDataController = new StoryDataController(this);
		mMenuController = new MapMenusController(this);
	}

	/**
	 * Initialize stories, their circles, and their icons so that we don't
	 * segfault since we call populateStories in onConnected
	 */
	private void initStoryLists() {
		mStories = new ArrayList<Story>();
		mCircles = new ArrayList<Circle>();
		mGroundOverlays = new ArrayList<GroundOverlay>();
	}

	private void initViews() {
		setContentView(R.layout.activity_map);

		// Main map views
		mMapContainer = (RelativeLayout) findViewById(R.id.map_container);
		mRegion = (TextView) findViewById(R.id.region);
		mMiddlebar = (LinearLayout) findViewById(R.id.middlebar);
		mButtonRelevant = (TextView) findViewById(R.id.button_relevant);
		mButtonSettingsAndFilters = (ImageView) findViewById(R.id.button_settings);
		mButtonBreaking = (TextView) findViewById(R.id.button_breaking);
		mStoryFooter = (RelativeLayout) findViewById(R.id.story_footer);
		mContainer = (PagerContainer) findViewById(R.id.pager_container);
		mFooterAdapter = new StoryAdapter(this);
		mPager = mContainer.getViewPager();
		mButtonRefresh = (ImageView) findViewById(R.id.button_refresh);
		mButtonNewStory = (ImageView) findViewById(R.id.button_new_story);

		// Settings menu views
		mMenuSettings = (RelativeLayout) findViewById(R.id.menu_settings);
		mIconProfile = (NetworkImageView) findViewById(R.id.icon_profile);
		mButtonProfile = (TextView) findViewById(R.id.button_profile);
		mButtonMapFilters = (TextView) findViewById(R.id.button_map_filters);

		// Filter menu views
		mLayoutFilters = (LinearLayout) findViewById(R.id.filters_layout);
		mButtonMapFiltersBack = (ImageView) findViewById(R.id.button_back_from_filter);
		mTogglerFilterAudio = (ImageView) findViewById(R.id.button_filter_mic);
		mTogglerFilterPicture = (ImageView) findViewById(R.id.button_filter_camera);
		mTogglerFilterVideo = (ImageView) findViewById(R.id.button_filter_video);
		mTogglerFilterPeople = (ImageView) findViewById(R.id.button_filter_people);
		mTogglerFilterCommunity = (ImageView) findViewById(R.id.button_filter_community);
		mTogglerFilterSports = (ImageView) findViewById(R.id.button_filter_sports);
		mTogglerFilterFood = (ImageView) findViewById(R.id.button_filter_food);
		mTogglerFilterPublicSafety = (ImageView) findViewById(R.id.button_filter_public_safety);
		mTogglerFilterArtsAndLife = (ImageView) findViewById(R.id.button_filter_arts_and_life);
	}

	private void initViewListeners() {
		// Main map view listeners
		mRegion.setOnEditorActionListener(mRegionEditorDoneListener);
		mRegion.setOnFocusChangeListener(mRegionFocusDoneListener);
		mButtonRelevant.setOnClickListener(mActivityOnClickListener);
		mButtonSettingsAndFilters.setOnClickListener(mActivityOnClickListener);
		mButtonBreaking.setOnClickListener(mActivityOnClickListener);
		mButtonRefresh.setOnClickListener(mActivityOnClickListener);
		mButtonNewStory.setOnClickListener(mActivityOnClickListener);

		// Settings menu view listeners
		mButtonProfile.setOnClickListener(mActivityOnClickListener);
		mButtonMapFilters.setOnClickListener(mActivityOnClickListener);

		// Filter menu view listeners
		mButtonMapFiltersBack.setOnClickListener(mActivityOnClickListener);
		mTogglerFilterAudio.setOnClickListener(mFilterSettingsToggler);
		mTogglerFilterPicture.setOnClickListener(mFilterSettingsToggler);
		mTogglerFilterVideo.setOnClickListener(mFilterSettingsToggler);
		mTogglerFilterPeople.setOnClickListener(mFilterSettingsToggler);
		mTogglerFilterCommunity.setOnClickListener(mFilterSettingsToggler);
		mTogglerFilterSports.setOnClickListener(mFilterSettingsToggler);
		mTogglerFilterFood.setOnClickListener(mFilterSettingsToggler);
		mTogglerFilterPublicSafety.setOnClickListener(mFilterSettingsToggler);
		mTogglerFilterArtsAndLife.setOnClickListener(mFilterSettingsToggler);
	}

	private void initScreenMeasurements() {
		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		mScreenWidthInPixels = size.x;
		mScreenHeightInPixels = size.y;
	}

	private void initPager() {
		initPagerSizeParameters();
		mPager.setAdapter(mFooterAdapter);
		mPager.setOffscreenPageLimit(mFooterAdapter.getCount());
		mPager.setPageMargin((int) Tools.dipToPixels(this, 4));
		mPager.setClipChildren(false);
		mPager.setOnPageChangeListener(mOnStorySwipe);
	}

	/**
	 * Sets up footer page margins for cross-device prettiness. The footer
	 * should occupy the same percentage of the screen across all devices, since
	 * screen height and width various from device to device. However, the pager
	 * needs to know how wide to make its pages to make them square.
	 */
	private void initPagerSizeParameters() {
		int footer_height = (int) (mScreenHeightInPixels * FOOTER_WEIGHT);

		// The following sets a story's margin equal to the screenWidth - story.
		// We divide by two because there are two margins (left and right).
		// We additionally account for the margins between the stories.
		FrameLayout.LayoutParams footerLayoutParams = (FrameLayout.LayoutParams) mPager.getLayoutParams();
		footerLayoutParams.setMargins(
				(int) ((mScreenWidthInPixels - footer_height) / 2) + (int) Tools.dipToPixels(this, 4), 0,
				(int) ((mScreenWidthInPixels - footer_height) / 2) + (int) Tools.dipToPixels(this, 4), 0);
		mPager.setLayoutParams(footerLayoutParams);
	}

	/**
	 * On logout, the profile activity sends a intent with a boolean extra named
	 * "finish" to the MapActivity. From the MapActivity (the oldest parent
	 * activity), we can call LoginActiviy again and call finish().
	 * 
	 * @return True if the user is logging out.
	 */
	private boolean handleLogoutIntent() {
		boolean finish = getIntent().getBooleanExtra("finish", false);
		if (finish) {
			Intent logoutIntent = new Intent(mContext, LoginActivity.class);
			mContext.startActivity(logoutIntent);
			startActivity(logoutIntent);
			SharedPreferences settings = getSharedPreferences("map_settings", MODE_PRIVATE);
			SharedPreferences.Editor editor = settings.edit();
			editor.putFloat("latitude", (float) USA.latitude);
			editor.putFloat("longitude", (float) USA.longitude);
			editor.putFloat("zoom", (float) ZOOM_USA);
			editor.putInt("current_story", 0);
			editor.commit();
			finish();
		}
		return finish;
	}

	/*
	 * Called when the Activity becomes visible.
	 */
	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onResume() {
		super.onResume();

		registerReceivers();
		setUpMapIfNeeded();
		getUserData();
		restoreSettings();
		mStoryDataController.clearAndPopulateStories();
		mIconProfile.setImageUrl(GlobalConstant.PROFILE_URL + mUserId, mImageLoader);
	}

	private void getUserData() {
		SharedPreferences userData = mContext.getSharedPreferences("UserData", Context.MODE_PRIVATE);
		mButtonProfile.setText(userData.getString("user_name", ""));
		mUserId = userData.getString("userid", null);
	}

	private void restoreSettings() {
		SharedPreferences settings = getSharedPreferences("map_settings", MODE_PRIVATE);
		double latitude;
		double longitude;
		float zoom;
		if (mCurrentLocation == null) {
			// Move Gmaps camera to USA
			latitude = settings.getFloat("latitude", (float) USA.latitude);
			longitude = settings.getFloat("longitude", (float) USA.longitude);
			zoom = settings.getFloat("zoom", (float) ZOOM_USA);
		} else {
			// Move Gmaps camera to current location
			latitude = settings.getFloat("latitude", (float) mCurrentLocation.getLatitude());
			longitude = settings.getFloat("longitude", (float) mCurrentLocation.getLongitude());
			zoom = settings.getFloat("zoom", (float) 12.0f);
		}
		mResumeStory = settings.getInt("current_story", 0);
		mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), zoom));
	}

	private void registerReceivers() {
		IntentFilter filter = new IntentFilter();
		filter.addAction("stories.loaded");
		filter.addAction("storyImage.loaded");
		registerReceiver(mReceiver, filter);
	}

	/*
	 * Called when the Activity is partially visible.
	 */
	@Override
	protected void onPause() {
		unRegisterReceivers();
		saveSettings();

		super.onPause();
	}

	private void saveSettings() {
		CameraPosition camPosition = mMap.getCameraPosition();
		double longitude = camPosition.target.longitude;
		double latitude = camPosition.target.latitude;
		double zoom = camPosition.zoom;
		SharedPreferences settings = getSharedPreferences("map_settings", MODE_PRIVATE);
		SharedPreferences.Editor editor = settings.edit();
		editor.putFloat("latitude", (float) latitude);
		editor.putFloat("longitude", (float) longitude);
		editor.putFloat("zoom", (float) zoom);
		editor.putInt("current_story", mCurrentStory);
		editor.commit();
	}

	private void unRegisterReceivers() {
		unregisterReceiver(mReceiver);
	}

	/*
	 * Called when the Activity is no longer visible.
	 */
	@Override
	protected void onStop() {
		super.onStop();
	}

	/* ***** ACTIVITY LIFECYCLE END ***** */

	/* ***** MAP SETUP BEGIN ***** */

	private void setUpMapIfNeeded() {
		// Do a null check to confirm that we have not already instantiated the
		// map.
		if (mMap == null) {
			mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
			// Check if we were successful in obtaining the map.
			if (mMap != null) {
				// The Map is verified. It is now safe to manipulate the map.
				setUpMap();
			}
		} else {
			setUpMap();
		}
	}

	private void setUpMap() {
		mMap.setMyLocationEnabled(true);
		mMap.setOnCameraChangeListener(this);
		mMap.setOnMapClickListener(this);
	}

	/* ***** MAP SETUP END ***** */

	/* ***** BUBBLES BEGIN ***** */

	void drawCirlesOnMap() {
		int i = 0;
		for (Story s : mStories) {
			// double newRadius = BubbleSizer.getBubbleSize(i, mStories.size(),
			// mMapWidthInMeters);
			// Force bubble size at zoom 13
			double newRadius = BubbleSizer.getBubbleSize(i, mStories.size(),
					GlobeTrigonometry.mapWidthInMeters(mScreenWidthInPixels, 13));
			s.setRadius(newRadius);
			mStories.get(i).setRadius(newRadius);
			drawBubble(s.lat, s.lng, s.radius, s.category);
			i++;
		}
		moveBubblesToPreventOverlap();
	}

	private void moveBubblesToPreventOverlap() {
		// TODO Auto-generated method stub
		
	}

	private void drawBubble(double lat, double lng, double radius, String category) {

		CircleOptions circleOptions;
		circleOptions = new CircleOptions().center(new LatLng(lat, lng)).radius(radius);
		final Circle c = mMap.addCircle(circleOptions);
		mCircles.add(c);

		GroundOverlayOptions groundOverlayOptions;
		if (mCircles.indexOf(c) == mResumeStory) {
			c.setFillColor(CategoryResourceHelper.getColorByCategory(category, HIGHLIGHT));
			c.setStrokeColor(CategoryResourceHelper.getStrokeColorByCategory(category, HIGHLIGHT));
			groundOverlayOptions = new GroundOverlayOptions()
					.image(BitmapDescriptorFactory.fromResource(CategoryResourceHelper
							.getActiveGroundOverlayByCategory(category))).anchor(0.5f, 0.5f)
					.position(new LatLng(lat, lng), (int) (radius * 3 / 4), (int) (radius * 3 / 4));
		} else {
			c.setFillColor(CategoryResourceHelper.getColorByCategory(category, SHADE));
			c.setStrokeColor(CategoryResourceHelper.getStrokeColorByCategory(category, SHADE));
			groundOverlayOptions = new GroundOverlayOptions()
					.image(BitmapDescriptorFactory.fromResource(CategoryResourceHelper
							.getGroundOverlayByCategory(category))).anchor(0.5f, 0.5f)
					.position(new LatLng(lat, lng), (int) (radius * 3 / 4), (int) (radius * 3 / 4));
		}
		GroundOverlay icon = mMap.addGroundOverlay(groundOverlayOptions);
		mGroundOverlays.add(icon);
	}

	/*
	 * private void updateBubbleSizes() { int i = 0; for (Story s : mStories) {
	 * double newRadius = BubbleSizer.getBubbleSize(i, mStories.size(),
	 * mMapWidthInMeters);
	 * 
	 * mStories.get(i).setRadius(newRadius);
	 * mCircles.get(i).setRadius(newRadius);
	 * mGroundOverlays.get(i).setDimensions((int) (newRadius * 3 / 4), (int)
	 * (newRadius * 3 / 4)); i++; } }
	 */

	/* ***** BUBBLES END ***** */

	/* ***** LISTENERS BEGIN ***** */

	/**
	 * When back is pressed and we are in a menu such as the settings menu or
	 * the filters menu, intuitively the user expects to be backed out of those
	 * menus. So that is what we do.
	 */
	@Override
	public void onBackPressed() {
		if (mMenuController.filtersMenuIsOpen) {
			mMenuController.hideFiltersLayout();
		} else if (mMenuController.settingsMenuIsOpen) {
			mMenuController.hideOrShowSettingsMenu();
		} else {
			super.onBackPressed();
		}
	}

	/*
	 * When we click on a bubble, open that story.
	 */
	@Override
	public void onMapClick(LatLng point) {
		for (Story s : mStories) {
			if (GlobeTrigonometry.distBetween(s.lat, s.lng, point.latitude, point.longitude) < s.radius) {
				mStoryDataController.openStory(s);
			}
		}
	}

	@Override
	public void onCameraChange(CameraPosition position) {
		Log.d("Zoom", "Zoom: " + position.zoom);

		// Update Bubbles
		if (mPreviousZoomLevel != position.zoom) {
			mMapWidthInMeters = GlobeTrigonometry.mapWidthInMeters(mScreenWidthInPixels, position.zoom);
			// updateBubbleSizes();
		}
		mPreviousZoomLevel = position.zoom;

		// Update City
		// updateCity();

		mStoryDataController.clearAndPopulateStories();
	}

	/* ***** LISTENERS END ***** */

	/* ***** MAP SEARCH BEGIN***** */

	private void updateCity() {
		LatLngBounds bounds = mMap.getProjection().getVisibleRegion().latLngBounds;
		Geocoder gcd = new Geocoder(mContext, Locale.getDefault());
		List<Address> addressesNorthEast;
		List<Address> addressesSouthWest;
		try {
			addressesNorthEast = gcd.getFromLocation(bounds.northeast.latitude, bounds.northeast.longitude, 1);
			addressesSouthWest = gcd.getFromLocation(bounds.southwest.latitude, bounds.southwest.longitude, 1);
			if ((addressesNorthEast.size() > 0) && (addressesSouthWest.size() > 0)) {
				String localityNorthEast = addressesNorthEast.get(0).getLocality();
				String localitySouthWest = addressesSouthWest.get(0).getLocality();
				if ((localityNorthEast != null) && (localitySouthWest != null)) {
					if (localityNorthEast.equals(localitySouthWest)) {
						// Change text
						mRegion.setText(localityNorthEast.toUpperCase(Locale.ENGLISH));
						// Don't make the search box focused
						mRegion.setSelected(false);
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private OnEditorActionListener mRegionEditorDoneListener = new TextView.OnEditorActionListener() {
		@Override
		public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
			if (actionId == EditorInfo.IME_ACTION_DONE) {
				searchMap();
				// hide virtual keyboard
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(mRegion.getWindowToken(), 0);
				return true;
			}
			return false;
		}
	};

	private OnFocusChangeListener mRegionFocusDoneListener = new OnFocusChangeListener() {

		public void onFocusChange(View v, boolean hasFocus) {
			if (!hasFocus)
				searchMap();
		}
	};

	private void searchMap() {
		Geocoder gcd = new Geocoder(mContext, Locale.getDefault());
		try {
			List<Address> l = gcd.getFromLocationName(mRegion.getText().toString(), 1);
			if (l.size() > 0) {
				mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(l.get(0).getLatitude(), l.get(0)
						.getLongitude()), 10));

			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/* ***** MAP SEARCH END ***** */
}
