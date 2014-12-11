package com.nooz.nooz.activity.article;

import java.io.IOException;

import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;

public class AudioModule implements ArticleModule {

	private static final String TAG = "AudioModule";
	private ArticleActivity mC;

	MediaPlayer mPlayer;
	boolean mCurrentlyPlayingAudio;

	public AudioModule(ArticleActivity articleActivity) {
		this.mC = articleActivity;
	}

	@Override
	public void init() {
		mCurrentlyPlayingAudio = false;
		mPlayer = new MediaPlayer();
	}

	@Override
	public void onPause() {
		if (mCurrentlyPlayingAudio) {
			stopPlaying();
		}
	}

	@Override
	public Boolean setDataSource(Uri myUri) {
		try {
			mPlayer.setDataSource(mC, myUri);
		} catch (IllegalArgumentException e) {
			Log.e(TAG, "IllegalArgumentException - There was an error setting the data source: "
					+ e.getCause().getMessage());
			return false;
		} catch (SecurityException e) {
			Log.e(TAG, "SecurityException - There was an error setting the data source: " + e.getCause().getMessage());
			return false;
		} catch (IllegalStateException e) {
			Log.e(TAG, "IllegalStateException - There was an error setting the data source: "
					+ e.getCause().getMessage());
			return false;
		} catch (IOException e) {
			Log.e(TAG, "IOException - There was an error setting the data source: " + e.getCause().getMessage());
			return false;
		}
		return true;
	}
	
	@Override
	public void onPlay() {
		if (!mCurrentlyPlayingAudio) {
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

}
