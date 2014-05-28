package com.nooz.nooz.activity;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityOptions;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.internal.ex;
import com.google.android.gms.location.LocationClient;
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
import com.google.gson.JsonObject;
import com.nooz.nooz.R;
import com.nooz.nooz.model.FilterSettings;
import com.nooz.nooz.model.Story;
import com.nooz.nooz.util.Alert;
import com.nooz.nooz.util.BubbleSizer;
import com.nooz.nooz.util.GlobeTrigonometry;
import com.nooz.nooz.util.SearchType;
import com.nooz.nooz.util.Tools;
import com.nooz.nooz.widget.PagerContainer;

public class MapActivity extends BaseFragmentActivity implements OnClickListener, OnMapClickListener,
		OnCameraChangeListener, GooglePlayServicesClient.ConnectionCallbacks,
		GooglePlayServicesClient.OnConnectionFailedListener {

	// Constants
	private static final String TAG = "MapActivity";
	private static final float FOOTER_WEIGHT = 0.29f;
	private static final boolean HIGHLIGHT = true;
	private static final boolean SHADE = false;
	private static final LatLng USA = new LatLng(37.09024, -95.712891);
	private static final int ZOOM_USA = 3;
	private static final int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
	private static final int REQUEST_CODE_RECOVER_PLAY_SERVICES = 1001;
	private static final int TOP_LEFT = 0;
	private static final int TOP_RIGHT = 1;
	private static final int BOTTOM_LEFT = 2;
	private static final int BOTTOM_RIGHT = 3;
	private static final String CONTAINER_NAME = "media";

	// Animations
	private Animation mSlideInBottom;
	private Animation mSlideOutBottom;
	private Animation mSlideInLeft;
	private Animation mSlideOutLeft;
	private Animation mFadeIn;
	private Animation mFadeOut;

	// Colors
	private static final int SEARCH_TYPE_ACTIVE_COLOR = 0xFF000000;
	private static final int SEARCH_TYPE_FADED_COLOR = 0xFF979797;
	private static int COLOR_PEOPLE;
	private static int COLOR_COMMUNITY;
	private static int COLOR_SPORTS;
	private static int COLOR_FOOD;
	private static int COLOR_PUBLIC_SAFETY;
	private static int COLOR_ARTS_AND_LIFE;
	private static final int COLOR_PEOPLE_STROKE = 0xFF8DCFFF;
	private static final int COLOR_COMMUNITY_STROKE = 0xFF6B9EF1;
	private static final int COLOR_SPORTS_STROKE = 0xFFBE9ABD;
	private static final int COLOR_FOOD_STROKE = 0xFF83D193;
	private static final int COLOR_PUBLIC_SAFETY_STROKE = 0xFFEEAF7A;
	private static final int COLOR_ARTS_AND_LIFE_STROKE = 0xFFAE7DCE;

	// Main map views
	private RelativeLayout mMapContainer;
	private GoogleMap mMap;
	private TextView mRegion;
	private LinearLayout mMiddlebar;
	private TextView mButtonRelevant;
	private ImageView mButtonSettingsAndFilters;
	private TextView mButtonBreaking;
	private RelativeLayout mStoryFooter;
	private PagerContainer mContainer;
	private ViewPager mPager;
	private ImageView mButtonRefresh;
	private ImageView mButtonNewStory;

	// Settings menu views
	private RelativeLayout mMenuSettings;
	private ImageView mIconProfile;
	private TextView mButtonProfile;
	private TextView mButtonMapFilters;
	private TextView mButtonTopNooz;

	// Filter menu views
	private LinearLayout mLayoutFilters;
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
	private List<Story> mStories;
	private List<Circle> mCircles;
	private List<GroundOverlay> mGroundOverlays;
	private Integer mCurrentStory = 0;

	//
	private LocationClient mLocationClient;
	private Location mCurrentLocation;
	private float mPreviousZoomLevel = -1.0f; // Init to a non-valid zoom value
	private boolean mIsZooming = false;
	protected boolean circlesAreOnMap;
	private SearchType mCurrentSearchType = SearchType.RELEVANT;
	private Boolean settingsMenuIsOpen = false;
	private Boolean filtersMenuIsOpen = false;
	private int mScreenWidthInPixels;
	private double mMapWidthInMeters;
	private int mResumeStory = 0;
	private FilterSettings mFilterSettings;

	/* ***** APP SETUP BEGIN ***** */

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);

		// Initialize stories, their circles, and their icons so that we don't
		// segfault since we call populateStories in onConnected
		mStories = new ArrayList<Story>();
		mCircles = new ArrayList<Circle>();
		mGroundOverlays = new ArrayList<GroundOverlay>();

		mLocationClient = new LocationClient(this, this, this);

		// Main map views
		mMapContainer = (RelativeLayout) findViewById(R.id.map_container);
		//
		mRegion = (TextView) findViewById(R.id.region);
		mRegion.setOnEditorActionListener(mRegionEditorDoneListener);
		mRegion.setOnFocusChangeListener(mRegionFocusDoneListener);
		//
		mMiddlebar = (LinearLayout) findViewById(R.id.middlebar);
		//
		mButtonRelevant = (TextView) findViewById(R.id.button_relevant);
		mButtonRelevant.setOnClickListener(this);
		//
		mButtonSettingsAndFilters = (ImageView) findViewById(R.id.button_settings);
		mButtonSettingsAndFilters.setOnClickListener(this);
		//
		mButtonBreaking = (TextView) findViewById(R.id.button_breaking);
		mButtonBreaking.setOnClickListener(this);
		//
		mStoryFooter = (RelativeLayout) findViewById(R.id.story_footer);
		// Set up footer logic
		mContainer = (PagerContainer) findViewById(R.id.pager_container);
		mPager = mContainer.getViewPager();
		PagerAdapter adapter = new StoryAdapter(this);
		mPager.setAdapter(adapter);
		mPager.setOffscreenPageLimit(adapter.getCount());
		mPager.setPageMargin((int) Tools.dipToPixels(this, 4));
		mPager.setClipChildren(false);
		mPager.setOnPageChangeListener(onStorySwipe);
		// Set up footer page margins for multi device prettiness
		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		mScreenWidthInPixels = size.x;
		int height = size.y;
		int footer_height = (int) (height * FOOTER_WEIGHT);
		// The following sets a story's margin equal to the screenWidth - story.
		// We divide by two because there are two margins (left and right).
		// We additionally account for the margins between the stories.
		FrameLayout.LayoutParams footerLayoutParams = (FrameLayout.LayoutParams) mPager.getLayoutParams();
		footerLayoutParams.setMargins(
				(int) ((mScreenWidthInPixels - footer_height) / 2) + (int) Tools.dipToPixels(this, 4), 0,
				(int) ((mScreenWidthInPixels - footer_height) / 2) + (int) Tools.dipToPixels(this, 4), 0);
		mPager.setLayoutParams(footerLayoutParams);
		//
		mButtonRefresh = (ImageView) findViewById(R.id.button_refresh);
		mButtonRefresh.setOnClickListener(this);
		//
		mButtonNewStory = (ImageView) findViewById(R.id.button_new_story);
		mButtonNewStory.setOnClickListener(this);

		// Settings menu views
		mMenuSettings = (RelativeLayout) findViewById(R.id.menu_settings);
		//
		mButtonProfile = (TextView) findViewById(R.id.button_profile);
		mButtonProfile.setOnClickListener(this);
		//
		mButtonMapFilters = (TextView) findViewById(R.id.button_map_filters);
		mButtonMapFilters.setOnClickListener(this);

		// Filter menu views
		mLayoutFilters = (LinearLayout) findViewById(R.id.filters_layout);
		//
		mButtonMapFiltersBack = (ImageView) findViewById(R.id.button_back_from_filter);
		mButtonMapFiltersBack.setOnClickListener(this);
		//
		mTogglerFilterAudio = (ImageView) findViewById(R.id.button_filter_mic);
		mTogglerFilterAudio.setOnClickListener(filterSettingsToggler);
		mTogglerFilterPicture = (ImageView) findViewById(R.id.button_filter_camera);
		mTogglerFilterPicture.setOnClickListener(filterSettingsToggler);
		mTogglerFilterVideo = (ImageView) findViewById(R.id.button_filter_video);
		mTogglerFilterVideo.setOnClickListener(filterSettingsToggler);
		mTogglerFilterPeople = (ImageView) findViewById(R.id.button_filter_people);
		mTogglerFilterPeople.setOnClickListener(filterSettingsToggler);
		mTogglerFilterCommunity = (ImageView) findViewById(R.id.button_filter_community);
		mTogglerFilterCommunity.setOnClickListener(filterSettingsToggler);
		mTogglerFilterSports = (ImageView) findViewById(R.id.button_filter_sports);
		mTogglerFilterSports.setOnClickListener(filterSettingsToggler);
		mTogglerFilterFood = (ImageView) findViewById(R.id.button_filter_food);
		mTogglerFilterFood.setOnClickListener(filterSettingsToggler);
		mTogglerFilterPublicSafety = (ImageView) findViewById(R.id.button_filter_public_safety);
		mTogglerFilterPublicSafety.setOnClickListener(filterSettingsToggler);
		mTogglerFilterArtsAndLife = (ImageView) findViewById(R.id.button_filter_arts_and_life);
		mTogglerFilterArtsAndLife.setOnClickListener(filterSettingsToggler);
		// Create filter settings
		mFilterSettings = new FilterSettings();

		// Colors
		COLOR_PEOPLE = getResources().getColor(R.color.category_people);
		COLOR_COMMUNITY = getResources().getColor(R.color.category_community);
		COLOR_SPORTS = getResources().getColor(R.color.category_sports);
		COLOR_FOOD = getResources().getColor(R.color.category_food);
		COLOR_PUBLIC_SAFETY = getResources().getColor(R.color.category_public_safety);
		COLOR_ARTS_AND_LIFE = getResources().getColor(R.color.category_arts_and_life);
		// Animations
		mSlideInBottom = AnimationUtils.loadAnimation(this, R.anim.slide_in_bottom);
		mSlideOutBottom = AnimationUtils.loadAnimation(this, R.anim.slide_out_bottom);
		mSlideInLeft = AnimationUtils.loadAnimation(this, R.anim.slide_in_left);
		mSlideOutLeft = AnimationUtils.loadAnimation(this, R.anim.slide_out_left);
		mFadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
		mFadeOut = AnimationUtils.loadAnimation(this, R.anim.fade_out);
	}

	/*
	 * Called when the Activity becomes visible.
	 */
	@Override
	protected void onStart() {
		super.onStart();
		// Connect the client.
		if (servicesConnected()) {
			mLocationClient.connect();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		IntentFilter filter = new IntentFilter();
		filter.addAction("stories.loaded");
		filter.addAction("storyImage.loaded");
		registerReceiver(receiver, filter);

		setUpMapIfNeeded();

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

	/*
	 * Called when the Activity is partially visible.
	 */
	@Override
	protected void onPause() {
		unregisterReceiver(receiver);

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

		mStories.clear();
		mMap.clear();

		super.onPause();
	}

	/*
	 * Called when the Activity is no longer visible.
	 */
	@Override
	protected void onStop() {
		// Disconnecting the client invalidates it.
		mLocationClient.disconnect();

		super.onStop();
	}

	/*
	 * Called by Location Services when the request to connect the client
	 * finishes successfully. At this point, you can request the current
	 * location or start periodic updates
	 */
	@Override
	public void onConnected(Bundle connectionHint) {
		// Display the connection status
		Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show();
		mCurrentLocation = mLocationClient.getLastLocation();
	}

	private void setUpMapIfNeeded() {
		// Do a null check to confirm that we have not already instantiated the
		// map.
		if (mMap == null) {
			mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
			// Check if we were successful in obtaining the map.
			if (mMap != null) {
				// The Map is verified. It is now safe to manipulate the map.
				setUpMapAndPopulateInitialStories();
			}
		} else {
			setUpMapAndPopulateInitialStories();
		}
	}

	private void setUpMapAndPopulateInitialStories() {
		setUpMap();
		populateInitialStories();
	}

	private void setUpMap() {
		mMap.setMyLocationEnabled(true);
		mMap.setOnCameraChangeListener(this);
		mMap.setOnMapClickListener(this);
	}

	/***
	 * Broadcast receiver handles blobs for stories when loaded
	 */
	private BroadcastReceiver receiver = new BroadcastReceiver() {
		public void onReceive(Context context, android.content.Intent intent) {
			String intentAction = intent.getAction();
			if (intentAction.equals("stories.loaded")) {
				try {
					getStoriesCallBack();
				} catch (Exception e) {
					Log.e(TAG, e.getMessage());
				}
			}
			if (intentAction.equals("storyImage.loaded")) {
				try {
					getStoryImageCallBack(intent.getIntExtra("index", -1));
				} catch (Exception e) {
					Log.e(TAG, e.getMessage());
				}
			}
		}
	};

	/* ***** APP SETUP END ***** */

	/* ***** LISTENERS BEGIN ***** */

	@SuppressLint("NewApi")
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.region:
			mRegion.setText("");
		case R.id.button_relevant:
			switchSearchTypes(R.id.button_relevant);
			break;
		case R.id.button_breaking:
			switchSearchTypes(R.id.button_breaking);
			break;
		case R.id.button_settings:
			hideOrShowSettingsMenu();
			break;
		case R.id.button_refresh:
			populateInitialStories();
			break;
		case R.id.button_new_story:
			if (mCurrentLocation == null) {
				Alert.createAndShowDialog("Please turn on Locations Services", "Location not found", mContext);
			} else {
				Intent mediaRecorderIntent = new Intent(getApplicationContext(), MediaRecorderActivity.class);
				startActivity(mediaRecorderIntent);
			}
			break;
		case R.id.story_item_layout:
			Bundle args = new Bundle();
			args.putParcelable("story", mStories.get(mCurrentStory));
			Intent readStoryIntent = new Intent(getApplicationContext(), ArticleActivity.class);
			readStoryIntent.putExtra("bundle", args);
			startActivity(readStoryIntent);
			break;
		case R.id.button_map_filters:
			showFiltersLayout();
			break;
		case R.id.button_back_from_filter:
			hideFiltersLayout();
			break;
		case R.id.button_profile:
			if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
				Intent profileIntent = new Intent(getApplicationContext(), ProfileActivity.class);
				Bundle bndlanimation = ActivityOptions.makeCustomAnimation(getApplicationContext(),
						R.anim.slide_in_left, R.anim.fade_out).toBundle();
				startActivity(profileIntent, bndlanimation);
			} else {
				Intent profileIntent = new Intent(getApplicationContext(), ProfileActivity.class);
				startActivity(profileIntent);
			}
		}
	}

	@Override
	public void onBackPressed() {
		if (filtersMenuIsOpen) {
			hideFiltersLayout();
		} else if (settingsMenuIsOpen) {
			hideOrShowSettingsMenu();
		} else {
			super.onBackPressed();
		}
	}

	@Override
	public void onMapClick(LatLng point) {
		for (Story s : mStories) {
			if (GlobeTrigonometry.distBetween(s.lat, s.lng, point.latitude, point.longitude) < s.radius) {
				Bundle args = new Bundle();
				args.putParcelable("story", s);
				Intent readStoryIntent = new Intent(getApplicationContext(), ArticleActivity.class);
				readStoryIntent.putExtra("bundle", args);
				startActivity(readStoryIntent);
			}
		}
	}

	@Override
	public void onCameraChange(CameraPosition position) {
		Log.d("Zoom", "Zoom: " + position.zoom);

		// Update Bubbles
		if (mPreviousZoomLevel != position.zoom) {
			mMapWidthInMeters = GlobeTrigonometry.mapWidthInMeters(mScreenWidthInPixels, position.zoom);
			updateBubbleSizes();
		}
		mPreviousZoomLevel = position.zoom;

		// Update City
		updateCity();

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

	/* ***** STORIES BEGIN ***** */

	private void populateInitialStories() {
		mStories.clear();
		mMap.clear();
		LatLngBounds bounds = mMap.getProjection().getVisibleRegion().latLngBounds;
		mNoozService.getAllStories(bounds, mFilterSettings);
	}

	private void getStoriesCallBack() {
		mStories = mNoozService.getLoadedStories();
		mNoozService.getBlobSases(CONTAINER_NAME, mStories);
		// Reset footer
		PagerAdapter adapter = new StoryAdapter(mContext);
		mPager.setAdapter(adapter);
		mPager.setOffscreenPageLimit(adapter.getCount());
		drawCirlesOnMap();
		mPager.setCurrentItem(mResumeStory);
	}

	private void getStoryImageCallBack(int i) {
		// Load the image using the SAS URL
		JsonObject blob = mNoozService.getLoadedStoryImage(i);
		String sasUrl = blob.getAsJsonPrimitive("sasUrl").toString();
		sasUrl = sasUrl.replace("\"", "");
		if ("PICTURE".equals(mStories.get(i).medium)) {
			(new ImageFetcherTask(sasUrl, i)).execute();
		}
		if ("VIDEO".equals(mStories.get(i).medium)) {

		}
		PagerAdapter adapter = new StoryAdapter(mContext);
		mPager.setAdapter(adapter);
		mPager.setOffscreenPageLimit(adapter.getCount());
		mPager.setCurrentItem(mResumeStory);
	}

	/**
	 * This class specifically handles fetching an image from a URL and setting
	 * the image view source on the screen
	 */
	private class ImageFetcherTask extends AsyncTask<Void, Void, Boolean> {
		private String mUrl;
		private Bitmap mBitmap;
		private Integer mIndex;

		public ImageFetcherTask(String url, int index) {
			mUrl = url;
			mIndex = index;
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			try {
				mBitmap = BitmapFactory.decodeStream((InputStream) new URL(mUrl).getContent());
			} catch (Exception e) {
				Log.e(TAG, e.getMessage());
				return false;
			}
			return true;
		}

		/***
		 * If the image was loaded successfully, set the image view
		 */
		@Override
		protected void onPostExecute(Boolean loaded) {
			if (loaded) {
				mStories.get(mIndex).setBitmap(mBitmap);
			}
		}
	}

	private void drawCirlesOnMap() {
		int i = 0;
		for (Story s : mStories) {
			double newRadius = BubbleSizer.getBubbleSize(i, mStories.size(), mMapWidthInMeters);
			s.setRadius(newRadius);
			mStories.get(i).setRadius(newRadius);
			drawBubble(s.lat, s.lng, s.radius, s.category);
			i++;
		}
		// circlesAreOnMap = true;
	}

	private void drawBubble(double lat, double lng, double radius, String category) {

		final double targetRadius = radius;
		final long duration = 400;
		final Handler handler = new Handler();
		final long start = SystemClock.uptimeMillis();
		final double startRadius = 0;
		final Interpolator interpolator = new LinearInterpolator();

		CircleOptions circleOptions;
		circleOptions = new CircleOptions().center(new LatLng(lat, lng)).radius(startRadius);
		// circleOptions = new CircleOptions().center(new LatLng(lat,
		// lng)).radius(targetRadius);
		final Circle c = mMap.addCircle(circleOptions);
		mCircles.add(c);

		GroundOverlayOptions groundOverlayOptions;
		if (mCircles.indexOf(c) == mResumeStory) {
			c.setFillColor(getColorByCategory(category, HIGHLIGHT));
			c.setStrokeColor(getStrokeColorByCategory(category, HIGHLIGHT));
			groundOverlayOptions = new GroundOverlayOptions()
					.image(BitmapDescriptorFactory.fromResource(getActiveGroundOverlayByCategory(category)))
					.anchor(0.5f, 0.5f).position(new LatLng(lat, lng), (int) (radius * 3 / 4), (int) (radius * 3 / 4));
		} else {
			c.setFillColor(getColorByCategory(category, SHADE));
			c.setStrokeColor(getStrokeColorByCategory(category, SHADE));
			groundOverlayOptions = new GroundOverlayOptions()
					.image(BitmapDescriptorFactory.fromResource(getGroundOverlayByCategory(category)))
					.anchor(0.5f, 0.5f).position(new LatLng(lat, lng), (int) (radius * 3 / 4), (int) (radius * 3 / 4));
		}
		GroundOverlay icon = mMap.addGroundOverlay(groundOverlayOptions);
		mGroundOverlays.add(icon);

		handler.post(new Runnable() {
			@Override
			public void run() {
				long elapsed = SystemClock.uptimeMillis() - start;
				float t = interpolator.getInterpolation((float) elapsed / duration);
				double r = Math.max(0, t * targetRadius + (1 - t) * startRadius);
				c.setRadius(r);
				if (t < 1.0) {
					// Post again 16ms later == 60 frames per second
					handler.postDelayed(this, 16);
				} else {
					// animation ended
				}
			}
		});

	}

	private void updateBubbleSizes() {
		int i = 0;
		for (Story s : mStories) {
			double newRadius = BubbleSizer.getBubbleSize(i, mStories.size(), mMapWidthInMeters);

			final double targetRadius = newRadius;
			final long duration = 400;
			final Handler handler = new Handler();
			final long start = SystemClock.uptimeMillis();
			final double startRadius = s.radius;
			final Interpolator interpolator = new LinearInterpolator();

			final Circle c = mCircles.get(i);

			handler.post(new Runnable() {
				@Override
				public void run() {
					long elapsed = SystemClock.uptimeMillis() - start;
					float t = interpolator.getInterpolation((float) elapsed / duration);
					double r = Math.max(0, t * targetRadius + (1 - t) * startRadius);
					c.setRadius(r);
					if (t < 1.0) {
						// Post again 16ms later == 60 frames per second
						handler.postDelayed(this, 16);
					} else {
						// animation ended
					}
				}
			});

			mStories.get(i).setRadius(newRadius);
			// mCircles.get(i).setRadius(newRadius);
			mGroundOverlays.get(i).setDimensions((int) (newRadius * 3 / 4), (int) (newRadius * 3 / 4));
			i++;
		}
	}

	/* ***** STORIES END ***** */

	/* ***** FOOTER ADAPTER AND ONPAGECHANGELISTENER BEGIN ***** */

	private class StoryAdapter extends PagerAdapter {

		private Context mContext;

		public StoryAdapter(Context context) {
			this.mContext = context;
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {

			LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View layout = inflater.inflate(R.layout.story_item, null);
			layout.setOnClickListener((OnClickListener) mContext);

			ImageView image = (ImageView) layout.findViewById(R.id.story_item_article_image);
			if ("PICTURE".equals(mStories.get(position).medium)) {
				image.setImageBitmap(mStories.get(position).bitmap);
			}

			ProgressBar loading = (ProgressBar) layout.findViewById(R.id.loading);
			loading.setVisibility(View.GONE);

			TextView title = (TextView) layout.findViewById(R.id.story_item_title);
			TextView author = (TextView) layout.findViewById(R.id.story_item_author);
			View categoryRuler = (View) layout.findViewById(R.id.categoryRuler);

			title.setText(mStories.get(position).headline);
			author.setText(mStories.get(position).firstName + " " + mStories.get(position).lastName);
			categoryRuler.setBackgroundColor(getColorByCategory(mStories.get(position).category, HIGHLIGHT));
			if (position == mResumeStory) {
				View storyItemShader = (View) layout.findViewById(R.id.story_item_shader);
				storyItemShader.setBackgroundColor(0x40000000);
			}

			layout.setTag(position);

			((ViewPager) container).addView(layout);

			return layout;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}

		@Override
		public int getCount() {
			return mStories.size();
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return (view == object);
		}
	}

	private OnPageChangeListener onStorySwipe = new OnPageChangeListener() {

		@Override
		public void onPageScrollStateChanged(int state) {
		}

		@Override
		public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
		}

		@Override
		public void onPageSelected(int position) {
			// Shade old view
			View layout = mPager.findViewWithTag(mCurrentStory);
			View storyItemShader = (View) layout.findViewById(R.id.story_item_shader);
			storyItemShader.setBackgroundColor(0xC0000000);
			mCircles.get(mCurrentStory).setFillColor(getColorByCategory(mStories.get(mCurrentStory).category, SHADE));
			mCircles.get(mCurrentStory).setStrokeColor(
					getStrokeColorByCategory(mStories.get(mCurrentStory).category, SHADE));
			mGroundOverlays
					.get(mCurrentStory)
					.setImage(
							BitmapDescriptorFactory.fromResource(getGroundOverlayByCategory(mStories.get(mCurrentStory).category)));

			// Change current selected view
			mCurrentStory = position;

			// Brighten current view
			layout = mPager.findViewWithTag(mCurrentStory);
			storyItemShader = (View) layout.findViewById(R.id.story_item_shader);
			storyItemShader.setBackgroundColor(0x40000000);
			mCircles.get(mCurrentStory).setFillColor(
					getColorByCategory(mStories.get(mCurrentStory).category, HIGHLIGHT));
			mCircles.get(mCurrentStory).setStrokeColor(
					getStrokeColorByCategory(mStories.get(mCurrentStory).category, HIGHLIGHT));
			mGroundOverlays.get(mCurrentStory)
					.setImage(
							BitmapDescriptorFactory.fromResource(getActiveGroundOverlayByCategory(mStories
									.get(mCurrentStory).category)));

		}

	};

	/* ***** FOOTER ADAPTER AND ONPAGECHANGELISTENER END ***** */

	/* ***** SETTINGS BEGIN ***** */

	OnClickListener filterSettingsToggler = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.button_filter_mic:
				toggle(R.id.button_filter_mic, mFilterSettings.Audio);
				break;
			case R.id.button_filter_camera:
				toggle(R.id.button_filter_camera, mFilterSettings.Picture);
				break;
			case R.id.button_filter_video:
				toggle(R.id.button_filter_video, mFilterSettings.Video);
				break;
			case R.id.button_filter_people:
				toggle(R.id.button_filter_people, mFilterSettings.People);
				break;
			case R.id.button_filter_community:
				toggle(R.id.button_filter_community, mFilterSettings.Community);
				break;
			case R.id.button_filter_sports:
				toggle(R.id.button_filter_sports, mFilterSettings.Sports);
				break;
			case R.id.button_filter_food:
				toggle(R.id.button_filter_food, mFilterSettings.Food);
				break;
			case R.id.button_filter_public_safety:
				toggle(R.id.button_filter_public_safety, mFilterSettings.PublicSafety);
				break;
			case R.id.button_filter_arts_and_life:
				toggle(R.id.button_filter_arts_and_life, mFilterSettings.ArtsAndLife);
				break;
			}
		}

		private void toggle(int imageViewId, Boolean currentlyOn) {
			if (currentlyOn) {
				ImageView v = (ImageView) findViewById(imageViewId);
				v.setImageResource(getFilterOffImageByViewId(imageViewId));
				mFilterSettings.toggle(imageViewId);
			} else {
				ImageView v = (ImageView) findViewById(imageViewId);
				v.setImageResource(getFilterOnImageByViewId(imageViewId));
				mFilterSettings.toggle(imageViewId);
			}
		}

		private int getFilterOffImageByViewId(int imageViewId) {
			switch (imageViewId) {
			case R.id.button_filter_mic:
				return R.drawable.filter_mic;
			case R.id.button_filter_camera:
				return R.drawable.filter_camera;
			case R.id.button_filter_video:
				return R.drawable.filter_video;
			case R.id.button_filter_people:
				return R.drawable.filter_people;
			case R.id.button_filter_community:
				return R.drawable.filter_community;
			case R.id.button_filter_sports:
				return R.drawable.filter_sports;
			case R.id.button_filter_food:
				return R.drawable.filter_food;
			case R.id.button_filter_public_safety:
				return R.drawable.filter_public_safety;
			case R.id.button_filter_arts_and_life:
				return R.drawable.filter_arts_and_life;
			}
			return -1;
		}

		private int getFilterOnImageByViewId(int imageViewId) {
			switch (imageViewId) {
			case R.id.button_filter_mic:
				return R.drawable.filter_mic_active;
			case R.id.button_filter_camera:
				return R.drawable.filter_camera_active;
			case R.id.button_filter_video:
				return R.drawable.filter_video_active;
			case R.id.button_filter_people:
				return R.drawable.filter_people_active;
			case R.id.button_filter_community:
				return R.drawable.filter_community_active;
			case R.id.button_filter_sports:
				return R.drawable.filter_sports_active;
			case R.id.button_filter_food:
				return R.drawable.filter_food_active;
			case R.id.button_filter_public_safety:
				return R.drawable.filter_public_safety_active;
			case R.id.button_filter_arts_and_life:
				return R.drawable.filter_arts_and_life_active;
			}
			return -1;
		}
	};

	/* ***** SETTINGS END ***** */

	/* ***** EXTRA MENUS HIDE/SHOW BEGIN ***** */

	private void hideFiltersLayout() {
		filtersMenuIsOpen = false;
		mLayoutFilters.setVisibility(View.GONE);
		mLayoutFilters.startAnimation(mSlideOutLeft);

		// Fade in the views underneath
		mMapContainer.setVisibility(View.VISIBLE);
		mMapContainer.startAnimation(mFadeIn);
		mMiddlebar.setVisibility(View.VISIBLE);
		mMiddlebar.startAnimation(mFadeIn);
		mMenuSettings.setVisibility(View.VISIBLE);
		mMenuSettings.startAnimation(mFadeIn);

	}

	private void showFiltersLayout() {
		filtersMenuIsOpen = true;
		mLayoutFilters.setVisibility(View.VISIBLE);
		mLayoutFilters.startAnimation(mSlideInLeft);

		// Fade out the views underneath
		// Prevent interaction with views underneath
		mMapContainer.setVisibility(View.INVISIBLE);
		mMapContainer.startAnimation(mFadeOut);
		mMiddlebar.setVisibility(View.INVISIBLE);
		mMiddlebar.startAnimation(mFadeOut);
		mMenuSettings.setVisibility(View.INVISIBLE);
		mMenuSettings.startAnimation(mFadeOut);

	}

	private void hideOrShowSettingsMenu() {
		if (settingsMenuIsOpen) {
			mStoryFooter.setVisibility(View.VISIBLE);
			mStoryFooter.startAnimation(mFadeIn);

			// change color of settings icon and show relevant, breaking buttons
			mButtonSettingsAndFilters.setImageResource(R.drawable.settings);

			mButtonRelevant.setVisibility(View.VISIBLE);
			mButtonRelevant.startAnimation(mFadeIn);
			mButtonBreaking.setVisibility(View.VISIBLE);
			mButtonBreaking.startAnimation(mFadeIn);

			mMenuSettings.setVisibility(View.GONE);
			mMenuSettings.startAnimation(mSlideOutBottom);

			settingsMenuIsOpen = false;
		} else {
			mStoryFooter.setVisibility(View.GONE);
			mStoryFooter.startAnimation(mFadeOut);

			// change color of settings icon and hide relevant, breaking buttons
			mButtonSettingsAndFilters.setImageResource(R.drawable.settings_active);

			mButtonRelevant.setVisibility(View.INVISIBLE);
			mButtonRelevant.startAnimation(mFadeOut);
			mButtonBreaking.setVisibility(View.INVISIBLE);
			mButtonBreaking.startAnimation(mFadeOut);

			mMenuSettings.setVisibility(View.VISIBLE);
			mMenuSettings.startAnimation(mSlideInBottom);

			settingsMenuIsOpen = true;
		}
	}

	/* ***** EXTRA MENUS HIDE/SHOW END ***** */

	/* ***** SEARCH TYPE BEGIN ***** */

	private void switchSearchTypes(int pressedButton) {
		// Change mCurrentSearchType
		if ((mCurrentSearchType == SearchType.RELEVANT) && (pressedButton == R.id.button_breaking)) {
			mCurrentSearchType = SearchType.BREAKING;
		} else if ((mCurrentSearchType == SearchType.BREAKING) && (pressedButton == R.id.button_relevant)) {
			mCurrentSearchType = SearchType.RELEVANT;
		}
		// Make sure UI is up to date
		if (mCurrentSearchType == SearchType.RELEVANT) {
			mButtonRelevant.setTextColor(SEARCH_TYPE_ACTIVE_COLOR);
			mButtonBreaking.setTextColor(SEARCH_TYPE_FADED_COLOR);
		} else {
			mButtonRelevant.setTextColor(SEARCH_TYPE_FADED_COLOR);
			mButtonBreaking.setTextColor(SEARCH_TYPE_ACTIVE_COLOR);
		}
	}

	/* ***** SEARCH TYPE END ***** */

	/* ***** GOOGLE PLAY SERVICES BLOAT BEGIN ***** */

	/*
	 * Called by Location Services if the connection to the location client
	 * drops because of an error.
	 */
	@Override
	public void onDisconnected() {
		// Display the connection status
		Toast.makeText(this, "Disconnected. Please re-connect.", Toast.LENGTH_SHORT).show();
	}

	/*
	 * Called by Location Services if the attempt to Location Services fails.
	 */
	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {
		/*
		 * Google Play services can resolve some errors it detects. If the error
		 * has a resolution, try sending an Intent to start a Google Play
		 * services activity that can resolve error.
		 */
		if (connectionResult.hasResolution()) {
			try {
				// Start an Activity that tries to resolve the error
				connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
				/*
				 * Thrown if Google Play services canceled the original
				 * PendingIntent
				 */
			} catch (IntentSender.SendIntentException e) {
				// Log the error
				e.printStackTrace();
			}
		} else {
			/*
			 * If no resolution is available, display a dialog to the user with
			 * the error.
			 */
			showErrorDialog(connectionResult.getErrorCode());
		}
	}

	/*
	 * Handle results returned to the FragmentActivity by Google Play services
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// Decide what to do based on the original request code
		switch (requestCode) {
		case CONNECTION_FAILURE_RESOLUTION_REQUEST:
			/*
			 * If the result code is Activity.RESULT_OK, try to connect again
			 */
			switch (resultCode) {
			case Activity.RESULT_OK:
				/*
				 * Try the request again
				 */
				break;
			}
		}
	}

	private boolean servicesConnected() {
		// Check that Google Play services is available
		int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
		// If Google Play services is available
		if (ConnectionResult.SUCCESS == resultCode) {
			// In debug mode, log the status
			Log.d("Location Updates", "Google Play services is available.");
			// Continue
			return true;
			// Google Play services was not available for some reason
		} else {
			// Get the error dialog from Google Play services
			Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(resultCode, this,
					CONNECTION_FAILURE_RESOLUTION_REQUEST);

			// If Google Play services can provide an error dialog
			if (errorDialog != null) {
				// Create a new DialogFragment for the error dialog
				ErrorDialogFragment errorFragment = new ErrorDialogFragment();
				// Set the dialog in the DialogFragment
				errorFragment.setDialog(errorDialog);
				// Show the error dialog in the DialogFragment
				errorFragment.show(getSupportFragmentManager(), "Location Updates");
			}
			return false;
		}
	}

	private void showErrorDialog(int code) {
		GooglePlayServicesUtil.getErrorDialog(code, this, REQUEST_CODE_RECOVER_PLAY_SERVICES).show();
	}

	/* ***** GOOGLE PLAY SERVICES BLOAT END ***** */

	/* ***** GET THEME RESOURCES BY CATEGORY BEGIN ***** */

	protected int getColorByCategory(String category, boolean highlight) {
		int retval;
		if ("People".equals(category)) {
			retval = COLOR_PEOPLE;
		} else if ("Community".equals(category)) {
			retval = COLOR_COMMUNITY;
		} else if ("Sports".equals(category)) {
			retval = COLOR_SPORTS;
		} else if ("Food".equals(category)) {
			retval = COLOR_FOOD;
		} else if ("Public Safety".equals(category)) {
			retval = COLOR_PUBLIC_SAFETY;
		} else { // Arts and Life
			retval = COLOR_ARTS_AND_LIFE;
		}
		return highlight ? retval : retval & 0xC0FFFFFF;
	}

	private int getStrokeColorByCategory(String category, boolean highlight) {
		int retval;
		if ("People".equals(category)) {
			retval = COLOR_PEOPLE_STROKE;
		} else if ("Community".equals(category)) {
			retval = COLOR_COMMUNITY_STROKE;
		} else if ("Sports".equals(category)) {
			retval = COLOR_SPORTS_STROKE;
		} else if ("Food".equals(category)) {
			retval = COLOR_FOOD_STROKE;
		} else if ("Public Safety".equals(category)) {
			retval = COLOR_PUBLIC_SAFETY_STROKE;
		} else { // Arts and Life
			retval = COLOR_ARTS_AND_LIFE_STROKE;
		}
		return highlight ? retval : retval & 0xC0FFFFFF;

	}

	private int getGroundOverlayByCategory(String category) {
		if ("People".equals(category)) {
			return R.drawable.people_white;
		} else if ("Community".equals(category)) {
			return R.drawable.community_white;
		} else if ("Sports".equals(category)) {
			return R.drawable.sports_white;
		} else if ("Food".equals(category)) {
			return R.drawable.food_white;
		} else if ("Public Safety".equals(category)) {
			return R.drawable.public_saftey_white;
		} else { // Arts and Life
			return R.drawable.arts_and_life_white;
		}
	}

	private int getActiveGroundOverlayByCategory(String category) {
		if ("People".equals(category)) {
			return R.drawable.people_white_active;
		} else if ("Community".equals(category)) {
			return R.drawable.community_white_active;
		} else if ("Sports".equals(category)) {
			return R.drawable.sports_white_active;
		} else if ("Food".equals(category)) {
			return R.drawable.food_white_active;
		} else if ("Public Safety".equals(category)) {
			return R.drawable.public_saftey_white_active;
		} else { // Arts and Life
			return R.drawable.arts_and_life_white_active;
		}
	}

	/* ***** GET THEME RESOURCES BY CATEGORY END ***** */

}
