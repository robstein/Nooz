package com.nooz.nooz.util;

/**
 * Represents the two flavors of searches that a user can use on the map:
 * relevant or breaking.
 * 
 * @author Rob Stein
 * 
 */
public enum SearchType {
	RELEVANT("RELEVANT"), BREAKING("BREAKING");

	private final String name;

	private SearchType(String s) {
		name = s;
	}

	public boolean equalsName(String otherName) {
		return (otherName == null) ? false : name.equals(otherName);
	}

	public String toString() {
		return name;
	}
}
