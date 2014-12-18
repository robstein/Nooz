package com.nooz_app.nooz;

/**
 * Created by rob on 12/17/14.
 */
public enum Medium {
    IMAGE(0), VIDEO(1), AUDIO(2);

    private final int id;

    private Medium(int n) {
        id = n;
    }

    public int value() {
        return id;
    }
}
