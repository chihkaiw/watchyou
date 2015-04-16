package com.example.a55123;


import com.example.a55123.support.NewListDataSQL;

import android.support.v7.app.ActionBarActivity;
import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

public class MainActivity extends ActionBarActivity implements OnClickListener {
	private static final String _TAG = "Sudoku";
	
	private Button camerabutton, rankbutton, mebutton;
	private ImageView imageView;
	private static final int CAMERA_REQUEST = 1888;
    SQLiteDatabase db;
	public String db_name = "MainPageSQL";
	public String table_name = "newtable";
	
	NewListDataSQL helper = new NewListDataSQL(MainActivity.this, db_name);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mebutton = (Button)findViewById(R.id.me_button);
		mebutton.setOnClickListener(this);
		rankbutton = (Button)findViewById(R.id.rank_button);
		rankbutton.setOnClickListener(this);
		camerabutton = (Button)findViewById(R.id.camera_button);
		
		imageView = (ImageView) this.findViewById(R.id.imageView1);
		imageView.setImageResource(R.drawable.ad);
		
		db = helper.getReadableDatabase();

		
		/*aboutButton.setOnClickListener(new View.OnClickListener() {
        	@Override
            public void onClick(View v) { 
 
        		Intent i = new Intent();
        		i.setClass(MainActivity.this,DDatePickerActivity.class);
    			startActivity(i);

        	 }
        		 
		 });*/
		
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		super.onCreateOptionsMenu(menu);
		MenuInflater menulist = getMenuInflater();
		menulist.inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onClick(View arg0) {
		switch(arg0.getId()){
			case R.id.me_button:
				Intent i = new Intent(this, Me.class);
				startActivity(i);
				break;
			case R.id.rank_button:
				Intent i_rank = new Intent(this, Rank.class);
				startActivity(i_rank);
				break;
			}
	}
	
	public void onClick_Camera(View view) {
		Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
	    startActivityForResult(cameraIntent, CAMERA_REQUEST);
	}
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    if (requestCode == CAMERA_REQUEST) {
	        if (resultCode == Activity.RESULT_OK) {
	            Bitmap photo = (Bitmap) data.getExtras().get("data");
	 
	            //ImageView imageView = (ImageView) this.findViewById(R.id.imageView1);
	            imageView.setImageBitmap(photo);
	        }
	    }
	}
	
	
	
}
