package com.nooz.nooz.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.google.gson.JsonObject;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.TableJsonOperationCallback;
import com.nooz.nooz.R;
import com.nooz.nooz.util.Alert;

public class LoginActivity extends BaseActivity implements OnClickListener {

	private static final String TAG = "LoginActivity";

	private LinearLayout mLinearLayoutSignup;
	private LinearLayout mLinearLayoutLogin;
	private LinearLayout mLinearLayoutLoginActivityTop;
	private Button mButtonSignup;
	private Button mButtonLogin;
	private EditText mInputTextLoginEmail;
	private EditText mInputTextLoginPassword;
	private EditText mInputTextSignupNameFirst;
	private EditText mInputTextSignupNameLast;
	private EditText mInputTextSignupEmail;
	private EditText mInputTextSignupPassword;
	private EditText mInputTextSignupPasswordConfirm;
	private Button mButtonBackLogin;
	private Button mButtonLoginSubmit;
	private Button mButtonBackSignup;
	private Button mButtonSignupSubmit;

	private Animation mSlideInBottom;
	private Animation mSlideOutBottom;

	ProgressDialog progress;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		mLinearLayoutSignup = (LinearLayout) findViewById(R.id.linearlayout_signup);
		mLinearLayoutLogin = (LinearLayout) findViewById(R.id.linearlayout_login);
		mLinearLayoutLoginActivityTop = (LinearLayout) findViewById(R.id.linear_layout_login_activity_top);
		mButtonSignup = (Button) findViewById(R.id.button_signup);
		mButtonLogin = (Button) findViewById(R.id.button_login);
		mInputTextLoginEmail = (EditText) findViewById(R.id.input_text_login_email);
		mInputTextLoginPassword = (EditText) findViewById(R.id.input_text_login_password);
		mInputTextSignupNameFirst = (EditText) findViewById(R.id.input_text_signup_name_first);
		mInputTextSignupNameLast = (EditText) findViewById(R.id.input_text_signup_name_last);
		mInputTextSignupEmail = (EditText) findViewById(R.id.input_text_signup_email);
		mInputTextSignupPassword = (EditText) findViewById(R.id.input_text_signup_password);
		mInputTextSignupPasswordConfirm = (EditText) findViewById(R.id.text_input_signup_password_confirm);
		mButtonBackLogin = (Button) findViewById(R.id.button_back_login);
		mButtonLoginSubmit = (Button) findViewById(R.id.button_login_submit);
		mButtonBackSignup = (Button) findViewById(R.id.button_back_signup);
		mButtonSignupSubmit = (Button) findViewById(R.id.button_signup_submit);

		mSlideInBottom = AnimationUtils.loadAnimation(this, R.anim.slide_in_bottom);
		mSlideOutBottom = AnimationUtils.loadAnimation(this, R.anim.slide_out_bottom);

		mButtonSignup.setOnClickListener(this);
		mButtonLogin.setOnClickListener(this);
		mButtonBackLogin.setOnClickListener(this);
		mButtonLoginSubmit.setOnClickListener(this);
		mButtonBackSignup.setOnClickListener(this);
		mButtonSignupSubmit.setOnClickListener(this);

