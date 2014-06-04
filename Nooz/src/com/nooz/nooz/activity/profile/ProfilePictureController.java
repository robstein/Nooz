package com.nooz.nooz.activity.profile;

import com.nooz.nooz.R;
import com.squareup.picasso.Picasso;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

public class ProfilePictureController {

	private static final String TAG = "ProfilePictureController";

	private ProfileActivity mC;

	// Animations
	private Animation mFadeIn;
	private Animation mFadeOut;

	private Uri mNewProfilePictureUri;

	ProfilePictureController(ProfileActivity c) {
		this.mC = c;

		mFadeIn = AnimationUtils.loadAnimation(mC, R.anim.fade_in);
		mFadeOut = AnimationUtils.loadAnimation(mC, R.anim.fade_out);
	}

	/**
	 * Fires off intent to select image from gallery
	 */
	void selectImage() {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("image/*");
		mC.startActivityForResult(intent, ProfileActivity.RESULT_LOAD_IMAGE);
	}

	/**
	 * Called directly from onActivityResult to handle profile pictures
	 * selection from gallery.
	 * 
	 * @param requestCode
	 * @param resultCode
	 * @param data
	 */
	public void handleResultFromGallery(int requestCode, int resultCode, Intent data) {
		try {
			// handle result from gallery select
			if (requestCode == ProfileActivity.RESULT_LOAD_IMAGE) {
				Uri currImageURI = data.getData();
				mNewProfilePictureUri = currImageURI;

				// Morph layout into profile picture cropping mode.
				morphLayoutToCropPictureMode();
				
				// Set the image view's image by using imageUri
				Picasso.with(mC).load(currImageURI).into(mC.mButtonPictureUncropped);
			}
		} catch (Exception ex) {
			Log.e(TAG, "An error occured loading picture from gallery: " + ex.getMessage());
		}
	}

	private void morphLayoutToCropPictureMode() {
		mC.mProfileLayout.setVisibility(View.INVISIBLE);
		mC.mProfileLayout.startAnimation(mFadeOut);
		mC.mCropPictureLayout.setVisibility(View.VISIBLE);
		mC.mCropPictureLayout.startAnimation(mFadeIn);
	}

	public void cancel() {
		mC.mProfileLayout.setVisibility(View.VISIBLE);
		mC.mProfileLayout.startAnimation(mFadeIn);
		mC.mCropPictureLayout.setVisibility(View.INVISIBLE);
		mC.mCropPictureLayout.startAnimation(mFadeOut);		
	}

	public void confirm() {
		// TODO Auto-generated method stub
		
	}

}
