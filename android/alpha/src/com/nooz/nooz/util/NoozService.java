package com.nooz.nooz.util;

import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.util.Log;
import android.util.Pair;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.microsoft.windowsazure.mobileservices.ApiJsonOperationCallback;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.MobileServiceJsonTable;
import com.microsoft.windowsazure.mobileservices.MobileServiceUser;
import com.microsoft.windowsazure.mobileservices.NextServiceFilterCallback;
import com.microsoft.windowsazure.mobileservices.ServiceFilter;
import com.microsoft.windowsazure.mobileservices.ServiceFilterRequest;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponseCallback;
import com.microsoft.windowsazure.mobileservices.TableJsonOperationCallback;
import com.nooz.nooz.activity.map.FilterSettings;
import com.nooz.nooz.activity.map.MapActivity;
import com.nooz.nooz.model.Comment;
import com.nooz.nooz.model.ProfileInfo;
import com.nooz.nooz.model.Story;

/**
 * Service object which handles all interaction with Azure.
 * 
 * @author Rob Stein
 * 
 */
public class NoozService {

	private static final String TAG = "NoozService";
	private static final String NOOZ_URL = "https://nooz.azure-mobile.net/";
	private static final String NOOZ_KEY = "TGeCQCabrSEBxuTBSAuJKqsXUnHBdb80";

	private Context mContext;
	private MobileServiceClient mClient;
	private MobileServiceJsonTable mTableAccounts;
	private MobileServiceJsonTable mTableStories;
	private MobileServiceJsonTable mTableRelevance;
	private MobileServiceJsonTable mTableBlobs;
	private MobileServiceJsonTable mTableComments;
	private MobileServiceJsonTable mTableCommentsRelevance;

	private List<Story> mLoadedStories;
	private JsonObject mLoadedBlob;
	private HashMap<Integer, JsonObject> mStoryImages;
	private ProfileInfo mLoadedProfileInfo;

