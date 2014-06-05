package com.nooz.nooz.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import com.android.volley.toolbox.NetworkImageView;
import com.nooz.nooz.R;
import com.nooz.nooz.util.Tools;

public class ProfileImageView extends NetworkImageView {

	private Paint mBorderPaint;
	private Context mContext;
	private Boolean mLarge;

	public ProfileImageView(Context context) {
		super(context);
		init(context);
	}

	public ProfileImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}

	public ProfileImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context, attrs);
	}

	private void init(Context context, AttributeSet attrs) {
		init(context);

		TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.ProfileImage, 0, 0);
		try {
			mLarge = a.getBoolean(R.styleable.ProfileImage_large, false);
		} finally {
			a.recycle();
		}
	}

	private void init(Context context) {
		mContext = context;
		mLarge = false;
		mBorderPaint = new Paint(0);
		mBorderPaint.setColor(0xffdedede);
		mBorderPaint.setStyle(Paint.Style.STROKE);
		mBorderPaint.setStrokeWidth(Tools.dipToPixels(context, 5));
		setScaleType(ScaleType.CENTER_CROP);
	}

	@Override
	protected void onDraw(Canvas canvas) {

		Drawable drawable = getDrawable();

		if (drawable == null) {
			return;
		}

		if (getWidth() == 0 || getHeight() == 0) {
			return;
		}
		Bitmap b = ((BitmapDrawable) drawable).getBitmap();
		Bitmap bitmap = b.copy(Bitmap.Config.ARGB_8888, true);

		int w = getWidth(), h = getHeight();

		Bitmap roundBitmap = getCroppedBitmap(bitmap, w);
		canvas.drawBitmap(roundBitmap, 0, 0, null);

		// Draw border
		if (mLarge) {
			canvas.drawCircle(w / 2, h / 2, (w + 1) / 2 - Tools.dipToPixels(mContext, 5f) / 2, mBorderPaint);
		} else {

		}

	}

	public static Bitmap getCroppedBitmap(Bitmap bmp, int radius) {
		Bitmap sbmp;
		if (bmp.getWidth() != radius || bmp.getHeight() != radius)
			sbmp = Bitmap.createScaledBitmap(bmp, radius, radius, false);
		else
			sbmp = bmp;
		Bitmap output = Bitmap.createBitmap(sbmp.getWidth(), sbmp.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int color = 0xffa19774;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, sbmp.getWidth(), sbmp.getHeight());

		paint.setAntiAlias(true);
		paint.setFilterBitmap(true);
		paint.setDither(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(Color.parseColor("#BAB399"));
		canvas.drawCircle(sbmp.getWidth() / 2 + 0.7f, sbmp.getHeight() / 2 + 0.7f, sbmp.getWidth() / 2 + 0.1f, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(sbmp, rect, rect, paint);

		return output;
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
