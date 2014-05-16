package com.nooz.nooz.activity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import com.nooz.nooz.R;
import com.nooz.nooz.widget.SquareImageView;



public class NewArticleActivity extends Activity {

	private SquareImageView mNewArticleImage;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_newarticle);
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
