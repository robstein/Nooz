package com.nooz.nooz.activity.article;

import java.util.Currency;

import android.content.Context;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.microsoft.windowsazure.mobileservices.TableJsonOperationCallback;
import com.nooz.nooz.R;
import com.nooz.nooz.model.Comment;
import com.nooz.nooz.util.Tools;

public class CommentAdapter extends BaseAdapter {

	private ArticleActivity mContext;
	private CommentThreadTree mCommentTree;

	public CommentAdapter(ArticleActivity context, CommentThreadTree commentThreadTree) {
		mContext = context;
		mCommentTree = commentThreadTree;
	}

	@Override
	public int getCount() {
		return mCommentTree.getCount() + 1;
	}

	@Override
	public Comment getItem(int position) {
		return mCommentTree.getItem(position - 1);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View retval;
		if (position == 0) {
			LayoutInflater infalInflater = (LayoutInflater) this.mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View storyView = infalInflater.inflate(R.layout.layout_story_content, null);

			mContext.initStoryViews(storyView);
			mContext.initStoryListeners();
			mContext.initPictureParameters();
			mContext.drawArticleHeadlineAuthorAndText();
			mContext.loadNetworkImages();

			retval = storyView;
		} else {
			if (convertView == null) {
				LayoutInflater infalInflater = (LayoutInflater) this.mContext
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = infalInflater.inflate(R.layout.comment_item, null);
			} else if (convertView.getId() == R.id.article_content) {
				LayoutInflater infalInflater = (LayoutInflater) this.mContext
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = infalInflater.inflate(R.layout.comment_item, null);
			}

			final Comment comment = (Comment) getItem(position);
			if (comment == null) {
				return convertView;
			}

			View viewReplyBar = convertView.findViewById(R.id.comment_reply_bar);
			TextView viewAuthor = (TextView) convertView.findViewById(R.id.comment_author);
			TextView viewDatetime = (TextView) convertView.findViewById(R.id.comment_datetime);
			final TextView viewScore = (TextView) convertView.findViewById(R.id.comment_score);
			TextView viewText = (TextView) convertView.findViewById(R.id.comment_text);
			final ImageButton viewUpvote = (ImageButton) convertView.findViewById(R.id.comment_plus);
			final ImageButton viewDownvote = (ImageButton) convertView.findViewById(R.id.comment_minus);
			ImageButton viewReply = (ImageButton) convertView.findViewById(R.id.comment_reply);

			// Fill in comment info
			viewAuthor.setText(comment.commenterName);
			viewDatetime.setText(Tools.getDate(comment.createdAt));
			viewScore.setText("" + (comment.up - comment.down));
			viewText.setText(comment.text);
			switch (comment.currentUserVote) {
			case 1: // If user has previously upvoted
				viewUpvote.setImageResource(R.drawable.ic_action_good_selected);
				break;
			case 0: // If user hsn't voted
				// Do nothing
				break;
			case -1: // If user has previously downvoted
				viewDownvote.setImageResource(R.drawable.ic_action_bad_selected);
				break;
			}

			// Comment onclickListeners
			viewUpvote.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// What is the current user's previous vote status?:
					switch (comment.currentUserVote) {
					case 1: // If user is un-upvoting
						comment.currentUserVote = 0;
						viewUpvote.setImageResource(R.drawable.ic_action_good);
						viewScore.setText("" + (Integer.parseInt(viewScore.getText().toString()) - 1));
						break;
					case 0: // If user is simply upvoting
						comment.currentUserVote = 1;
						viewUpvote.setImageResource(R.drawable.ic_action_good_selected);
						viewScore.setText("" + (Integer.parseInt(viewScore.getText().toString()) + 1));
						break;
					case -1: // If user is changing downvote to upvote
						comment.currentUserVote = 1;
						viewUpvote.setImageResource(R.drawable.ic_action_good_selected);
						viewDownvote.setImageResource(R.drawable.ic_action_bad);
						viewScore.setText("" + (Integer.parseInt(viewScore.getText().toString()) + 2));
						break;
					}
					mContext.getNoozService().saveCommentRelevanceInput(comment.id, comment.currentUserVote,
							mContext.onSaveRelevance);
				}
			});
			viewDownvote.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// What is the current user's previous vote status?:
					switch (comment.currentUserVote) {
					case 1: // If user is changing upvote to downvote
						comment.currentUserVote = -1;
						viewUpvote.setImageResource(R.drawable.ic_action_good);
						viewDownvote.setImageResource(R.drawable.ic_action_bad_selected);
						viewScore.setText("" + (Integer.parseInt(viewScore.getText().toString()) - 2));
						break;
					case 0: // If user is simply downvoting
						comment.currentUserVote = -1;
						viewDownvote.setImageResource(R.drawable.ic_action_bad_selected);
						viewScore.setText("" + (Integer.parseInt(viewScore.getText().toString()) - 1));
						break;
					case -1: // If user is un-downvoting
						comment.currentUserVote = 0;
						viewDownvote.setImageResource(R.drawable.ic_action_bad);
						viewScore.setText("" + (Integer.parseInt(viewScore.getText().toString()) + 1));
						break;
					}
					mContext.getNoozService().saveCommentRelevanceInput(comment.id, comment.currentUserVote,
							mContext.onSaveRelevance);
				}
			});
			viewReply.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					mContext.mButtonPostComment.setVisibility(View.VISIBLE);
					mContext.mInputTextComment.setVisibility(View.VISIBLE);
					mContext.mRelevanceFooter.setVisibility(View.GONE);
					mContext.parentIdOfCommentToBe = comment.id;
				}
			});

			// Add tree bar
			RelativeLayout.LayoutParams replyBarParams = (RelativeLayout.LayoutParams) viewReplyBar.getLayoutParams();
			replyBarParams.width = (int) Tools.dipToPixels(mContext, 10 * (comment.depth - 1));
			viewReplyBar.setLayoutParams(replyBarParams);

			retval = convertView;
		}
		return retval;
	}
}
