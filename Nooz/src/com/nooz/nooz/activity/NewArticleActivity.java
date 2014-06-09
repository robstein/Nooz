package com.nooz.nooz.activity;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.JsonObject;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.TableJsonOperationCallback;
import com.nooz.nooz.R;
import com.nooz.nooz.util.Alert;
import com.nooz.nooz.util.GlobalConstant;
import com.nooz.nooz.widget.SquareImageView;

/**
 * 
 * @author Rob Stein
 * 
 */
public class NewArticleActivity extends BaseFragmentActivity implements OnClickListener {

	private static final String TAG = "NewArticleActivity";

	private LinearLayout mLayoutStoryDetails;
	private SquareImageView mNewArticleImage;
	private Spinner mCategorySpinner;
	private TextView mTextButtonBreak;
	private TextView mSpinnerCustom;
	private ImageView mNewArticleLogo;
	private ImageView mTogglerShareFacebook;
	private ImageView mTogglerShareTwitter;
	private ImageView mTogglerShareTumblr;

	private int mScreenWidthInPixels;

	protected String mSpinnerCategorySelection;
	private EditText mInputTextHeadline;
	private EditText mInputTextCaption;
	private EditText mInputTextKeywords;
	private Location mLocation;
	private boolean mShareOnFacebook = false;
	private boolean mShareOnTwitter = false;
	private boolean mShareOnTumblr = false;

	ProgressDialog progress;

	byte[] mImageData;
	Bitmap mBitmap;

