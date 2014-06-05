package com.nooz.nooz.activity.profile;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.nooz.nooz.R;
import com.nooz.nooz.util.GlobalConstant;
import com.soundcloud.android.crop.Crop;

public class ProfilePictureController {

	private static final String TAG = "ProfilePictureController";

	private ProfileActivity mC;

	// Animations
	private Animation mFadeIn;
	private Animation mFadeOut;

	private File mNewPic;

	ProfilePictureController(ProfileActivity c) {
		this.mC = c;

		mFadeIn = AnimationUtils.loadAnimation(mC, R.anim.fade_in);
		mFadeOut = AnimationUtils.loadAnimation(mC, R.anim.fade_out);
	}

	/**
	 * Fires off intent to select image from gallery
	 */
	void selectImage() {
		Crop.pickImage(mC);
	}

	void beginCrop(Uri source) {
		Uri outputUri = Uri.fromFile(new File(mC.getCacheDir(), "cropped"));
		new Crop(source).output(outputUri).asSquare().start(mC);
	}

	void handleCrop(int resultCode, Intent result) {
		if (resultCode == Activity.RESULT_OK) {
			mNewPic = new File(Crop.getOutput(result).getPath());
			mC.getNoozService().getSasForNewBlob(GlobalConstant.PROFILE_PIC_CONTAINER_NAME, mC.mUserId);
		} else if (resultCode == Crop.RESULT_ERROR) {
			Toast.makeText(mC, Crop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * Saves the chosen profile picture to the Azure blob url. Also removes the
	 * URL from the cache, so the new picture will show up.
	 * 
	 * @param sasUrl
	 */
	public void uploadProfilePicture(String sasUrl) {
		// Upload picture
		(new ImageUploaderTask(sasUrl)).execute();
		// Invalidate and remove Url from cache
		mC.getRequestQueue().getCache().invalidate(GlobalConstant.PROFILE_URL + mC.mUserId, true);
		mC.getRequestQueue().getCache().remove(GlobalConstant.PROFILE_URL + mC.mUserId);
	}

	/***
	 * Handles uploading an image to a specified url
	 */
	class ImageUploaderTask extends AsyncTask<Void, Void, Boolean> {
		private String mUrl;

		public ImageUploaderTask(String url) {
			mUrl = url;
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			try {
				FileInputStream fis = new FileInputStream(mNewPic);
				int bytesRead = 0;
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				byte[] b = new byte[1024];
				while ((bytesRead = fis.read(b)) != -1) {
					bos.write(b, 0, bytesRead);
				}
				byte[] bytes = bos.toByteArray();
				fis.close();
				// Post our image data (byte array) to the server
				URL url = new URL(mUrl.replace("\"", ""));
				HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
				urlConnection.setDoOutput(true);
				urlConnection.setRequestMethod("PUT");
				urlConnection.addRequestProperty("Content-Type", "image/jpeg");
				urlConnection.setRequestProperty("Content-Length", "" + bytes.length);
				// Write image data to server
				DataOutputStream wr = new DataOutputStream(urlConnection.getOutputStream());
				wr.write(bytes);
				wr.flush();
				wr.close();
				int response = urlConnection.getResponseCode();
				// If we successfully uploaded, return true
				if (response == 201 && urlConnection.getResponseMessage().equals("Created")) {
					return true;
				}
			} catch (Exception ex) {
				Log.e(TAG, ex.getMessage());
			}
			return false;
		}

		@Override
		protected void onPostExecute(Boolean uploaded) {
			if (uploaded) {
				Log.d(TAG, "Blob uploaded");
			}
		}
	}

}
