package com.nooz.nooz;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.nooz.nooz.util.BitmapLruCache;
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

	public ImageLoader getImageLoader() {
		return NoozSingleton.getInstance().getImageLoader();
	}

	public <T> void addToRequestQueue(Request<T> req, String tag) {
		NoozSingleton.getInstance().addToRequestQueue(req, tag);
	}

	public <T> void addToRequestQueue(Request<T> req) {
		NoozSingleton.getInstance().addToRequestQueue(req);
	}

	public void cancelPendingRequests(Object tag) {
		NoozSingleton.getInstance().cancelPendingRequests(tag);
	}

}
