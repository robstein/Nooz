package com.nooz.nooz;

import android.app.Activity;
import android.app.Application;

import com.nooz.nooz.util.NoozService;

/**
 * Nooz Application uses singleton design pattern
 * 
 * @author Rob Stein
 * 
 */
public class NoozApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();

		// Initialize the singletons so their instances
		// are bound to the application process.
		initSingletons();
	}

	protected void initSingletons() {
		// Initialize the instance of the singleton
		NoozSingleton.initInstance();
	}

	/**
	 * 
	 * @param activity
	 *            current activity
	 */
	public void setCurrentActivity(Activity activity) {
		NoozSingleton.getInstance().setCurrentActivity(activity);
	}

	/**
	 * 
	 * @return current activity
	 */
	public Activity getCurrentActivity() {
		return NoozSingleton.getInstance().getCurrentActivity();
	}

	/**
	 * 
	 * @return the application NoozService
	 */
	public NoozService getNoozService() {
		return NoozSingleton.getInstance().getNoozService();
	}
}
