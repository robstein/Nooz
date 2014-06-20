package com.nooz.nooz.activity.article;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
			TextView viewScore = (TextView) convertView.findViewById(R.id.comment_score);
			TextView viewText = (TextView) convertView.findViewById(R.id.comment_text);
			ImageButton viewReply = (ImageButton) convertView.findViewById(R.id.comment_reply);

			// Fill in comment info
			viewAuthor.setText(comment.commenterName);
			viewDatetime.setText(Tools.getDate(comment.createdAt));
			viewScore.setText("" + (comment.up - comment.down));
			viewText.setText(comment.text);
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
