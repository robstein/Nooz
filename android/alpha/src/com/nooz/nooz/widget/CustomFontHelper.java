package com.nooz.nooz.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;

import com.nooz.nooz.R;

public class CustomFontHelper {

	public static void init(View v, Context context, AttributeSet attrs) {
		// Typeface.createFromAsset doesn't work in the layout editor.
		// Skipping...
		if (v.isInEditMode()) {
			return;
		}

		TypedArray styledAttrs = context.obtainStyledAttributes(attrs, R.styleable.TypefacedTextView);
		String fontName = styledAttrs.getString(R.styleable.TypefacedTextView_typeface);
		styledAttrs.recycle();

		if (fontName != null) {
			Typeface typeface = Typeface.createFromAsset(context.getAssets(), "fonts/" + fontName);
			if (v instanceof EditText) {
				((EditText) v).setTypeface(typeface);
			} else if (v instanceof TextView) {
				((TextView) v).setTypeface(typeface);
			}
		}
	}
}
