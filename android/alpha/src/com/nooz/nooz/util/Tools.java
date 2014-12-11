package com.nooz.nooz.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;

/**
 * Simple generic tools class.
 * 
 * @author Rob Stein
 * 
 */
public class Tools {

	/**
	 * Turns dips to pixels.
	 * 
	 * @param context
	 *            current user's context
	 * @param dipValue
	 *            the input dips
	 * @return the output pixels
	 */
	public static float dipToPixels(Context context, float dipValue) {
		DisplayMetrics metrics = context.getResources().getDisplayMetrics();
		return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, metrics);
	}

	/**
	 * Given the a date from the Azure sql database, this method returns the
	 * date in the locale time zone with a format like 7:46PM May. 31, 2014
	 * 
	 * @param __createdAt
	 * @return formatted date
	 */
	public static String getDate(String __createdAt) {
		SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy'-'MM'-'dd'T'HH':'mm':'ss", Locale.US);
		inputFormat.setTimeZone(TimeZone.getTimeZone("Etc/UTC"));
		SimpleDateFormat outputFormat = new SimpleDateFormat("h':'mmaa'  'MMM. dd, yyyy", java.util.Locale.getDefault());

		// Adjust locale and zone appropriately
		try {
			Date date = inputFormat.parse(__createdAt);
			return outputFormat.format(date);
		} catch (ParseException e) {
			Log.e("Tools.getDate", "Error parsing date: " + e.getCause().getMessage());
			return "";
		}
	}

}
