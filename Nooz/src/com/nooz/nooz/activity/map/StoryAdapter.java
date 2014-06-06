package com.nooz.nooz.activity.map;

import com.android.volley.toolbox.NetworkImageView;
import com.nooz.nooz.R;
import com.nooz.nooz.util.CategoryResourceHelper;
import com.nooz.nooz.util.GlobalConstant;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class StoryAdapter extends PagerAdapter {

	private MapActivity mC;

	public StoryAdapter(MapActivity c) {
		this.mC = c;
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {

		LayoutInflater inflater = (LayoutInflater) mC.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.story_item, null);
		View storyItemShader = (View) layout.findViewById(R.id.story_item_shader);
		storyItemShader.setOnClickListener((OnClickListener) mC.mActivityOnClickListener);

		NetworkImageView image = (NetworkImageView) layout.findViewById(R.id.story_item_article_image);
		if ("PICTURE".equals(mC.mStories.get(position).medium)) {
			image.setImageUrl(GlobalConstant.MEDIA_URL + mC.mStories.get(position).id, mC.getImageLoader());
		}
		if ("AUDIO".equals(mC.mStories.get(position).medium)) {
			ProgressBar loading = (ProgressBar) layout.findViewById(R.id.loading);
			loading.setVisibility(View.GONE);

			ImageView mic = (ImageView) layout.findViewById(R.id.story_medium_icon);
			mic.setImageDrawable(mC.getResources().getDrawable(R.drawable.mic_small));
		}

		TextView title = (TextView) layout.findViewById(R.id.story_item_title);
		TextView author = (TextView) layout.findViewById(R.id.story_item_author);
		View categoryRuler = (View) layout.findViewById(R.id.categoryRuler);

		title.setText(mC.mStories.get(position).headline);
		author.setText(mC.mStories.get(position).firstName + " " + mC.mStories.get(position).lastName);
		categoryRuler.setBackgroundColor(CategoryResourceHelper.getColorByCategory(mC.mStories.get(position).category,
				mC.HIGHLIGHT));
		if (position == mC.mResumeStory) {
			storyItemShader = (View) layout.findViewById(R.id.story_item_shader);
			storyItemShader.setBackgroundDrawable((mC.getResources().getDrawable(R.drawable.selector_footer_story_item_highlighted)));
		}

		layout.setTag(position);

		((ViewPager) container).addView(layout);

		return layout;
	}

	/**
	 * This way, when you call notifyDataSetChanged(), the view pager will
	 * remove all views and reload them all. As so the reload effect is
	 * obtained.
	 **/
	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView((View) object);
	}

	@Override
	public int getItemPosition(Object object) {
		return POSITION_NONE;
	}

	@Override
	public int getCount() {
		return mC.mStories.size();
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return (view == object);
	}
}
