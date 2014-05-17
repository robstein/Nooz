package com.nooz.nooz.util;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.util.Pair;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

import com.google.gson.JsonObject;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.MobileServiceJsonTable;
import com.microsoft.windowsazure.mobileservices.MobileServiceUser;
import com.microsoft.windowsazure.mobileservices.NextServiceFilterCallback;
import com.microsoft.windowsazure.mobileservices.ServiceFilter;
import com.microsoft.windowsazure.mobileservices.ServiceFilterRequest;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponseCallback;
import com.microsoft.windowsazure.mobileservices.TableJsonOperationCallback;
import com.nooz.nooz.activity.LoginActivity;

public class AuthService {

	private static final String TAG = "AuthService";

	private Context mContext;
	private MobileServiceClient mClient;
	private MobileServiceJsonTable mTableAccounts;

	public String getUserName() {
		return mClient.getCurrentUser().toString();
	}

	public AuthService(Context context) {

		mContext = context;
		try {
			mClient = new MobileServiceClient("https://nooz.azure-mobile.net/", "TGeCQCabrSEBxuTBSAuJKqsXUnHBdb80",
					mContext).withFilter(new MyServiceFilter());

			mTableAccounts = mClient.getTable("accounts");
		} catch (MalformedURLException e) {
			Log.e(TAG, "There was an error creating the Mobile Service.  Verify the URL");
		}

	}

	public boolean isUserAuthenticated() {
		SharedPreferences settings = mContext.getSharedPreferences("UserData", Context.MODE_PRIVATE);
		if (settings != null) {
			String userId = settings.getString("userid", null);
			String token = settings.getString("token", null);
			if (userId != null && !userId.equals("")) {
				setUser(userId, token);
				return true;
			}
		}
		return false;
	}

	public void login(String email, String password, TableJsonOperationCallback callback) {
		JsonObject customUser = new JsonObject();
		customUser.addProperty("email", email);
		customUser.addProperty("password", password);
		List<Pair<String, String>> parameters = new ArrayList<Pair<String, String>>();
		parameters.add(new Pair<String, String>("login", "true"));
		mTableAccounts.insert(customUser, parameters, callback);
	}

	public void registerUser(String firstName, String lastName, String email, String password, TableJsonOperationCallback callback) {
		JsonObject newUser = new JsonObject();
		newUser.addProperty("firstName", firstName);
		newUser.addProperty("lastName", lastName);
		newUser.addProperty("email", email);
		newUser.addProperty("password", password);
		mTableAccounts.insert(newUser, callback);
	}

	public void setUserAndSaveDataLogin(JsonObject jsonObject) {
		JsonObject user = jsonObject.getAsJsonObject("user");
		String userId = user.getAsJsonPrimitive("userId").getAsString();
		String token = jsonObject.getAsJsonPrimitive("token").getAsString();
		setUser(userId, token);
		saveUserData();
	}

	private void setUser(String userId, String token) {
		MobileServiceUser user = new MobileServiceUser(userId);
		user.setAuthenticationToken(token);
		mClient.setCurrentUser(user);
	}

	private void saveUserData() {
		SharedPreferences settings = mContext.getSharedPreferences("UserData", Context.MODE_PRIVATE);
		SharedPreferences.Editor preferencesEditor = settings.edit();
		preferencesEditor.putString("userid", mClient.getCurrentUser().getUserId());
		preferencesEditor.putString("token", mClient.getCurrentUser().getAuthenticationToken());
		preferencesEditor.commit();
	}

	private class MyServiceFilter implements ServiceFilter {
		@Override
		public void handleRequest(final ServiceFilterRequest request,
				final NextServiceFilterCallback nextServiceFilterCallback,
				final ServiceFilterResponseCallback responseCallback) {
			if (nextServiceFilterCallback != null) {
				nextServiceFilterCallback.onNext(request, new ServiceFilterResponseCallback() {
					@Override
					public void onResponse(ServiceFilterResponse response, Exception exception) {
						if (exception != null) {
							Log.e(TAG, "MyServiceFilter onResponse Exception: " + exception.getMessage());
						}

						responseCallback.onResponse(response, exception);
					}
				});
			}
		}
	}

	public void logout() {
		// Clear the cookies so they won't auto login to a provider again
		CookieSyncManager.createInstance(mContext);
		CookieManager cookieManager = CookieManager.getInstance();
		cookieManager.removeAllCookie();
		// Clear the user id and token from the shared preferences
		SharedPreferences settings = mContext.getSharedPreferences("UserData", Context.MODE_PRIVATE);
		SharedPreferences.Editor preferencesEditor = settings.edit();
		preferencesEditor.clear();
		preferencesEditor.apply();
		// Clear the user and return to the login activity
		mClient.logout();
		Intent loggedInIntent = new Intent(mContext, LoginActivity.class);
		mContext.startActivity(loggedInIntent);
		((Activity) mContext).finish();
	}

}
