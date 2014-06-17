package com.nooz.nooz.activity.profile;

public class UserDataController {

	private ProfileActivity mC;

	UserDataController(ProfileActivity c) {
		this.mC = c;
	}

	/**
	 * Communicates with NoozSerive
	 */
	public void populateProfile() {
		mC.getNoozService().getProfileInfo(mC.mUserId);
	}

	/**
	 * Call from broadcast receiver to fill views in profile activity.
	 */
	public void getUserData() {
		mC.mProfileInfo = mC.getNoozService().getLoadedProfileInfo();
		mC.mProfileName.setText(mC.mProfileInfo.firstName + " " + mC.mProfileInfo.lastName);
		mC.mProfileLocation.setText(mC.mProfileInfo.homeLocation);
		//mC.mButtonProfileNumbers.setText(mC.mProfileInfo.userScore.toString());
	}

}
