package com.nooz.nooz.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;
import android.util.DisplayMetrics;

import com.android.volley.toolbox.ImageLoader.ImageCache;
import com.google.android.gms.internal.in;

public class BitmapLruCache extends LruCache<String, Bitmap> implements ImageCache {

	/**
	 * Idea from example at
	 * http://developer.android.com/training/displaying-bitmaps
	 * /cache-bitmap.html
	 * 
	 * @return
	 */
	public static int getDefaultLruCacheSize() {
		final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
		final int cacheSize = maxMemory / 8;
		return cacheSize;
	}

	/**
	 * Ficus Kirkpatrick's idea -
	 * https://developers.google.com/events/io/sessions/325304728
	 * 
	 * @param context
	 * @return
	 */
	public static int getCacheSize(Context context) {
		final DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
		final int screenWidth = displayMetrics.widthPixels;
		final int screenHeight = displayMetrics.heightPixels;
		final int screenBytes = screenWidth * screenHeight * 4; // 4 bytes per
																// pixel
		return screenBytes * 3;
	}

	public BitmapLruCache(Context c) {
		this(getCacheSize(c));
	}

	public BitmapLruCache(int sizeInKiloBytes) {
		super(sizeInKiloBytes);
	}

	@Override
	protected int sizeOf(String key, Bitmap value) {
		return value.getRowBytes() * value.getHeight() / 1024;
	}

	@Override
	public Bitmap getBitmap(String url) {
		return get(url);
	}

	@Override
	public void putBitmap(String url, Bitmap bitmap) {
		put(url, bitmap);
	}
}