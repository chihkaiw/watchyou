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
import android.widget.ImageButton;
import android.widget.ImageView;

public class MainActivity extends Activity implements OnClickListener {
	
	private Button camerabutton, rankbutton, mebutton,signupbutton;
	private ImageView init_view;
    SQLiteDatabase db;
	public String db_name = "MainPageSQL";
	public String table_name = "newtable";
	
	NewListDataSQL helper = new NewListDataSQL(MainActivity.this, db_name);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		init_view = (ImageView)findViewById(R.id.main_init_imagebutton);
		init_view.setImageResource(R.drawable.main_page);
		init_view.setOnClickListener(this);
		mebutton = (Button)findViewById(R.id.me_button);
		mebutton.setOnClickListener(this);
		rankbutton = (Button)findViewById(R.id.rank_button);
		rankbutton.setOnClickListener(this);
		camerabutton = (Button)findViewById(R.id.camera_button);
		camerabutton.setOnClickListener(this);
		signupbutton = (Button)findViewById(R.id.signin_button_main_test);
		signupbutton.setOnClickListener(this);
		
		
		db = helper.getReadableDatabase();
		
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
				
			case R.id.camera_button:
				Intent i_submit = new Intent(this, Submit_task.class);
				startActivity(i_submit);
				break;
				
			case R.id.signin_button_main_test:
				Intent i_test = new Intent(this, SignUp.class);
				startActivity(i_test);
				break;
				
			case R.id.main_init_imagebutton:
				init_view.setVisibility(View.GONE);
			}
	}
	
}
