package com.nooz.nooz.model;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Story model which gets stuffed from Azure Mobile Service custom api:
 * getNooz.js. Stories are also parcelable as they must be passed through
 * intents to different activities.
 * 
 * @author Rob Stein
 * 
 */
public class Story implements Parcelable {

	/**
	 * story id
	 */
	@SerializedName("id")
	public String id;

	/**
	 * author id
	 */
	@SerializedName("authorId")
	public String authorId;

	/**
	 * story author's first name
	 */
	@SerializedName("firstName")
	public String firstName;

	/**
	 * story author's last name
	 */
	@SerializedName("lastName")
	public String lastName;

	/**
	 * story's category
	 */
	@SerializedName("category")
	public String category;

	/**
	 * story headline
	 */
	@SerializedName("headline")
	public String headline;

	/**
	 * story caption
	 */
	@SerializedName("caption")
	public String caption;

	/**
	 * story keyword1
	 */
	@SerializedName("keyword1")
	public String keyword1;

	/**
	 * story keyword2
	 */
	@SerializedName("keyword2")
	public String keyword2;

	/**
	 * story keyword3
	 */
	@SerializedName("keyword3")
	public String keyword3;

	/**
	 * latitude componenent of geolocation of where story occured
	 */
	@SerializedName("lat")
	public Double lat;

	/**
	 * longitude componenent of geolocation of where story occured
	 */
	@SerializedName("lng")
	public Double lng;

	/**
	 * date and time that the story broke
	 */
	@SerializedName("__createdAt")
	public String __createdAt;

	/**
	 * current user's story preference, either 0, -1, or 1
	 */
	@SerializedName("user_relevance")
	public Integer userRelevance;

	/**
	 * total relevance score for this story
	 */
	@SerializedName("relevantScore")
	public Integer scoreRelevance;
	/**
	 * total irrelevance score for this story
	 */
	@SerializedName("irrelevantScore")
	public Integer scoreIrrelevance;

	/**
	 * medium that was recorded for this story
	 */
	@SerializedName("medium")
	public String medium;

	/**
	 * radius of this story on a map
	 */
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
		dest.writeInt(userRelevance);
		dest.writeInt(scoreRelevance);
		dest.writeInt(scoreIrrelevance);
		dest.writeString(medium);
		dest.writeString(authorId);

		dest.writeDouble(radius);
	}

	/**
	 * Constructor from Parcel
	 * 
	 * @param pc
	 *            parcel from which to read back fields
	 */
	public Story(Parcel pc) {
		// reads back fields IN THE ORDER they were written
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
		userRelevance = pc.readInt();
		scoreRelevance = pc.readInt();
		scoreIrrelevance = pc.readInt();
		medium = pc.readString();
		authorId = pc.readString();

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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Story other = (Story) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	/* ***** GETTERS AND SETTERS ***** */

	/**
	 * 
	 * @return current user relevance score; either -1, 0, or 1
	 */
	public Integer getUserRelevance() {
		return userRelevance;
	}

	/**
	 * 
	 * @param userRelevance
	 */
	public void setUserRelevance(Integer userRelevance) {
		this.userRelevance = userRelevance;
	}

	/**
	 * 
	 * @return story bubble radius
	 */
	public Double getRadius() {
		return radius;
	}

	/**
	 * 
	 * @param radius
	 */
	public void setRadius(Double radius) {
		this.radius = radius;
	}

	/**
	 * 
	 * @return story id
	 */
	public String getId() {
		return id;
	}

	/**
	 * 
	 * @param id
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * 
	 * @return author first name
	 */
	public String getFirstName() {
		return firstName;
	}

	/**
	 * 
	 * @param firstName
	 *            author's first name
	 */
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	/**
	 * 
	 * @return author last name
	 */
	public String getLastName() {
		return lastName;
	}

	/**
	 * 
	 * @param lastName
	 *            authors last name
	 */
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	/**
	 * 
	 * @return story category
	 */
	public String getCategory() {
		return category;
	}

	/**
	 * 
	 * @param category
	 */
	public void setCategory(String category) {
		this.category = category;
	}

	/**
	 * 
	 * @return story headline
	 */
	public String getHeadline() {
		return headline;
	}

	/**
	 * 
	 * @param headline
	 */
	public void setHeadline(String headline) {
		this.headline = headline;
	}

	/**
	 * 
	 * @return story caption
	 */
	public String getCaption() {
		return caption;
	}

	/**
	 * 
	 * @param caption
	 */
	public void setCaption(String caption) {
		this.caption = caption;
	}

	/**
	 * 
	 * @return keyword1
	 */
	public String getKeyword1() {
		return keyword1;
	}

	/**
	 * 
	 * @param keyword1
	 */
	public void setKeyword1(String keyword1) {
		this.keyword1 = keyword1;
	}

	/**
	 * 
	 * @return keyword2
	 */
	public String getKeyword2() {
		return keyword2;
	}

	/**
	 * 
	 * @param keyword2
	 */
	public void setKeyword2(String keyword2) {
		this.keyword2 = keyword2;
	}

	/**
	 * 
	 * @return keyword3
	 */
	public String getKeyword3() {
		return keyword3;
	}

	/**
	 * 
	 * @param keyword3
	 */
	public void setKeyword3(String keyword3) {
		this.keyword3 = keyword3;
	}

	/**
	 * 
	 * @return lat component of geolocation of story
	 */
	public Double getLat() {
		return lat;
	}

	/**
	 * 
	 * @param lat
	 *            latitude of geolocation of story
	 */
	public void setLat(Double lat) {
		this.lat = lat;
	}

	/**
	 * 
	 * @return lng component of geolocation of story
	 */
	public Double getLng() {
		return lng;
	}

	/**
	 * 
	 * @param lng
	 *            longitude of geolocation of story
	 */
	public void setLng(Double lng) {
		this.lng = lng;
	}

	/**
	 * 
	 * @return date and time article was posted
	 */
	public String get__createdAt() {
		return __createdAt;
	}

	/**
	 * 
	 * @param __createdAt
	 *            date and time article was posted
	 */
	public void set__createdAt(String __createdAt) {
		this.__createdAt = __createdAt;
	}

	/**
	 * 
	 * @return total relevance score
	 */
	public Integer getScoreRelevance() {
		return scoreRelevance;
	}

	/**
	 * 
	 * @param scoreRelevance
	 *            total relevance score
	 */
	public void setScoreRelevance(Integer scoreRelevance) {
		this.scoreRelevance = scoreRelevance;
	}

	/**
	 * 
	 * @return total irrelevance score
	 */
	public Integer getScoreIrrelevance() {
		return scoreIrrelevance;
	}

	/**
	 * 
	 * @param scoreIrrelevance
	 *            total irrelevance score
	 */
	public void setScoreIrrelevance(Integer scoreIrrelevance) {
		this.scoreIrrelevance = scoreIrrelevance;
	}

	/**
	 * 
	 * @return the story's medium
	 */
	public String getMedium() {
		return medium;
	}

	/**
	 * 
	 * @param medium
	 */
	public void setMedium(String medium) {
		this.medium = medium;
	}

	/**
	 * 
	 * @return authorId
	 */
	public String getAuthorId() {
		return authorId;
	}

	/**
	 * 
	 * @param authorId
	 */
	public void setAuthorId(String authorId) {
		this.authorId = authorId;
	}

}
