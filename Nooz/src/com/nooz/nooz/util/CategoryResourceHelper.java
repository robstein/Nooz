package com.nooz.nooz.util;

import android.graphics.drawable.Drawable;

import com.nooz.nooz.R;

/**
 * Static resource retriever for the various categories.
 * 
 * @author Rob Stein
 * 
 */
public class CategoryResourceHelper {

	private static final int COLOR_PEOPLE = 0xFF32B4FF;
	private static final int COLOR_COMMUNITY = 0xFF377DEC;
	private static final int COLOR_SPORTS = 0xFFE84C3D;
	private static final int COLOR_FOOD = 0xFF3CB34B;
	private static final int COLOR_PUBLIC_SAFETY = 0xFFED7B22;
	private static final int COLOR_ARTS_AND_LIFE = 0xFF9A55BF;
	private static final int COLOR_PEOPLE_STROKE = 0xFF8DCFFF;
	private static final int COLOR_COMMUNITY_STROKE = 0xFF6B9EF1;
	private static final int COLOR_SPORTS_STROKE = 0xFFEF766B;
	private static final int COLOR_FOOD_STROKE = 0xFF83D193;
	private static final int COLOR_PUBLIC_SAFETY_STROKE = 0xFFEEAF7A;
	private static final int COLOR_ARTS_AND_LIFE_STROKE = 0xFFAE7DCE;

	public static final int COLOR_WHITE = 0xFFFFFFFF;

	public static int getColorByCategory(String category, boolean highlight) {
		int retval;
		if ("People".equals(category)) {
			retval = COLOR_PEOPLE;
		} else if ("Community".equals(category)) {
			retval = COLOR_COMMUNITY;
		} else if ("Sports".equals(category)) {
			retval = COLOR_SPORTS;
		} else if ("Food".equals(category)) {
			retval = COLOR_FOOD;
		} else if ("Public Safety".equals(category)) {
			retval = COLOR_PUBLIC_SAFETY;
		} else { // Arts and Life
			retval = COLOR_ARTS_AND_LIFE;
		}
		return highlight ? retval : retval & 0xC0FFFFFF;
	}

	public static int getStrokeColorByCategory(String category, boolean highlight) {
		int retval;
		if ("People".equals(category)) {
			retval = COLOR_PEOPLE_STROKE;
		} else if ("Community".equals(category)) {
			retval = COLOR_COMMUNITY_STROKE;
		} else if ("Sports".equals(category)) {
			retval = COLOR_SPORTS_STROKE;
		} else if ("Food".equals(category)) {
			retval = COLOR_FOOD_STROKE;
		} else if ("Public Safety".equals(category)) {
			retval = COLOR_PUBLIC_SAFETY_STROKE;
		} else { // Arts and Life
			retval = COLOR_ARTS_AND_LIFE_STROKE;
		}
		return highlight ? retval : retval & 0xC0FFFFFF;

	}

	public static int getGroundOverlayByCategory(String category) {
		if ("People".equals(category)) {
			return R.drawable.people_white;
		} else if ("Community".equals(category)) {
			return R.drawable.community_white;
		} else if ("Sports".equals(category)) {
			return R.drawable.sports_white;
		} else if ("Food".equals(category)) {
			return R.drawable.food_white;
		} else if ("Public Safety".equals(category)) {
			return R.drawable.public_saftey_white;
		} else { // Arts and Life
			return R.drawable.arts_and_life_white;
		}
	}

	public static int getActiveGroundOverlayByCategory(String category) {
		if ("People".equals(category)) {
			return R.drawable.people_white_active;
		} else if ("Community".equals(category)) {
			return R.drawable.community_white_active;
		} else if ("Sports".equals(category)) {
			return R.drawable.sports_white_active;
		} else if ("Food".equals(category)) {
			return R.drawable.food_white_active;
		} else if ("Public Safety".equals(category)) {
			return R.drawable.public_saftey_white_active;
		} else { // Arts and Life
			return R.drawable.arts_and_life_white_active;
		}
	}

	public static int getCommentsByCategory(String category) {
		if ("People".equals(category)) {
			return R.drawable.comments_people;
		} else if ("Community".equals(category)) {
			return R.drawable.comments_community;
		} else if ("Sports".equals(category)) {
			return R.drawable.comments_sports;
		} else if ("Food".equals(category)) {
			return R.drawable.comments_food;
		} else if ("Public Safety".equals(category)) {
			return R.drawable.comments_public_safety;
		} else { // Arts and Life
			return R.drawable.comments_arts_and_life_fullsize;
		}
	}

