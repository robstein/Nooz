package com.nooz.nooz.mediarecorder;

import java.io.File;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.Face;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.widget.Toast;

import com.commonsware.cwac.camera.CameraFragment;
import com.commonsware.cwac.camera.CameraHost;
import com.commonsware.cwac.camera.PictureTransaction;
import com.commonsware.cwac.camera.SimpleCameraHost;

public class NoozCameraFragment extends CameraFragment {

	public static NoozCameraFragment newInstance() {
		return new NoozCameraFragment();
	}

	@Override
	public void onCreate(Bundle state) {
		super.onCreate(state);

		SimpleCameraHost.Builder builder = new SimpleCameraHost.Builder(new NoozCameraHost(getActivity()));
		setHost(builder.useFullBleedPreview(true).build());
	}

	class NoozCameraHost extends SimpleCameraHost implements Camera.FaceDetectionListener {

		boolean supportsFaces = false;
		int mScreenWidthInPixels;

		public NoozCameraHost(Context _ctxt) {
			super(_ctxt);
			mScreenWidthInPixels = ((MediaRecorderActivity) _ctxt).mScreenWidthInPixels;
		}

		@Override
		public void onCameraFail(CameraHost.FailureReason reason) {
			super.onCameraFail(reason);
			Toast.makeText(getActivity(), "Sorry, but you cannot use the camera now!", Toast.LENGTH_LONG).show();
		}

		@Override
		public void onFaceDetection(Face[] faces, Camera camera) {
			// Do nothing
		}

		/**
		 * This doesn't get called because I override getPhotoPath()
		 */
		@Override
		protected String getPhotoFilename() {
			return "test.jpg";
		}

		@Override
		protected File getPhotoPath() {
			return new File(getActivity().getFilesDir().getAbsolutePath() + "/picture.jpg");
		}

		@Override
		protected File getVideoPath() {
			return new File(getActivity().getFilesDir().getAbsolutePath() + "/video.jpg");
		}

		@Override
		public void saveImage(PictureTransaction xact, byte[] image) {
			// Wait for image to finish saving
			super.saveImage(xact, image);

			// Clear capturing picture flag
			((MediaRecorderActivity) getActivity()).mIsCapturingPicture = false;

			// Launch new activity
			((MediaRecorderActivity) getActivity()).launchNewArticleActivity();
		}

		@Override
		public Size getPreviewSize(int displayOrientation, int width, int height, Parameters parameters) {
			return super.getPreviewSize(displayOrientation, mScreenWidthInPixels, mScreenWidthInPixels, parameters);
		}

		/**
		 * So that the preview doesn't keep going.
		 */
		@Override
		public boolean useSingleShotMode() {
			return true;
		}

	}

}
