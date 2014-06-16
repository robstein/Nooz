package com.nooz.nooz.mediarecorder;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.hardware.Camera.Face;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.media.ExifInterface;
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

		private static final int PHOTO_WIDTH = 720;

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
			// Decode byte array
			Bitmap original = BitmapFactory.decodeByteArray(image, 0, image.length);

			// Crop bitmap
			int width = original.getWidth();
			int height = original.getHeight();
			int min = Math.min(width, height);
			int left = 0;
			int top = 0;
			if (width == min) {
				left = 0;
				top = (height - min) / 2;
			} else if (height == min) {
				top = 0;
				left = (width - min) / 2;
			}
			Bitmap cropped = Bitmap.createBitmap(original, left, top, min, min);

			// Resize bitmap
			Bitmap resized = Bitmap.createScaledBitmap(cropped, PHOTO_WIDTH, PHOTO_WIDTH, true);

			// Bitmap to byte array
			ByteArrayOutputStream blob = new ByteArrayOutputStream();
			resized.compress(Bitmap.CompressFormat.JPEG, 100, blob);
			byte[] smallerImage = blob.toByteArray();

			// Wait for image to finish saving
			File photo = getPhotoPath();
			if (photo.exists()) {
				photo.delete();
			}
			try {
				FileOutputStream fos = new FileOutputStream(photo.getPath());
				BufferedOutputStream bos = new BufferedOutputStream(fos);
				bos.write(smallerImage);
				bos.flush();
				fos.getFD().sync();
				bos.close();

				// Rotate the image if necessary
				ExifInterface ei = new ExifInterface(photo.getPath().toString());
				int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
				Boolean rotated = false;
				Bitmap rotatedImage;
				byte[] rotatedByteArray = { 0x0000 };
				switch (orientation) {
				case ExifInterface.ORIENTATION_NORMAL:
					rotated = true;
					rotatedImage = rotateImage(resized, 90);
					// Bitmap to byte array
					blob = new ByteArrayOutputStream();
					rotatedImage.compress(Bitmap.CompressFormat.JPEG, 100, blob);
					rotatedByteArray = blob.toByteArray();
					break;
				case ExifInterface.ORIENTATION_ROTATE_90:
					rotated = true;
					rotatedImage = rotateImage(resized, 180);
					// Bitmap to byte array
					blob = new ByteArrayOutputStream();
					rotatedImage.compress(Bitmap.CompressFormat.JPEG, 100, blob);
					rotatedByteArray = blob.toByteArray();
					break;
				case ExifInterface.ORIENTATION_ROTATE_180:
					rotated = true;
					rotatedImage = rotateImage(resized, 270);
					// Bitmap to byte array
					blob = new ByteArrayOutputStream();
					rotatedImage.compress(Bitmap.CompressFormat.JPEG, 100, blob);
					rotatedByteArray = blob.toByteArray();
					break;
				}
				if (rotated) {
					photo.delete();
					fos = new FileOutputStream(photo.getPath());
					bos = new BufferedOutputStream(fos);
					bos.write(rotatedByteArray);
					bos.flush();
					fos.getFD().sync();
					bos.close();
				}
			} catch (java.io.IOException e) {
				handleException(e);
			}

			// Clear capturing picture flag
			((MediaRecorderActivity) getActivity()).mIsCapturingPicture = false;

			// Launch new activity
			((MediaRecorderActivity) getActivity()).launchNewArticleActivity();
		}

		private Bitmap rotateImage(Bitmap source, int rotation) {

			// 2. rotate matrix by postconcatination
			Matrix matrix = new Matrix();
			matrix.postRotate(rotation);

			// 3. create Bitmap from rotated matrix
			return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
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