	public static int getScoreBackgroundByCategory(String category) {
		if ("People".equals(category)) {
			return R.drawable.relevance_score_people;
		} else if ("Community".equals(category)) {
			return R.drawable.relevance_score_community;
		} else if ("Sports".equals(category)) {
			return R.drawable.relevance_score_sports;
		} else if ("Food".equals(category)) {
			return R.drawable.relevance_score_food;
		} else if ("Public Safety".equals(category)) {
			return R.drawable.relevance_score_public_safety;
		} else { // Arts and Life
			return R.drawable.relevance_score_arts_and_life;
		}
	}

	public static int getButtonBorderByCategory(String category) {
		if ("People".equals(category)) {
			return R.drawable.text_button_people;
		} else if ("Community".equals(category)) {
			return R.drawable.text_button_community;
		} else if ("Sports".equals(category)) {
			return R.drawable.text_button_sports;
		} else if ("Food".equals(category)) {
			return R.drawable.text_button_food;
		} else if ("Public Safety".equals(category)) {
			return R.drawable.text_button_public_safety;
		} else { // Arts and Life
			return R.drawable.text_button_arts_and_life;
		}
	}

	public static int getInfoByCategory(String category) {
		if ("People".equals(category)) {
			return R.drawable.info_people;
		} else if ("Community".equals(category)) {
			return R.drawable.info_community;
		} else if ("Sports".equals(category)) {
			return R.drawable.info_sports;
		} else if ("Food".equals(category)) {
			return R.drawable.info_food;
		} else if ("Public Safety".equals(category)) {
			return R.drawable.info_public_safety;
		} else { // Arts and Life
			return R.drawable.info_arts_and_life;
		}
	}

	public static int getColorByCategory(String category) {
		int retval;
		if ("People".equals(category)) {
			retval = COLOR_PEOPLE;
		} else if ("Community".equals(category)) {
			retval = COLOR_COMMUNITY;
		} else if ("Sports".equals(category)) {
			retval = COLOR_SPORTS;
		} else if ("Food".equals(category)) {
			retval = COLOR_FOOD;
		} else if ("Public Safety".equals(category)) {
			retval = COLOR_PUBLIC_SAFETY;
		} else { // Arts and Life
			retval = COLOR_ARTS_AND_LIFE;
		}
		return retval;
	}

	public static int getLogoByCategory(String category) {
		if ("People".equals(category)) {
			return R.drawable.people_solid_fullsize;
		} else if ("Community".equals(category)) {
			return R.drawable.community_solid_fullsize;
		} else if ("Sports".equals(category)) {
			return R.drawable.sports_solid_fullsize;
		} else if ("Food".equals(category)) {
			return R.drawable.food_solid_fullsize;
		} else if ("Public Safety".equals(category)) {
			return R.drawable.public_saftey_solid_fullsize;
		} else { // Arts and Life
			return R.drawable.arts_and_life_solid_fullsize;
		}
	}

	public static int getThemeByCategory(String category) {
		if ("People".equals(category)) {
			return R.style.Theme_People;
		} else if ("Community".equals(category)) {
			return R.style.Theme_Community;
		} else if ("Sports".equals(category)) {
			return R.style.Theme_Sports;
		} else if ("Food".equals(category)) {
			return R.style.Theme_Food;
		} else if ("Public Safety".equals(category)) {
			return R.style.Theme_Public_safety;
		} else { // Arts and Life
			return R.style.Theme_Arts_and_life;
		}
	}

	public static int getActionBarIconByCategory(String category) {
		if ("People".equals(category)) {
			return R.drawable.ic_action_people;
		} else if ("Community".equals(category)) {
			return R.drawable.ic_action_community;
		} else if ("Sports".equals(category)) {
			return R.drawable.ic_action_sports;
		} else if ("Food".equals(category)) {
			return R.drawable.ic_action_food;
		} else if ("Public Safety".equals(category)) {
			return R.drawable.ic_action_public_safety;
		} else { // Arts and Life
			return R.drawable.ic_action_arts_and_life;
		}
		
		
	}
}
