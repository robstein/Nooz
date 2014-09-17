package com.nooz.nooz.activity.map;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.impl.execchain.MinimalClientExec;

import android.content.Context;
import android.hardware.Camera.Size;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.nooz.nooz.R;
import com.nooz.nooz.model.Story;
import com.nooz.nooz.util.Tools;
import com.twotoasters.clusterkraf.Clusterkraf;
import com.twotoasters.clusterkraf.CustomOnCameraChangeCallable;
import com.twotoasters.clusterkraf.InputPoint;
import com.twotoasters.clusterkraf.Options;
import com.twotoasters.clusterkraf.Options.ClusterClickBehavior;
import com.twotoasters.clusterkraf.Options.ClusterInfoWindowClickBehavior;
import com.twotoasters.clusterkraf.Options.SinglePointClickBehavior;

/**
 * The StoryReservoir manages collecting stories from Azure and figuring out
 * which ones to display on the map. It handles map zooms, pans, and search type
 * switches. It also handles the bubbles on the map. It should also handle the
 * footer at the bottom of the map.
 * 
 * @author Rob Stein
 * 
 */
public class StoryReservoir {

	private MapActivity mContext;
	private QuadTree mLoadedRealStoryCount;
	private List<Story> mLoadedStories;
	private List<Story> mDisplayedStories;
	private ArrayList<InputPoint> mClusterkrafInputPoints;
	private GoogleMap mMap;
	Clusterkraf mClusterkraf;

	public StoryReservoir(Context c) {
		mContext = (MapActivity) c;
	}

	public void setMap(GoogleMap map) {
		mMap = map;
	}

	public void getInitialStories() {
		LatLngBounds bounds = mContext.mMap.getProjection().getVisibleRegion().latLngBounds;
		mContext.getNoozService().searchNoozInRegionAndGetStoryCountQuadTree(bounds, mContext.mFilterSettings,
				mContext.mMenuController.mCurrentSearchType);
	}

	public void getInitialStoriesCallback() {
		saveRealStoryCount(mContext.getNoozService().getLoadedRealStoryCount());
		initLoadedStories(mContext.getNoozService().getLoadedStories());
		commitToMap();
	}

	public void updateStoryReservoirOnCameraChange() {
		/*
		 * If stories displayed < 10, add more from loadedStories
		 */

		/*
		 * If stories loaded / total number of real stories < epsilon, add more
		 * stories from azure
		 */
	}

	public void changeSearchType() {
		// TODO Auto-generated method stub

	}

	public void commitToMap() {
		buildClusterkrafInputPoints();
		initClusterkraf();
	}

	private void buildClusterkrafInputPoints() {
		mClusterkrafInputPoints = new ArrayList<InputPoint>(mDisplayedStories.size());
		for (Story s : mDisplayedStories) {
			mClusterkrafInputPoints.add(new InputPoint(new LatLng(s.lat, s.lng), s));
		}
	}

	private void initClusterkraf() {
		if (mMap != null && mClusterkrafInputPoints != null && mClusterkrafInputPoints.size() > 0) {
			Options options = new Options();
			applyOptionsToClusterkrafOptions(options);
			mClusterkraf = new Clusterkraf(mMap, options, mClusterkrafInputPoints, new CustomOnCameraChangeCallable() {
				@Override
				public void onCameraChange() {
					updateStoryReservoirOnCameraChange();
				}
			});
		}
	}

	private void applyOptionsToClusterkrafOptions(Options options) {
		options.setTransitionInterpolator(new AccelerateDecelerateInterpolator());
		options.setPixelDistanceToJoinCluster((int) Tools.dipToPixels(mContext, 72));

		options.setZoomToBoundsAnimationDuration(500);
		options.setShowInfoWindowAnimationDuration(500);
		options.setExpandBoundsFactor(0.5d);
		options.setSinglePointClickBehavior(SinglePointClickBehavior.NO_OP);
		options.setClusterClickBehavior(ClusterClickBehavior.ZOOM_TO_BOUNDS);
		options.setClusterInfoWindowClickBehavior(ClusterInfoWindowClickBehavior.ZOOM_TO_BOUNDS);

		options.setZoomToBoundsPadding(mContext.getResources().getDrawable(R.drawable.ic_map_bubble_cluster)
				.getIntrinsicHeight());
		options.setMarkerOptionsChooser(new NoozMarkerOptionsChooser(mContext));
		options.setOnMarkerClickDownstreamListener(new NoozOnMarkerClickDownstreamListener(mContext));
		options.setProcessingListener((MapActivity) mContext);
	}

	private void saveRealStoryCount(QuadTree loadedRealStoryCount) {
		mLoadedRealStoryCount = loadedRealStoryCount;
	}

	private void initLoadedStories(List<Story> loadedStories) {
		mLoadedStories = loadedStories;
		mDisplayedStories = mLoadedStories.subList(0, Math.min(10, mLoadedStories.size()));
	}

	/**
	 * When pausing, we clear all of the clusterkraf's markers in order to
	 * conserve memory. When (if) we resume, we can rebuild from where we left
	 * off.
	 */
	public void onPause() {
		if (mClusterkraf != null) {
			mClusterkraf.clear();
			mClusterkraf = null;
		}
	}
}
