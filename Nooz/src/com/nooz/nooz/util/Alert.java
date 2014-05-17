package com.nooz.nooz.util;

import android.app.AlertDialog;
import android.content.Context;

public class Alert {

	public static void createAndShowDialog(Exception exception, String title, Context c) {
		Throwable ex = exception;
		if (exception.getCause() != null) {
			ex = exception.getCause();
		}
		createAndShowDialog(ex.getMessage(), title, c);
	}

	public static void createAndShowDialog(String message, String title, Context c) {
		AlertDialog.Builder builder = new AlertDialog.Builder(c);
		builder.setMessage(message);
		builder.setTitle(title);
		builder.create().show();
	}

}
