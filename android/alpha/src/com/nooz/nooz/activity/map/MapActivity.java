package com.nooz.nooz.activity.map;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.WeakHashMap;

import android.app.ActionBar;
import android.app.ActionBar.OnNavigationListener;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v4.widget.DrawerLayout;
import android.view.Display;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.android.volley.toolbox.NetworkImageView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.nooz.nooz.R;
import com.nooz.nooz.activity.ActivityGestureDetector;
import com.nooz.nooz.activity.BaseLocationFragmentActivity;
import com.nooz.nooz.activity.LoginActivity;
import com.nooz.nooz.activity.article.ArticleLauncher;
import com.nooz.nooz.activity.profile.ProfileLauncher;
import com.nooz.nooz.activity.settings.SettingsActivity;
import com.nooz.nooz.mediarecorder.MediaRecorderActivity;
import com.nooz.nooz.model.Story;
import com.nooz.nooz.util.Alert;
import com.nooz.nooz.util.GlobalConstant;
import com.nooz.nooz.util.Tools;
import com.nooz.nooz.widget.PagerContainer;
import com.twotoasters.clusterkraf.Clusterkraf;
import com.twotoasters.clusterkraf.Clusterkraf.ProcessingListener;
import com.twotoasters.clusterkraf.CustomOnCameraChangeCallable;
import com.twotoasters.clusterkraf.InputPoint;
import com.twotoasters.clusterkraf.Options;
import com.twotoasters.clusterkraf.Options.ClusterClickBehavior;
import com.twotoasters.clusterkraf.Options.ClusterInfoWindowClickBehavior;
import com.twotoasters.clusterkraf.Options.SinglePointClickBehavior;

/**
 * 
 * @author Rob Stein
 * 
 */
public class MapActivity extends BaseLocationFragmentActivity implements ProcessingListener {

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
	ScrollView mMenuSettings;
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
	WeakHashMap<Marker, Story> mMarkers;
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

	private GestureDetector mGestureDetector;
	private OnTouchListener mGestureListener;

	Clusterkraf clusterkraf;
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private String[] mDrawerText;

	/* ***** ACTIVITY LIFECYCLE BEGIN ***** */

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (handleLogoutIntent()) {
			return;
		}

