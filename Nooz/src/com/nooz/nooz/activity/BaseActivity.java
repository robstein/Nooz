package com.nooz.nooz.activity;

import android.app.Activity;
import android.os.Bundle;

import com.nooz.nooz.NoozApplication;
import com.nooz.nooz.util.NoozService;

public class BaseActivity extends Activity {
	protected NoozService mNoozService;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		NoozApplication myApp = (NoozApplication) getApplication();
		myApp.setCurrentActivity(this);
		mNoozService = myApp.getNoozService();
		mNoozService.setContext(this);
	}
}
