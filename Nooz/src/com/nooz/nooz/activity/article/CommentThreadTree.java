package com.nooz.nooz.activity.article;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import com.nooz.nooz.model.Comment;

public class CommentThreadTree {

	CommentTreeNode root;
	HashMap<String, CommentTreeNode> parentMap;
	HashSet<String> parentSet;

	public CommentThreadTree(List<Comment> listOfComments) {
		// Initially add the "no parent" node to the parentSet
		parentSet = new HashSet<String>();
		parentSet.add(Comment.NONE);

		// Initially add a sentinel node to the parentMap
		parentMap = new HashMap<String, CommentTreeNode>();
		parentMap.put(Comment.NONE, new CommentTreeNode(false));

		// Build the tree
		addParents(listOfComments);
		connectNodes(listOfComments);
	}

	/**
	 * First go through and add all the parents.
	 * 
	 * @param listOfComments
	 */
	private void addParents(List<Comment> listOfComments) {
		for (Comment c : listOfComments) {
			if (parentSet.contains(c.parentId)) {
				continue;
			} else {
				// Haven't seen that parent before, add it
				parentSet.add(c.parentId);
				parentMap.put(c.parentId, new CommentTreeNode(c.parentId));
			}
		}
	}

	private void connectNodes(List<Comment> listOfComments) {
		for (Comment c : listOfComments) {
			if (parentSet.contains(c)) {
				finishBuilding(c);
			} else {
				parentMap.get(c.parentId).addChild(c);
			}
		}
	}

	private void finishBuilding(Comment c) {
		CommentTreeNode incompleteComment = parentMap.get(c.id);
		incompleteComment.comment.setCommenterName(c.commenterName);
		incompleteComment.comment.setCreatedAt(c.createdAt);
		incompleteComment.comment.setCurrentUserVote(c.currentUserVote);
		incompleteComment.comment.setDown(c.down);
		incompleteComment.comment.setParentId(c.parentId);
		incompleteComment.comment.setText(c.text);
		incompleteComment.comment.setUp(c.up);
	}

}
