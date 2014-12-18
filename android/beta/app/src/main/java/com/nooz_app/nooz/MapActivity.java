package com.nooz_app.nooz;

import android.app.ActionBar;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;


public class MapActivity extends BaseActivity {

    private static final LatLng DEFAULT_LOCATION = new LatLng(37.09024, -95.712891);
    private static final int DEFAULT_ZOOM = 3;

    /***
     * BroadcastReceiver handles blobs for stories when loaded
     */
    MapBroadcastReceiver mReceiver;

    /**
     * Stores the current SearchType: "RELEVANT" or "BREAKING"
     *
     * @see com.nooz_app.nooz.RankingAlgorithm
     */
    private RankingAlgorithm mRankingAlgorithm = RankingAlgorithm.RELEVANT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (logoutFromExternalIntent()) {
            return;
        }

        setContentView(R.layout.activity_map);

        //initFields();
        //initOptionsMenuSpinner();

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.main_container, new MapFragment())
                    .commit();
            getFragmentManager().beginTransaction()
                    .add(R.id.footer_container, new ReelFragment())
                    .commit();
        }
    }

    /*
    private void initOptionsMenuSpinner() {
        SpinnerAdapter mSpinnerAdapter = ArrayAdapter.createFromResource(this, R.array.map_actionbar_spinner_list,
                android.R.layout.simple_spinner_dropdown_item);
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        actionBar.setListNavigationCallbacks(mSpinnerAdapter, new ActionBar.OnNavigationListener() {
            @Override
            public boolean onNavigationItemSelected(int itemPosition, long itemId) {
                switchSearchTypes(itemPosition);
                return true;
            }
        });
    }
    */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.map, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case R.id.action_new:
                //attemptMakeNewStory();
                return true;
            case R.id.action_profile:
                //ProfileLauncher.openProfile(this, mUserId);
                return true;
            case R.id.action_map_filters:
                //mMenuController.showFiltersLayout();
                return true;
            case R.id.action_settings:
                //Intent settingsIntent = new Intent(getApplicationContext(), SettingsActivity.class);
                //startActivity(settingsIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
        //restoreSettings();
        syncStories();
    }

    private void registerReceivers() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_STORIES_LOADED);
        filter.addAction(ACTION_STORYIMAGE_LOADED_);
        filter.addAction(ACTION_RELEVANCE_UPDATED);
        registerReceiver(mReceiver, filter);
    }

    /*
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
    */

    /*
	 * Called when the Activity is partially visible.
	 */
    @Override
    protected void onPause() {
        unRegisterReceivers();
        //saveSettings();

        super.onPause();
    }

    /*
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
    */

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

    void switchSearchTypes(int toRankingAlgorithm) {
        if ((mRankingAlgorithm == RankingAlgorithm.RELEVANT) && (toRankingAlgorithm == RankingAlgorithm.BREAKING.value())) {
            mRankingAlgorithm = RankingAlgorithm.BREAKING;
        } else if ((mRankingAlgorithm == RankingAlgorithm.BREAKING) && (toRankingAlgorithm == RankingAlgorithm.RELEVANT.value())) {
            mRankingAlgorithm = RankingAlgorithm.RELEVANT;
        } else {
            /* Do nothing */
            return;
        }
        syncStories();
    }

    void syncStories() {
        LatLngBounds bounds = ((MapFragment) getFragmentManager().findFragmentById(R.id.main_container)).getMap().getProjection().getVisibleRegion().latLngBounds;
        //getNoozService().getAllStories(bounds, mFilterSettings, mRankingAlgorithm, null);
    }

    /**
    * On logout from another activity, an intent with a boolean extra named
    * "finish" is sent to the MapActivity. From the MapActivity (the oldest parent
    * activity), we can call LoginActiviy again and call finish().
    *
    * @return True if the user is logging out.
    */
    private boolean logoutFromExternalIntent() {
        boolean finish = getIntent().getBooleanExtra("finish", false);
        if (finish) {
            logout();
        }
        return finish;
    }

    public void logoutFromActionBar() {
        logout();
    }

    private void logout() {
        //Intent logoutIntent = new Intent(mContext, LoginActivity.class);
        //mContext.startActivity(logoutIntent);
        //startActivity(logoutIntent);

        // When logging off reset SharedPrefs to defaults.
        SharedPreferences settings = getSharedPreferences("map_settings", MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putFloat("latitude", (float) DEFAULT_LOCATION.latitude);
        editor.putFloat("longitude", (float) DEFAULT_LOCATION.longitude);
        editor.putFloat("zoom", (float) DEFAULT_ZOOM);
        editor.putInt("current_story", ZERO);
        editor.commit();

        finish();
    }



    static class MapBroadcastReceiver extends BroadcastReceiver {

        private static final String TAG = "MapBroadcastReceiver";

        @Override
        public void onReceive(Context context, Intent intent) {
            String intentAction = intent.getAction();
            if (ACTION_STORIES_LOADED.equals(intentAction)) {
                try {
                    //((MapActivity) context).mStoryDataController.getStoriesCallBack();
                } catch (Exception e) {
                    Log.e(TAG, "There was a problem in getStoriesCallBack: " + e.getMessage());
                }
            }
            if (ACTION_RELEVANCE_UPDATED.equals(intentAction)) {
                //((MapActivity) context).handleUpdateRelevance(intent.getStringExtra("id"), intent.getIntExtra("input", 0));
            }
        }

    }


}
