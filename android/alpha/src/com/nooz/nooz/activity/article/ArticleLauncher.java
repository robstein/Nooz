package com.nooz.nooz.activity.article;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.nooz.nooz.model.Story;

public class ArticleLauncher {

	/**
	 * Starts an activity to view the provided story
	 * 
	 * @param s
	 *            Story to open
	 */
	public static void openStory(Context c, Story s) {
		Bundle args = new Bundle();
		args.putParcelable("story", s);
		Intent readStoryIntent = new Intent(c.getApplicationContext(), ArticleActivity.class);
		readStoryIntent.putExtra("bundle", args);
		c.startActivity(readStoryIntent);
	}

}
