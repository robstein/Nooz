package com.nooz.nooz.activity;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.TableJsonOperationCallback;
import com.nooz.nooz.R;
import com.nooz.nooz.model.Story;
import com.nooz.nooz.util.Alert;
import com.nooz.nooz.util.CategoryResourceHelper;

/**
 * 
 * @author Rob Stein
 * 
 */
public class ArticleActivity extends BaseLocationFragmentActivity implements OnClickListener {
	private static final String TAG = "ArticleActivity";

	private static final int COLOR_WHITE = 0xFFFFFFFF;
	private static final String CONTAINER_NAME = "media";

	private ImageView mArticleCategoryLogo;
	private TextView mArticleCategory;
	private ImageView mArticleInfo;
	private ImageView mArticleImage;
	private RelativeLayout mArticleHeader;
	private TextView mHeadline;
	private ImageView mAuthorPicture;
	private TextView mAuthor;
	private TextView mCaption;
	private LinearLayout mButtonRelevant;
	private TextView mRelevanceScore;
	private TextView mRelevanceLabel;
	private LinearLayout mButtonIrrelevant;
	private TextView mIrrelevanceScore;
	private TextView mIrrelevanceLabel;
	private ImageView mButtonComments;

	private MediaPlayer mPlayer = null;
	private boolean mCurrentlyPlayingAudio = false;
	private boolean mLoaded = false;
	private Story mStory;
	private Boolean mRelevant = false;
	private Boolean mIrrelevant = false;
	private Integer mScoreRel = 0;
	private Integer mScoreIrr = 0;

	private int mScreenWidthInPixels;

