package com.nooz.nooz.activity.article;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.nooz.nooz.util.GlobalConstant;

public class ArticleBroadcastReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		String intentAction = intent.getAction();
		if (GlobalConstant.BLOB_LOADED_ACTION.equals(intentAction)) {
			((ArticleActivity) context).mArticleDataController.handleBlob();
		} else if (GlobalConstant.COMMENTS_LOADED_ACTION.equals(intentAction)) {
			((ArticleActivity) context).mArticleDataController.loadComments();
		}
	}

}
