package com.nooz.nooz.widget;

import java.io.IOException;

import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * A basic camera preview class
 * 
 * @author Rob Stein
 * 
 */
public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
	private static final String TAG = "CameraPreview";

	private Context mContext;
	private SurfaceHolder mHolder;
	private Camera mCamera;

	// private int mWidth;
	// private int mHeight;

	private boolean mIsPreviewRunning = false;

	public CameraPreview(Context context, Camera camera) {
		super(context);
		mCamera = camera;

		// Install a SurfaceHolder.Callback so we get notified when the
		// underlying surface is created and destroyed.
		mHolder = getHolder();
		mHolder.addCallback(this);
		// deprecated setting, but required on Android versions prior to 3.0
		mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

		mContext = context;

		// mWidth = width;
		// mHeight = height;
	}

	public void surfaceCreated(SurfaceHolder holder) {
		// The Surface has been created, now tell the camera where to draw the
		// preview.
		try {
			mCamera.setPreviewDisplay(holder);
			mCamera.startPreview();
		} catch (IOException e) {
			Log.d(TAG, "Error setting camera preview: " + e.getMessage());
		}
	}

	/*
	 * @Override protected void onMeasure(int widthMeasureSpec, int
	 * heightMeasureSpec) { super.onMeasure(widthMeasureSpec,
	 * heightMeasureSpec); //mWidth = MeasureSpec.getSize(widthMeasureSpec);
	 * //mHeight = mWidth; //setMeasuredDimension(mWidth, mWidth);
	 * setMeasuredDimension(mHeight,mWidth); }
	 */

	public void surfaceDestroyed(SurfaceHolder holder) {
		// empty. Take care of releasing the Camera preview in your activity.
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		// If your preview can change or rotate, take care of those events here.
		// Make sure to stop the preview before resizing or reformatting it.

		if (mHolder.getSurface() == null) {
			// preview surface does not exist
			return;
		}

		// stop preview before making changes
		if (mIsPreviewRunning) {
			try {
				mCamera.stopPreview();
			} catch (Exception e) {
				// ignore: tried to stop a non-existent preview
			}
		}

		// set preview size and make any resize, rotate or
		// reformatting changes here
		/*
		 * List<Camera.Size> sizes =
		 * mCamera.getParameters().getSupportedPreviewSizes(); Double ratio =
		 * (double) (width / height); Double smallestDifference =
		 * Double.MAX_VALUE; Camera.Size previewSize = sizes.get(0);
		 * for(Camera.Size cs : sizes) { Double diff =
		 * Math.abs((cs.width/cs.height) - ratio); if (diff <
		 * smallestDifference) { previewSize = cs; smallestDifference = diff; }
		 * } Camera.Parameters parameters = mCamera.getParameters();
		 * parameters.setPreviewSize(previewSize.width, previewSize.height);
		 * mCamera.setParameters(parameters);
		 * 
		 * mCamera.autoFocus(new Camera.AutoFocusCallback() {
		 * 
		 * @Override public void onAutoFocus(boolean success, Camera camera) {
		 * // TODO Auto-generated method stub
		 * 
		 * }
		 * 
		 * });
		 */

		mCamera.autoFocus(new Camera.AutoFocusCallback() {

			@Override
			public void onAutoFocus(boolean success, Camera camera) {
				// TODO Auto-generated method stub

			}

		});

		// start preview with new settings
		try {
			mCamera.setPreviewDisplay(mHolder);
			mCamera.startPreview();
			mIsPreviewRunning = true;
		} catch (Exception e) {
			Log.d(TAG, "Error starting camera preview: " + e.getMessage());
		}
	}
}