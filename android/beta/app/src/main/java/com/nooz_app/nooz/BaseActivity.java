package com.nooz_app.nooz;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

/**
 * Created by rob on 11/30/14.
 */
public class BaseActivity extends Activity {

    /**
     * Human-readable form of the zero magic constant. Use for element zero of lists and whatnot.
     */
    protected static final int ZERO = 0;

    protected static final String ACTION_STORIES_LOADED = "stories.loaded";
    protected static final String ACTION_STORYIMAGE_LOADED_ = "storyImage.loaded";
    protected static final String ACTION_RELEVANCE_UPDATED = "relevance.updated";


    protected Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        NoozApplication app = (NoozApplication) getApplication();
        app.setActivity(this);
        //app.setup-parse-stuff

        mContext = this;
    }


}
