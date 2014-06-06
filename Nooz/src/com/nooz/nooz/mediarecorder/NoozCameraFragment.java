package com.nooz.nooz.mediarecorder;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.Face;
import android.os.Bundle;
import android.widget.Toast;

import com.commonsware.cwac.camera.CameraFragment;
import com.commonsware.cwac.camera.CameraHost;
import com.commonsware.cwac.camera.SimpleCameraHost;

public class NoozCameraFragment extends CameraFragment {

	

	class NoozCameraHost extends SimpleCameraHost implements Camera.FaceDetectionListener {
		boolean supportsFaces = false;

		public NoozCameraHost(Context _ctxt) {
			super(_ctxt);
		}

		@Override
		public void autoFocusAvailable() {

		}

		@Override
		public void onCameraFail(CameraHost.FailureReason reason) {
			super.onCameraFail(reason);

			Toast.makeText(getActivity(), "Sorry, but you cannot use the camera now!", Toast.LENGTH_LONG).show();
		}

		@Override
		public void onFaceDetection(Face[] faces, Camera camera) {
			// TODO Auto-generated method stub

		}

	}
}
