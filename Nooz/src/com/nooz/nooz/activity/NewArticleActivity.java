package com.nooz.nooz.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.nooz.nooz.R;
import com.nooz.nooz.util.Tools;
import com.nooz.nooz.widget.SquareImageView;

public class NewArticleActivity extends Activity {

	private LinearLayout mLayoutStoryDetails;
	private SquareImageView mNewArticleImage;
	private Spinner mCategorySpinner;
	private TextView mTextButtonBreak;
	private TextView mSpinnerCustom;
	private ImageView mNewArticleLogo;

	private int mScreenWidthInPixels;
	protected String mSpinnerCategorySelection;

	public static final int TOP_BAR_HEIGHT = 61;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_newarticle);

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
		byte[] image_data = getIntent().getByteArrayExtra("image");
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
			changeThemeColor(chooseCategoryAdapterView, R.drawable.text_button, R.color.category_color_black, R.drawable.cancel_new_article);
		} else if ("People".equals(mSpinnerCategorySelection)) {
			changeThemeColor(chooseCategoryAdapterView, R.drawable.text_button_people, R.color.category_people, R.drawable.people_solid);
		} else if ("Community".equals(mSpinnerCategorySelection)) {
			changeThemeColor(chooseCategoryAdapterView, R.drawable.text_button_community, R.color.category_community, R.drawable.community_solid);
		} else if ("Sports".equals(mSpinnerCategorySelection)) {
			changeThemeColor(chooseCategoryAdapterView, R.drawable.text_button_sports, R.color.category_sports, R.drawable.sports_solid);
		} else if ("Food".equals(mSpinnerCategorySelection)) {
			changeThemeColor(chooseCategoryAdapterView, R.drawable.text_button_food, R.color.category_food, R.drawable.food_solid);
		} else if ("Public Saftey".equals(mSpinnerCategorySelection)) {
			changeThemeColor(chooseCategoryAdapterView, R.drawable.text_button_public_safety, R.color.category_public_safety, R.drawable.public_saftey_solid);
		} else { // Arts and Life
			changeThemeColor(chooseCategoryAdapterView, R.drawable.text_button_arts_and_life, R.color.category_arts_and_life, R.drawable.arts_and_life_solid);
		}
	}

	@SuppressLint("NewApi")
	private void changeThemeColor(AdapterView<?> chooseCategoryAdapterView, int drawableButtonBorders, int textColor, int logo) {
		int currentapiVersion = android.os.Build.VERSION.SDK_INT;
		if (currentapiVersion >= android.os.Build.VERSION_CODES.JELLY_BEAN){
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
