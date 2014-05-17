package com.nooz.nooz.widget;

import java.io.IOException;

import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/** A basic Camera preview class */
public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
	private static final String TAG = "CameraPreview";

	private Context mContext;
	private SurfaceHolder mHolder;
	private Camera mCamera;

	private int mWidth;
	private int mHeight;

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

	// Don't do this yet
	//@Override
	//protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
	//	super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	//	mWidth = MeasureSpec.getSize(widthMeasureSpec);
	//	mHeight = mWidth;
	//	setMeasuredDimension(mWidth, mWidth);
	//}

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
		try {
			mCamera.stopPreview();
		} catch (Exception e) {
			// ignore: tried to stop a non-existent preview
		}

		// set preview size and make any resize, rotate or
		// reformatting changes here
		// Parameters parameters = mCamera.getParameters();
		// List<Camera.Size> previewSizes =
		// parameters.getSupportedPreviewSizes();
		// Camera.Size previewSize = previewSizes.get(0);
		// parameters.setPreviewSize(previewSize.width, previewSize.height);

		/*
		 * Display display =
		 * ((WindowManager)mContext.getSystemService(Context.WINDOW_SERVICE
		 * )).getDefaultDisplay();
		 * 
		 * if(display.getRotation() == Surface.ROTATION_0) {
		 * parameters.setPreviewSize(previewSize.height, previewSize.width);
		 * mCamera.setDisplayOrientation(90); } if(display.getRotation() ==
		 * Surface.ROTATION_90) { parameters.setPreviewSize(previewSize.width,
		 * previewSize.height); } if(display.getRotation() ==
		 * Surface.ROTATION_180) { parameters.setPreviewSize(previewSize.height,
		 * previewSize.width); } if(display.getRotation() ==
		 * Surface.ROTATION_270) { parameters.setPreviewSize(previewSize.width,
		 * previewSize.height); mCamera.setDisplayOrientation(180); }
		 */
		// mCamera.setParameters(parameters);
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

		} catch (Exception e) {
			Log.d(TAG, "Error starting camera preview: " + e.getMessage());
		}
	}
}