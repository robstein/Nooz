package com.nooz.nooz.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Point;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.nooz.nooz.R;
import com.nooz.nooz.util.DisplayUserFullNameCallbackInterface;
import com.nooz.nooz.util.SearchType;
import com.nooz.nooz.util.Tools;
import com.nooz.nooz.widget.PagerContainer;

public class MapActivity extends BaseFragmentActivity implements OnClickListener,
		GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener {

	private PagerContainer mContainer;
	private ViewPager mPager;
	private TextView mButtonRelevant;
	private TextView mButtonBreaking;
	private ImageView mButtonSettingsAndFilters;
	private ImageView mButtonRefresh;
	private ImageView mButtonNewStory;
	private GoogleMap mMap;
	private RelativeLayout mStoryFooter;
	private RelativeLayout mMenuSettings;
	private TextView mButtonProfile;

	private LocationClient mLocationClient;

	private Location mCurrentLocation;
	private SearchType mCurrentSearchType = SearchType.RELEVANT;
	private Boolean settingsMenuIsOpen = false;

	private Animation mSlideInBottom;
	private Animation mSlideOutBottom;
	private Animation mFadeIn;
	private Animation mFadeOut;

	private static final int SEARCH_TYPE_ACTIVE_COLOR = 0xFF000000;
	private static final int SEARCH_TYPE_FADED_COLOR = 0xFF979797;
	private static final LatLng USA = new LatLng(37.09024, -95.712891);
	private static final int ZOOM_USA = 3;
	private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
	private static final int REQUEST_CODE_RECOVER_PLAY_SERVICES = 1001;
	private static final float FOOTER_WEIGHT = 0.29f;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);

		mContainer = (PagerContainer) findViewById(R.id.pager_container);
		mPager = mContainer.getViewPager();
		PagerAdapter adapter = new MyPagerAdapter(this);
		mPager.setAdapter(adapter);
		// Necessary or the pager will only have one extra page to show
		// make this at least however many pages you can see
		mPager.setOffscreenPageLimit(adapter.getCount());
		// A little space between pages
		mPager.setPageMargin((int) Tools.dipToPixels(this, 4));
		// If hardware acceleration is enabled, you should also remove
		// clipping on the pager for its children.
		mPager.setClipChildren(false);

		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		int width = size.x;
		int height = size.y;
		int footer_height = (int) (height * FOOTER_WEIGHT);
		FrameLayout.LayoutParams footerLayoutParams = (FrameLayout.LayoutParams) mPager.getLayoutParams();
		footerLayoutParams.setMargins((int) ((width - footer_height) / 2) + (int) Tools.dipToPixels(this, 4), 0,
				(int) ((width - footer_height) / 2) + (int) Tools.dipToPixels(this, 4), 0);
		mPager.setLayoutParams(footerLayoutParams);

		mMenuSettings = (RelativeLayout) findViewById(R.id.menu_settings);
		mStoryFooter = (RelativeLayout) findViewById(R.id.story_footer);
		mButtonRelevant = (TextView) findViewById(R.id.button_relevant);
		mButtonBreaking = (TextView) findViewById(R.id.button_breaking);
		mButtonSettingsAndFilters = (ImageView) findViewById(R.id.button_settings);
		mButtonRefresh = (ImageView) findViewById(R.id.button_refresh);
		mButtonNewStory = (ImageView) findViewById(R.id.button_new_story);
		mButtonRelevant.setOnClickListener(this);
		mButtonBreaking.setOnClickListener(this);
		mButtonSettingsAndFilters.setOnClickListener(this);
		mButtonRefresh.setOnClickListener(this);
		mButtonNewStory.setOnClickListener(this);
		mSlideInBottom = AnimationUtils.loadAnimation(this, R.anim.slide_in_bottom);
		mSlideOutBottom = AnimationUtils.loadAnimation(this, R.anim.slide_out_bottom);
		mFadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
		mFadeOut = AnimationUtils.loadAnimation(this, R.anim.fade_out);

		mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
		setUpMapIfNeeded();
		mLocationClient = new LocationClient(this, this, this);

		drawCirlesOnMap();

		displayUserFullName();
	}

	private void displayUserFullName() {
		mNoozService.getUserFullName(new DisplayUserFullNameCallback());
	}

	private class DisplayUserFullNameCallback implements DisplayUserFullNameCallbackInterface {

		@Override
		public void displayUserFullName(String userName) {
			mButtonProfile = (TextView) findViewById(R.id.button_profile);
			mButtonProfile.setText(userName);
		}

	}

	private void drawCirlesOnMap() {
		CircleOptions circleOptions;

		circleOptions = new CircleOptions().center(new LatLng(40.1012894, -88.2358381)).radius(100);
		Circle bluecircle = mMap.addCircle(circleOptions);
		bluecircle.setStrokeColor(0xC06DA3ED);
		bluecircle.setFillColor(0xC0377DEC);

		mMap.addGroundOverlay(new GroundOverlayOptions()
				.image(BitmapDescriptorFactory.fromResource(R.drawable.community_white)).anchor(0.5f, 0.5f)
				.position(new LatLng(40.1012894, -88.2358381), 60f, 60f));

		circleOptions = new CircleOptions().center(new LatLng(40.103766, -88.235417)).radius(25);
		Circle greencircle = mMap.addCircle(circleOptions);
		greencircle.setStrokeColor(0xC03CB34B);
		greencircle.setFillColor(0xC08DC88E);

		mMap.addGroundOverlay(new GroundOverlayOptions()
				.image(BitmapDescriptorFactory.fromResource(R.drawable.food_white)).anchor(0.5f, 0.5f)
				.position(new LatLng(40.103766, -88.235417), 20f, 20f));

	}

	/*
	 * Called when the Activity becomes visible.
	 */
	@Override
	protected void onStart() {
		super.onStart();
		// Connect the client.
		mLocationClient.connect();
	}

	@Override
	protected void onResume() {
		super.onResume();
		setUpMapIfNeeded();
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

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
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
			break;
		case R.id.button_new_story:
			Intent mediaRecorderIntent = new Intent(getApplicationContext(), MediaRecorderActivity.class);
			startActivity(mediaRecorderIntent);
			break;
		}
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

	// Nothing special about this adapter, just throwing up colored views for
	// demo
	private class MyPagerAdapter extends PagerAdapter {

		private Context mContext;

		public MyPagerAdapter(Context context) {
			this.mContext = context;
		}

		@Override
		public void setPrimaryItem(ViewGroup container, int position, Object object) {
			LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(mContext.LAYOUT_INFLATER_SERVICE);
			View layout = inflater.inflate(R.layout.story_item, null);
			View storyItemShader = (View) layout.findViewById(R.id.story_item_shader);
			storyItemShader.setBackgroundColor(0x80000000);
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {

			LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(mContext.LAYOUT_INFLATER_SERVICE);
			View layout = inflater.inflate(R.layout.story_item, null);

			TextView title = (TextView) layout.findViewById(R.id.story_item_title);
			TextView author = (TextView) layout.findViewById(R.id.story_item_author);
			View categoryRuler = (View) layout.findViewById(R.id.categoryRuler);

			if (position != 0) {
				title.setText("Illinois Men's Wrestling Wins 1st");
				author.setText("Drew Smith");
				categoryRuler.setBackgroundColor(0xFFE84C3D);
			} else {
				title.setText("Spring Engineering Career Fair");
				author.setText("Matt Birkel");
				categoryRuler.setBackgroundColor(0xFF377DEC);
			}

			((ViewPager) container).addView(layout);

			return layout;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}

		@Override
		public int getCount() {
			return 5;
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return (view == object);
		}
	}

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
		// CameraPosition cameraPosition = new
		// CameraPosition.Builder().target(USA).zoom(ZOOM_USA).build();
		// mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
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
		mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mCurrentLocation.getLatitude(),
				mCurrentLocation.getLongitude()), 12.0f));
	}

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

	void showErrorDialog(int code) {
		GooglePlayServicesUtil.getErrorDialog(code, this, REQUEST_CODE_RECOVER_PLAY_SERVICES).show();
	}
}
