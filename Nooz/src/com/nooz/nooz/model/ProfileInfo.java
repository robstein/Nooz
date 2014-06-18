package com.nooz.nooz.model;

import com.google.gson.annotations.SerializedName;

public class ProfileInfo {
	/**
	 * user id of the profile owner
	 */
	@SerializedName("id")
	public String id;

	/**
	 * name of the profile owner
	 */
	@SerializedName("name")
	public String name;

	/**
	 * Home location of the profile owner
	 */
	@SerializedName("homeLocation")
	public String homeLocation;

	/**
	 * Score of the profile owner
	 */
	@SerializedName("userScore")
	public Integer userScore;

}
