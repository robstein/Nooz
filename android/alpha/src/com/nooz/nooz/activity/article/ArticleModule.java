package com.nooz.nooz.activity.article;

import android.net.Uri;

public interface ArticleModule {

	public void init();
	public void onPause();
	public Boolean setDataSource(Uri myUri);
	public void onPlay();
}
