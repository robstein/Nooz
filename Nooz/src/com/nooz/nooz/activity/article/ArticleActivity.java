package com.nooz.nooz.activity.article;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Point;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.NetworkImageView;
import com.google.gson.JsonObject;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.TableJsonOperationCallback;
import com.nooz.nooz.R;
import com.nooz.nooz.activity.BaseLocationFragmentActivity;
import com.nooz.nooz.activity.profile.ProfileLauncher;
import com.nooz.nooz.model.Comment;
import com.nooz.nooz.model.Story;
import com.nooz.nooz.util.Alert;
import com.nooz.nooz.util.CategoryResourceHelper;
import com.nooz.nooz.util.GlobalConstant;

/**
 * 
 * @author Rob Stein
 * 
 */
public class ArticleActivity extends BaseLocationFragmentActivity implements OnClickListener {
	private static final String TAG = "ArticleActivity";

	private ImageView mArticleCategoryLogo;
	private TextView mArticleCategory;
	private TextView mArticleDate;
	private ImageView mArticleInfo;
	NetworkImageView mArticleImage;
	private RelativeLayout mArticleHeader;
	private TextView mHeadline;
	private NetworkImageView mAuthorPicture;
	private TextView mAuthor;
	private TextView mCaption;
	private LinearLayout mButtonRelevant;
	private TextView mRelevanceScore;
	private TextView mRelevanceLabel;
	private LinearLayout mButtonIrrelevant;
	private TextView mIrrelevanceScore;
	private TextView mIrrelevanceLabel;
	private ImageView mButtonComments;
	ExpandableListView mLayoutComments;
	Button mButtonPostComment;
	EditText mInputTextComment;

	boolean mLoaded;
	Story mStory;
	private Boolean mRelevant;
	private Boolean mIrrelevant;
	private Integer mScoreRel;
	private Integer mScoreIrr;
	private int mScreenWidthInPixels;
	String parentIdOfCommentToBe;

	/***
	 * Broadcast mReceiver handles a blob being loaded
	 */
	private ArticleBroadcastReceiver mReceiver;

	ArticleDataController mArticleDataController;

	ArticleModule mMediaModule;

