package com.nooz.nooz.mediarecorder;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.nooz.nooz.R;
import com.nooz.nooz.activity.BaseLocationFragmentActivity;
import com.nooz.nooz.activity.NewArticleActivity;
import com.nooz.nooz.util.Alert;
import com.nooz.nooz.util.MediaMode;

/**
 * 
 * @author Rob Stein
 * 
 */
public class MediaRecorderActivity extends BaseLocationFragmentActivity {

	private static final String TAG = "MediaRecorderActivity";
	public static final int MEDIA_TYPE_AUDIO = 0;
	public static final int MEDIA_TYPE_IMAGE = 1;
	public static final int MEDIA_TYPE_VIDEO = 2;
	private static final int COLOR_RED = 0xFFFF0000;
	private static final int COLOR_WHITE = 0xFFFFFFFF;

	private ImageView mButtonCancelNewMedia;
	private RelativeLayout mRelativeLayoutCamera;
	NoozCameraFragment mCameraFragment;
	private ImageView mButtonRecordAudio;
	private ImageView mButtonCapturePicture;
	private ImageView mButtonRecordVideo;

	private int mScreenWidthInPixels;
	protected MediaMode mMode;
	protected boolean mIsRecordingAudio;
	protected boolean mIsCapturingPicture;
	protected boolean mIsRecordingVideo;

	/* ***** ACTIVITY SETUP BEGIN ***** */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initFields();
		initViews();
		initViewListeners();
		initScreenMeasurements();
		initSquareCameraParameters();
	}

	private void initFields() {
		mMode = MediaMode.PICTURE;
		mIsRecordingAudio = false;
		mIsCapturingPicture = false;
		mIsRecordingVideo = false;
	}

	private void initViews() {
		setContentView(R.layout.activity_media_recorder);
		mButtonCancelNewMedia = (ImageView) findViewById(R.id.btn_cancel_new_media);
		mRelativeLayoutCamera = (RelativeLayout) findViewById(R.id.camera_layout_camera);
		mCameraFragment = (NoozCameraFragment) getFragmentManager().findFragmentById(R.id.camera_preview);
		mButtonRecordAudio = (ImageView) findViewById(R.id.btn_record_audio);
		mButtonCapturePicture = (ImageView) findViewById(R.id.btn_snap_picture);
		mButtonRecordVideo = (ImageView) findViewById(R.id.btn_record_video);
	}

	private void initViewListeners() {
		// Add a listener to the cancel button
		mButtonCancelNewMedia.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		// Add listeners to the control buttons
		mButtonRecordAudio.setOnTouchListener(new AudioButtonTouchListner());
		mButtonCapturePicture.setOnTouchListener(new CameraButtonTouchListner());
		mButtonRecordVideo.setOnTouchListener(new VideoButtonTouchListner());
	}

	private void initScreenMeasurements() {
		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		mScreenWidthInPixels = size.x;
	}

	private void initSquareCameraParameters() {
		RelativeLayout.LayoutParams cameraLayoutParams = (RelativeLayout.LayoutParams) mRelativeLayoutCamera
				.getLayoutParams();
		cameraLayoutParams.height = mScreenWidthInPixels;
		cameraLayoutParams.width = mScreenWidthInPixels;
		mRelativeLayoutCamera.setLayoutParams(cameraLayoutParams);
	}

	/*
	 * Called when the Activity becomes visible.
	 */
	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onPause() {
		super.onPause();
		releaseRecorder(); // release MediaRecorder first
	}

	private void releaseRecorder() {
		if (mRecorder != null) {
			mRecorder.release(); // release the recorder object
			mRecorder = null;
		}
	}

	/*
	 * Called when the Activity is no longer visible.
	 */
	@Override
	protected void onStop() {
		super.onStop();
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
							mCameraFragment.takeSimplePicture();
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

}
