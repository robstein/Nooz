package com.nooz.nooz.util;

public enum SearchType {
	RELEVANT ("RELEVANT"), BREAKING ("BREAKING");
	
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
