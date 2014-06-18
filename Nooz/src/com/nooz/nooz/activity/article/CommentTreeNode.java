package com.nooz.nooz.activity.article;

import java.util.ArrayList;
import java.util.List;

import com.nooz.nooz.model.Comment;

public class CommentTreeNode {

	Comment comment;
	List<CommentTreeNode> children;

	public CommentTreeNode(Comment c) {
		comment = c;
		children = new ArrayList<CommentTreeNode>();
	}

	/**
	 * Constructs and incomplete comment. Only contains the id. This comment was
	 * build from a child which supplied its parent id.
	 * 
	 * @param id
	 */
	public CommentTreeNode(String id) {
		comment = new Comment(id);
		children = new ArrayList<CommentTreeNode>();
	}

	/**
	 * If the input boolean is false, constructs a sentinel node comment.
	 * 
	 * @param b
	 */
	public CommentTreeNode(boolean b) {
		if (!b) {
			comment = new Comment(Comment.NONE);
			children = new ArrayList<CommentTreeNode>();
		}
	}

	public CommentTreeNode addChild(Comment c, boolean insertAtBeginning) {
		CommentTreeNode newNode = new CommentTreeNode(c);
		if (insertAtBeginning) {
			children.add(0, newNode);
		} else {
			children.add(newNode);
		}
		return newNode;
	}
}
