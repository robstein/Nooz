package com.nooz.nooz.activity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.location.Location;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.nooz.nooz.R;
import com.nooz.nooz.util.Tools;
import com.nooz.nooz.widget.CameraPreview;

public class MediaRecorderActivity extends BaseFragmentActivity implements OnClickListener,
		GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener {

	private static final String TAG = "MediaRecorderActivity";
	private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
	private static final int REQUEST_CODE_RECOVER_PLAY_SERVICES = 1001;
	public static final int MEDIA_TYPE_AUDIO = 0;
	public static final int MEDIA_TYPE_IMAGE = 1;
	public static final int MEDIA_TYPE_VIDEO = 2;
	public static final int TOP_BAR_HEIGHT = 61;

	private Camera mCamera;
	private CameraPreview mCameraPreview;
	private FrameLayout mFrameLayoutPreview;

	private ImageView mButtonCancelNewMedia;
	private ImageView mButtonCapturePicture;

	private MediaRecorder mMediaRecorder;
	private LinearLayout mMediaControlLayer;

	private LocationClient mLocationClient;
	private Location mCurrentLocation;
	private int mScreenWidthInPixels;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_media_recorder);

		// Move the control buttons down to where they should be
		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		mScreenWidthInPixels = size.x;
		mMediaControlLayer = (LinearLayout) findViewById(R.id.media_control_layer);
		RelativeLayout.LayoutParams controlLayoutParams = (RelativeLayout.LayoutParams) mMediaControlLayer
				.getLayoutParams();
		controlLayoutParams.setMargins(0, (int) Tools.dipToPixels(this, TOP_BAR_HEIGHT) + mScreenWidthInPixels, 0, 0);
		mMediaControlLayer.setLayoutParams(controlLayoutParams);

		// Create an instance of Camera
		mCamera = getCameraInstance();
		if (mCamera == null) {
			finish();
		}
		mCamera.setDisplayOrientation(90);
		Camera.Parameters camParams = mCamera.getParameters();
		camParams.setRotation(90);
		List<String> focusModes = camParams.getSupportedFocusModes();
		if (focusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
			camParams.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
		}
		List<Camera.Size> pictureSizes = camParams.getSupportedPictureSizes();
		camParams.setPictureSize(pictureSizes.get(0).width, pictureSizes.get(0).height);

		/*
		 * if
		 * (pictureSizes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE
		 * )) {
		 * camParams.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE
		 * ); } else if
		 * (pictureSizes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
		 * camParams.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO); }
		 */

		mCamera.setParameters(camParams);

		// Create our Preview view and set it as the content of our activity.
		mCameraPreview = new CameraPreview(this, mCamera);
		mFrameLayoutPreview = (FrameLayout) findViewById(R.id.camera_preview);
		mFrameLayoutPreview.addView(mCameraPreview);

		// Add a listener to the cancel button
		mButtonCancelNewMedia = (ImageView) findViewById(R.id.btn_cancel_new_media);
		mButtonCancelNewMedia.setOnClickListener(this);
		// Add a listener to the Capture button
		mButtonCapturePicture = (ImageView) findViewById(R.id.btn_snap_picture);
		mButtonCapturePicture.setOnClickListener(this);

		//
		mLocationClient = new LocationClient(this, this, this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_cancel_new_media:
			finish();
			break;
		case R.id.btn_snap_picture:
			mCamera.takePicture(null, null, mPictureCallback);
			break;
		}
	}

	/*
	 * Called when the Activity becomes visible.
	 */
	@Override
	protected void onStart() {
		super.onStart();
		// Connect the client.
		mLocationClient.connect();
	}

	@Override
	protected void onPause() {
		super.onPause();
		// releaseMediaRecorder(); // release MediaRecorder first
		releaseCamera(); // release the camera immediately on pause event
	}

	/*
	 * Called when the Activity is no longer visible.
	 */
	@Override
	protected void onStop() {
		// Disconnecting the client invalidates it.
		mLocationClient.disconnect();
		super.onStop();
	}

	private PictureCallback mPictureCallback = new PictureCallback() {

		@Override
		public void onPictureTaken(byte[] data, Camera camera) {

			// Save to file:
			Bitmap bmp = scaleDownBitmap(BitmapFactory.decodeByteArray(data, 0, data.length), mScreenWidthInPixels/3, mContext);
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			// compress because when we don't we get a failed binder transaction
			bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
			byte[] bytes = stream.toByteArray();

			// Pass byte array to NewArticleActivity
			Bundle args = new Bundle();
			args.putByteArray("image", bytes);
			args.putParcelable("location", mCurrentLocation);

			Intent newStoryIntent = new Intent(getApplicationContext(), NewArticleActivity.class);
			newStoryIntent.putExtra("bundle", args);
			startActivity(newStoryIntent);
			finish();
		}
	};

	private static Bitmap scaleDownBitmap(Bitmap photo, int newHeight, Context context) {
		final float densityMultiplier = context.getResources().getDisplayMetrics().density;
		int h = (int) (newHeight * densityMultiplier);
		int w = (int) (h * photo.getWidth() / ((double) photo.getHeight()));
		photo = Bitmap.createScaledBitmap(photo, w, h, true);
		return photo;
	}

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
			Log.d(TAG, "Camera is not available (in use or does not exist) " + e.getMessage());
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

	/*
	 * Handle results returned to the FragmentActivity by Google Play services
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// Decide what to do based on the original request code
		switch (requestCode) {
		case CONNECTION_FAILURE_RESOLUTION_REQUEST:
			/*
			 * If the result code is Activity.RESULT_OK, try to connect again
			 */
			switch (resultCode) {
			case Activity.RESULT_OK:
				/*
				 * Try the request again
				 */
				break;
			}
		}
	}

	private boolean servicesConnected() {
		// Check that Google Play services is available
		int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
		// If Google Play services is available
		if (ConnectionResult.SUCCESS == resultCode) {
			// In debug mode, log the status
			Log.d("Location Updates", "Google Play services is available.");
			// Continue
			return true;
			// Google Play services was not available for some reason
		} else {
			// Get the error dialog from Google Play services
			Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(resultCode, this,
					CONNECTION_FAILURE_RESOLUTION_REQUEST);

			// If Google Play services can provide an error dialog
			if (errorDialog != null) {
				// Create a new DialogFragment for the error dialog
				ErrorDialogFragment errorFragment = new ErrorDialogFragment();
				// Set the dialog in the DialogFragment
				errorFragment.setDialog(errorDialog);
				// Show the error dialog in the DialogFragment
				errorFragment.show(getSupportFragmentManager(), "Location Updates");
			}
			return false;
		}
	}

	/*
	 * Called by Location Services when the request to connect the client
	 * finishes successfully. At this point, you can request the current
	 * location or start periodic updates
	 */
	@Override
	public void onConnected(Bundle connectionHint) {
		// Display the connection status
		Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show();
		mCurrentLocation = mLocationClient.getLastLocation();
	}

	/*
	 * Called by Location Services if the connection to the location client
	 * drops because of an error.
	 */
	@Override
	public void onDisconnected() {
		// Display the connection status
		Toast.makeText(this, "Disconnected. Please re-connect.", Toast.LENGTH_SHORT).show();
	}

	/*
	 * Called by Location Services if the attempt to Location Services fails.
	 */
	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {
		/*
		 * Google Play services can resolve some errors it detects. If the error
		 * has a resolution, try sending an Intent to start a Google Play
		 * services activity that can resolve error.
		 */
		if (connectionResult.hasResolution()) {
			try {
				// Start an Activity that tries to resolve the error
				connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
				/*
				 * Thrown if Google Play services canceled the original
				 * PendingIntent
				 */
			} catch (IntentSender.SendIntentException e) {
				// Log the error
				e.printStackTrace();
			}
		} else {
			/*
			 * If no resolution is available, display a dialog to the user with
			 * the error.
			 */
			showErrorDialog(connectionResult.getErrorCode());
		}
	}

	void showErrorDialog(int code) {
		GooglePlayServicesUtil.getErrorDialog(code, this, REQUEST_CODE_RECOVER_PLAY_SERVICES).show();
	}

}
