package com.nooz.nooz.util;

import android.app.AlertDialog;
import android.content.Context;

/**
 * A simple static function container for displaying errors to the user.
 * 
 * @author Rob Stein
 * 
 */
public class Alert {

	/**
	 * 
	 * Builds an AlertDialog from an exception and title.
	 * 
	 * @param exception
	 *            exception to show
	 * @param title
	 *            title to show
	 * @param c
	 *            application context
	 */
	public static void createAndShowDialog(Exception exception, String title, Context c) {
		Throwable ex = exception;
		if (exception.getCause() != null) {
			ex = exception.getCause();
		}
		createAndShowDialog(ex.getMessage(), title, c);
	}

	/**
	 * Builds an AlertDialog with a message and title.
	 * 
	 * @param message
	 *            message for AlertDialog
	 * @param title
	 *            title of AlertDialog
	 * @param c
	 *            application context
	 */
	public static void createAndShowDialog(String message, String title, Context c) {
		AlertDialog.Builder builder = new AlertDialog.Builder(c);
		builder.setMessage(message);
		builder.setTitle(title);
		builder.create().show();
	}

}
