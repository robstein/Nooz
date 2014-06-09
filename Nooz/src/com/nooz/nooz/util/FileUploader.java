package com.nooz.nooz.util;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import android.os.AsyncTask;

public abstract class FileUploader {

	String mUrl;
	String mFilePath;

	public FileUploader(String url, String filePath) {
		mUrl = url;
		mFilePath = filePath;
	}

	public void uploadFile() {
		(new FileUploaderTask()).execute();
	}
	
	public abstract void onSuccess();
	
	public abstract void onFail();

	/***
	 * Handles uploading an image to a specified url
	 */
	class FileUploaderTask extends AsyncTask<Void, Void, Boolean> {

		@Override
		protected Boolean doInBackground(Void... params) {

			byte[] bytes = getBytesFromFile();
			if (bytes == null) {
				return false;
			}

			HttpURLConnection conn = writeFileToServer(bytes);
			if (conn == null) {
				return false;
			}

			boolean retval = verifyUpload(conn);
			return retval;
		}

		@Override
		protected void onPostExecute(Boolean uploaded) {
			if (uploaded) {
				onSuccess();
			} else {
				onFail();
			}
		}

		private byte[] getBytesFromFile() {
			File file = new File(mFilePath);
			FileInputStream fis;
			try {
				fis = new FileInputStream(file);
				int bytesRead = 0;
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				byte[] b = new byte[1024];

				while ((bytesRead = fis.read(b)) != -1) {
					bos.write(b, 0, bytesRead);
				}

				byte[] bytes = bos.toByteArray();
				fis.close();
				return bytes;

			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		private HttpURLConnection writeFileToServer(byte[] bytes) {
			URL url;
			try {
				url = new URL(mUrl.replace("\"", ""));

				HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
				urlConnection.setDoOutput(true);
				urlConnection.setRequestMethod("PUT");
				urlConnection.addRequestProperty("Content-Type", "image/jpeg");
				urlConnection.setRequestProperty("Content-Length", "" + bytes.length);
				// Write image data to server
				DataOutputStream wr = new DataOutputStream(urlConnection.getOutputStream());
				wr.write(bytes);
				wr.flush();
				wr.close();
				return urlConnection;
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		private boolean verifyUpload(HttpURLConnection conn) {
			try {
				// If we successfully uploaded, return true
				if (conn.getResponseCode() == 201 && conn.getResponseMessage().equals("Created")) {
					return true;
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return false;
		}

	}
}
