package com.nooz.nooz.activity.article;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import com.nooz.nooz.model.Comment;

public class CommentThreadTree {

	private CommentTreeNode root;
	private HashMap<String, CommentTreeNode> parentMap;
	private HashSet<String> parentSet;
	private List<Comment> preOrderTraversedTree;
	private int count;

	static final boolean INSERT_AT_END = false;
	static final boolean INSERT_AT_BEGINNING = false;

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

		// Calculate count
		calculateCount();

		// Set depths
		calculateDepths();

		// Generate list
		generateList();
	}

	/**
	 * Adds a comment to the tree.
	 * 
	 * @param c
	 */
	public void addComment(Comment c) {
		// Find the parent
		CommentTreeNode parent = findNodeById(c.parentId);

		// Add new comment's parent to the parent set
		if (!parentSet.contains(c.parentId)) {
			// Haven't seen that parent before, add it
			parentSet.add(c.parentId);
			parentMap.put(c.parentId, parent);
		}

		// Make new node a child of the parent
		CommentTreeNode newNode = parent.addChild(c, INSERT_AT_END);

		// Add to count
		count += 1;

		// Set new node's depth
		newNode.comment.setDepth(parent.comment.getDepth() + 1);

		// Generate list
		// All of the above code in this method is trying to be efficient, yet
		// we need to do a linear traversal (or at least a linear lookup into
		// the comment list) to finally add the new comment :(
		generateList();
	}

	/**
	 * Node lookup.
	 * 
	 * Runtime efficiency: O(log n)
	 * 
	 * @param id
	 * @return
	 */
	private CommentTreeNode findNodeById(String id) {
		return findNodeById(root, id);
	}

	private CommentTreeNode findNodeById(CommentTreeNode node, String id) {
		if (node == null) {
			return null;
		}
		if (node.comment == null) {
			return null;
		}
		if (node.comment.id == null) {
			return null;
		}
		if (id.equals(node.comment.id)) {
			return node;
		} else {
			for (CommentTreeNode child : node.children) {
				CommentTreeNode potentialRetVal = findNodeById(child, id);
				if (potentialRetVal != null) {
					return potentialRetVal;
				}
			}
		}
		return null;
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
			if (parentSet.contains(c.id)) {
				CommentTreeNode filledInNode = finishBuilding(c);
				parentMap.get(c.parentId).addChild(filledInNode, INSERT_AT_END);
			} else {
				parentMap.get(c.parentId).addChild(c, INSERT_AT_END);
			}
		}
	}

	private CommentTreeNode finishBuilding(Comment c) {
		CommentTreeNode incompleteComment = parentMap.get(c.id);
		incompleteComment.comment.setCommenterName(c.commenterName);
		incompleteComment.comment.setCreatedAt(c.createdAt);
		incompleteComment.comment.setCurrentUserVote(c.currentUserVote);
		incompleteComment.comment.setDown(c.down);
		incompleteComment.comment.setParentId(c.parentId);
		incompleteComment.comment.setText(c.text);
		incompleteComment.comment.setUp(c.up);
		return incompleteComment;
	}

	/**
	 * Returns the number of comments in the tree.
	 * 
	 * @return
	 */
	public int getCount() {
		return count;
	}

	public Comment getItem(int position) {
		return preOrderTraversedTree.get(position);
	}

	private void calculateCount() {
		int sum = 0;
		for (Map.Entry<String, CommentTreeNode> entry : parentMap.entrySet()) {
			sum += entry.getValue().children.size();
		}
		count = sum;
	}

	private void calculateDepths() {
		setDepths(root, 0);
	}

	private void setDepths(CommentTreeNode node, int depth) {
		if (node == null)
			return;
		node.comment.setDepth(depth);
		for (CommentTreeNode child : node.children) {
			setDepths(child, depth + 1);
		}
	}

	private void generateList() {
		preOrderTraversedTree = new ArrayList<Comment>();
		preOrderTraverse(root);
	}

	private void preOrderTraverse(CommentTreeNode node) {
		if (node == null)
			return;
		if (node != root) {
			preOrderTraversedTree.add(node.comment);
		}
		for (CommentTreeNode child : node.children) {
			preOrderTraverse(child);
		}
	}
}
