package com.nooz.nooz.activity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.nooz.nooz.R;
import com.nooz.nooz.util.Tools;
import com.nooz.nooz.widget.CameraPreview;

public class MediaRecorderActivity extends Activity {
	private static final String TAG = "MediaRecorderActivity";

	private Camera mCamera;
	private CameraPreview mPreview;
	private MediaRecorder mMediaRecorder;
	private LinearLayout mMediaControlLayer;
	
	private int mScreenWidthInPixels;

	public static final int MEDIA_TYPE_IMAGE = 1;
	public static final int MEDIA_TYPE_VIDEO = 2;

	public static final int TOP_BAR_HEIGHT = 61;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_media_recorder);

		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		int mScreenWidthInPixels = size.x;
		mMediaControlLayer = (LinearLayout) findViewById(R.id.media_control_layer);
		RelativeLayout.LayoutParams controlLayoutParams = (RelativeLayout.LayoutParams) mMediaControlLayer
				.getLayoutParams();
		controlLayoutParams.setMargins(0, (int) Tools.dipToPixels(this, TOP_BAR_HEIGHT) + mScreenWidthInPixels, 0, 0);
		mMediaControlLayer.setLayoutParams(controlLayoutParams);

		// Create an instance of Camera
		mCamera = getCameraInstance();
		mCamera.setDisplayOrientation(90);
		Camera.Parameters camParams = mCamera.getParameters();
		camParams.setRotation(90);
		//camParams.set("orientation", "portrait");
		List<String> focusModes = camParams.getSupportedFocusModes();
		if (focusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
			camParams.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
		}
		List<Camera.Size> pictureSizes = camParams.getSupportedPictureSizes();
		camParams.setPictureSize(pictureSizes.get(0).width, pictureSizes.get(0).height);
		List<Camera.Size> previewSizes = camParams.getSupportedPreviewSizes();
		camParams.setPictureSize(previewSizes.get(0).width, previewSizes.get(0).height);

		if (pictureSizes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
			camParams.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
		}
		
		mCamera.setParameters(camParams);

		// Create our Preview view and set it as the content of our activity.
		mPreview = new CameraPreview(this, mCamera);
		FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
		preview.addView(mPreview);

		// Add a listener to the Capture button
		ImageView captureButton = (ImageView) findViewById(R.id.btn_snap_picture);
		captureButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// get an image from the camera
				mCamera.takePicture(null, null, mPictureCallback);
			}
		});
	}

	@Override
	protected void onPause() {
		super.onPause();
		// releaseMediaRecorder(); // release MediaRecorder first
		releaseCamera(); // release the camera immediately on pause event
	}

	private PictureCallback mPictureCallback = new PictureCallback() {

		@Override
		public void onPictureTaken(byte[] data, Camera camera) {
			
			int[] pixels = new int[mScreenWidthInPixels*mScreenWidthInPixels];//the size of the array is the dimensions of the sub-photo
	        ByteArrayOutputStream bos = new ByteArrayOutputStream();
	        Bitmap bitmap = BitmapFactory.decodeByteArray(data , 0, data.length);
	        bitmap.getPixels(pixels, 0, 1, 0, 0, mScreenWidthInPixels, mScreenWidthInPixels);//the stride value is (in my case) the width value
	        bitmap = Bitmap.createBitmap(pixels, 0, 1, mScreenWidthInPixels, mScreenWidthInPixels, Config.ARGB_8888);//ARGB_8888 is a good quality configuration
	        bitmap.compress(CompressFormat.JPEG, 100, bos);//100 is the best quality possibe
	        byte[] square = bos.toByteArray();
			
			//Save to file:
			File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
			if (pictureFile == null) {
				Log.d(TAG, "Error creating media file, check storage permissions");
				return;
			}

			try {
				FileOutputStream fos = new FileOutputStream(pictureFile);
				fos.write(square);
				fos.close();
			} catch (FileNotFoundException e) {
				Log.d(TAG, "File not found: " + e.getMessage());
			} catch (IOException e) {
				Log.d(TAG, "Error accessing file: " + e.getMessage());
			}
			
			// Pass bypte array to NewArticleActivity
			Intent newStoryIntent = new Intent(getApplicationContext(), NewArticleActivity.class);
			newStoryIntent.putExtra("image", square);
			newStoryIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
			startActivity(newStoryIntent);
			finish();
		}
	};

	/** Create a File for saving the image */
	private static File getOutputMediaFile(int type) {
		// To be safe, you should check that the SDCard is mounted
		// using Environment.getExternalStorageState() before doing this.
		File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
				"Nooz");
		// This location works best if you want the created images to be shared
		// between applications and persist after your app has been uninstalled.

		// Create the storage directory if it does not exist
		if (!mediaStorageDir.exists()) {
			if (!mediaStorageDir.mkdirs()) {
				Log.d("MyCameraApp", "failed to create directory");
				return null;
			}
		}

		// Create a media file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		File mediaFile;
		if (type == MEDIA_TYPE_IMAGE) {
			mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");
		} else if (type == MEDIA_TYPE_VIDEO) {
			mediaFile = new File(mediaStorageDir.getPath() + File.separator + "VID_" + timeStamp + ".mp4");
		} else {
			return null;
		}
		return mediaFile;
	}

	/** A safe way to get an instance of the Camera object. */
	public static Camera getCameraInstance() {
		Camera c = null;
		try {
			c = Camera.open(); // attempt to get a Camera instance
		} catch (Exception e) {
			// Camera is not available (in use or does not exist)
		}
		return c; // returns null if camera is unavailable
	}

	private void releaseMediaRecorder() {
		if (mMediaRecorder != null) {
			mMediaRecorder.reset(); // clear recorder configuration
			mMediaRecorder.release(); // release the recorder object
			mMediaRecorder = null;
			mCamera.lock(); // lock camera for later use
		}
	}

	private void releaseCamera() {
		if (mCamera != null) {
			mCamera.release(); // release the camera for other applications
			mCamera = null;
		}
	}

	private int pixelsToDips(int i) {
		Resources r = getResources();
		float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, i, r.getDisplayMetrics());
		return (int) Math.floor(px);
	}
}
