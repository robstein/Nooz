package com.nooz.nooz.util;

/**
 * Static class for global constants.
 * 
 * @author Rob Stein
 * 
 */
public class GlobalConstant {

	public static final String CONTAINER_NAME = "media";

	/**
	 * Height in pixels of the top pseudo-action bar.
	 */
	public static final int TOP_BAR_HEIGHT = 61;

	/**
	 * Intent action constant for communication between NoozService and a
	 * Broadcast Receiver handling profile info loads.
	 */
	public static final String PROFILE_INFO_LOADED_ACTION = "profile info loaded";
}