	/**
	 * New NoozService.
	 * 
	 * @param context
	 *            current context
	 */
	public NoozService(Context context) {
		mContext = context;
		try {
			mClient = new MobileServiceClient(NOOZ_URL, NOOZ_KEY, mContext).withFilter(new MyServiceFilter());

			mTableAccounts = mClient.getTable("accounts");
			mTableStories = mClient.getTable("stories");
			mTableRelevance = mClient.getTable("relevance");
			mTableBlobs = mClient.getTable("BlobBlobs");
			mTableComments = mClient.getTable("comments");
			mTableCommentsRelevance = mClient.getTable("comments_relevance");

		} catch (MalformedURLException e) {
			Log.e(TAG, "There was an error creating the Mobile Service.  Verify the URL");
		}

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

	/**
	 * Change NoozService context
	 * 
	 * @param context
	 */
	public void setContext(Context context) {
		mContext = context;
		mClient.setContext(context);
	}

	/**
	 * Check if user should be automatically logged in
	 * 
	 * @return true if user is authenticated, false if user should not be logged
	 *         in
	 */
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

	/**
	 * Sends the user's new info to be saved in Azure. Returns with callback.
	 * 
	 * @param name
	 *            new user name
	 * @param email
	 *            new user email
	 * @param password
	 *            new user password
	 * @param callback
	 *            the callback function
	 */
	public void registerUser(String name, String email, String password, TableJsonOperationCallback callback) {
		JsonObject newUser = new JsonObject();
		newUser.addProperty("name", name);
		newUser.addProperty("email", email);
		newUser.addProperty("password", password);
		mTableAccounts.insert(newUser, callback);
	}

	/**
	 * Attempts to authenticated user login info in Azure. Returns with
	 * callback.
	 * 
	 * @param email
	 *            user's email
	 * @param password
	 *            user's password
	 * @param callback
	 *            the callback function
	 */
	public void login(String email, String password, TableJsonOperationCallback callback) {
		JsonObject customUser = new JsonObject();
		customUser.addProperty("email", email);
		customUser.addProperty("password", password);
		List<Pair<String, String>> parameters = new ArrayList<Pair<String, String>>();
		parameters.add(new Pair<String, String>("login", "true"));
		mTableAccounts.insert(customUser, parameters, callback);
	}

	/**
	 * Save login info.
	 * 
	 * @param jsonObject
	 *            Response object
	 */
	public void setUserAndSaveDataLogin(JsonObject jsonObject) {
		JsonObject user = jsonObject.getAsJsonObject("user");
		String userId = user.getAsJsonPrimitive("userId").getAsString();
		String token = jsonObject.getAsJsonPrimitive("token").getAsString();
		String name = jsonObject.getAsJsonPrimitive("name").getAsString();
		setUser(userId, token);
		saveUserData(name);
	}

	private void setUser(String userId, String token) {
		MobileServiceUser user = new MobileServiceUser(userId);
		user.setAuthenticationToken(token);
		mClient.setCurrentUser(user);
	}

	private void saveUserData(String name) {
		SharedPreferences settings = mContext.getSharedPreferences("UserData", Context.MODE_PRIVATE);
		SharedPreferences.Editor preferencesEditor = settings.edit();
		preferencesEditor.putString("userid", getUserId());
		preferencesEditor.putString("token", mClient.getCurrentUser().getAuthenticationToken());
		preferencesEditor.putString("user_name", name);
		preferencesEditor.commit();
	}

	/**
	 * 
	 * @return userId of the current authenticated user
	 */
	public String getUserId() {
		return mClient.getCurrentUser().getUserId();
	}

	public String getUserName() {
		SharedPreferences userData = mContext.getSharedPreferences("UserData", Context.MODE_PRIVATE);
		return userData.getString("user_name", "");
	}

	/**
	 * Logs the user out of Nooz
	 */
	public void logoutFromActivityOnTopOfMap() {
		logout();

		// Clear the user and return to the login activity
		// In order to return to the login activity without leaving some other
		// activity (namely MapActivity) on the Activity stack, we point our
		// intent to MapActivity and have a special logout case in its onCreate
		Intent logoutIntent = new Intent(mContext, MapActivity.class);
		logoutIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		logoutIntent.putExtra("finish", true);
		mContext.startActivity(logoutIntent);
		((Activity) mContext).finish();
	}

	/**
	 * Logs the user out of Nooz
	 */
	public void logoutFromMap() {
		logout();
		((MapActivity) mContext).handleLogoutFromActionBar();
	}

	private void logout() {
		// Clear the cookies so they won't auto login to a provider again
		CookieSyncManager.createInstance(mContext);
		CookieManager cookieManager = CookieManager.getInstance();
		cookieManager.removeAllCookie();

		// Clear the user id and token from the shared preferences
		SharedPreferences settings = mContext.getSharedPreferences("UserData", Context.MODE_PRIVATE);
		SharedPreferences.Editor preferencesEditor = settings.edit();
		preferencesEditor.clear();
		preferencesEditor.apply();

		mClient.logout();
	}

	/**
	 * Sends the user's relevance input to Azure. Returns with callback.
	 * 
	 * @param storyId
	 *            the story id
	 * @param input
	 *            the input: -1, 0, or 1
	 * @param currentLocation
	 * @param callback
	 *            the callback function
	 */
	public void saveRelevanceInput(String storyId, Integer input, Location currentLocation,
			TableJsonOperationCallback callback) {
		JsonObject relevanceinput = new JsonObject();
		relevanceinput.addProperty("story_id", storyId);
		relevanceinput.addProperty("user_id", mClient.getCurrentUser().getUserId());
		relevanceinput.addProperty("input", input);
		relevanceinput.addProperty("lat", currentLocation.getLatitude());
		relevanceinput.addProperty("lng", currentLocation.getLongitude());
		List<Pair<String, String>> parameters = new ArrayList<Pair<String, String>>();
		parameters.add(new Pair<String, String>("postRelevanceInput", "true"));
		mTableRelevance.insert(relevanceinput, parameters, callback);
	}

	/**
	 * Save the upvote or downvote (specified by Integer input) to Azure.
	 * Returns with the callback.
	 * 
	 * @param commentId
	 * @param input
	 * @param onUpvote
	 */
	public void saveCommentRelevanceInput(String commentId, Integer input, TableJsonOperationCallback callback) {
		JsonObject relevanceinput = new JsonObject();
		relevanceinput.addProperty("comment_id", commentId);
		relevanceinput.addProperty("voter_id", mClient.getCurrentUser().getUserId());
		relevanceinput.addProperty("input", input);
		List<Pair<String, String>> parameters = new ArrayList<Pair<String, String>>();
		parameters.add(new Pair<String, String>("postCommentRelevanceInput", "true"));
		mTableCommentsRelevance.insert(relevanceinput, parameters, callback);
	}

	/**
	 * Saves the user's story to Azure. Returns with callback.
	 * <p>
	 * DOES NOT DO ANYTHING WITH AUDIO, PICTURES, OR VIDEO
	 * 
	 * @param medium
	 *            story medium
	 * @param category
	 *            story category
	 * @param headline
	 *            story headline
	 * @param caption
	 *            story caption
	 * @param keyword1
	 *            story keyword 1
	 * @param keyword2
	 *            story keyword 2
	 * @param keyword3
	 *            story keyword 3
	 * @param location
	 *            story location
	 * @param sharefb
	 *            whether to share on facebook or not
	 * @param sharetw
	 *            whether to share on twitter or not
	 * @param sharetu
	 *            whether to share on tumblr or not
	 * @param callback
	 *            the callback function
	 */
	public void saveStory(String medium, String category, String headline, String caption, String keyword1,
			String keyword2, String keyword3, LatLng location, boolean sharefb, boolean sharetw, boolean sharetu,
			TableJsonOperationCallback callback) {
		JsonObject story = new JsonObject();
		story.addProperty("author_id", mClient.getCurrentUser().getUserId());
		story.addProperty("medium", medium);
		story.addProperty("category", category);
		story.addProperty("headline", headline);
		story.addProperty("caption", caption);
		story.addProperty("keyword1", keyword1);
		story.addProperty("keyword2", keyword2);
		story.addProperty("keyword3", keyword3);
		story.addProperty("lat", location.latitude);
		story.addProperty("lng", location.longitude);
		story.addProperty("sharefb", sharefb);
		story.addProperty("sharetw", sharetw);
		story.addProperty("sharetu", sharetu);
		List<Pair<String, String>> parameters = new ArrayList<Pair<String, String>>();
		parameters.add(new Pair<String, String>("postStory", "true"));
		mTableStories.insert(story, parameters, callback);
	}

	/**
	 * 
	 * @return List of Story objects
	 */
	public List<Story> getLoadedStories() {
		return mLoadedStories;
	}

	/**
	 * 
	 * @return the loaded blob
	 */
	public JsonObject getLoadedBlob() {
		return this.mLoadedBlob;
	}

	/**
	 * 
	 * @param i
	 *            index
	 * @return storyimage blob at index i
	 */
	public JsonObject getLoadedStoryImage(int i) {
		return mStoryImages.get(i);
	}

	/**
	 * Queries Azure for stories. A list of Story objects will be loaded into
	 * the NoozService and will be available from getLoadedStories() after a
	 * broadcast intent is sent out.
	 * 
	 * @param bounds
	 *            boundaries of the map
	 * @param filterSettings
	 *            filter settings
	 * @param currentSearchType
	 *            relevant, breaking, or profile
	 * @param authorUserId
	 * @see #getLoadedStories()
	 */
	public void getAllStories(LatLngBounds bounds, FilterSettings filterSettings, SearchType currentSearchType,
			String authorUserId) {
		JsonObject body = new JsonObject();
		body.addProperty("searchType", currentSearchType.toString());
		body.addProperty("user_id", mClient.getCurrentUser().getUserId());
		if (SearchType.PROFILE.equals(currentSearchType)) {
			body.addProperty("author_user_id", authorUserId);
		} else {
			body.addProperty("northeastLat", bounds.northeast.latitude);
			body.addProperty("northeastLng", bounds.northeast.longitude);
			body.addProperty("southwestLat", bounds.southwest.latitude);
			body.addProperty("southwestLng", bounds.southwest.longitude);
			body.addProperty("defaultMedium", filterSettings.DefaultMedium);
			body.addProperty("audio", filterSettings.Audio);
			body.addProperty("picture", filterSettings.Picture);
			body.addProperty("video", filterSettings.Video);
			body.addProperty("defaultCategory", filterSettings.DefaultCategory);
			body.addProperty("people", filterSettings.People);
			body.addProperty("community", filterSettings.Community);
			body.addProperty("sports", filterSettings.Sports);
			body.addProperty("food", filterSettings.Food);
			body.addProperty("publicSaftey", filterSettings.PublicSafety);
			body.addProperty("artsAndLife", filterSettings.ArtsAndLife);
		}
		mClient.invokeApi("getnooz", body, new ApiJsonOperationCallback() {

			@Override
			public void onCompleted(JsonElement jsonObject, Exception exception, ServiceFilterResponse response) {
				if (exception == null) {
					Type listType = new TypeToken<List<Story>>() {
					}.getType();
					List<Story> stories = new Gson().fromJson(jsonObject, listType);

					// In case we don't have user_relevance
					for (Story s : stories) {
						if (s.userRelevance == null)
							s.setUserRelevance(0);
					}

					mLoadedStories = stories;

					Intent broadcast = new Intent();
					broadcast.setAction(GlobalConstant.STORIES_LOADED_ACTION);
					mContext.sendBroadcast(broadcast);

				} else {
					Log.e(TAG, "There was an error retrieving stories: " + exception.getMessage());
				}
			}

		});

	}

	/**
	 * Inserts a blob
	 * 
	 * @param containerName
	 *            requested blob container
	 * @param blobName
	 *            requested blob name
	 */
	public void getSasForNewBlob(String containerName, String blobName) {
		// Create the json Object we'll send over and fill it with the required
		// id property - otherwise we'll get kicked back
		JsonObject blob = new JsonObject();
		blob.addProperty("id", 0);
		// Create parameters to pass in the blob details. We do this with params
		// because it would be stripped out if we put it on the blob object
		List<Pair<String, String>> parameters = new ArrayList<Pair<String, String>>();
		parameters.add(new Pair<String, String>("containerName", containerName));
		parameters.add(new Pair<String, String>("blobName", blobName));
		mTableBlobs.insert(blob, parameters, new TableJsonOperationCallback() {
			@Override
			public void onCompleted(JsonObject jsonObject, Exception exception, ServiceFilterResponse response) {
				if (exception != null) {
					Log.e(TAG, "There was an error inserting a blob: " + exception.getCause().getMessage());
					return;
				}
				// Set the loaded blob
				mLoadedBlob = jsonObject;
				// Broadcast that we are ready to upload the blob data
				Intent broadcast = new Intent();
				broadcast.setAction(GlobalConstant.BLOB_CREATED_ACTION);
				mContext.sendBroadcast(broadcast);
			}
		});
	}

	/**
	 * Loads data from an individual blob. The blob data will be loaded into the
	 * NoozService and will be available from getLoadedBlob() after a broadcast
	 * intent is sent out.
	 * 
	 * @param containerName
	 *            requested blob container
	 * @param blobName
	 *            requested blob name
	 * @see #getLoadedBlob()
	 */
	public void getBlobSas(String containerName, String blobName) {
		// Create the json Object we'll send over and fill it with the required
		// id property - otherwise we'll get kicked back
		JsonObject blob = new JsonObject();
		blob.addProperty("id", 0);
		// Create parameters to pass in the blob details. We do this with params
		// because it would be stripped out if we put it on the blob object
		List<Pair<String, String>> parameters = new ArrayList<Pair<String, String>>();
		parameters.add(new Pair<String, String>("containerName", containerName));
		parameters.add(new Pair<String, String>("blobName", blobName));
		mTableBlobs.insert(blob, parameters, new TableJsonOperationCallback() {
			@Override
			public void onCompleted(JsonObject jsonObject, Exception exception, ServiceFilterResponse response) {
				if (exception != null) {
					Log.e(TAG, "There was an error loading an individual blob: " + exception.getCause().getMessage());
					return;
				}
				// Set the loaded blob
				mLoadedBlob = jsonObject;
				// Broadcast that the blob is loaded
				Intent broadcast = new Intent();
				broadcast.setAction(GlobalConstant.BLOB_LOADED_ACTION);
				mContext.sendBroadcast(broadcast);
			}
		});
	}

	/**
	 * Loads blob data for a list of Story objects. A HashMap<Integer,
	 * JsonObject> of blob data will be loaded into the NoozService and will be
	 * available from getLoadedStoryImage(int) after a broadcast intent is sent
	 * out.
	 * 
	 * @param containerName
	 * @param stories
	 * @see #getLoadedStoryImage(int)
	 */
	public void getBlobSases(String containerName, List<Story> stories) {
		int i = 0;
		mStoryImages = new HashMap<Integer, JsonObject>();
		for (Story s : stories) {
			final int index = i;
			if ("PICTURE".equals(s.medium) || "VIDEO".equals(s.medium)) {
				// Create the json Object
				JsonObject blob = new JsonObject();
				blob.addProperty("id", 0);
				// Create parameters
				List<Pair<String, String>> parameters = new ArrayList<Pair<String, String>>();
				parameters.add(new Pair<String, String>("containerName", containerName));
				parameters.add(new Pair<String, String>("blobName", s.id));
				mTableBlobs.insert(blob, parameters, new TableJsonOperationCallback() {
					@Override
					public void onCompleted(JsonObject jsonObject, Exception exception, ServiceFilterResponse response) {
						if (exception != null) {
							Log.e(TAG, "There was an error loading blob data for a list of stories: "
									+ exception.getCause().getMessage());
							return;
						}
						// Set the loaded blob
						mStoryImages.put(index, jsonObject);
						// Broadcast that the blob is loaded
						Intent broadcast = new Intent();
						broadcast.setAction("storyImage.loaded");
						broadcast.putExtra("index", index);
						mContext.sendBroadcast(broadcast);
					}
				});
			}
			i++;
		}
	}

	public ProfileInfo getLoadedProfileInfo() {
		return mLoadedProfileInfo;
	}

	/**
	 * Queries Azure for profile info which will be loaded into
	 * mLoadedProfileInfo.
	 * 
	 * @param userId
	 */
	public void getProfileInfo(String userId) {
		JsonObject body = new JsonObject();
		body.addProperty("user_id", userId);

		mClient.invokeApi("getprofile", body, new ApiJsonOperationCallback() {

			@Override
			public void onCompleted(JsonElement jsonObject, Exception exception, ServiceFilterResponse response) {
				if (exception == null) {
					ProfileInfo profileInfo = new Gson().fromJson(jsonObject, ProfileInfo.class);
					mLoadedProfileInfo = profileInfo;

					// In case we don't have location
					if (profileInfo.homeLocation == null)
						profileInfo.homeLocation = "";

					Intent broadcast = new Intent();
					broadcast.setAction(GlobalConstant.PROFILE_INFO_LOADED_ACTION);
					mContext.sendBroadcast(broadcast);

				} else {
					Log.e(TAG, "There was an error retrieving profile info: " + exception.getMessage());
				}
			}

		});
	}

	private List<Comment> mLoadedComments;

	public List<Comment> getLoadedComments() {
		return mLoadedComments;
	}

	public void getComments(String id) {
		JsonObject body = new JsonObject();
		body.addProperty("user_id", mClient.getCurrentUser().getUserId());
		body.addProperty("story_id", id);

		mClient.invokeApi("getstorycomments", body, new ApiJsonOperationCallback() {

			@Override
			public void onCompleted(JsonElement jsonObject, Exception exception, ServiceFilterResponse response) {
				if (exception == null) {
					Type listType = new TypeToken<List<Comment>>() {
					}.getType();
					List<Comment> comments = new Gson().fromJson(jsonObject, listType);

					// In case we don't have user's relevance
					for (Comment c : comments) {
						if (c.getCurrentUserVote() == null)
							c.setCurrentUserVote(0);
					}

					mLoadedComments = comments;

					Intent broadcast = new Intent();
					broadcast.setAction(GlobalConstant.COMMENTS_LOADED_ACTION);
					mContext.sendBroadcast(broadcast);

				} else {
					Log.e(TAG, "There was an error retrieving stories: " + exception.getMessage());
				}
			}

		});
	}

	public void postComment(String text, String parentIdOfCommentToBe, String storyId,
			TableJsonOperationCallback callback) {
		JsonObject newComment = new JsonObject();
		newComment.addProperty("commenter_id", mClient.getCurrentUser().getUserId());
		newComment.addProperty("parent_id", parentIdOfCommentToBe);
		newComment.addProperty("story_id", storyId);
		newComment.addProperty("text", text);
		List<Pair<String, String>> parameters = new ArrayList<Pair<String, String>>();
		parameters.add(new Pair<String, String>("postComment", "true"));
		mTableComments.insert(newComment, parameters, callback);
	}

}
