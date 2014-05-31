package com.nooz.nooz;

import android.app.Activity;

import com.nooz.nooz.util.NoozService;

/**
 * Nooz Singleton maintains single NoozService
 * 
 * @author Rob Stein
 * 
 */
public class NoozSingleton {
	private static NoozSingleton instance;

	/**
	 * Singleton design pattern initializer
	 */
	public static void initInstance() {
		if (instance == null) {
			// Create the instance
			instance = new NoozSingleton();
		}
	}

	/**
	 * 
	 * @return singleton instance
	 */
	public static NoozSingleton getInstance() {
		// Return the instance
		return instance;
	}

	private NoozSingleton() {
		// Constructor hidden because this is a singleton
	}

	/* *** CUSTOM STUFF *** */

	private Activity mCurrentActivity;
	private NoozService mNoozService;

	/**
	 * 
	 * @param activity
	 *            current activity
	 */
	public void setCurrentActivity(Activity activity) {
		mCurrentActivity = activity;
	}

	/**
	 * 
	 * @return the current activity
	 */
	public Activity getCurrentActivity() {
		return mCurrentActivity;
	}

	/**
	 * Gets the application's one and only NoozService
	 * 
	 * @return the NoozService
	 */
	public NoozService getNoozService() {
		if (mNoozService == null) {
			mNoozService = new NoozService(mCurrentActivity);
		}
		return mNoozService;
	}
}
