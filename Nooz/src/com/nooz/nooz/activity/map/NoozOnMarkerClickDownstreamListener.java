package com.nooz.nooz.activity.map;

import android.content.Context;

import com.google.android.gms.maps.model.Marker;
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
		return false;
	}

}
