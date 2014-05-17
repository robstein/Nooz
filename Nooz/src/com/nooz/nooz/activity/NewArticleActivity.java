package com.nooz.nooz.activity;

import java.io.ByteArrayOutputStream;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
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
	
	private int mScreenWidthInPixels;
	public static final int TOP_BAR_HEIGHT = 61;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_newarticle);
		
		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		mScreenWidthInPixels = size.x;
		mLayoutStoryDetails = (LinearLayout) findViewById(R.id.layout_story_details);
		RelativeLayout.LayoutParams controlLayoutParams = (RelativeLayout.LayoutParams) mLayoutStoryDetails
				.getLayoutParams();
		controlLayoutParams.setMargins(0, (int) Tools.dipToPixels(this, TOP_BAR_HEIGHT) + mScreenWidthInPixels, 0, 0);
		mLayoutStoryDetails.setLayoutParams(controlLayoutParams);
		
		byte[] image_data  = getIntent().getByteArrayExtra("image");
		int[] pixels = new int[mScreenWidthInPixels*mScreenWidthInPixels];//the size of the array is the dimensions of the sub-photo
        //ByteArrayOutputStream bos = new ByteArrayOutputStream();
        Bitmap bitmap = BitmapFactory.decodeByteArray(image_data , 0, image_data.length);
        bitmap.getPixels(pixels, 0, mScreenWidthInPixels, 0, (int) Tools.dipToPixels(this, 81), mScreenWidthInPixels, mScreenWidthInPixels);//the stride value is (in my case) the width value
        bitmap = Bitmap.createBitmap(pixels, 0, mScreenWidthInPixels, mScreenWidthInPixels, mScreenWidthInPixels, Config.ARGB_8888);//ARGB_8888 is a good quality configuration
        //bitmap.compress(CompressFormat.JPEG, 100, bos);//100 is the best quality possible
        //byte[] square = bos.toByteArray();
		
		mNewArticleImage = (SquareImageView) findViewById(R.id.new_article_image);
		//Bitmap bmp = BitmapFactory.decodeByteArray(image_data, 0, image_data.length);
		mNewArticleImage.setImageBitmap(bitmap);
		
		/* IMPORTANT
		 * I need to clip the image. but for now I'm going to save the whole thing.
		 * (more than just the square
		 */
		
		
		 
	}
}
