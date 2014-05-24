package com.nooz.nooz.util;

public enum MediaMode {
	AUDIO("AUDIO"), PICTURE("PICTURE"), VIDEO("VIDEO");

	private final String name;

	private MediaMode(String s) {
		name = s;
	}

	public boolean equalsName(String otherName) {
		return (otherName == null) ? false : name.equals(otherName);
	}

	public String toString() {
		return name;
	}
}
