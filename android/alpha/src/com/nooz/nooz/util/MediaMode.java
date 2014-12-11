package com.nooz.nooz.util;

/**
 * Represents the different types of media that the MediaRecorderActivity can
 * record: audio, pictures, video.
 * 
 * @author Rob Stein
 * 
 */
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
