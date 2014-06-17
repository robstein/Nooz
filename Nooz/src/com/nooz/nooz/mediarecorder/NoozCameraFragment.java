package com.nooz.nooz.mediarecorder;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Camera.Face;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.media.ExifInterface;
import android.mediautil.image.jpeg.LLJTran;
import android.mediautil.image.jpeg.LLJTranException;
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
		private static final int IMAGE_MAX_SIZE = 720;

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
			// saveJpegViaLosslessTransformation(image);
			saveJpegTheEasyWay(image);

			// Clear capturing picture flag
			((MediaRecorderActivity) getActivity()).mIsCapturingPicture = false;

			// Launch new activity
			((MediaRecorderActivity) getActivity()).launchNewArticleActivity();
		}

		private void saveJpegViaLosslessTransformation(byte[] imageByteArray) {
			// 1. Initialize LLJTran and Read the entire Image including Appx
			// markers
			InputStream inputStream = new ByteArrayInputStream(imageByteArray);
			LLJTran llj = new LLJTran(inputStream);
			try {
				llj.read(LLJTran.READ_ALL, true);
			} catch (LLJTranException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				inputStream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// 2. Crop it to the specified Bounds
			int width = llj.getWidth();
			int height = llj.getHeight();
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
			Rect cropArea = new Rect();
			cropArea.set(left, top, left + min, top + min);
			llj.transform(LLJTran.CROP, LLJTran.OPT_DEFAULTS, cropArea);
		}

		private void saveJpegTheEasyWay(byte[] image) {
			// Decode image size
			BitmapFactory.Options opts = new BitmapFactory.Options();
			opts.inJustDecodeBounds = true;
			BitmapFactory.decodeByteArray(image, 0, image.length, opts);
			int scale = 1;
			if (opts.outHeight > IMAGE_MAX_SIZE || opts.outWidth > IMAGE_MAX_SIZE) {
				scale = (int) Math.pow(
						2,
						(int) Math.ceil(Math.log(IMAGE_MAX_SIZE / (double) Math.max(opts.outHeight, opts.outWidth))
								/ Math.log(0.5)));
			}

			// Decode byte array with inSampleSize
			BitmapFactory.Options opts2 = new BitmapFactory.Options();
			opts2.inSampleSize = scale;
			Bitmap original = BitmapFactory.decodeByteArray(image, 0, image.length, opts2);

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
		}

		private Bitmap rotateImage(Bitmap source, int rotation) {
			// 1. rotate matrix by postconcatination
			Matrix matrix = new Matrix();
			matrix.postRotate(rotation);

			// 2. create Bitmap from rotated matrix
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
