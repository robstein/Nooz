package com.nooz.nooz.util;

/**
 * Represents one of the six Nooz categories: people, community, sports, food,
 * public safety, arts and life.
 * 
 * @author Rob Stein
 * 
 */
public enum Category {
	PEOPLE("PEOPLE"), COMMUNITY("COMMUNITY"), SPORTS("SPORTS"), FOOD("FOOD"), PUBLIC_SAFETY("PUBLIC_SAFETY"), ARTS_AND_LIFE(
			"ARTS_AND_LIFE");

	private final String name;

	private Category(String s) {
		name = s;
	}

	public boolean equalsName(String otherName) {
		return (otherName == null) ? false : name.equals(otherName);
	}

	public String toString() {
		return name;
	}
}
