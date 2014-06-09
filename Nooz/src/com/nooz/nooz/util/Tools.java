package com.nooz.nooz.util;

import android.content.Context;
import android.util.DisplayMetrics;
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

}
