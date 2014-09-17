package com.nooz.nooz.util;

public enum ContentType {
	IMAGE_JPEG("image/jpeg");

	private final String name;

	private ContentType(String s) {
		name = s;
	}

	public boolean equalsName(String otherName) {
		return (otherName == null) ? false : name.equals(otherName);
	}

	public String toString() {
		return name;
	}
}
