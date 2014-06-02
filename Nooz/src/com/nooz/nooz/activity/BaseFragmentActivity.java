package com.nooz.nooz.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.nooz.nooz.NoozApplication;
import com.nooz.nooz.util.BlobReceiver;
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		NoozApplication myApp = (NoozApplication) getApplication();
		myApp.setCurrentActivity(this);
		mNoozService = myApp.getNoozService();
		mNoozService.setContext(this);

		mContext = this;
	}
	
	

	public NoozService getNoozService() {
		return mNoozService;
	}



	private static final String STORIES_TEXT_LOADED = "stories.loaded";
	private static final String STORY_IMAGE_LOADED = "storyImage.loaded";

}
