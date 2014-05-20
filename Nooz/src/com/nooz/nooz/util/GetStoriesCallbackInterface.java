package com.nooz.nooz.util;

import java.util.List;

import com.nooz.nooz.model.Story;

public interface GetStoriesCallbackInterface {
	public void onComplete(List<Story> stories);
}
