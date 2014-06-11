package com.nooz.nooz.activity.profile;

import java.util.ArrayList;
import java.util.List;

import com.nooz.nooz.model.Story;
import com.nooz.nooz.util.SearchType;

import android.content.Context;

public class ProfileStoriesController {

	private ProfileActivity mC;
	List<Story> mStories;

	public ProfileStoriesController(ProfileActivity profileActivity) {
		mC = profileActivity;
		mStories = new ArrayList<Story>();
	}

	public void populateProfileStories() {
		mC.getNoozService().getAllStories(null, null, SearchType.PROFILE, mC.mUserId);
	}

	public void getStoriesCallBack() {
		List<Story> newStories = mC.getNoozService().getLoadedStories();
		mStories = newStories;
		mC.mProfileStoriesAdapter.notifyDataSetChanged();
	}
}
