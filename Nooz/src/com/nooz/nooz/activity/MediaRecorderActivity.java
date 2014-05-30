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
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.location.Location;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
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
import com.nooz.nooz.util.Alert;
import com.nooz.nooz.util.MediaMode;
import com.nooz.nooz.util.Tools;
import com.nooz.nooz.widget.CameraPreview;

public class MediaRecorderActivity extends BaseFragmentActivity implements
		GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener {

	private static final String TAG = "MediaRecorderActivity";
	private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
	private static final int REQUEST_CODE_RECOVER_PLAY_SERVICES = 1001;
	public static final int MEDIA_TYPE_AUDIO = 0;
	public static final int MEDIA_TYPE_IMAGE = 1;
	public static final int MEDIA_TYPE_VIDEO = 2;
	public static final int TOP_BAR_HEIGHT = 61;
	private static final int COLOR_RED = 0xFFFF0000;
	private static final int COLOR_WHITE = 0xFFFFFFFF;

	private Camera mCamera;
	private CameraPreview mCameraPreview;
	private FrameLayout mFrameLayoutPreview;

	private ImageView mButtonCancelNewMedia;
	private LinearLayout mMediaControlLayer;
	private ImageView mButtonRecordAudio;
	private ImageView mButtonCapturePicture;
	private ImageView mButtonRecordVideo;

	private LocationClient mLocationClient;
	private Location mCurrentLocation;
	private int mScreenWidthInPixels;
	protected MediaMode mMode = MediaMode.PICTURE;
	protected boolean mIsRecordingAudio = false;
	protected boolean mIsCapturingPicture = false;
	protected boolean mIsRecordingVideo = false;

	/* ***** ACTIVITY SETUP BEGIN ***** */
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
		Camera.Parameters camParams = mCamera.getParameters();
		camParams.setRotation(90);
		mCamera.setParameters(camParams);
		mCamera.setDisplayOrientation(90);

		List<String> focusModes = camParams.getSupportedFocusModes();
		if (focusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
			camParams.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
		}
		
		// Create our Preview view and set it as the content of our activity.
		mCameraPreview = new CameraPreview(this, mCamera);
		mFrameLayoutPreview = (FrameLayout) findViewById(R.id.camera_preview);
		mFrameLayoutPreview.addView(mCameraPreview);

		// Add a listener to the cancel button
		mButtonCancelNewMedia = (ImageView) findViewById(R.id.btn_cancel_new_media);
		mButtonCancelNewMedia.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		// Add a listener to the audio record button
		mButtonRecordAudio = (ImageView) findViewById(R.id.btn_record_audio);
		mButtonRecordAudio.setOnTouchListener(new AudioButtonTouchListner());
		// Add a listener to the Capture button
		mButtonCapturePicture = (ImageView) findViewById(R.id.btn_snap_picture);
		mButtonCapturePicture.setOnTouchListener(new CameraButtonTouchListner());
		// Add a listener to the Capture button
		mButtonRecordVideo = (ImageView) findViewById(R.id.btn_record_video);
		mButtonRecordVideo.setOnTouchListener(new VideoButtonTouchListner());

		//
		mLocationClient = new LocationClient(this, this, this);
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

	/*
	 * Called when the Activity becomes visible.
	 */
	@Override
	protected void onStart() {
		super.onStart();
		// Connect the client.
		if (servicesConnected()) {
			mLocationClient.connect();
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

	@Override
	protected void onPause() {
		super.onPause();
		releaseRecorder(); // release MediaRecorder first
		releaseCamera(); // release the camera immediately on pause event
	}

	private void releaseRecorder() {
		if (mRecorder != null) {
			mRecorder.release(); // release the recorder object
			mRecorder = null;
		}
	}

	private void releaseCamera() {
		if (mCamera != null) {
			mCamera.release(); // release the camera for other applications
			mCamera = null;
		}
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

	/*
	 * Called by Location Services if the connection to the location client
	 * drops because of an error.
	 */
	@Override
	public void onDisconnected() {
		// Display the connection status
		Toast.makeText(this, "Disconnected. Please re-connect.", Toast.LENGTH_SHORT).show();
	}

	/* ***** ACTIVITY SETUP END ***** */

	/* ***** AUDIO RECORDING BEGIN ***** */

	private MediaRecorder mRecorder = null;

	private void startRecording() {
		mRecorder = new MediaRecorder();
		mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
		mRecorder.setOutputFile(getFilesDir().getAbsolutePath() + "/audio.3gp");
		mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
		try {
			mRecorder.prepare();
		} catch (IOException e) {
			Log.e(TAG, "prepare() failed");
		}
		mRecorder.start();
	}

	private void stopRecording() {
		mRecorder.stop();
		mRecorder.release();
		mRecorder = null;
	}

	/* ***** AUDIO RECORDING END ***** */

	/* ***** PICTURE CAPTURING BEGIN ***** */

	private PictureCallback mPictureCallback = new PictureCallback() {

		@Override
		public void onPictureTaken(byte[] data, Camera camera) {
			// Save to file:
			Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			bmp.compress(Bitmap.CompressFormat.JPEG, 100, stream);
			byte[] bytes = stream.toByteArray();
			// convert array of bytes into file
			try {
				FileOutputStream fileOuputStream = new FileOutputStream(getFilesDir().getAbsolutePath()
						+ "/picture.jpg");
				fileOuputStream.write(bytes);
				fileOuputStream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// Go to NewArticleActivity
			Bundle args = new Bundle();
			args.putParcelable("location", mCurrentLocation);
			args.putCharSequence("medium", mMode.toString());
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

	/* ***** PICTURE CAPTURING END ***** */

	/* ***** CONTROL BUTTON ONTOUCHLISTENERS BEGIN ***** */

	private class AudioButtonTouchListner implements OnTouchListener {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			// Disable if using another mode
			if (!mIsCapturingPicture && !mIsRecordingVideo) {
				// If not disabled:
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					if (mMode != MediaMode.AUDIO) {
						// If we aren't in audio record mode
						// Do nothing until release
					} else {
						// If we are in audio record mode
						if (!mIsRecordingAudio) {
							// If we aren't recording, but we just clicked to do
							// so
						} else {
							// If we were just recording, but just clicked to
							// stop
						}
					}
					return true;
				}

				if (event.getAction() == MotionEvent.ACTION_UP) {

					if (mMode != MediaMode.AUDIO) {
						// If we aren't in audio record mode
						// Change highlight circle
						switch (mMode) {
						case PICTURE:
							// Un-highlight camera
							mButtonCapturePicture.setImageResource(R.drawable.camera_grey);
							break;
						case VIDEO:
							// Un-highlight video recorder
							mButtonRecordVideo.setImageResource(R.drawable.recorder_grey);
							break;
						default:
							break;
						}
						// Highlight Mic
						mButtonRecordAudio.setImageResource(R.drawable.mic_active);
						// then put us in audio record mode
						mMode = MediaMode.AUDIO;

					} else {
						// If we are in audio record mode

						// Check to make sure location is not null
						if (mCurrentLocation == null) {
							Alert.createAndShowDialog("Please turn on Locations Services", "Location not found",
									mContext);
							return false;
						} else {

							if (!mIsRecordingAudio) {
								// If we literally just clicked to record
								Drawable button = getResources().getDrawable(R.drawable.mic_active);
								button.setColorFilter(COLOR_RED, Mode.MULTIPLY);
								((ImageView) v).setImageDrawable(button);

								// start doing recording stuff
								startRecording();

								// Set recording flag
								mIsRecordingAudio = true;
							} else {
								// If we literally just clicked to stop
								Drawable button = getResources().getDrawable(R.drawable.mic_active);
								button.setColorFilter(COLOR_WHITE, Mode.SRC_ATOP);
								((ImageView) v).setImageDrawable(button);

								// start saving it and moving on
								stopRecording();

								// Clear recording flag
								mIsRecordingAudio = false;

								// Go to NewArticleActivity
								Bundle args = new Bundle();
								args.putParcelable("location", mCurrentLocation);
								args.putCharSequence("medium", mMode.toString());
								Intent newStoryIntent = new Intent(getApplicationContext(), NewArticleActivity.class);
								newStoryIntent.putExtra("bundle", args);
								startActivity(newStoryIntent);
								finish();
							}
						}
					}
					return true;
				}
			}
			return false;
		}
	}

	private class CameraButtonTouchListner implements OnTouchListener {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			// Disable if using another mode
			if (!mIsRecordingAudio && !mIsRecordingVideo) {
				// If not disabled:
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					if (mMode != MediaMode.PICTURE) {
						// If we aren't in cameara mode
						// Do nothing until release
					} else {
						// If we are in camera mode

						// Check to make sure location is not null
						if (mCurrentLocation == null) {
							Alert.createAndShowDialog("Please turn on Locations Services", "Location not found",
									mContext);
							return false;
						} else {

							// We just clicked so make it show that
							Drawable button = getResources().getDrawable(R.drawable.camera_active);
							button.setColorFilter(COLOR_RED, Mode.MULTIPLY);
							((ImageView) v).setImageDrawable(button);
							// Set capturing picture flag
							mIsCapturingPicture = true;

							// take the picture
							// picturecallback will save the picture
							// picturecallback also launches new intent
							mCamera.takePicture(null, null, mPictureCallback);
						}
					}
					return true;
				}

				if (event.getAction() == MotionEvent.ACTION_UP) {

					if (mMode != MediaMode.PICTURE) {
						// If we aren't in picture mode
						// Change highlight circle

						switch (mMode) {
						case AUDIO:
							// Un-highlight mic
							mButtonRecordAudio.setImageResource(R.drawable.mic_grey);
							break;
						case VIDEO:
							// Un-highlight video recorder
							mButtonRecordVideo.setImageResource(R.drawable.recorder_grey);
							break;
						default:
							break;
						}
						// Highlight camera
						mButtonCapturePicture.setImageResource(R.drawable.camera_active);
						// then put us in picture mode
						mMode = MediaMode.PICTURE;

					} else {
						// If we are in picture mode:

						// Check to make sure location is not null
						if (mCurrentLocation == null) {
							return false;
						} else {

							// Draw the button back
							Drawable button = getResources().getDrawable(R.drawable.camera_active);
							button.setColorFilter(COLOR_WHITE, Mode.SRC_ATOP);
							((ImageView) v).setImageDrawable(button);
							// Clear capturing picture flag
							mIsCapturingPicture = false;
						}
					}
					return true;

				}
			}
			return false;
		}
	}

	private class VideoButtonTouchListner implements OnTouchListener {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			// Disable if using another mode
			if (!mIsRecordingAudio && !mIsCapturingPicture) {
				// If not disabled:
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					if (mMode != MediaMode.VIDEO) {
						// If we aren't in video record mode
						// Do nothing until release
					} else {
						// If we are in video record mode
						if (!mIsRecordingVideo) {
							// If we aren't recording, but we just clicked to do
							// so
						} else {
							// If we were just recording, but just clicked to
							// stop
						}
					}
					return true;
				}

				if (event.getAction() == MotionEvent.ACTION_UP) {

					if (mMode != MediaMode.VIDEO) {
						// If we aren't in video record mode
						// Change highlight circle
						switch (mMode) {
						case AUDIO:
							// Un-highlight mic
							mButtonRecordAudio.setImageResource(R.drawable.mic_grey);
							break;
						case PICTURE:
							// Un-highlight camera
							mButtonCapturePicture.setImageResource(R.drawable.camera_grey);
							break;
						default:
							break;
						}
						// Highlight Video recorder
						mButtonRecordVideo.setImageResource(R.drawable.recorder_active);
						// then put us in video record mode
						mMode = MediaMode.VIDEO;

					} else {
						// Check to make sure location is not null
						if (mCurrentLocation == null) {
							Alert.createAndShowDialog("Please turn on Locations Services", "Location not found",
									mContext);
							return false;
						} else {
							// If we are in video record mode
							if (!mIsRecordingVideo) {
								// If we literally just clicked to record
								Drawable button = getResources().getDrawable(R.drawable.recorder_active);
								button.setColorFilter(COLOR_RED, Mode.MULTIPLY);
								((ImageView) v).setImageDrawable(button);
								mIsRecordingVideo = true;

								// start doing recording stuff
							} else {
								// If we literally just clicked to stop
								Drawable button = getResources().getDrawable(R.drawable.recorder_active);
								button.setColorFilter(COLOR_WHITE, Mode.SRC_ATOP);
								((ImageView) v).setImageDrawable(button);
								mIsRecordingVideo = false;

								// start saving it and moving on
							}
						}
					}
					return true;
				}
			}
			return false;
		}
	}

	/* ***** CONTROL BUTTON ONTOUCHLISTENERS END ***** */

	/* ***** GOOGLE PLAY SERVICES BLOAT BEGIN ***** */

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

	/* ***** GOOGLE PLAY SERVICES BLOAT END ***** */

}
