package com.nooz.nooz.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class Story implements Parcelable {

	@SerializedName("id")
	public String id;
	@SerializedName("firstName")
	public String firstName;
	@SerializedName("lastName")
	public String lastName;
	@SerializedName("category")
	public String category;
	@SerializedName("headline")
	public String headline;
	@SerializedName("caption")
	public String caption;
	@SerializedName("keyword1")
	public String keyword1;
	@SerializedName("keyword2")
	public String keyword2;
	@SerializedName("keyword3")
	public String keyword3;
	@SerializedName("lat")
	public Double lat;
	@SerializedName("lng")
	public Double lng;
	@SerializedName("__createdAt")
	public String __createdAt;

	public Double radius;

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(id);
		dest.writeString(firstName);
		dest.writeString(lastName);
		dest.writeString(category);
		dest.writeString(headline);
		dest.writeString(caption);
		dest.writeString(keyword1);
		dest.writeString(keyword2);
		dest.writeString(keyword3);
		dest.writeDouble(lat);
		dest.writeDouble(lng);
		dest.writeString(__createdAt);

		dest.writeDouble(radius);
	}

	/* Constructor from Parcel, reads back fields IN THE ORDER they were written */
	public Story(Parcel pc) {
		id = pc.readString();
		firstName = pc.readString();
		lastName = pc.readString();
		category = pc.readString();
		headline = pc.readString();
		caption = pc.readString();
		keyword1 = pc.readString();
		keyword2 = pc.readString();
		keyword3 = pc.readString();
		lat = pc.readDouble();
		lng = pc.readDouble();
		__createdAt = pc.readString();
		radius = pc.readDouble();
	}

	public static final Parcelable.Creator<Story> CREATOR = new Parcelable.Creator<Story>() {
		public Story createFromParcel(Parcel pc) {
			return new Story(pc);
		}

		public Story[] newArray(int size) {
			return new Story[size];
		}
	};

	public Double getRadius() {
		return radius;
	}

	public void setRadius(Double radius) {
		this.radius = radius;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getHeadline() {
		return headline;
	}

	public void setHeadline(String headline) {
		this.headline = headline;
	}

	public String getCaption() {
		return caption;
	}

	public void setCaption(String caption) {
		this.caption = caption;
	}

	public String getKeyword1() {
		return keyword1;
	}

	public void setKeyword1(String keyword1) {
		this.keyword1 = keyword1;
	}

	public String getKeyword2() {
		return keyword2;
	}

	public void setKeyword2(String keyword2) {
		this.keyword2 = keyword2;
	}

	public String getKeyword3() {
		return keyword3;
	}

	public void setKeyword3(String keyword3) {
		this.keyword3 = keyword3;
	}

	public Double getLat() {
		return lat;
	}

	public void setLat(Double lat) {
		this.lat = lat;
	}

	public Double getLng() {
		return lng;
	}

	public void setLng(Double lng) {
		this.lng = lng;
	}

	public String get__createdAt() {
		return __createdAt;
	}

	public void set__createdAt(String __createdAt) {
		this.__createdAt = __createdAt;
	}

}
