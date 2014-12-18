package com.nooz_app.nooz;

/**
 * Represents the flavors of searches that are used when retrieving stories:
 *
 * @author Rob Stein
 *
 */
public enum RankingAlgorithm {
    RELEVANT(0), BREAKING(1), PROFILE(2);

    private final int id;

    private RankingAlgorithm(int n) {
        id = n;
    }

    public int value() {
        return id;
    }
}
