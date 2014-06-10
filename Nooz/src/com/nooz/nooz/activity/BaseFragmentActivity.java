package com.nooz.nooz.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.nooz.nooz.NoozApplication;
import com.nooz.nooz.util.NoozService;

/**
 * 
 * @author Rob Stein
 * 
 */
public class BaseFragmentActivity extends FragmentActivity {
	private static final String TAG = "BaseFragmentActivity";
	protected NoozService mNoozService;
	protected Context mContext;
	protected ImageLoader mImageLoader;
	protected RequestQueue mRequestQueue;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		NoozApplication myApp = (NoozApplication) getApplication();
		myApp.setCurrentActivity(this);
		mImageLoader = myApp.getImageLoader();
		mRequestQueue = myApp.getRequestQueue();
		mNoozService = myApp.getNoozService();
		mNoozService.setContext(this);

		mContext = this;
	}

	public NoozService getNoozService() {
		return mNoozService;
	}

	public RequestQueue getRequestQueue() {
		return mRequestQueue;
	}

	public ImageLoader getImageLoader() {
		return mImageLoader;
	}
}
