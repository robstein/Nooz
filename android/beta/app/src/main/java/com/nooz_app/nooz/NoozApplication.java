package com.nooz_app.nooz;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.graphics.Point;
import android.view.Display;

import com.parse.Parse;
import com.parse.ParseACL;

/**
 * Created by rob on 11/30/14.
 */
public class NoozApplication extends Application {

    /**
     * Reference to the current Activity. This member is set in BaseActivity's
     * onCreate method.
     */
    private Activity mActivity;

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

    public NoozApplication() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Parse.initialize(this, "HMxlpuCAr7OhDqEXTKM7KbU6TVeg1hJvav4XyHfv", "zM7fwyk7B3DC6nEKTtLy356JPHCg9lSLcodFS0ZS");

        ParseACL defaultACL = new ParseACL();
        defaultACL.setPublicReadAccess(true);
        ParseACL.setDefaultACL(defaultACL, true);

        // get shared prefs from here

    }

    /**
     * Public setter method for mActivity. Should only be called from abstract activities
     * @param activity the current activity
     */
    public void setActivity(Activity activity) {
        mActivity = activity;
    }

    /**
     * Public getter method for mActivity.
     * @return the current activity
     */
    public Activity getActivity() {
        return mActivity;
    }

    /**
     * Public getter method for mScreenHeightInPixels
     * @return mScreenHeightInPixels
     */
    public int getScreenHeightInPixels() {
        return mScreenHeightInPixels;
    }

    /**
     * Public getter method for mScreenWidthInPixels
     * @return mScreenWidthInPixels
     */
    public int mScreenWidthInPixels() {
        return mScreenWidthInPixels;
    }

    private void initScreenMeasurements() {
        Display display = mActivity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        mScreenWidthInPixels = size.x;
        mScreenHeightInPixels = size.y;
    }

}
