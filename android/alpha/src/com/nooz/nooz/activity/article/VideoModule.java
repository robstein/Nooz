package com.nooz.nooz.activity.article;

import android.media.MediaPlayer;
import android.net.Uri;

public class VideoModule implements ArticleModule {

	private ArticleActivity mC;

	MediaPlayer mPlayer;
	boolean mCurrentlyPlayingVideo;

	public VideoModule(ArticleActivity articleActivity) {
		this.mC = articleActivity;
		mCurrentlyPlayingVideo = false;
	}

	@Override
	public void init() {
		mCurrentlyPlayingVideo = false;
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub

	}

	@Override
	public Boolean setDataSource(Uri myUri) {
		return false;
	}

	@Override
	public void onPlay() {
		// TODO Auto-generated method stub

	}

}
