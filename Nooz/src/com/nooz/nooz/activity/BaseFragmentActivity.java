package com.nooz.nooz.activity;

import com.nooz.nooz.NoozApplication;
import com.nooz.nooz.util.NoozService;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public class BaseFragmentActivity extends FragmentActivity {
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
