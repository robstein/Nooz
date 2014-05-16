package com.nooz.nooz.activity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.nooz.nooz.R;
import com.nooz.nooz.util.Tools;
import com.nooz.nooz.widget.SquareImageView;



public class NewArticleActivity extends Activity {

	private LinearLayout mLayoutStoryDetails;
	private SquareImageView mNewArticleImage;
	
	public static final int TOP_BAR_HEIGHT = 61;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_newarticle);
		
		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		int width = size.x;
		mLayoutStoryDetails = (LinearLayout) findViewById(R.id.layout_story_details);
		RelativeLayout.LayoutParams controlLayoutParams = (RelativeLayout.LayoutParams) mLayoutStoryDetails
				.getLayoutParams();
		controlLayoutParams.setMargins(0, (int) Tools.dipToPixels(this, TOP_BAR_HEIGHT) + width, 0, 0);
		mLayoutStoryDetails.setLayoutParams(controlLayoutParams);
		
		byte[] image_data  = getIntent().getByteArrayExtra("image");
		
		mNewArticleImage = (SquareImageView) findViewById(R.id.new_article_image);
		Bitmap bmp = BitmapFactory.decodeByteArray(image_data, 0, image_data.length);
		mNewArticleImage.setImageBitmap(bmp);
		
		/* IMPORTANT
		 * I need to clip the image. but for now I'm going to save the whole thing.
		 * (more than just the square
		 */
		
		
		 
	}
}
