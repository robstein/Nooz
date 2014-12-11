package com.nooz.nooz.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

public class AudioIntensityFeedback extends View {

	private Paint mCenterCirclePaint;
	private int mWidth;
	private int mHeight;
	private double mLastRadius;
	private double mRadius;

	public AudioIntensityFeedback(Context context) {
		super(context);
		init();
		// TODO Auto-generated constructor stub
	}

	public AudioIntensityFeedback(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
		// TODO Auto-generated constructor stub
	}

	public AudioIntensityFeedback(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
		// TODO Auto-generated constructor stub
	}
	
	private void init() {
		mRadius = -1;
		// Set up the paint for the center circle
		mCenterCirclePaint = new Paint(0);
		mCenterCirclePaint.setColor(0xFFFFF5F5);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.drawCircle(mWidth/2, mHeight/2, (float) mRadius, mCenterCirclePaint);
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		mWidth = MeasureSpec.getSize(widthMeasureSpec);
		mHeight = MeasureSpec.getSize(heightMeasureSpec);
	}
	
	public void reportAmplitude(int amplitude) {
		double db = 20.0 * Math.log10(amplitude/10);
		
		final Handler handler = new Handler();
		final long start = SystemClock.uptimeMillis();
		final double startPoint = mLastRadius;
		final double endPoint = db * 3;
		final long duration = 250;
		
		final DecelerateInterpolator interpolator = new DecelerateInterpolator();
		handler.post(new Runnable() {
			@Override
			public void run() {
				long elapsed = SystemClock.uptimeMillis() - start;
				float t = interpolator.getInterpolation((float) elapsed / duration);
				
				mRadius = t * endPoint + (1 - t) * startPoint;

				if (t < 1.0) {
					// Post again 16ms later.
					handler.postDelayed(this, 16);
				} else {
					
				}
			}
		});
		
		
		
		mLastRadius = db * 3;
		invalidate();
	}
}
