package com.example.a55123;


import java.util.ArrayList;

import android.support.v7.app.ActionBarActivity;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.view.View.OnClickListener;

import java.lang.reflect.Field;

public class Me_supervise_personal extends Activity{
	
	private Button me_supervise_personal_back, good_buuton, fail_button;
	private ImageView BigimageView;
	private int[] imagelist = {
		    R.drawable.a1, R.drawable.a2, R.drawable.a3,
		    R.drawable.a4, R.drawable.a5
	};
	private String[] imgText = {
		    "owl", "pen", "goat", "di", "cow"
	};
	private LinearLayout myGallery;
	private static ArrayList<Integer> images = new ArrayList<Integer>();
	private int width;
	private int height;
	private float density;
	private int densityDpi;
	private ArrayList<ImageView> imageView_database = new ArrayList<ImageView>();
	private Field[] fields;
	private int check_change=0;
	private boolean Good=false, Failed=false;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_me_supervise_personal);
		
		me_supervise_personal_back = (Button)findViewById(R.id.me_back_supervise_personal);
		me_supervise_personal_back.setOnClickListener(new View.OnClickListener() {		
			@Override
			public void onClick(View v) {
				Me_supervise_personal.this.finish();			
			}
		});
		
		BigimageView = (ImageView)findViewById(R.id.me_back_supervise_personal_Big_image);
		BigimageView.setImageResource(R.drawable.ad);
		
		good_buuton = (Button)findViewById(R.id.me_supervise_personal_Good);
		good_buuton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				AlertDialog.Builder dialog_Good = new AlertDialog.Builder(Me_supervise_personal.this);
				dialog_Good.setTitle("Are You Sure ??");
				dialog_Good.setCancelable(true);
				dialog_Good.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			        public void onClick(DialogInterface dialog, int which) {
			        	dialog.cancel();
			      } });
				dialog_Good.setPositiveButton("Yes",new DialogInterface.OnClickListener() {
		        	@Override
					public void onClick(DialogInterface dialog,int id) {
		        		Good=true;
				        String x = ""+Good;
				        Toast.makeText(Me_supervise_personal.this, x, Toast.LENGTH_SHORT).show();
						Me_supervise_personal.this.finish();
					}
				  });
				dialog_Good.show();
			}
		});
		fail_button = (Button)findViewById(R.id.me_supervise_personal_Failed);
		fail_button.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				AlertDialog.Builder dialog_Failed = new AlertDialog.Builder(Me_supervise_personal.this);
				dialog_Failed.setTitle("Are You Sure ??");
				dialog_Failed.setCancelable(true); 
				dialog_Failed.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			        public void onClick(DialogInterface dialog, int which) {
			        	dialog.cancel();
			      } });
				dialog_Failed.setPositiveButton("Yes",new DialogInterface.OnClickListener() {
		        	@Override
					public void onClick(DialogInterface dialog,int id) {
		        		Failed=true;
				        String x = ""+Failed;
				        Toast.makeText(Me_supervise_personal.this, x, Toast.LENGTH_SHORT).show();
						Me_supervise_personal.this.finish();
					}
				  });
				dialog_Failed.show();
			}
		});
		
		setImages();
		setScreens();
		findViews();
		initdata();
		
		setImageListener();
		
	}
	
	/*@Override
	public void onClick(View v) {
		switch(v.getId()){
			case R.id.me_supervise_personal_Good:
				AlertDialog.Builder dialog_Good = new AlertDialog.Builder(Me_supervise_personal.this);
				dialog_Good.setTitle("Are You Sure ??");
				dialog_Good.setCancelable(true); 
				dialog_Good.setPositiveButton("Yes",new DialogInterface.OnClickListener() {
		        	@Override
					public void onClick(DialogInterface dialog_Good,int id) {
		        		Good=true;
				        String x = ""+Good;
				        Toast.makeText(Me_supervise_personal.this, x, Toast.LENGTH_SHORT).show();
						Me_supervise_personal.this.finish();
					}
				  });
				break;
			case R.id.me_supervise_personal_Failed:
				AlertDialog.Builder dialog_Failed = new AlertDialog.Builder(Me_supervise_personal.this);
				dialog_Failed.setTitle("Are You Sure ??");
				dialog_Failed.setCancelable(true); 
				dialog_Failed.setPositiveButton("Yes",new DialogInterface.OnClickListener() {
		        	@Override
					public void onClick(DialogInterface dialog_Failed,int id) {
		        		Failed=true;
				        String x = ""+Failed;
				        Toast.makeText(Me_supervise_personal.this, x, Toast.LENGTH_SHORT).show();
						Me_supervise_personal.this.finish();
					}
				  });
				break;
			}
	}*/

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.me_supervise_personal, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	
		 
		// 用反射機制來獲取資源中的圖片ID
	private void setImages() {
		fields = R.drawable.class.getDeclaredFields();
		for (Field field : fields) {
			if (!"icon".equals(field.getName())) {
				int index = 0;
				try {
						index = field.getInt(R.drawable.class);
				} catch (IllegalArgumentException e) {
							e.printStackTrace();
				} catch (IllegalAccessException e) {
							e.printStackTrace();
				}
				images.add(index);
			}
		}
	}
		 
		/* 手機屏幕屬性 */
	private void setScreens() {
		DisplayMetrics metric = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metric);
		width = metric.widthPixels/5; // 屏幕寬度（像素）
		height = metric.heightPixels/10; // 屏幕高度（像素）
		density = metric.density; // 屏幕密度（0.75 / 1.0 / 1.5）
		densityDpi = metric.densityDpi; // 屏幕密度DPI（120 / 160 / 240）
	}
	 
	private View insertImage(Integer id, int i) {
		LinearLayout layout = new LinearLayout(getApplicationContext());
		layout.setLayoutParams(new LayoutParams(width, height));
		layout.setGravity(Gravity.CENTER);
		 
		ImageView imageview = new ImageView(getApplicationContext());
		imageview.setLayoutParams(new LayoutParams(width, height));
		imageview.setBackgroundResource(id);
		imageView_database.add(imageview);
		 
		layout.addView(imageview);
		return layout;
	}
	
	private void findViews() {
		myGallery = (LinearLayout) findViewById(R.id.me_supervise_personal_gallery);
	}
		 
	private void initdata() {
		int i=0;
		for (Integer id : images) {
			myGallery.addView(insertImage(id,i));
			i++;
		}
	}
	private void setImageListener(){
		int image_number = imageView_database.size();
		for(int i=0 ; i<image_number ; i++){
			ImageView temp_image = imageView_database.get(i);
			final int j=i;
			temp_image.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					
					int x=0;
					try {
						x = fields[j].getInt(R.drawable.class);
					} catch (IllegalAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					BigimageView.setImageResource(x);
					check_change=x;
				}
			});
		}
	}
}
