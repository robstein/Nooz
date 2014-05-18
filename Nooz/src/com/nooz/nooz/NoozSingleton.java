package com.nooz.nooz;

import android.app.Activity;

import com.nooz.nooz.util.NoozService;

public class NoozSingleton {
	private static NoozSingleton instance;

	public static void initInstance() {
		if (instance == null) {
			// Create the instance
			instance = new NoozSingleton();
		}
	}

	public static NoozSingleton getInstance() {
		// Return the instance
		return instance;
	}

	private NoozSingleton() {
		// Constructor hidden because this is a singleton
	}

	/**** CUSTOM STUFF ****/
	
	private Activity mCurrentActivity;
	private NoozService mNoozService;

	public void setCurrentActivity(Activity activity) {
		mCurrentActivity = activity;
	}

	public Activity getCurrentActivity() {
		return mCurrentActivity;
	}

	public NoozService getNoozService() {
		if (mNoozService == null) {
			mNoozService = new NoozService(mCurrentActivity);
		}
		return mNoozService;
	}
}
