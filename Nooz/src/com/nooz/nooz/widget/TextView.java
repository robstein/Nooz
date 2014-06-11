package com.nooz.nooz.widget;

import android.content.Context;
import android.util.AttributeSet;

public class TextView extends android.widget.TextView {

	public TextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		CustomFontHelper.init(this, context, attrs);
	}

}
