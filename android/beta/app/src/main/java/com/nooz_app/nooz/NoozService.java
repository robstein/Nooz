package com.nooz_app.nooz;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by rob on 12/17/14.
 */
public interface NoozService {

    public boolean isUserAuthenticated();
    public void registerUser(String name, String email, String password);
    public void loginUser(String email, String password);
    public void logout();

    public void saveStoryRelevanceInput(Object story, Integer input);
    public void saveStory(Medium medium, Category category, String headline, String caption, LatLng location);

}
