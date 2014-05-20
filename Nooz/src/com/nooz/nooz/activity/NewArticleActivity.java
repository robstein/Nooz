package com.nooz.nooz.activity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
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
import com.nooz.nooz.util.Tools;
import com.nooz.nooz.widget.SquareImageView;

public class NewArticleActivity extends BaseActivity implements OnClickListener {

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

	public static final int TOP_BAR_HEIGHT = 61;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_newarticle);
		Bundle bundle = getIntent().getParcelableExtra("bundle");
		mLocation = bundle.getParcelable("location");

		// create spinner
		mCategorySpinner = (Spinner) findViewById(R.id.spinner_choose_category);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_custom, getResources()
				.getStringArray(R.array.category_array));
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		((Spinner) mCategorySpinner).setAdapter(adapter);
		mCategorySpinner.setOnItemSelectedListener(selectCategoryListener);
		mSpinnerCustom = (TextView) findViewById(R.id.customSpinnerItemTextView);

		// move create story details views below the image
		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		mScreenWidthInPixels = size.x;
		mLayoutStoryDetails = (LinearLayout) findViewById(R.id.layout_story_details);
		RelativeLayout.LayoutParams controlLayoutParams = (RelativeLayout.LayoutParams) mLayoutStoryDetails
				.getLayoutParams();
		controlLayoutParams.setMargins(0, (int) Tools.dipToPixels(this, TOP_BAR_HEIGHT) + mScreenWidthInPixels, 0, 0);
		mLayoutStoryDetails.setLayoutParams(controlLayoutParams);

		// display the image
		byte[] image_data = bundle.getByteArray("image");
		// the size of the array is the dimensions of the sub-photo
		int[] pixels = new int[mScreenWidthInPixels * mScreenWidthInPixels];
		// ByteArrayOutputStream bos = new ByteArrayOutputStream();
		Bitmap bitmap = BitmapFactory.decodeByteArray(image_data, 0, image_data.length);
		bitmap.getPixels(pixels, 0, mScreenWidthInPixels, 0, (int) Tools.dipToPixels(this, 81), mScreenWidthInPixels,
				mScreenWidthInPixels);// the stride value is (in my case) the
										// width value
		bitmap = Bitmap.createBitmap(pixels, 0, mScreenWidthInPixels, mScreenWidthInPixels, mScreenWidthInPixels,
				Config.ARGB_8888);// ARGB_8888 is a good quality configuration
		// bitmap.compress(CompressFormat.JPEG, 100, bos);//100 is the best
		// quality possible
		// byte[] square = bos.toByteArray();

		mNewArticleImage = (SquareImageView) findViewById(R.id.new_article_image);
		// Bitmap bmp = BitmapFactory.decodeByteArray(image_data, 0,
		// image_data.length);
		mNewArticleImage.setImageBitmap(bitmap);

		/*
		 * IMPORTANT I need to clip the image. but for now I'm going to save the
		 * whole thing. (more than just the square
		 */

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
		mNoozService.saveStory(category, headline, caption, keywords.get(0), keywords.get(1), keywords.get(2),
				location, shareOnFacebook, shareOnTwitter, shareOnTumblr, onPostNooz);
	}

	TableJsonOperationCallback onPostNooz = new TableJsonOperationCallback() {
		@Override
		public void onCompleted(JsonObject jsonObject, Exception exception, ServiceFilterResponse response) {
			removeSplashLoadingScreen();
			if (exception == null) {
				finish();
			} else {
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
		} else if ("Public Saftey".equals(mSpinnerCategorySelection)) {
			changeThemeColor(chooseCategoryAdapterView, R.drawable.text_button_public_safety,
					R.color.category_public_safety, R.drawable.public_saftey_solid);
		} else { // Arts and Life
			changeThemeColor(chooseCategoryAdapterView, R.drawable.text_button_arts_and_life,
					R.color.category_arts_and_life, R.drawable.arts_and_life_solid);
		}
	}

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

}
