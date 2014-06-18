package com.nooz.nooz.model;

import com.google.gson.annotations.SerializedName;

/**
 * Comment model which gets stuffed from a Azure Mobile Service custom api.
 * 
 * @author Rob Stein
 * 
 */
public class Comment {

	/**
	 * This string constant can be used as the parent for comments with no
	 * parent.
	 */
	public static final String NONE = "no parent";

	public Comment(String id) {
		this.id = id;
	}

	/**
	 * id of the comment
	 */
	@SerializedName("id")
	public String id;

	/**
	 * The name of the commenter
	 */
	@SerializedName("commenterName")
	public String commenterName;

	/**
	 * date and time that the comment was posted
	 */
	@SerializedName("createdAt")
	public String createdAt;

	/**
	 * text/body of the comment
	 */
	@SerializedName("text")
	public String text;

	@SerializedName("up")
	public Integer up;

	@SerializedName("down")
	public Integer down;

	@SerializedName("currentUserVote")
	public Integer currentUserVote;

	/**
	 * id of the comment's parent. is "null" if the comment has no parent
	 */
	@SerializedName("parentId")
	public String parentId;

	/**
	 * Depth of the comment in the comment tree. 1 if the parent is the root.
	 */
	public int depth;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCommenterName() {
		return commenterName;
	}

	public void setCommenterName(String commenterName) {
		this.commenterName = commenterName;
	}

	public String getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Integer getUp() {
		return up;
	}

	public void setUp(Integer up) {
		this.up = up;
	}

	public Integer getDown() {
		return down;
	}

	public void setDown(Integer down) {
		this.down = down;
	}

	public Integer getCurrentUserVote() {
		return currentUserVote;
	}

	public void setCurrentUserVote(Integer currentUserVote) {
		this.currentUserVote = currentUserVote;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public int getDepth() {
		return depth;
	}

	public void setDepth(int depth) {
		this.depth = depth;
	}

}