	/* ***** ACTIVITY SETUP BEGIN ***** */

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_article);
		Bundle bundle = getIntent().getParcelableExtra("bundle");
		mStory = bundle.getParcelable("story");
		mNoozService.getBlobSas(CONTAINER_NAME, mStory.id);

		mArticleCategoryLogo = (ImageView) findViewById(R.id.article_category_logo);
		mArticleCategory = (TextView) findViewById(R.id.article_category);
		mArticleInfo = (ImageView) findViewById(R.id.article_info);
		mArticleImage = (ImageView) findViewById(R.id.article_image);
		// mArticleHeader = (RelativeLayout) findViewById(R.id.article_header);
		mHeadline = (TextView) findViewById(R.id.headline);
		mAuthorPicture = (ImageView) findViewById(R.id.author_picture);
		mAuthor = (TextView) findViewById(R.id.author);
		mCaption = (TextView) findViewById(R.id.caption);
		mButtonRelevant = (LinearLayout) findViewById(R.id.button_relevant);
		mRelevanceScore = (TextView) findViewById(R.id.relevance_score);
		mRelevanceLabel = (TextView) findViewById(R.id.relevance_label);
		mButtonIrrelevant = (LinearLayout) findViewById(R.id.button_irrelevant);
		mIrrelevanceScore = (TextView) findViewById(R.id.irrelevance_score);
		mIrrelevanceLabel = (TextView) findViewById(R.id.irrelevance_label);
		mButtonComments = (ImageView) findViewById(R.id.btn_comments);

		// Make picture the right size
		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		mScreenWidthInPixels = size.x;
		RelativeLayout.LayoutParams imageLayoutParams = (RelativeLayout.LayoutParams) mArticleImage.getLayoutParams();
		imageLayoutParams.height = mScreenWidthInPixels;
		imageLayoutParams.width = mScreenWidthInPixels;
		mArticleImage.setLayoutParams(imageLayoutParams);

		mArticleImage.setOnClickListener(this);
		mArticleInfo.setOnClickListener(this);
		mButtonRelevant.setOnClickListener(this);
		mButtonIrrelevant.setOnClickListener(this);
		mButtonComments.setOnClickListener(this);

		mArticleCategoryLogo.setImageResource(CategoryResourceHelper.getLogoByCategory(mStory.category));
		mArticleCategory.setText(mStory.category);
		mArticleCategory.setTextColor(CategoryResourceHelper.getColorByCategory(mStory.category));
		mArticleInfo.setImageResource(CategoryResourceHelper.getInfoByCategory(mStory.category));
		// Set mArticleImage
		// Happens in the broadcast recevier
		mHeadline.setText(mStory.headline);
		// Set mAuthorPicture
		mAuthor.setText(mStory.firstName + " " + mStory.lastName);
		mCaption.setText(mStory.caption);
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
		mRelevanceLabel.setTextColor(CategoryResourceHelper.getColorByCategory(mStory.category));
		mIrrelevanceLabel.setTextColor(CategoryResourceHelper.getColorByCategory(mStory.category));
		mButtonComments.setImageResource(CategoryResourceHelper.getCommentsByCategory(mStory.category));

		if (mStory.userRelevance == 1) {
			invertRelevant();
		} else if (mStory.userRelevance == -1) {
			invertIrrelevant();
		}

		mRelevanceScore.setText(mStory.scoreRelevance.toString());
		mIrrelevanceScore.setText(mStory.scoreIrrelevance.toString());
	}

	/***
	 * Register for broadcasts
	 */
	@Override
	protected void onResume() {
		IntentFilter filter = new IntentFilter();
		filter.addAction("blob.loaded");
		registerReceiver(receiver, filter);
		super.onResume();
	}

	/***
	 * Unregister for broadcasts
	 */
	@Override
	protected void onPause() {
		unregisterReceiver(receiver);
		if (mCurrentlyPlayingAudio) {
			stopPlaying();
		}
		super.onPause();
	}

	/***
	 * Broadcast receiver handles a blob being loaded
	 */
	private BroadcastReceiver receiver = new BroadcastReceiver() {
		public void onReceive(Context context, android.content.Intent intent) {
			String intentAction = intent.getAction();
			if (intentAction.equals("blob.loaded")) {
				// Load the image using the SAS URL
				JsonObject blob = mNoozService.getLoadedBlob();
				String sasUrl = blob.getAsJsonPrimitive("sasUrl").toString();
				sasUrl = sasUrl.replace("\"", "");
				if ("AUDIO".equals(mStory.medium)) {
					(new AudioFetcherTask(sasUrl)).execute();
					mArticleImage.setImageDrawable(getResources().getDrawable(R.drawable.play));
					mLoaded = true;
				}
				if ("PICTURE".equals(mStory.medium)) {
					(new ImageFetcherTask(sasUrl)).execute();
					mLoaded = true;
				}
				if ("VIDEO".equals(mStory.medium)) {

				}
			}
		}
	};

	/* ***** ACTIVITY SETUP END ***** */

	/* ***** ONCLICKLISTENERS BEGIN ***** */

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.article_image:
			if ("AUDIO".equals(mStory.medium)) {
				if (mLoaded) {
					onPlay(!mCurrentlyPlayingAudio);
				}
			}
			break;
		case R.id.button_relevant:
			clickRelevant();
			break;
		case R.id.button_irrelevant:
			clickIrrelevant();
			break;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}

	/* ***** ONCLICKLISTENERS END ***** */

	/* ***** AUDIO BEGIN ***** */

	private void onPlay(boolean start) {
		if (start) {
			startPlaying();
		} else {
			stopPlaying();
		}
	}

	private void startPlaying() {
		try {
			mPlayer.prepare();
			mPlayer.start();
			mCurrentlyPlayingAudio = true;
		} catch (IOException e) {
			Log.e(TAG, "prepare() failed");
		}
	}

	private void stopPlaying() {
		mPlayer.release();
		mPlayer = null;
		mCurrentlyPlayingAudio = false;
	}

	/**
	 * This class specifically handles fetching an audio file from a URL and
	 * loading the player with its data
	 */
	private class AudioFetcherTask extends AsyncTask<Void, Void, Boolean> {
		private String mUrl;
		private Bitmap mBitmap;

		public AudioFetcherTask(String url) {
			mUrl = url;
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			try {
				mPlayer = new MediaPlayer();
				Log.d(TAG, "mPlayer is not null");
				Uri myUri = Uri.parse(mUrl);
				mPlayer.setDataSource(mContext, myUri);
			} catch (Exception e) {
				Log.e(TAG, e.getMessage());
				return false;
			}
			return true;
		}

		@Override
		protected void onPostExecute(Boolean loaded) {

		}
	}

	/* ***** AUDIO END ***** */

	/**
	 * This class specifically handles fetching an image from a URL and setting
	 * the image view source on the screen
	 */
	private class ImageFetcherTask extends AsyncTask<Void, Void, Boolean> {
		private String mUrl;
		private Bitmap mBitmap;

		public ImageFetcherTask(String url) {
			mUrl = url;
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			try {
				mBitmap = BitmapFactory.decodeStream((InputStream) new URL(mUrl).getContent());
			} catch (Exception e) {
				Log.e(TAG, e.getMessage());
				return false;
			}
			return true;
		}

		/***
		 * If the image was loaded successfully, set the image view
		 */
		@Override
		protected void onPostExecute(Boolean loaded) {
			if (loaded) {
				mArticleImage.setImageBitmap(mBitmap);
			}
		}
	}

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
		mNoozService.saveRelevanceInput(mStory.id, input, mCurrentLocation, onSaveRelevance);
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
			mRelevanceScore.setTextColor(COLOR_WHITE);
			mRelevanceLabel.setTextColor(CategoryResourceHelper.getColorByCategory(mStory.category));
			mRelevant = false;
		} else {
			int currentapiVersion = android.os.Build.VERSION.SDK_INT;
			Drawable button = getResources().getDrawable(
					CategoryResourceHelper.getButtonBorderByCategory(mStory.category));
			Drawable scoreCircle = getResources().getDrawable(
					CategoryResourceHelper.getScoreBackgroundByCategory(mStory.category));
			button.setColorFilter(CategoryResourceHelper.getColorByCategory(mStory.category), Mode.MULTIPLY);
			scoreCircle.setColorFilter(COLOR_WHITE, Mode.SRC_ATOP);
			if (currentapiVersion >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
				mButtonRelevant.setBackground(button);
				mRelevanceScore.setBackground(scoreCircle);
			} else {
				mButtonRelevant.setBackgroundDrawable(button);
				mRelevanceScore.setBackgroundDrawable(scoreCircle);
			}
			mRelevanceScore.setTextColor(CategoryResourceHelper.getColorByCategory(mStory.category));
			mRelevanceLabel.setTextColor(COLOR_WHITE);
			mRelevant = true;
		}
	}

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
			mIrrelevanceScore.setTextColor(COLOR_WHITE);
			mIrrelevanceLabel.setTextColor(CategoryResourceHelper.getColorByCategory(mStory.category));
			mIrrelevant = false;
		} else {
			int currentapiVersion = android.os.Build.VERSION.SDK_INT;
			Drawable button = getResources().getDrawable(
					CategoryResourceHelper.getButtonBorderByCategory(mStory.category));
			Drawable scoreCircle = getResources().getDrawable(
					CategoryResourceHelper.getScoreBackgroundByCategory(mStory.category));
			button.setColorFilter(CategoryResourceHelper.getColorByCategory(mStory.category), Mode.MULTIPLY);
			scoreCircle.setColorFilter(COLOR_WHITE, Mode.SRC_ATOP);
			if (currentapiVersion >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
				mButtonIrrelevant.setBackground(button);
				mIrrelevanceScore.setBackground(scoreCircle);
			} else {
				mButtonIrrelevant.setBackgroundDrawable(button);
				mIrrelevanceScore.setBackgroundDrawable(scoreCircle);
			}
			mIrrelevanceScore.setTextColor(CategoryResourceHelper.getColorByCategory(mStory.category));
			mIrrelevanceLabel.setTextColor(COLOR_WHITE);
			mIrrelevant = true;
		}
	}

	/* ***** RELEVANCE BEGIN ***** */

	/* ***** GET THEME RESOURCES BY CATEGORY BEGIN ***** */

	/* ***** GET THEME RESOURCES BY CATEGORY END ***** */

}