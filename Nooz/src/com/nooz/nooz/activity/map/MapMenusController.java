package com.nooz.nooz.activity.map;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.nooz.nooz.R;
import com.nooz.nooz.util.SearchType;

public class MapMenusController {

	private MapActivity mC;

	// Colors
	private static final int SEARCH_TYPE_ACTIVE_COLOR = 0xFF000000;
	private static final int SEARCH_TYPE_FADED_COLOR = 0xFF979797;

	private static final int SPINNER_POSITION_BREAKING = 1;
	private static final int SPINNER_POSITION_RELEVANT = 0;

	// Animations
	private Animation mSlideInBottom;
	private Animation mSlideOutBottom;
	private Animation mSlideInLeft;
	private Animation mSlideOutLeft;
	private Animation mFadeIn;
	private Animation mFadeOut;

	/**
	 * Stores the current SearchType: "RELEVANT" or "BREAKING"
	 * 
	 * @see com.nooz.nooz.util.SearchType
	 */
	SearchType mCurrentSearchType = SearchType.RELEVANT;

	/**
	 * Boolean representation of whether or not the filters menu is currently
	 * open.
	 */
	Boolean filtersMenuIsOpen = false;

	MapMenusController(MapActivity mapActivity) {
		this.mC = mapActivity;

		// Animations
		mSlideInBottom = AnimationUtils.loadAnimation(mC, R.anim.slide_in_bottom);
		mSlideOutBottom = AnimationUtils.loadAnimation(mC, R.anim.slide_out_bottom);
		mSlideInLeft = AnimationUtils.loadAnimation(mC, R.anim.slide_in_left);
		mSlideOutLeft = AnimationUtils.loadAnimation(mC, R.anim.slide_out_left);
		mFadeIn = AnimationUtils.loadAnimation(mC, R.anim.fade_in);
		mFadeOut = AnimationUtils.loadAnimation(mC, R.anim.fade_out);
	}

	/* ***** EXTRA MENUS HIDE/SHOW BEGIN ***** */

	void hideFiltersLayout() {
		filtersMenuIsOpen = false;
		mC.mLayoutFilters.setVisibility(View.GONE);
		mC.mLayoutFilters.startAnimation(mSlideOutLeft);

		// Fade in the views underneath
		mC.mMapContainer.setVisibility(View.VISIBLE);
		mC.mMapContainer.startAnimation(mFadeIn);
		// mC.mMiddlebar.setVisibility(View.VISIBLE);
		// mC.mMiddlebar.startAnimation(mFadeIn);
		// mC.mMenuSettings.setVisibility(View.VISIBLE);
		// mC.mMenuSettings.startAnimation(mFadeIn);
		
		mC.getActionBar().show();
	}

	void showFiltersLayout() {
		filtersMenuIsOpen = true;
		mC.mLayoutFilters.setVisibility(View.VISIBLE);
		mC.mLayoutFilters.startAnimation(mSlideInLeft);

		// Fade out the views underneath
		// Prevent interaction with views underneath
		mC.mMapContainer.setVisibility(View.INVISIBLE);
		mC.mMapContainer.startAnimation(mFadeOut);
		// mC.mMiddlebar.setVisibility(View.INVISIBLE);
		// mC.mMiddlebar.startAnimation(mFadeOut);
		// mC.mMenuSettings.setVisibility(View.INVISIBLE);
		// mC.mMenuSettings.startAnimation(mFadeOut);
		
		mC.getActionBar().hide();

	}

	/* ***** EXTRA MENUS HIDE/SHOW END ***** */

	/* ***** SEARCH TYPE BEGIN ***** */

	void switchSearchTypes(int pressedButton) {
		// Change mCurrentSearchType
		if ((mCurrentSearchType == SearchType.RELEVANT) && (pressedButton == SPINNER_POSITION_BREAKING)) {
			mCurrentSearchType = SearchType.BREAKING;
			mC.mStoryDataController.clearAndPopulateStories();
		} else if ((mCurrentSearchType == SearchType.BREAKING) && (pressedButton == SPINNER_POSITION_RELEVANT)) {
			mCurrentSearchType = SearchType.RELEVANT;
			mC.mStoryDataController.clearAndPopulateStories();
		}
	}

	/* ***** SEARCH TYPE END ***** */
}