		initOptionsMenuSpinner();
		// initDrawer();
		initFields();
		initViews();
		initViewListeners();
		initScreenMeasurements();
		initPager();
	}

	private void initOptionsMenuSpinner() {
		SpinnerAdapter mSpinnerAdapter = ArrayAdapter.createFromResource(this, R.array.map_actionbar_spinner_list,
				android.R.layout.simple_spinner_dropdown_item);
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		actionBar.setListNavigationCallbacks(mSpinnerAdapter, mOnNavigationListener);
	}

	private void initDrawer() {
		mDrawerText = getResources().getStringArray(R.array.drawer_array_text);

		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.left_drawer);
		// Set the adapter for the list view

		mDrawerList.setAdapter(new BaseAdapter() {

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				if (convertView == null) {
					LayoutInflater infalInflater = (LayoutInflater) mContext
							.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
					convertView = infalInflater.inflate(R.layout.drawer_list_item, null);
				}
				TextView text = (TextView) convertView.findViewById(R.id.drawer_item_text);
				text.setText(getItem(position));
				return convertView;
			}

			@Override
			public long getItemId(int position) {
				return position;
			}

			@Override
			public String getItem(int position) {
				return mDrawerText[position];
			}

			@Override
			public int getCount() {
				return mDrawerText.length;
			}
		});
		// Set the list's click listener
		mDrawerList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub

			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu items for use in the action bar
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.map_activity_actionbar, menu);
		return super.onCreateOptionsMenu(menu);
	}

	public boolean onOptionsItemSelected(android.view.MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_new:
			attemptMakeNewStory();
			return true;
		case R.id.action_profile:
			ProfileLauncher.openProfile(this, mUserId);
			return true;
		case R.id.action_map_filters:
			mMenuController.showFiltersLayout();
			return true;
		case R.id.action_settings:
			Intent settingsIntent = new Intent(getApplicationContext(), SettingsActivity.class);
			startActivity(settingsIntent);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	OnNavigationListener mOnNavigationListener = new OnNavigationListener() {
		@Override
		public boolean onNavigationItemSelected(int itemPosition, long itemId) {
			mMenuController.switchSearchTypes(itemPosition);
			return true;
		}
	};

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
		mMarkers = new WeakHashMap<Marker, Story>();
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
		mMenuSettings = (ScrollView) findViewById(R.id.menu_settings);
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
		mRegion.setOnClickListener(mActivityOnClickListener);
		mButtonRelevant.setOnClickListener(mActivityOnClickListener);
		mButtonSettingsAndFilters.setOnClickListener(mActivityOnClickListener);
		mButtonBreaking.setOnClickListener(mActivityOnClickListener);
		mButtonRefresh.setOnClickListener(mActivityOnClickListener);
		mButtonNewStory.setOnClickListener(mActivityOnClickListener);

		// Settings menu view listeners
		mButtonProfile.setOnClickListener(mActivityOnClickListener);
		mButtonMapFilters.setOnClickListener(mActivityOnClickListener);

		// Filter menu view listeners
		initGestureDetectionListeners();
		mLayoutFilters.setOnTouchListener(mGestureListener);
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

	private void initGestureDetectionListeners() {
		mGestureDetector = new GestureDetector(this, new ActivityGestureDetector() {
			@Override
			public void onSwipeLeft() {
				mMenuController.hideFiltersLayout();
			}
		});
		mGestureListener = new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				return mGestureDetector.onTouchEvent(event);
			}
		};
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
			handleLogout();
		}
		return finish;
	}

	public void handleLogoutFromActionBar() {
		handleLogout();
	}

	private void handleLogout() {
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
		// SharedPreferences userData =
		// mContext.getSharedPreferences("UserData", Context.MODE_PRIVATE);
		// mButtonProfile.setText(userData.getString("user_name", ""));
		// mUserId = userData.getString("userid", null);
		mButtonProfile.setText(mNoozService.getUserName());
		mUserId = mNoozService.getUserId();

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
		filter.addAction(GlobalConstant.RELEVANCE_UPDATE_ACTION);
		registerReceiver(mReceiver, filter);
	}

	/*
	 * Called when the Activity is partially visible.
	 */
	@Override
	protected void onPause() {
		unRegisterReceivers();
		saveSettings();

		/*
		 * When pausing, we clear all of the clusterkraf's markers in order to
		 * conserve memory. When (if) we resume, we can rebuild from where we
		 * left off.
		 */
		if (clusterkraf != null) {
			clusterkraf.clear();
			clusterkraf = null;
		}

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
				setUpMapAndInitClusterkraf();
			}
		} else {
			setUpMapAndInitClusterkraf();
		}
	}

	private void setUpMapAndInitClusterkraf() {
		mMap.setMyLocationEnabled(false);
		UiSettings uiSettings = mMap.getUiSettings();
		uiSettings.setZoomControlsEnabled(true);
		mMap.setOnMarkerClickListener(new OnMarkerClickListener() {
			@Override
			public boolean onMarkerClick(Marker marker) {
				ArticleLauncher.openStory(mContext, mMarkers.get(marker));
				return false; // Don't show info-window
			}
		});
		// Init Clusterkraf
		initClusterkraf();
	}

	/* ***** MAP SETUP END ***** */

	/* ***** BUBBLES BEGIN ***** */

	void drawCirlesOnMap() {
		// int i = 0;
		// for (Story s : mStories) {
		// addMarker(s);
		// i++;
		// }
		buildClusterkrafInputPoints();
		initClusterkraf();
	}

	private void addMarker(Story s) {
		Marker marker = mMap.addMarker(new MarkerOptions().position(new LatLng(s.lat, s.lng)).anchor(.5f, .5f)
				.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_bubble_cluster)));

	}

	ArrayList<InputPoint> mClusterkrafInputPoints;

	private void buildClusterkrafInputPoints() {
		mClusterkrafInputPoints = new ArrayList<InputPoint>(mStories.size());
		for (Story s : mStories) {
			mClusterkrafInputPoints.add(new InputPoint(new LatLng(s.lat, s.lng), s));
		}
	}

	private void initClusterkraf() {
		if (mMap != null && mClusterkrafInputPoints != null && mClusterkrafInputPoints.size() > 0) {
			Options options = new Options();
			applyOptionsToClusterkrafOptions(options);
			this.clusterkraf = new Clusterkraf(mMap, options, mClusterkrafInputPoints,
					new CustomOnCameraChangeCallable() {
						@Override
						public void onCameraChange() {
							mStoryDataController.clearAndPopulateStories();
						}
					});
		}
	}

	private void applyOptionsToClusterkrafOptions(Options options) {
		options.setTransitionInterpolator(new AccelerateDecelerateInterpolator());
		options.setPixelDistanceToJoinCluster((int) Tools.dipToPixels(this, 72));

		options.setZoomToBoundsAnimationDuration(500);
		options.setShowInfoWindowAnimationDuration(500);
		options.setExpandBoundsFactor(0.5d);
		options.setSinglePointClickBehavior(SinglePointClickBehavior.NO_OP);
		options.setClusterClickBehavior(ClusterClickBehavior.ZOOM_TO_BOUNDS);
		options.setClusterInfoWindowClickBehavior(ClusterInfoWindowClickBehavior.ZOOM_TO_BOUNDS);

		options.setZoomToBoundsPadding(getResources().getDrawable(R.drawable.ic_map_bubble_cluster)
				.getIntrinsicHeight());
		options.setMarkerOptionsChooser(new NoozMarkerOptionsChooser(this));
		options.setOnMarkerClickDownstreamListener(new NoozOnMarkerClickDownstreamListener(this));
		options.setProcessingListener(this);
	}

	private DelayedIndeterminateProgressBarRunnable delayedIndeterminateProgressBarRunnable;
	private final Handler handler = new Handler();
	private static final long DELAY_CLUSTERING_SPINNER_MILLIS = 200l;

	@Override
	public void onClusteringStarted() {
		if (delayedIndeterminateProgressBarRunnable == null) {
			delayedIndeterminateProgressBarRunnable = new DelayedIndeterminateProgressBarRunnable(this);
			handler.postDelayed(delayedIndeterminateProgressBarRunnable, DELAY_CLUSTERING_SPINNER_MILLIS);
		}
	}

	@Override
	public void onClusteringFinished() {
		if (delayedIndeterminateProgressBarRunnable != null) {
			handler.removeCallbacks(delayedIndeterminateProgressBarRunnable);
			delayedIndeterminateProgressBarRunnable = null;
		}
		setProgressBarIndeterminateVisibility(false);
	}

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

	public void attemptMakeNewStory() {
		if (getCurrentLocation() == null) {
			Alert.createAndShowDialog("Please turn on Locations Services", "Location not found", this);
		} else {
			Intent mediaRecorderIntent = new Intent(getApplicationContext(), MediaRecorderActivity.class);
			startActivity(mediaRecorderIntent);
		}
	}

	void handleUpdateRelevance(String storyId, int input) {

		int openedStoryIndex = -1;
		for (Story s : mStories) {
			if (storyId.equals(s.id)) {
				openedStoryIndex = mStories.indexOf(s);
				break;
			}
		}
		if (openedStoryIndex == -1) {
			return;
		}

		int prevRelevance = mStories.get(openedStoryIndex).userRelevance;
		switch (prevRelevance) {
		case 1:
			switch (input) {
			case 1:
				break;
			case -1:
				mStories.get(openedStoryIndex).scoreIrrelevance++;
				mStories.get(openedStoryIndex).scoreRelevance--;
				break;
			case 0:
				mStories.get(openedStoryIndex).scoreRelevance--;
				break;
			}
			break;
		case -1:
			switch (input) {
			case 1:
				mStories.get(openedStoryIndex).scoreRelevance++;
				mStories.get(openedStoryIndex).scoreIrrelevance--;
				break;
			case -1:
				break;
			case 0:
				mStories.get(openedStoryIndex).scoreIrrelevance--;
				break;
			}
			break;
		case 0:
			switch (input) {
			case 1:
				mStories.get(openedStoryIndex).scoreRelevance++;
				break;
			case -1:
				mStories.get(openedStoryIndex).scoreIrrelevance++;
				break;
			case 0:
				break;
			}
			break;
		}

		mStories.get(openedStoryIndex).userRelevance = input;
	}

}
