package com.nooz.nooz.activity.map;

import android.content.Context;

import com.google.android.gms.maps.model.Marker;
import com.nooz.nooz.activity.article.ArticleLauncher;
import com.nooz.nooz.model.Story;
import com.twotoasters.clusterkraf.ClusterPoint;
import com.twotoasters.clusterkraf.OnMarkerClickDownstreamListener;

public class NoozOnMarkerClickDownstreamListener implements OnMarkerClickDownstreamListener {

	Context mContext;

	public NoozOnMarkerClickDownstreamListener(MapActivity mapActivity) {
		mContext = mapActivity;
	}

	@Override
	public boolean onMarkerClick(Marker marker, ClusterPoint clusterPoint) {
		// TODO Auto-generated method stub
		if (clusterPoint.size() == 1) {
			Story s = (Story) clusterPoint.getPointAtOffset(0).getTag();
			ArticleLauncher.openStory(mContext, s);
		}
		return false;
	}

}
