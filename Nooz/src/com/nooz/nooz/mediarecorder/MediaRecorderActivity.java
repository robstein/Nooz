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
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.commonsware.cwac.camera.PictureTransaction;
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
public class MediaRecorderActivity extends BaseLocationFragmentActivity implements OnClickListener {

	private static final String TAG = "MediaRecorderActivity";
	public static final int MEDIA_TYPE_AUDIO = 0;
	public static final int MEDIA_TYPE_IMAGE = 1;
	public static final int MEDIA_TYPE_VIDEO = 2;
	private static final int COLOR_RED = 0xFFFF0000;
	private static final int COLOR_WHITE = 0xFFFFFFFF;

	private ImageView mButtonCancelNewMedia;
	private RelativeLayout mRelativeLayoutCamera;
	NoozCameraFragment mCameraFragment;
	NoozAudioFragment mAudioFragment;
	private ImageView mButtonRecordAudio;
	ImageView mButtonCapturePicture;
	private ImageView mButtonRecordVideo;

	int mScreenWidthInPixels;
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
		initNoozMediaFragments();
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
		mButtonRecordAudio = (ImageView) findViewById(R.id.btn_record_audio);
		mButtonCapturePicture = (ImageView) findViewById(R.id.btn_snap_picture);
		mButtonRecordVideo = (ImageView) findViewById(R.id.btn_record_video);
	}

	private void initNoozMediaFragments() {
		// Camera fragment
		mCameraFragment = NoozCameraFragment.newInstance();
		getSupportFragmentManager().beginTransaction().replace(R.id.container, mCameraFragment).commit();

		// Audio fragment
		mAudioFragment = NoozAudioFragment.newInstance();
	}

	private void initViewListeners() {
		// Add a listener to the cancel button
		mButtonCancelNewMedia.setOnClickListener(this);
		// Add listeners to the control buttons
		mButtonRecordAudio.setOnClickListener(this);
		mButtonCapturePicture.setOnClickListener(this);
		mButtonRecordVideo.setOnClickListener(this);
		// Set auto focus click listener
		mRelativeLayoutCamera.setOnClickListener(this);
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

		Runnable mAudioIntensityRunnable = new Runnable() {

			@Override
			public void run() {

				while (true) {

					try {
						Thread.sleep(250);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

					if (mRecorder != null) {
						int amplitude = mRecorder.getMaxAmplitude();
						mAudioFragment.reportAmplitude(amplitude);
						// Here you can put condition (low/high)
						Log.i("AMPLITUDE", "" + amplitude);

					}

				}
			}
		};
		new Thread(mAudioIntensityRunnable).start();
	}

	private void stopRecording() {
		mRecorder.stop();
		mRecorder.release();
		mRecorder = null;
	}

	/* ***** AUDIO RECORDING END ***** */

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_cancel_new_media:
			finish();
			break;
		case R.id.btn_record_audio:
			handleAudioClick();
			break;
		case R.id.btn_snap_picture:
			handlePictureClick();
			break;
		case R.id.btn_record_video:
			handleVideoClick();
			break;
		case R.id.camera_layout_camera:
			mCameraFragment.autoFocus();
			break;
		}
	}

	private void handleAudioClick() {
		// Disable if using another mode
		if (!mIsCapturingPicture && !mIsRecordingVideo) {
			if (mMode != MediaMode.AUDIO) {
				// If we aren't in audio record mode

				// Change highlight circle
				switch (mMode) {
				case PICTURE:
					// Un-highlight camera
					mButtonCapturePicture.setImageResource(R.drawable.selector_button_camera_grey);
					break;
				case VIDEO:
					// Un-highlight video recorder
					mButtonRecordVideo.setImageResource(R.drawable.selector_button_recorder_grey);
					break;
				default:
					break;
				}

				// Highlight Mic
				mButtonRecordAudio.setImageResource(R.drawable.selector_button_mic_active);

				// Change camera to audio recorder fragment
				getSupportFragmentManager().beginTransaction().replace(R.id.container, mAudioFragment).commit();

				// then put us in audio record mode
				mMode = MediaMode.AUDIO;

			} else {
				// If we are in audio record mode

				// Check to make sure location is not null
				if (mCurrentLocation == null) {
					Alert.createAndShowDialog("Please turn on Locations Services", "Location not found", mContext);
					return;
				} else {
					if (!mIsRecordingAudio) {
						// If we literally just clicked to record
						mButtonRecordAudio.setImageResource(R.drawable.selector_button_mic_active_recording);

						// start doing recording stuff
						startRecording();

						// Set recording flag
						mIsRecordingAudio = true;
					} else {
						// If we literally just clicked to stop
						mButtonRecordAudio.setImageResource(R.drawable.selector_button_mic_active);

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
		}
	}

	private void handlePictureClick() {
		// Disable if using another mode
		if (!mIsRecordingAudio && !mIsRecordingVideo) {
			// If not disabled:
			if (mMode != MediaMode.PICTURE) {
				// If we aren't in camera mode

				// Change highlight circle
				switch (mMode) {
				case AUDIO:
					// Un-highlight mic
					mButtonRecordAudio.setImageResource(R.drawable.selector_button_mic_grey);
					// Switch fragments
					getSupportFragmentManager().beginTransaction().replace(R.id.container, mCameraFragment).commit();
					break;
				case VIDEO:
					// Un-highlight video recorder
					mButtonRecordVideo.setImageResource(R.drawable.selector_button_recorder_grey);
					break;
				default:
					break;
				}

				// Highlight camera
				mButtonCapturePicture.setImageResource(R.drawable.selector_button_camera_active);
				// then put us in picture mode
				mMode = MediaMode.PICTURE;
			} else {
				// Check to make sure location is not null
				if (mCurrentLocation == null) {
					Alert.createAndShowDialog("Please turn on Locations Services", "Location not found", mContext);
					return;
				} else {
					// Set capturing picture flag
					mIsCapturingPicture = true;

					// Disable Control buttons
					mButtonCapturePicture.setEnabled(false);

					// take the picture/save the picture
					mCameraFragment.takePicture(new PictureTransaction(mCameraFragment.getHost()));
				}
			}
		}
	}

	private void handleVideoClick() {
		// Disable if using another mode
		if (!mIsRecordingAudio && !mIsCapturingPicture) {
			if (mMode != MediaMode.VIDEO) {
				// If we aren't in video record mode
				// Change highlight circle
				switch (mMode) {
				case AUDIO:
					// Un-highlight mic
					mButtonRecordAudio.setImageResource(R.drawable.selector_button_mic_grey);
					// Switch fragments
					getSupportFragmentManager().beginTransaction().replace(R.id.container, mCameraFragment).commit();
					break;
				case PICTURE:
					// Un-highlight camera
					mButtonCapturePicture.setImageResource(R.drawable.selector_button_camera_grey);
					break;
				default:
					break;
				}
				// Highlight Video recorder
				mButtonRecordVideo.setImageResource(R.drawable.selector_button_recorder_active);
				// then put us in video record mode
				mMode = MediaMode.VIDEO;

			} else {
				// If we are in audio record mode

				// Check to make sure location is not null
				if (mCurrentLocation == null) {
					Alert.createAndShowDialog("Please turn on Locations Services", "Location not found", mContext);
					return;
				} else {
					// If we are in video record mode
					if (!mIsRecordingVideo) {
						// If we literally just clicked to record
						mButtonRecordVideo.setImageResource(R.drawable.selector_button_recorder_active_recording);

						mIsRecordingVideo = true;

						// start doing recording stuff
					} else {
						// If we literally just clicked to stop
						mButtonRecordVideo.setImageResource(R.drawable.selector_button_recorder_active);

						mIsRecordingVideo = false;

						// start saving it and moving on
					}
				}
			}
		}
	}

	void launchNewArticleActivity() {
		Bundle args = new Bundle();
		args.putParcelable("location", mCurrentLocation);
		args.putCharSequence("medium", mMode.toString());
		Intent newStoryIntent = new Intent(getApplicationContext(), NewArticleActivity.class);
		newStoryIntent.putExtra("bundle", args);
		startActivity(newStoryIntent);
		finish();
	}

}
