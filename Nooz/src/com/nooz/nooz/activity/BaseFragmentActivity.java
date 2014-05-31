package com.nooz.nooz.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.nooz.nooz.NoozApplication;
import com.nooz.nooz.util.NoozService;

/**
 * 
 * @author Rob Stein
 *
 */
public class BaseFragmentActivity extends FragmentActivity {
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
}
