package com.nooz.nooz;

import android.app.Activity;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.nooz.nooz.util.BitmapLruCache;
import com.nooz.nooz.util.NoozService;

/**
 * Nooz Singleton maintains single NoozService and Volley core objects
 * 
 * @author Rob Stein
 * 
 */
public class NoozSingleton {

	public static final String TAG = NoozSingleton.class.getSimpleName();

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
	public static synchronized NoozSingleton getInstance() {
		// Return the instance
		return instance;
	}

	private NoozSingleton() {
		// Constructor hidden because this is a singleton
	}

	/* *** CUSTOM STUFF *** */

	private Activity mCurrentActivity;
	private NoozService mNoozService;

	private RequestQueue mRequestQueue;
	private ImageLoader mImageLoader;
	private BitmapLruCache mCache;

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

	public RequestQueue getRequestQueue() {
		if (mRequestQueue == null) {
			mRequestQueue = Volley.newRequestQueue(mCurrentActivity);
		}
		return mRequestQueue;
	}

	public ImageLoader getImageLoader() {
		getRequestQueue();
		if (mImageLoader == null) {
			mImageLoader = new ImageLoader(getRequestQueue(), new BitmapLruCache(mCurrentActivity));
		}
		return this.mImageLoader;
	}

	public <T> void addToRequestQueue(Request<T> req, String tag) {
		// set the default tag if tag is empty
		req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
		getRequestQueue().add(req);
	}

	public <T> void addToRequestQueue(Request<T> req) {
		req.setTag(TAG);
		getRequestQueue().add(req);
	}

	public void cancelPendingRequests(Object tag) {
		if (mRequestQueue != null) {
			mRequestQueue.cancelAll(tag);
		}
	}

}
