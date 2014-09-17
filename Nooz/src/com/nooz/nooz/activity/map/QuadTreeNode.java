package com.nooz.nooz.activity.map;

import com.google.android.gms.maps.model.LatLngBounds;
import com.google.common.primitives.UnsignedInteger;

public class QuadTreeNode {

	UnsignedInteger depth;
	LatLngBounds bounds;
	UnsignedInteger count;

	public QuadTreeNode() {
	}

	public QuadTreeNode(LatLngBounds latLngBounds) {
		bounds = latLngBounds;
	}

}
