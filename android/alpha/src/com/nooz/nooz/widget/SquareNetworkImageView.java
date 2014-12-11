package com.nooz.nooz.widget;

import android.content.Context;
import android.util.AttributeSet;

import com.android.volley.toolbox.NetworkImageView;

/**
 * Simple Square ImageView
 * 
 * @author Rob Stein
 * 
 */
public class SquareNetworkImageView extends NetworkImageView {
	public SquareNetworkImageView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public SquareNetworkImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public SquareNetworkImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		int size = 0;
		int width = getMeasuredWidth();
		int height = getMeasuredHeight();
		int widthWithoutPadding = width - getPaddingLeft() - getPaddingRight();
		int heigthWithoutPadding = height - getPaddingTop() - getPaddingBottom();

		// set the dimensions
		if (widthWithoutPadding > heigthWithoutPadding) {
			size = heigthWithoutPadding;
		} else {
			size = widthWithoutPadding;
		}

		setMeasuredDimension(size + getPaddingLeft() + getPaddingRight(), size + getPaddingTop() + getPaddingBottom());
	}
}
