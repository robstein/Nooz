package com.nooz.nooz.activity.article;

import java.util.List;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;

import com.google.gson.JsonObject;
import com.nooz.nooz.model.Comment;
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

	public void getComments() {
		mC.getNoozService().getComments(mC.mStory.id);
	}

	public void loadComments() {
		List<Comment> listOfComments = mC.getNoozService().getLoadedComments();
		CommentThreadTree commentTree = new CommentThreadTree(listOfComments);
		commentTree.inflate(mC, mC.mLayoutComments);
	}
}