	private String mMedium;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_newarticle);
		Bundle bundle = getIntent().getParcelableExtra("bundle");
		mLocation = bundle.getParcelable("location");
		mMedium = (String) bundle.getCharSequence("medium");

		// create spinner
		mCategorySpinner = (Spinner) findViewById(R.id.spinner_choose_category);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_custom, getResources()
				.getStringArray(R.array.category_array));
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		((Spinner) mCategorySpinner).setAdapter(adapter);
		mCategorySpinner.setOnItemSelectedListener(selectCategoryListener);
		mSpinnerCustom = (TextView) findViewById(R.id.customSpinnerItemTextView);

		// display the image
		mNewArticleImage = (SquareImageView) findViewById(R.id.new_article_image);
		if ("AUDIO".equals(mMedium)) {
			mNewArticleImage.setImageDrawable(getResources().getDrawable(R.drawable.micbig));
		}
		if ("PICTURE".equals(mMedium)) {
			File imgFile = new File(getFilesDir().getAbsolutePath() + "/picture.jpg");
			if (imgFile.exists()) {
				Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
				mNewArticleImage.setImageBitmap(myBitmap);
			}
		}
		if ("VIDEO".equals(mMedium)) {

		}

		// Make the square image fill the width of the screen
		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		mScreenWidthInPixels = size.x;
		RelativeLayout.LayoutParams imageLayoutParams = (RelativeLayout.LayoutParams) mNewArticleImage
				.getLayoutParams();
		imageLayoutParams.height = mScreenWidthInPixels;
		imageLayoutParams.width = mScreenWidthInPixels;
		mNewArticleImage.setLayoutParams(imageLayoutParams);

		//
		mTextButtonBreak = (TextView) findViewById(R.id.btn_break_post);
		mNewArticleLogo = (ImageView) findViewById(R.id.btn_new_article_logo);
		mTogglerShareFacebook = (ImageView) findViewById(R.id.btn_share_facebook);
		mTogglerShareTwitter = (ImageView) findViewById(R.id.btn_share_twitter);
		mTogglerShareTumblr = (ImageView) findViewById(R.id.btn_share_tumblr);

		mTextButtonBreak.setOnClickListener(this);
		mNewArticleLogo.setOnClickListener(this);
		mTogglerShareFacebook.setOnClickListener(this);
		mTogglerShareTwitter.setOnClickListener(this);
		mTogglerShareTumblr.setOnClickListener(this);

		mInputTextHeadline = (EditText) findViewById(R.id.input_headline);
		mInputTextHeadline.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (mInputTextHeadline.getText().toString().length() > 0) {
					mInputTextHeadline.setSelection(0);
				}
			}
		});
		mInputTextCaption = (EditText) findViewById(R.id.input_caption);
		mInputTextKeywords = (EditText) findViewById(R.id.input_keywords);
	}

	private void splashLoadingScreen() {
		progress = ProgressDialog.show(this, "Submitting to Nooz", "Please wait", true);
	}

	private void removeSplashLoadingScreen() {
		progress.dismiss();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_break_post:
			postNooz();
			break;
		case R.id.btn_new_article_logo:
			break;
		case R.id.btn_share_facebook:
			toggleFacebookButton();
			break;
		case R.id.btn_share_twitter:
			toggleTwitterButton();
			break;
		case R.id.btn_share_tumblr:
			toggleTumblrButton();
			break;
		}
	}

	private void postNooz() {
		String category = mSpinnerCategorySelection;
		String headline = mInputTextHeadline.getText().toString();
		String caption = mInputTextCaption.getText().toString();
		List<String> keywords = getKeywordList(mInputTextKeywords.getText().toString());
		LatLng location = new LatLng(mLocation.getLatitude(), mLocation.getLongitude());
		boolean shareOnFacebook = mShareOnFacebook;
		boolean shareOnTwitter = mShareOnTwitter;
		boolean shareOnTumblr = mShareOnTumblr;

		if ("Choose Category".equals(category)) {
			Alert.createAndShowDialog("Please choose a category", "Invalid story", this);
			return;
		}
		if ("".equals(headline)) {
			Alert.createAndShowDialog("Please write a headline", "Invalid story", this);
			return;
		}
		if ("".equals(caption)) {
			Alert.createAndShowDialog("Please add a caption", "Invalid story", this);
			return;
		}
		if ("".equals(keywords.get(0))) {
			Alert.createAndShowDialog("Please add a keyword", "Invalid story", this);
			return;
		}

		splashLoadingScreen();
		mNoozService.saveStory(mMedium, category, headline, caption, keywords.get(0), keywords.get(1), keywords.get(2),
				location, shareOnFacebook, shareOnTwitter, shareOnTumblr, onPostNooz);
	}

	TableJsonOperationCallback onPostNooz = new TableJsonOperationCallback() {
		@Override
		public void onCompleted(JsonObject jsonObject, Exception exception, ServiceFilterResponse response) {
			if (exception == null) {
				String story_id = jsonObject.getAsJsonPrimitive("id").getAsString();
				mNoozService.getSasForNewBlob(GlobalConstant.CONTAINER_NAME, story_id);
			} else {
				removeSplashLoadingScreen();
				Log.e(TAG, "Error posting story in: " + exception.getMessage());
				Alert.createAndShowDialog(exception, "Error", mContext);
			}
		}
	};

	private List<String> getKeywordList(String string) {
		List<String> items = new ArrayList<String>(Arrays.asList(string.split("\\s*,\\s*")));
		while (items.size() < 3) {
			items.add("null");
		}
		return items;
	}

	private void toggleFacebookButton() {
		if (mShareOnFacebook) {
			mShareOnFacebook = false;
			mTogglerShareFacebook.setImageResource(R.drawable.share_facebook);
		} else {
			mShareOnFacebook = true;
			mTogglerShareFacebook.setImageResource(R.drawable.share_facebook_pressed);
		}
	}

	private void toggleTwitterButton() {
		if (mShareOnTwitter) {
			mShareOnTwitter = false;
			mTogglerShareTwitter.setImageResource(R.drawable.share_twitter);
		} else {
			mShareOnTwitter = true;
			mTogglerShareTwitter.setImageResource(R.drawable.share_twitter_pressed);
		}
	}

	private void toggleTumblrButton() {
		if (mShareOnTumblr) {
			mShareOnTumblr = false;
			mTogglerShareTumblr.setImageResource(R.drawable.share_tumblr);
		} else {
			mShareOnTumblr = true;
			mTogglerShareTumblr.setImageResource(R.drawable.share_tumblr_pressed);
		}
	}

	OnItemSelectedListener selectCategoryListener = new OnItemSelectedListener() {
		public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
			mSpinnerCategorySelection = (String) parent.getItemAtPosition(pos);
			handleCategoryChange(parent);
		}

		public void onNothingSelected(AdapterView<?> parent) {
		}
	};

	private void handleCategoryChange(AdapterView<?> chooseCategoryAdapterView) {
		if ("Choose Category".equals(mSpinnerCategorySelection)) {
			changeThemeColor(chooseCategoryAdapterView, R.drawable.text_button, R.color.category_color_black,
					R.drawable.cancel_new_article);
		} else if ("People".equals(mSpinnerCategorySelection)) {
			changeThemeColor(chooseCategoryAdapterView, R.drawable.text_button_people, R.color.category_people,
					R.drawable.people_solid);
		} else if ("Community".equals(mSpinnerCategorySelection)) {
			changeThemeColor(chooseCategoryAdapterView, R.drawable.text_button_community, R.color.category_community,
					R.drawable.community_solid);
		} else if ("Sports".equals(mSpinnerCategorySelection)) {
			changeThemeColor(chooseCategoryAdapterView, R.drawable.text_button_sports, R.color.category_sports,
					R.drawable.sports_solid);
		} else if ("Food".equals(mSpinnerCategorySelection)) {
			changeThemeColor(chooseCategoryAdapterView, R.drawable.text_button_food, R.color.category_food,
					R.drawable.food_solid);
		} else if ("Public Safety".equals(mSpinnerCategorySelection)) {
			changeThemeColor(chooseCategoryAdapterView, R.drawable.text_button_public_safety,
					R.color.category_public_safety, R.drawable.public_saftey_solid);
		} else { // Arts and Life
			changeThemeColor(chooseCategoryAdapterView, R.drawable.text_button_arts_and_life,
					R.color.category_arts_and_life, R.drawable.arts_and_life_solid);
		}
	}

	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	private void changeThemeColor(AdapterView<?> chooseCategoryAdapterView, int drawableButtonBorders, int textColor,
			int logo) {
		int currentapiVersion = android.os.Build.VERSION.SDK_INT;
		if (currentapiVersion >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
			mCategorySpinner.setBackground(getResources().getDrawable(drawableButtonBorders));
			mTextButtonBreak.setBackground(getResources().getDrawable(drawableButtonBorders));
		} else {
			mCategorySpinner.setBackgroundDrawable(getResources().getDrawable(drawableButtonBorders));
			mTextButtonBreak.setBackgroundDrawable(getResources().getDrawable(drawableButtonBorders));
		}
		mTextButtonBreak.setTextColor(getResources().getColor(textColor));
		((TextView) chooseCategoryAdapterView.getChildAt(0)).setTextColor(getResources().getColor(textColor));
		mNewArticleLogo.setImageResource(logo);

	}

	/* ***** Blob uploading ***** */

	/***
	 * Register for broadcasts
	 */
	@Override
	protected void onResume() {
		IntentFilter filter = new IntentFilter();
		filter.addAction("blob.created");
		registerReceiver(receiver, filter);
		super.onResume();
	}

	/***
	 * Unregister for broadcasts
	 */
	@Override
	protected void onPause() {
		unregisterReceiver(receiver);
		super.onPause();
	}

	/***
	 * Broadcast receiver handles a new blob being created
	 */
	private BroadcastReceiver receiver = new BroadcastReceiver() {
		public void onReceive(Context context, android.content.Intent intent) {
			String intentAction = intent.getAction();
			if (GlobalConstant.BLOB_CREATED_ACTION.equals(intentAction)) {
				// If a blob has been created, upload the image
				JsonObject blob = mNoozService.getLoadedBlob();
				String sasUrl = blob.getAsJsonPrimitive("sasUrl").toString();
				if ("AUDIO".equals(mMedium)) {
					(new AudioUploaderTask(sasUrl)).execute();
				}
				if ("PICTURE".equals(mMedium)) {
					(new ImageUploaderTask(sasUrl)).execute();
				}
				if ("VIDEO".equals(mMedium)) {

				}
			}
		}
	};

	/***
	 * Handles uploading an audio file to a specified url
	 */
	class AudioUploaderTask extends AsyncTask<Void, Void, Boolean> {
		private String mUrl;

		public AudioUploaderTask(String url) {
			mUrl = url;
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			try {
				String path = mContext.getFilesDir().getAbsolutePath().toString();
				String filename = "audio.3gp";
				File file = new File(path, filename);
				FileInputStream fis = new FileInputStream(file);
				int bytesRead = 0;
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				byte[] b = new byte[1024];
				while ((bytesRead = fis.read(b)) != -1) {
					bos.write(b, 0, bytesRead);
				}
				byte[] bytes = bos.toByteArray();
				fis.close();
				// Post our image data (byte array) to the server
				URL url = new URL(mUrl.replace("\"", ""));
				HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
				urlConnection.setDoOutput(true);
				urlConnection.setRequestMethod("PUT");
				urlConnection.addRequestProperty("Content-Type", "audio/3gpp");
				urlConnection.setRequestProperty("Content-Length", "" + bytes.length);
				// Write image data to server
				DataOutputStream wr = new DataOutputStream(urlConnection.getOutputStream());
				wr.write(bytes);
				wr.flush();
				wr.close();
				int response = urlConnection.getResponseCode();
				// If we successfully uploaded, return true
				if (response == 201 && urlConnection.getResponseMessage().equals("Created")) {
					return true;
				}
			} catch (Exception ex) {
				Log.e(TAG, ex.getMessage());
			}
			return false;
		}

		@Override
		protected void onPostExecute(Boolean uploaded) {
			if (uploaded) {
				removeSplashLoadingScreen();
				Log.d(TAG, "Blob uploaded");
				finish();
			}
		}
	}

	/***
	 * Handles uploading an image to a specified url
	 */
	class ImageUploaderTask extends AsyncTask<Void, Void, Boolean> {
		private String mUrl;

		public ImageUploaderTask(String url) {
			mUrl = url;
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			try {
				String path = mContext.getFilesDir().getAbsolutePath().toString();
				String filename = "picture.jpg";
				File file = new File(path, filename);
				FileInputStream fis = new FileInputStream(file);
				int bytesRead = 0;
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				byte[] b = new byte[1024];
				while ((bytesRead = fis.read(b)) != -1) {
					bos.write(b, 0, bytesRead);
				}
				byte[] bytes = bos.toByteArray();
				fis.close();
				// Post our image data (byte array) to the server
				URL url = new URL(mUrl.replace("\"", ""));
				HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
				urlConnection.setDoOutput(true);
				urlConnection.setRequestMethod("PUT");
				urlConnection.addRequestProperty("Content-Type", "image/jpeg");
				urlConnection.setRequestProperty("Content-Length", "" + bytes.length);
				// Write image data to server
				DataOutputStream wr = new DataOutputStream(urlConnection.getOutputStream());
				wr.write(bytes);
				wr.flush();
				wr.close();
				int response = urlConnection.getResponseCode();
				// If we successfully uploaded, return true
				if (response == 201 && urlConnection.getResponseMessage().equals("Created")) {
					return true;
				}
			} catch (Exception ex) {
				Log.e(TAG, ex.getMessage());
			}
			return false;
		}

		@Override
		protected void onPostExecute(Boolean uploaded) {
			if (uploaded) {
				removeSplashLoadingScreen();
				Log.d(TAG, "Blob uploaded");
				finish();
			} else {

			}
		}
	}

}