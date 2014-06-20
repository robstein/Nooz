package com.nooz.nooz.activity.map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Build;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.nooz.nooz.R;
import com.twotoasters.clusterkraf.ClusterPoint;
import com.twotoasters.clusterkraf.MarkerOptionsChooser;

public class NoozMarkerOptionsChooser extends MarkerOptionsChooser {

	private Context mContext;
	private final Paint clusterPaint;

	public NoozMarkerOptionsChooser(MapActivity mapActivity) {
		mContext = mapActivity;

		Resources res = mContext.getResources();

		clusterPaint = new Paint();
		clusterPaint.setColor(Color.BLACK);
		clusterPaint.setAlpha(255);
		clusterPaint.setTextAlign(Paint.Align.CENTER);
		clusterPaint.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD_ITALIC));
		clusterPaint.setTextSize(res.getDimension(R.dimen.cluster_text_size_medium));
	}

	@Override
	public void choose(MarkerOptions markerOptions, ClusterPoint clusterPoint) {
		Resources res = mContext.getResources();

		markerOptions.icon(BitmapDescriptorFactory.fromBitmap(getClusterBitmap(res, R.drawable.ic_cluster_bubble,
				clusterPoint.size())));

		markerOptions.anchor(0.5f, 0.5f);
	}

	@SuppressLint("NewApi")
	private Bitmap getClusterBitmap(Resources res, int resourceId, int clusterSize) {
		BitmapFactory.Options options = new BitmapFactory.Options();

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			options.inMutable = true;
		}
		Bitmap bitmap = BitmapFactory.decodeResource(res, resourceId, options);
		if (bitmap.isMutable() == false) {
			bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
		}

		Canvas canvas = new Canvas(bitmap);

		Paint paint = clusterPaint;
		float originY = bitmap.getHeight() * 0.56f;

		if (clusterSize != 1) {
			canvas.drawText(String.valueOf(clusterSize), bitmap.getWidth() * 0.5f, originY, paint);
		}

		return bitmap;
	}

}