		// If user is already authenticated, bypass logging in
		if (mNoozService.isUserAuthenticated()) {
			Intent loggedInIntent = new Intent(getApplicationContext(), MapActivity.class);
			startActivity(loggedInIntent);
			finish();
		}

	}

	private void splashLoadingScreen() {
		progress = ProgressDialog.show(this, "Logging in", "Please wait", true);
	}

	private void removeSplashLoadingScreen() {
		progress.dismiss();
	}

	private void showSignupForms() {
		mLinearLayoutLoginActivityTop.setVisibility(View.INVISIBLE);
		mLinearLayoutLoginActivityTop.startAnimation(mSlideOutBottom);
		mLinearLayoutSignup.setVisibility(View.VISIBLE);
		mLinearLayoutSignup.startAnimation(mSlideInBottom);
		mInputTextSignupNameFirst.requestFocus();
	}

	private void showLoginForms() {
		mLinearLayoutLoginActivityTop.setVisibility(View.INVISIBLE);
		mLinearLayoutLoginActivityTop.startAnimation(mSlideOutBottom);
		mLinearLayoutLogin.setVisibility(View.VISIBLE);
		mLinearLayoutLogin.startAnimation(mSlideInBottom);
		mInputTextLoginEmail.requestFocus();
	}

	private void goBackFromLogin() {
		mLinearLayoutLogin.setVisibility(View.INVISIBLE);
		mLinearLayoutLogin.startAnimation(mSlideOutBottom);
		mLinearLayoutLoginActivityTop.setVisibility(View.VISIBLE);
		mLinearLayoutLoginActivityTop.startAnimation(mSlideInBottom);
	}

	private void submitLogin() {
		if (mInputTextLoginPassword.getText().toString().equals("")
				|| mInputTextLoginEmail.getText().toString().equals("")) {
			Log.w(TAG, "Email or password not entered");
			Alert.createAndShowDialog("Email or password not entered", "Oops!", mButtonLoginSubmit.getContext());
			return;
		}
		splashLoadingScreen();
		mNoozService.login(mInputTextLoginEmail.getText().toString(), mInputTextLoginPassword.getText().toString(),
				onLogin);
	}

	private void goBackFromSignup() {
		mLinearLayoutSignup.setVisibility(View.INVISIBLE);
		mLinearLayoutSignup.startAnimation(mSlideOutBottom);
		mLinearLayoutLoginActivityTop.setVisibility(View.VISIBLE);
		mLinearLayoutLoginActivityTop.startAnimation(mSlideInBottom);
	}

	private void submitSignup() {
		// TODO we should be showing something to the user
		if (mInputTextSignupEmail.getText().toString().equals("")
				|| mInputTextSignupPassword.getText().toString().equals("")
				|| mInputTextSignupPasswordConfirm.getText().toString().equals("")
				|| mInputTextSignupNameFirst.getText().toString().equals("")
				|| mInputTextSignupNameLast.getText().toString().equals("")) {
			Log.w(TAG, "You must enter all fields to register");
			Alert.createAndShowDialog("You must enter all fields to register", "Oops!", this);
			return;
		} else if (!mInputTextSignupPassword.getText().toString()
				.equals(mInputTextSignupPasswordConfirm.getText().toString())) {
			Log.w(TAG, "The passwords you've entered don't match");
			Alert.createAndShowDialog("The passwords you've entered don't match", "Oops!", this);
			return;
		} else {
			splashLoadingScreen();
			mNoozService.registerUser(mInputTextSignupNameFirst.getText().toString(), mInputTextSignupNameLast
					.getText().toString(), mInputTextSignupEmail.getText().toString(), mInputTextSignupPassword
					.getText().toString(), onRegisterUser);
		}
	}

	private TableJsonOperationCallback onRegisterUser = new TableJsonOperationCallback() {
		@Override
		public void onCompleted(JsonObject jsonObject, Exception exception, ServiceFilterResponse response) {
			if (exception == null) {
				String email = jsonObject.getAsJsonPrimitive("email").getAsString();
				String password = jsonObject.getAsJsonPrimitive("password").getAsString();
				mNoozService.login(email, password, onLogin);
			} else {
				Log.e(TAG, "There was an error registering the user: " + exception.getMessage());
				Alert.createAndShowDialog(exception, "Error", mContext);
				removeSplashLoadingScreen();
			}
		}

	};

	private TableJsonOperationCallback onLogin = new TableJsonOperationCallback() {
		@Override
		public void onCompleted(JsonObject jsonObject, Exception exception, ServiceFilterResponse response) {
			if (exception == null) {
				// If they've registered successfully, we'll save
				// and set the user and then show the logged in
				// activity
				mNoozService.setUserAndSaveDataLogin(jsonObject);
				Intent loggedInIntent = new Intent(getApplicationContext(), MapActivity.class);
				startActivity(loggedInIntent);
				finish();
			} else {
				Log.e(TAG, "Error loggin in: " + exception.getMessage());
				Alert.createAndShowDialog(exception, "Error", mContext);
				removeSplashLoadingScreen();
			}
		}
	};

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.button_signup:
			showSignupForms();
			break;
		case R.id.button_login:
			showLoginForms();
			break;
		case R.id.button_back_login:
			goBackFromLogin();
			break;
		case R.id.button_login_submit:
			submitLogin();
			break;
		case R.id.button_back_signup:
			goBackFromSignup();
			break;
		case R.id.button_signup_submit:
			submitSignup();
			break;

		}
	}

}
