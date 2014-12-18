package com.nooz_app.nooz;

/**
 * Created by rob on 12/17/14.
 */
public enum Category {
    PEOPLE(0), COMMUNITY(1), Sports(2), FOOD(3), PUBLIC_SAFETY(4), ARTS_AND_LIFE(5);

    private final int id;

    private Category(int n) {
        id = n;
    }

    public int value() {
        return id;
    }
}
