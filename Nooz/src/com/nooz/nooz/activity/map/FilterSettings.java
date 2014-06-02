package com.nooz.nooz.activity.map;

import com.nooz.nooz.R;

/**
 * FilterSettings for searching the map.
 * 
 * @author Rob Stein
 * 
 */
public class FilterSettings {

	// Time

	// Medium
	public Boolean DefaultMedium; // means all mediums are true
	public Boolean Audio;
	public Boolean Picture;
	public Boolean Video;

	// Category
	public Boolean DefaultCategory; // means all are true
	public Boolean People;
	public Boolean Community;
	public Boolean Sports;
	public Boolean Food;
	public Boolean PublicSafety;
	public Boolean ArtsAndLife;

	/**
	 * New default FilterSettings object which has every medium and category
	 * boolean set to false. The DefaultMedium and DefaultCategory boolean
	 * variables are always true when all of their section's variables are
	 * false. Thus they are initially set to true by default.
	 **/
	public FilterSettings() {
		// Time

		// Medium
		DefaultMedium = true;
		Audio = false;
		Picture = false;
		Video = false;

		// Category
		DefaultCategory = true;
		People = false;
		Community = false;
		Sports = false;
		Food = false;
		PublicSafety = false;
		ArtsAndLife = false;
	}

	/**
	 * Toggles the boolean value associated with a given button on the
	 * filters/settings layout. If this causes all booleans in the Medium
	 * section or the Category section to be false, the DefaultMedium or
	 * DefaultCategory variable will be set to true.
	 * 
	 * @param imageViewId
	 *            the given filters button's id
	 */
	public void toggle(int imageViewId) {
		switch (imageViewId) {
		case R.id.button_filter_mic:
			if (true == Audio) {
				Audio = false;
			} else {
				Audio = true;
			}
			break;
		case R.id.button_filter_camera:
			if (true == Picture) {
				Picture = false;
			} else {
				Picture = true;
			}
			break;
		case R.id.button_filter_video:
			if (true == Video) {
				Video = false;
			} else {
				Video = true;
			}
			break;
		case R.id.button_filter_people:
			if (true == People) {
				People = false;
			} else {
				People = true;
			}
			break;
		case R.id.button_filter_community:
			if (true == Community) {
				Community = false;
			} else {
				Community = true;
			}
			break;
		case R.id.button_filter_sports:
			if (true == Sports) {
				Sports = false;
			} else {
				Sports = true;
			}
			break;
		case R.id.button_filter_food:
			if (true == Food) {
				Food = false;
			} else {
				Food = true;
			}
			break;
		case R.id.button_filter_public_safety:
			if (true == PublicSafety) {
				PublicSafety = false;
			} else {
				PublicSafety = true;
			}
			break;
		case R.id.button_filter_arts_and_life:
			if (true == ArtsAndLife) {
				ArtsAndLife = false;
			} else {
				ArtsAndLife = true;
			}
			break;
		}

		// Toggle DefaultMedium if need be
		if (!Audio && !Picture && !Video) {
			// Make default if all are off
			DefaultMedium = true;
		} else if (!Audio || !Picture || !Video) {
			// Turn off default if just one or two is off
			DefaultMedium = false;
		} else if (Audio && Picture && Video) {
			// Turn on when all are on
			DefaultMedium = true;
		}

		// Toggle DefaultCategory if need be
		if (!People && !Community && !Sports && !Food && !PublicSafety && !ArtsAndLife) {
			DefaultCategory = true;
		} else if (!People || !Community || !Sports || !Food || !PublicSafety || !ArtsAndLife) {
			DefaultCategory = false;
		} else if (People && Community && Sports && Food && PublicSafety && ArtsAndLife) {
			DefaultCategory = true;
		}
	}
}
