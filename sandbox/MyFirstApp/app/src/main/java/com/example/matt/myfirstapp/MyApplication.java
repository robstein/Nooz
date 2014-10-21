package com.example.matt.myfirstapp;

import android.app.Application;
import com.parse.Parse;

/**
 * Created by Matt on 10/12/2014.
 */
public class MyApplication extends Application {

    @Override
    public void onCreate()
    {
        super.onCreate();

        /* Initialize Parse using the shit they gave us */
        Parse.initialize(this, "HMxlpuCAr7OhDqEXTKM7KbU6TVeg1hJvav4XyHfv", "zM7fwyk7B3DC6nEKTtLy356JPHCg9lSLcodFS0ZS");
    }
}
