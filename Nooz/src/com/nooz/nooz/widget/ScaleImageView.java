package com.nooz.nooz.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

/**
 * ImageView that keeps aspect ratio when scaled
 */
public class ScaleImageView extends ImageView {

	private static final String TAG = "ScaleImageView";
	private static final float INVERTER_MATRIX[] = { -1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 1.0f, 0.0f,
			0.0f, 0.0f, -1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 1.0f, 1.0f, 0.0f };

	private Paint mPaint;
	private float mX;
	private float mY;
	private int mRadius;
	private float mStartX;
	private float mEndX;
	private float mStartY;
	private float mEndY;

	public ScaleImageView(Context context) {
		super(context);
		init();
	}

	public ScaleImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public ScaleImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	private void init() {
		mX = 0;
		mY = 0;
		mRadius = -1;
		// Set up the paint for the circle
		mPaint = new Paint(0);
		mPaint.setColor(0x80ffffff);
		//mPaint.setStyle(Paint.Style.STROKE);
		//mPaint.setStrokeWidth(1);

	}

	@Override
	public void draw(Canvas canvas) {
		super.draw(canvas);

		/*
		int r = mRadius + 1;
		while (((mX - r >= mStartX) || (mX + r <= mEndX))
				|| ((mY - r >= mStartY) || (mY + r <= mEndY))) {
			canvas.drawCircle(mX, mY, r, mPaint);
			r++;
		}
		*/
		
		canvas.drawCircle(mX, mY, mRadius, mPaint);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		if ((event.getAction() == MotionEvent.ACTION_DOWN) || event.getAction() == MotionEvent.ACTION_MOVE) {

			if (((event.getX() - mRadius >= mStartX) && (event.getX() + mRadius <= mEndX))
					&& ((event.getY() - mRadius >= mStartY) && (event.getY() + mRadius <= mEndY))) {
				mX = event.getX();
				mY = event.getY();
				mRadius = 200;

				invalidate();
				return true;
			}
		}

		return false;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		mStartX = 0;
		mEndX = MeasureSpec.getSize(widthMeasureSpec);
		mStartY = 0;//getRelativeTop(this);
		mEndY = MeasureSpec.getSize(heightMeasureSpec);

		try {
			Drawable drawable = getDrawable();

			if (drawable == null) {
				setMeasuredDimension(0, 0);
			} else {
				float imageSideRatio = (float) drawable.getIntrinsicWidth() / (float) drawable.getIntrinsicHeight();
				float viewSideRatio = (float) MeasureSpec.getSize(widthMeasureSpec)
						/ (float) MeasureSpec.getSize(heightMeasureSpec);
				if (imageSideRatio >= viewSideRatio) {
					// Image is wider than the display (ratio)
					int width = MeasureSpec.getSize(widthMeasureSpec);
					int height = (int) (width / imageSideRatio);
					setMeasuredDimension(width, height);
					mStartY = ((float) MeasureSpec.getSize(heightMeasureSpec) - height) / 2;
					mEndY = (float) MeasureSpec.getSize(heightMeasureSpec) - mStartY;
				} else {
					// Image is taller than the display (ratio)
					int height = MeasureSpec.getSize(heightMeasureSpec);
					int width = (int) (height * imageSideRatio);
					setMeasuredDimension(width, height);
					mStartX = ((float) MeasureSpec.getSize(widthMeasureSpec) - width) / 2;
					mEndX = (float) MeasureSpec.getSize(widthMeasureSpec) - mStartX;
				}
				Log.d(TAG, "mStartX = " + mStartX);
				Log.d(TAG, "mEndX = " + mEndX);
				Log.d(TAG, "mStartY = " + mStartY);
				Log.d(TAG, "mEndY = " + mEndY);
			}
		} catch (Exception e) {
			super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		}
	}

	private int getRelativeTop(View myView) {
		if (myView.getParent() == myView.getRootView())
			return myView.getTop();
		else
			return myView.getTop() + getRelativeTop((View) myView.getParent());
	}
}