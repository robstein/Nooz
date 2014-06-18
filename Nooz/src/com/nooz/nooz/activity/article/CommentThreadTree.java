package com.nooz.nooz.activity.article;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import android.content.Context;
import android.widget.ExpandableListView;

import com.nooz.nooz.model.Comment;

public class CommentThreadTree {

	CommentTreeNode root;
	HashMap<String, CommentTreeNode> parentMap;
	HashSet<String> parentSet;
	private ExpandableListView mLayoutComments;
	private Context mContext;
	private CommentAdapter mCommentAdapter;

	public CommentThreadTree(List<Comment> listOfComments) {
		// Initially add the "no parent" node to the parentSet
		parentSet = new HashSet<String>();
		parentSet.add(Comment.NONE);

		// Initially add a sentinel node to the parentMap
		parentMap = new HashMap<String, CommentTreeNode>();
		root = new CommentTreeNode(false);
		parentMap.put(Comment.NONE, root);

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

	public void inflate(Context c, ExpandableListView layoutComments) {
		mContext = c;
		mLayoutComments = layoutComments;

		List<String> listDataHeader = new ArrayList<String>();
		for (CommentTreeNode child : root.children) {
			listDataHeader.add(child.comment.id);
		}

		mCommentAdapter = new CommentAdapter(mContext, listDataHeader, new HashMap<String, List<String>>());
		layoutComments.setAdapter(mCommentAdapter);

		// inflate(root, 0);
	}

	/**
	 * (Recursive) Pre-order traversal
	 * 
	 * @param curr
	 * @param depth
	 */
	private void inflate(CommentTreeNode curr, int depth) {
		if (curr == null) {
			return;
		}
		if (curr != root) {
			// writeComment(curr.comment, depth);
		}
		for (CommentTreeNode node : curr.children) {
			inflate(node, depth + 1);
		}
	}
}
