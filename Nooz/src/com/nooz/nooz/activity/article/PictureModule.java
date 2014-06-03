package com.nooz.nooz.activity.article;

import android.media.MediaPlayer;
import android.net.Uri;

public class PictureModule implements ArticleModule {

	private ArticleActivity mC;

	public PictureModule(ArticleActivity articleActivity) {
		this.mC = articleActivity;
	}
	
	@Override
	public void init() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub

	}

	@Override
	public Boolean setDataSource(Uri myUri) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onPlay() {
		// TODO Auto-generated method stub

	}

}
