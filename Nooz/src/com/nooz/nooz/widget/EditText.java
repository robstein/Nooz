package com.nooz.nooz.widget;

import android.content.Context;
import android.util.AttributeSet;

public class EditText extends android.widget.EditText {

	public EditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		CustomFontHelper.init(this, context, attrs);
	}

}
