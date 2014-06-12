package com.nooz.nooz.activity.profile;

import com.android.volley.toolbox.NetworkImageView;
import com.nooz.nooz.R;
import com.nooz.nooz.activity.article.ArticleLauncher;
import com.nooz.nooz.util.CategoryResourceHelper;
import com.nooz.nooz.util.GlobalConstant;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ProfileStoryAdapter extends BaseAdapter {

	private ProfileActivity mC;

	public ProfileStoryAdapter(ProfileActivity profileActivity) {
		mC = profileActivity;
	}

	@Override
	public int getCount() {
		return mC.mProfileStoriesController.mStories.size();
	}

	@Override
	public Object getItem(int position) {
		return mC.mProfileStoriesController.mStories.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View layout;
		// Recycle layout if possible
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) mC.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			layout = inflater.inflate(R.layout.story_item, null);
		} else {
			layout = convertView;
		}

		View storyItemShader = (View) layout.findViewById(R.id.story_item_shader);
		storyItemShader.setBackgroundDrawable((mC.getResources()
				.getDrawable(R.drawable.selector_footer_story_item_highlighted)));
		storyItemShader.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ArticleLauncher.openStory(mC, mC.mProfileStoriesController.mStories.get(position));
			}
		});

		NetworkImageView image = (NetworkImageView) layout.findViewById(R.id.story_item_article_image);
		if ("PICTURE".equals(mC.mProfileStoriesController.mStories.get(position).medium)) {
			image.setImageUrl(GlobalConstant.MEDIA_URL + mC.mProfileStoriesController.mStories.get(position).id,
					mC.getImageLoader());
			Log.i("nooz debug", GlobalConstant.MEDIA_URL + mC.mProfileStoriesController.mStories.get(position).id);
		}

		TextView title = (TextView) layout.findViewById(R.id.story_item_title);
		TextView author = (TextView) layout.findViewById(R.id.story_item_author);
		View categoryRuler = (View) layout.findViewById(R.id.categoryRuler);

		title.setText(mC.mProfileStoriesController.mStories.get(position).headline);
		author.setText(mC.mProfileStoriesController.mStories.get(position).firstName + " "
				+ mC.mProfileStoriesController.mStories.get(position).lastName);
		categoryRuler.setBackgroundColor(CategoryResourceHelper.getColorByCategory(
				mC.mProfileStoriesController.mStories.get(position).category, ProfileActivity.HIGHLIGHT));

		layout.setTag(position);

		return layout;
	}
}
