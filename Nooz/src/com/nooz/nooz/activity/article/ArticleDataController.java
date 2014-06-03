package com.nooz.nooz.activity.article;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.JsonObject;
import com.nooz.nooz.R;
import com.nooz.nooz.util.GlobalConstant;

public class ArticleDataController {

	private static final String TAG = "ArticleDataController";
	private static final boolean FORCE_STOP = true;

	private ArticleActivity mC;

	public ArticleDataController(ArticleActivity a) {
		this.mC = a;
	}

	void populateMedia() {
		mC.getNoozService().getBlobSas(GlobalConstant.CONTAINER_NAME, mC.mStory.id);
	}

	void handleBlob() {
		JsonObject blob = mC.getNoozService().getLoadedBlob();
		String sasUrl = blob.getAsJsonPrimitive("sasUrl").toString();
		sasUrl = sasUrl.replace("\"", "");
		if ("AUDIO".equals(mC.mStory.medium)) {
			(new ArticleAudioFetcherTask(sasUrl)).execute();
		}
		if ("PICTURE".equals(mC.mStory.medium)) {
			(new ArticleImageFetcherTask(sasUrl)).execute();
		}
		if ("VIDEO".equals(mC.mStory.medium)) {

		}
	}

	private class ArticleAudioFetcherTask extends AsyncTask<Void, Void, Boolean> {
		private static final String TAG = "ArticleImageFetcherTask";
		private String mUrl;
		private Bitmap mBitmap;

		public ArticleAudioFetcherTask(String url) {
			mUrl = url;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			// Show loading
			// TODO
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			Uri myUri = Uri.parse(mUrl);
			return mC.mMediaModule.setDataSource(myUri);
		}

		/***
		 * If the image was loaded successfully, set the image view
		 */
		@Override
		protected void onPostExecute(Boolean loaded) {
			// Hide loading
			// TODO
			if (loaded) {
				// SHow image
				mC.mArticleImage.setImageBitmap(mBitmap);
			} else {
				// Show error
				// TODO
			}
			// Set loaded flag
			mC.mLoaded = true;
		}
	}

	private class ArticleImageFetcherTask extends AsyncTask<Void, Void, Boolean> {
		private static final String TAG = "ArticleImageFetcherTask";
		private String mUrl;
		private Bitmap mBitmap;

		public ArticleImageFetcherTask(String url) {
			mUrl = url;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			// Show loading
			// TODO
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			boolean retval = true;
			Exception ex = new Exception();
			try {
				mBitmap = BitmapFactory.decodeStream((InputStream) new URL(mUrl).getContent());
			} catch (MalformedURLException e) {
				retval = false;
				ex = e;
			} catch (FileNotFoundException e) {
				retval = false;
				ex = e;
			} catch (IOException e) {
				retval = false;
				ex = e;
			}

			if (!retval) {
				cancel(FORCE_STOP);
			}
			
			if(ex instanceof MalformedURLException) {
				Log.e(TAG, "MalformedURLException - Bad blob url: " + ex.getCause().getMessage());
			}
			if(ex instanceof FileNotFoundException) {
				Log.e(TAG, "FileNotFoundException - No blob at url: " + ex.getCause().getMessage());
			}
			if(ex instanceof IOException) {
				Log.e(TAG, "IOException - There was an error decoding bitmap from URL: " + ex.getCause().getMessage());
			}

			return retval;
		}

		/***
		 * If the image was loaded successfully, set the image view
		 */
		@Override
		protected void onPostExecute(Boolean loaded) {
			// Hide loading
			// TODO
			if (loaded) {
				// SHow image
				mC.mArticleImage.setImageBitmap(mBitmap);
			} else {
				// Show error
				// TODO
			}
			// Set loaded flag
			mC.mLoaded = true;
		}
	}
}
