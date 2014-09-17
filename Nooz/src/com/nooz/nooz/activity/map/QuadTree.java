package com.nooz.nooz.activity.map;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

public class QuadTree {

	QuadTreeNode root;

	public QuadTree() {
		root = new QuadTreeNode(new LatLngBounds(new LatLng(-90, -180), new LatLng(90, 180)));
	}

}
