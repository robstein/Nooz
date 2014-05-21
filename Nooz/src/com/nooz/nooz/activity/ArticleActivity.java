package com.nooz.nooz.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nooz.nooz.R;
import com.nooz.nooz.model.Story;

public class ArticleActivity extends BaseActivity {

	private Story mStory;

	private ImageView mArticleCategoryLogo;
	private TextView mArticleCategory;
	private ImageView mArticleInfo;
	private ImageView mArticleImage;
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

	private static int COLOR_PEOPLE;
	private static int COLOR_COMMUNITY;
	private static int COLOR_SPORTS;
	private static int COLOR_FOOD;
	private static int COLOR_PUBLIC_SAFETY;
	private static int COLOR_ARTS_AND_LIFE;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_article);
		Bundle bundle = getIntent().getParcelableExtra("bundle");
		mStory = bundle.getParcelable("story");

		COLOR_PEOPLE = getResources().getColor(R.color.category_people);
		COLOR_COMMUNITY = getResources().getColor(R.color.category_community);
		COLOR_SPORTS = getResources().getColor(R.color.category_sports);
		COLOR_FOOD = getResources().getColor(R.color.category_food);
		COLOR_PUBLIC_SAFETY = getResources().getColor(R.color.category_public_safety);
		COLOR_ARTS_AND_LIFE = getResources().getColor(R.color.category_arts_and_life);

		mArticleCategoryLogo = (ImageView) findViewById(R.id.article_category_logo);
		mArticleCategory = (TextView) findViewById(R.id.article_category);
		mArticleInfo = (ImageView) findViewById(R.id.article_info);
		mArticleImage = (ImageView) findViewById(R.id.article_image);
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

		mArticleCategoryLogo.setImageResource(getLogoByCategory(mStory.category));
		mArticleCategory.setText(mStory.category);
		mArticleCategory.setTextColor(getColorByCategory(mStory.category));
		mArticleInfo.setImageResource(getInfoByCategory(mStory.category));
		// Set mArticleImage
		mHeadline.setText(mStory.headline);
		// Set mAuthorPicture
		mAuthor.setText(mStory.firstName + " " + mStory.lastName);
		mCaption.setText(mStory.caption);
		int currentapiVersion = android.os.Build.VERSION.SDK_INT;
		if (currentapiVersion >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
			mButtonRelevant.setBackground(getResources().getDrawable(getButtonBorderByCategory(mStory.category)));
			mButtonIrrelevant.setBackground(getResources().getDrawable(getButtonBorderByCategory(mStory.category)));
			mRelevanceScore.setBackground(getResources().getDrawable(getScoreBackgroundByCategory(mStory.category)));
			mIrrelevanceScore.setBackground(getResources().getDrawable(getScoreBackgroundByCategory(mStory.category)));
		} else {
			mButtonRelevant.setBackgroundDrawable(getResources()
					.getDrawable(getButtonBorderByCategory(mStory.category)));
			mButtonIrrelevant.setBackgroundDrawable(getResources().getDrawable(
					getButtonBorderByCategory(mStory.category)));
			mRelevanceScore.setBackgroundDrawable(getResources().getDrawable(
					getScoreBackgroundByCategory(mStory.category)));
			mIrrelevanceScore.setBackgroundDrawable(getResources().getDrawable(
					getScoreBackgroundByCategory(mStory.category)));
		}
		mRelevanceLabel.setTextColor(getColorByCategory(mStory.category));
		mIrrelevanceLabel.setTextColor(getColorByCategory(mStory.category));
		mButtonComments.setImageResource(getCommentsByCategory(mStory.category));
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}

	private int getCommentsByCategory(String category) {
		if ("People".equals(category)) {
			return R.drawable.comments_people;
		} else if ("Community".equals(category)) {
			return R.drawable.comments_community;
		} else if ("Sports".equals(category)) {
			return R.drawable.comments_sports;
		} else if ("Food".equals(category)) {
			return R.drawable.comments_food;
		} else if ("Public Safety".equals(category)) {
			return R.drawable.comments_public_safety;
		} else { // Arts and Life
			return R.drawable.comments_arts_and_life_fullsize;
		}
	}

	private int getScoreBackgroundByCategory(String category) {
		if ("People".equals(category)) {
			return R.drawable.relevance_score_people;
		} else if ("Community".equals(category)) {
			return R.drawable.relevance_score_community;
		} else if ("Sports".equals(category)) {
			return R.drawable.relevance_score_sports;
		} else if ("Food".equals(category)) {
			return R.drawable.relevance_score_food;
		} else if ("Public Safety".equals(category)) {
			return R.drawable.relevance_score_public_safety;
		} else { // Arts and Life
			return R.drawable.relevance_score_arts_and_life;
		}
	}

	private int getButtonBorderByCategory(String category) {
		if ("People".equals(category)) {
			return R.drawable.text_button_people;
		} else if ("Community".equals(category)) {
			return R.drawable.text_button_community;
		} else if ("Sports".equals(category)) {
			return R.drawable.text_button_sports;
		} else if ("Food".equals(category)) {
			return R.drawable.text_button_food;
		} else if ("Public Safety".equals(category)) {
			return R.drawable.text_button_public_safety;
		} else { // Arts and Life
			return R.drawable.text_button_arts_and_life;
		}
	}

	private int getInfoByCategory(String category) {
		if ("People".equals(category)) {
			return R.drawable.info_people;
		} else if ("Community".equals(category)) {
			return R.drawable.info_community;
		} else if ("Sports".equals(category)) {
			return R.drawable.info_sports;
		} else if ("Food".equals(category)) {
			return R.drawable.info_food;
		} else if ("Public Safety".equals(category)) {
			return R.drawable.info_public_safety;
		} else { // Arts and Life
			return R.drawable.info_arts_and_life;
		}
	}

	private int getColorByCategory(String category) {
		int retval;
		if ("People".equals(category)) {
			retval = COLOR_PEOPLE;
		} else if ("Community".equals(category)) {
			retval = COLOR_COMMUNITY;
		} else if ("Sports".equals(category)) {
			retval = COLOR_SPORTS;
		} else if ("Food".equals(category)) {
			retval = COLOR_FOOD;
		} else if ("Public Safety".equals(category)) {
			retval = COLOR_PUBLIC_SAFETY;
		} else { // Arts and Life
			retval = COLOR_ARTS_AND_LIFE;
		}
		return retval;
	}

	private int getLogoByCategory(String category) {
		if ("People".equals(category)) {
			return R.drawable.people_solid_fullsize;
		} else if ("Community".equals(category)) {
			return R.drawable.community_solid_fullsize;
		} else if ("Sports".equals(category)) {
			return R.drawable.sports_solid_fullsize;
		} else if ("Food".equals(category)) {
			return R.drawable.food_solid_fullsize;
		} else if ("Public Safety".equals(category)) {
			return R.drawable.public_saftey_solid_fullsize;
		} else { // Arts and Life
			return R.drawable.arts_and_life_solid_fullsize;
		}
	}
}
