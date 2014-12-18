package com.nooz_app.nooz;

import com.google.android.gms.maps.model.LatLng;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

/**
 * Created by rob on 12/17/14.
 */
public class ParseDAL implements NoozService {

    public boolean isUserAuthenticated() {
        return ParseUser.getCurrentUser() != null;
    }

    public void registerUser(String name, String email, String password) {
        ParseUser user = new ParseUser();
        user.setUsername(email);
        user.setPassword(password);
        user.setEmail(email);

        user.put("name", name);

        user.signUpInBackground(new SignUpCallback() {
            public void done(ParseException e) {
                if (e == null) {
                    // Hooray! Let them use the app now.
                } else {
                    // Sign up didn't succeed. Look at the ParseException
                    // to figure out what went wrong
                }
            }
        });
    }

    public void loginUser(String email, String password) {
        ParseUser.logInInBackground(email, password, new LogInCallback() {
            public void done(ParseUser user, ParseException e) {
                if (user != null) {
                    // Hooray! The user is logged in.
                } else {
                    // Signup failed. Look at the ParseException to see what happened.
                }
            }
        });
    }

    public void logout() {
        ParseUser.logOut();
    }

    public void saveStoryRelevanceInput(Object story, Integer input) {
        ParseObject vote = new ParseObject("StoryVote");
        vote.put("value", input);
        vote.put("story", story);
        vote.put("voter", ParseUser.getCurrentUser());
        vote.saveInBackground();
    }

    public void saveStory(Medium medium, Category category, String headline, String caption, LatLng location) {
        ParseObject story = new ParseObject("Story");
        story.put("mediumId", medium.value());
        story.put("author", ParseUser.getCurrentUser());
        story.put("categoryId", category.value());
        story.put("headline", headline);
        story.put("caption", caption);
        story.put("location", new ParseGeoPoint(location.latitude, location.longitude));

        // add array of hashtags
        // add array of sharePlatforms

        story.saveInBackground();
    }


}