	/* ***** ACTIVITY SETUP BEGIN ***** */

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initFields();
		initViews();
		initViewListeners();
		initScreenMeasurements();
		initPictureParameters();
		initBundleParameters();
		initStory();
		initModule();
	}

	private void initFields() {
		mReceiver = new ArticleBroadcastReceiver();
		mArticleDataController = new ArticleDataController(this);
		mLoaded = false;
		mRelevant = false;
		mIrrelevant = false;
		mScoreRel = 0;
		mScoreIrr = 0;
		parentIdOfCommentToBe = Comment.NONE;
	}

	private void initViews() {
		setContentView(R.layout.activity_article);
		mArticleCategoryLogo = (ImageView) findViewById(R.id.article_category_logo);
		mArticleCategory = (TextView) findViewById(R.id.article_category);
		mArticleDate = (TextView) findViewById(R.id.article_date);
		mArticleInfo = (ImageView) findViewById(R.id.article_info);
		mArticleImage = (NetworkImageView) findViewById(R.id.article_image);
		mArticleHeader = (RelativeLayout) findViewById(R.id.article_header);
		mHeadline = (TextView) findViewById(R.id.headline);
		mAuthorPicture = (NetworkImageView) findViewById(R.id.author_picture);
		mAuthor = (TextView) findViewById(R.id.author);
		mCaption = (TextView) findViewById(R.id.caption);
		mButtonRelevant = (LinearLayout) findViewById(R.id.button_relevant);
		mRelevanceScore = (TextView) findViewById(R.id.relevance_score);
		mRelevanceLabel = (TextView) findViewById(R.id.relevance_label);
		mButtonIrrelevant = (LinearLayout) findViewById(R.id.button_irrelevant);
		mIrrelevanceScore = (TextView) findViewById(R.id.irrelevance_score);
		mIrrelevanceLabel = (TextView) findViewById(R.id.irrelevance_label);
		mButtonComments = (ImageView) findViewById(R.id.btn_comments);
		mLayoutComments = (ExpandableListView) findViewById(R.id.comments);
		mButtonPostComment = (Button) findViewById(R.id.btn_post_comment);
		mInputTextComment = (EditText) findViewById(R.id.input_text_comment);
	}

	private void initViewListeners() {
		mArticleInfo.setOnClickListener(this);
		mArticleImage.setOnClickListener(this);
		mAuthorPicture.setOnClickListener(this);
		mAuthor.setOnClickListener(this);
		mButtonRelevant.setOnClickListener(this);
		mButtonIrrelevant.setOnClickListener(this);
		mButtonComments.setOnClickListener(this);
		mButtonPostComment.setOnClickListener(this);
	}

	private void initScreenMeasurements() {
		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		mScreenWidthInPixels = size.x;
	}

	/**
	 * Make the article picture the right size
	 */
	private void initPictureParameters() {
		RelativeLayout.LayoutParams imageLayoutParams = (RelativeLayout.LayoutParams) mArticleImage.getLayoutParams();
		imageLayoutParams.height = mScreenWidthInPixels;
		imageLayoutParams.width = mScreenWidthInPixels;
		mArticleImage.setLayoutParams(imageLayoutParams);
	}

	private void initBundleParameters() {
		Bundle bundle = getIntent().getParcelableExtra("bundle");
		mStory = bundle.getParcelable("story");
	}

	/**
	 * Uses the input parcelable story to populate the whole activity's layout.
	 * However, the media is not loaded until a broadcast receiver is registered
	 * to receive isLoaded intents.
	 */
	private void initStory() {
		drawArticleHeader();
		drawArticleHeadlineAuthorAndText();
		drawArticleRelevance();
		drawArticleComments();
	}

	private void drawArticleHeader() {
		mArticleCategoryLogo.setImageResource(CategoryResourceHelper.getLogoByCategory(mStory.category));
		mArticleCategory.setText(mStory.category);
		mArticleCategory.setTextColor(CategoryResourceHelper.getColorByCategory(mStory.category));
		mArticleDate.setText(getDate(mStory.__createdAt));
		mArticleDate.setTextColor(CategoryResourceHelper.getColorByCategory(mStory.category));
		// mArticleInfo.setImageResource(CategoryResourceHelper.getInfoByCategory(mStory.category));
	}

	private String getDate(String __createdAt) {
		SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy'-'MM'-'dd'T'HH':'mm':'ss", Locale.US);
		inputFormat.setTimeZone(TimeZone.getTimeZone("Etc/UTC"));
		SimpleDateFormat outputFormat = new SimpleDateFormat("MMM. dd, yyyy", java.util.Locale.getDefault());

		// Adjust locale and zone appropriately
		try {
			Date date = inputFormat.parse(__createdAt);
			return outputFormat.format(date);
		} catch (ParseException e) {
			Log.e(TAG, "Error parsing date: " + e.getCause().getMessage());
			return "";
		}
	}

	private void drawArticleHeadlineAuthorAndText() {
		mHeadline.setText(mStory.headline);
		// TODO Set mAuthorPicture
		mAuthor.setText(mStory.firstName + " " + mStory.lastName);
		mCaption.setText(mStory.caption);
	}

	@SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	private void drawArticleRelevance() {
		// Draw buttons
		int currentapiVersion = android.os.Build.VERSION.SDK_INT;
		if (currentapiVersion >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
			mButtonRelevant.setBackground(getResources().getDrawable(
					CategoryResourceHelper.getButtonBorderByCategory(mStory.category)));
			mButtonIrrelevant.setBackground(getResources().getDrawable(
					CategoryResourceHelper.getButtonBorderByCategory(mStory.category)));
			mRelevanceScore.setBackground(getResources().getDrawable(
					CategoryResourceHelper.getScoreBackgroundByCategory(mStory.category)));
			mIrrelevanceScore.setBackground(getResources().getDrawable(
					CategoryResourceHelper.getScoreBackgroundByCategory(mStory.category)));
		} else {
			mButtonRelevant.setBackgroundDrawable(getResources().getDrawable(
					CategoryResourceHelper.getButtonBorderByCategory(mStory.category)));
			mButtonIrrelevant.setBackgroundDrawable(getResources().getDrawable(
					CategoryResourceHelper.getButtonBorderByCategory(mStory.category)));
			mRelevanceScore.setBackgroundDrawable(getResources().getDrawable(
					CategoryResourceHelper.getScoreBackgroundByCategory(mStory.category)));
			mIrrelevanceScore.setBackgroundDrawable(getResources().getDrawable(
					CategoryResourceHelper.getScoreBackgroundByCategory(mStory.category)));
		}

		// Draw text inside circles
		mRelevanceLabel.setTextColor(CategoryResourceHelper.getColorByCategory(mStory.category));
		mIrrelevanceLabel.setTextColor(CategoryResourceHelper.getColorByCategory(mStory.category));
		mRelevanceScore.setText(mStory.scoreRelevance.toString());
		mIrrelevanceScore.setText(mStory.scoreIrrelevance.toString());

		// Invert if user has already inverted a button
		if (mStory.userRelevance == 1) {
			invertRelevant();
		} else if (mStory.userRelevance == -1) {
			invertIrrelevant();
		}
	}

	private void drawArticleComments() {
		mButtonComments.setImageResource(CategoryResourceHelper.getCommentsByCategory(mStory.category));
	}

	private void initModule() {
		getModule(mStory.medium);
		mMediaModule.init();
	}

	private void getModule(String medium) {
		// Init AudioModule
		if ("AUDIO".equals(medium)) {
			mMediaModule = new AudioModule(this);
		}
		// Init PictureModule
		if ("PICTURE".equals(medium)) {
			mMediaModule = new PictureModule(this);
		}
		// Init VideoModule
		if ("VIDEO".equals(medium)) {
			mMediaModule = new VideoModule(this);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

		registerReceivers();
		mArticleImage.setImageUrl(GlobalConstant.MEDIA_URL + mStory.id, mImageLoader);
		mAuthorPicture.setImageUrl(GlobalConstant.PROFILE_URL + mStory.authorId, mImageLoader);

		initComments();
	}

	private void registerReceivers() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(GlobalConstant.BLOB_LOADED_ACTION);
		filter.addAction(GlobalConstant.COMMENTS_LOADED_ACTION);
		registerReceiver(mReceiver, filter);
	}

	private void initComments() {
		mArticleDataController.getComments();
	}

	@Override
	protected void onPause() {
		unRegisterReceivers();

		mMediaModule.onPause();

		super.onPause();
	}

	private void unRegisterReceivers() {
		unregisterReceiver(mReceiver);
	}

	/* ***** ACTIVITY SETUP END ***** */

	/* ***** ONCLICKLISTENERS BEGIN ***** */

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.article_image:
			if (mLoaded) {
				mMediaModule.onPlay();
			}
			break;
		case R.id.author_picture:
			ProfileLauncher.openProfile(this, mStory.authorId);
			finish();
			break;
		case R.id.author:
			ProfileLauncher.openProfile(this, mStory.authorId);
			finish();
			break;
		case R.id.button_relevant:
			clickRelevant();
			break;
		case R.id.button_irrelevant:
			clickIrrelevant();
			break;
		case R.id.btn_comments:
			// TODO program this
			break;
		case R.id.btn_post_comment:
			mNoozService.postComment(mInputTextComment.getText().toString(), parentIdOfCommentToBe, mStory.id, onPostComment);
			break;
		}
	}
	
	TableJsonOperationCallback onPostComment = new TableJsonOperationCallback() {
		@Override
		public void onCompleted(JsonObject jsonObject, Exception exception, ServiceFilterResponse response) {
			if (exception == null) {
				Log.i("nooz debug", "posted the comment!");
			} else {
				Log.e("nooz debug", "Error posting comment: " + exception.getMessage());
			}
		}
	};

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}

	/* ***** ONCLICKLISTENERS END ***** */

	/* ***** RELEVANCE BEGIN ***** */

	private void clickRelevant() {
		if (mIrrelevant) {
			invertIrrelevant();
			Integer newScore = Integer.parseInt((String) mIrrelevanceScore.getText()) - 1;
			mIrrelevanceScore.setText(newScore.toString());
		}
		invertRelevant();
		Integer change = mRelevant ? 1 : -1;
		Integer newScore = Integer.parseInt((String) mRelevanceScore.getText()) + change;
		mRelevanceScore.setText(newScore.toString());

		saveRelevanceInput();
	}

	private void clickIrrelevant() {
		if (mRelevant) {
			invertRelevant();
			Integer newScore = Integer.parseInt((String) mRelevanceScore.getText()) - 1;
			mRelevanceScore.setText(newScore.toString());
		}
		invertIrrelevant();
		Integer change = mIrrelevant ? 1 : -1;
		Integer newScore = Integer.parseInt((String) mIrrelevanceScore.getText()) + change;
		mIrrelevanceScore.setText(newScore.toString());

		saveRelevanceInput();
	}

	private void saveRelevanceInput() {
		Integer input = 0;
		if (mRelevant) {
			input = 1;
		} else if (mIrrelevant) {
			input = -1;
		}
		updateRelevanceInMapActivity(input);
		mNoozService.saveRelevanceInput(mStory.id, input, mCurrentLocation, onSaveRelevance);
	}

	private void updateRelevanceInMapActivity(Integer input) {
		Intent broadcast = new Intent();
		broadcast.setAction(GlobalConstant.RELEVANCE_UPDATE_ACTION);
		broadcast.putExtra("id", mStory.id);
		broadcast.putExtra("input", input);
		mContext.sendBroadcast(broadcast);
	}

	TableJsonOperationCallback onSaveRelevance = new TableJsonOperationCallback() {

		@Override
		public void onCompleted(JsonObject jsonObject, Exception exception, ServiceFilterResponse response) {
			if (exception == null) {
				Toast.makeText(mContext, "Sumbitted Relevance", Toast.LENGTH_SHORT).show();
				Log.d(TAG, "Submitted Relevance!");
			} else {
				Log.e(TAG, "Error submitting relevance: " + exception.getMessage());
				Alert.createAndShowDialog(exception, "Error", mContext);
			}
		}
	};

	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	private void invertRelevant() {
		if (mRelevant) {
			int currentapiVersion = android.os.Build.VERSION.SDK_INT;
			Drawable button = getResources().getDrawable(
					CategoryResourceHelper.getButtonBorderByCategory(mStory.category));
			Drawable scoreCircle = getResources().getDrawable(
					CategoryResourceHelper.getScoreBackgroundByCategory(mStory.category));
			if (currentapiVersion >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
				mButtonRelevant.setBackground(button);
				mRelevanceScore.setBackground(scoreCircle);
			} else {
				mButtonRelevant.setBackgroundDrawable(button);
				mRelevanceScore.setBackgroundDrawable(scoreCircle);
			}
			mRelevanceScore.setTextColor(CategoryResourceHelper.COLOR_WHITE);
			mRelevanceLabel.setTextColor(CategoryResourceHelper.getColorByCategory(mStory.category));
			mRelevant = false;
		} else {
			int currentapiVersion = android.os.Build.VERSION.SDK_INT;
			Drawable button = getResources().getDrawable(
					CategoryResourceHelper.getButtonBorderByCategory(mStory.category));
			Drawable scoreCircle = getResources().getDrawable(
					CategoryResourceHelper.getScoreBackgroundByCategory(mStory.category));
			button.setColorFilter(CategoryResourceHelper.getColorByCategory(mStory.category), Mode.MULTIPLY);
			scoreCircle.setColorFilter(CategoryResourceHelper.COLOR_WHITE, Mode.SRC_ATOP);
			if (currentapiVersion >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
				mButtonRelevant.setBackground(button);
				mRelevanceScore.setBackground(scoreCircle);
			} else {
				mButtonRelevant.setBackgroundDrawable(button);
				mRelevanceScore.setBackgroundDrawable(scoreCircle);
			}
			mRelevanceScore.setTextColor(CategoryResourceHelper.getColorByCategory(mStory.category));
			mRelevanceLabel.setTextColor(CategoryResourceHelper.COLOR_WHITE);
			mRelevant = true;
		}
	}

	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	private void invertIrrelevant() {
		if (mIrrelevant) {
			int currentapiVersion = android.os.Build.VERSION.SDK_INT;
			Drawable button = getResources().getDrawable(
					CategoryResourceHelper.getButtonBorderByCategory(mStory.category));
			Drawable scoreCircle = getResources().getDrawable(
					CategoryResourceHelper.getScoreBackgroundByCategory(mStory.category));
			if (currentapiVersion >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
				mButtonIrrelevant.setBackground(button);
				mIrrelevanceScore.setBackground(scoreCircle);
			} else {
				mButtonIrrelevant.setBackgroundDrawable(button);
				mIrrelevanceScore.setBackgroundDrawable(scoreCircle);
			}
			mIrrelevanceScore.setTextColor(CategoryResourceHelper.COLOR_WHITE);
			mIrrelevanceLabel.setTextColor(CategoryResourceHelper.getColorByCategory(mStory.category));
			mIrrelevant = false;
		} else {
			int currentapiVersion = android.os.Build.VERSION.SDK_INT;
			Drawable button = getResources().getDrawable(
					CategoryResourceHelper.getButtonBorderByCategory(mStory.category));
			Drawable scoreCircle = getResources().getDrawable(
					CategoryResourceHelper.getScoreBackgroundByCategory(mStory.category));
			button.setColorFilter(CategoryResourceHelper.getColorByCategory(mStory.category), Mode.MULTIPLY);
			scoreCircle.setColorFilter(CategoryResourceHelper.COLOR_WHITE, Mode.SRC_ATOP);
			if (currentapiVersion >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
				mButtonIrrelevant.setBackground(button);
				mIrrelevanceScore.setBackground(scoreCircle);
			} else {
				mButtonIrrelevant.setBackgroundDrawable(button);
				mIrrelevanceScore.setBackgroundDrawable(scoreCircle);
			}
			mIrrelevanceScore.setTextColor(CategoryResourceHelper.getColorByCategory(mStory.category));
			mIrrelevanceLabel.setTextColor(CategoryResourceHelper.COLOR_WHITE);
			mIrrelevant = true;
		}
	}

	/* ***** RELEVANCE END ***** */

}