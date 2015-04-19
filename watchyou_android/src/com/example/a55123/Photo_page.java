package com.example.a55123;

import java.io.FileNotFoundException;
import java.util.ArrayList;

import com.example.a55123.support.NewListDataSQL;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class Photo_page extends Activity implements OnClickListener{
	
	private Button back, addnew, submit;
	private ImageView imageview;
	private int choosewhich=Integer.MAX_VALUE;
	private static final int CAMERA_REQUEST = 1888;
	private static final int ALBUM_REQUEST = 999;
	
	private Bundle bundle;
	private String _ID, title;
	private int Year, Month, Day;
	
	SQLiteDatabase db;
    public String db_name = "MainPageSQL";
	public String table_name = "newtable";
	NewListDataSQL helper = new NewListDataSQL(Photo_page.this, db_name);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_photo_page);
		
		db = helper.getWritableDatabase();
		
		 bundle = getIntent().getExtras();
		 ArrayList<Integer> box;
		 box = bundle.getIntegerArrayList("dateinfomation");
		 Year=box.get(0);
		 Month=box.get(1);
		 Day=box.get(2);
		 
		 String[] str = bundle.getStringArray("Title and ID");
		 title = str[0];
		 _ID = str[1];
		
		
		back = (Button)findViewById(R.id.photo_page_back);
		back.setOnClickListener(this);
		addnew = (Button)findViewById(R.id.photo_page_addnew);
		addnew.setOnClickListener(this);
		submit = (Button)findViewById(R.id.photo_page_submit);
		submit.setOnClickListener(this);
		
		imageview = (ImageView)findViewById(R.id.photo_page_imageview);
	}
	
	@Override
	public void onClick(View v) {
		switch(v.getId()){
			case R.id.photo_page_back:
				Intent i = new Intent(Photo_page.this,Submit_task.class);
        	    setResult(Activity.RESULT_OK,i);
				finish();
				break;
				
			case R.id.photo_page_addnew:
				AlertDialog.Builder dialog = new AlertDialog.Builder(Photo_page.this);
		        dialog.setTitle("Choose Photo Source");
		        dialog.setItems(R.array.photo_oprion, new DialogInterface.OnClickListener() {
		               public void onClick(DialogInterface dialog, int which) {
		                   // The 'which' argument contains the index position
		                   // of the selected item
		            	   choosewhich=which;
		            	   if(choosewhich == 0){
		            		   Intent intent = new Intent();
	            	           intent.setType("image/*");                                        
	            	           intent.setAction(Intent.ACTION_GET_CONTENT); 
	            	           startActivityForResult(intent, ALBUM_REQUEST);
		             	   }
		             	   else if(choosewhich == 1){
		             		   	Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
	        			    	startActivityForResult(cameraIntent, CAMERA_REQUEST); 
		             	   }
		               }
		        });
		        dialog.show();
				break;
				
			case R.id.photo_page_submit:
				ComfirmSubmitStatusAtTable();
				
        	    //finish();
				break;
		}
		
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    if (requestCode == CAMERA_REQUEST) {
	        if (resultCode == Activity.RESULT_OK) {
	            Bitmap photo = (Bitmap) data.getExtras().get("data");
	            imageview.setImageBitmap(photo);
	        }
	    }
	    if(requestCode == ALBUM_REQUEST){
		    if (resultCode == RESULT_OK) {
		        Uri uri = data.getData();
		        Log.e("uri", uri.toString());
		        ContentResolver cr = this.getContentResolver();
		        try {
		        Bitmap bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));
		        imageview.setImageBitmap(bitmap);
		        } catch (FileNotFoundException e) {
		         Log.e("Exception", e.getMessage(),e);
		        }
		     }
	    }
	       super.onActivityResult(requestCode, resultCode, data);
     }
	
	private void ComfirmSubmitStatusAtTable(){
		
		ContentValues cv = new ContentValues();
		cv.put("submit", true);
		long long1 = db.update(table_name, cv, "_ID=" + _ID, null);
		
	   if (long1 == -1) {
 		   Toast.makeText(Photo_page.this,"Submit Fail！", Toast.LENGTH_SHORT).show();
 	   }
 	   else{    
 		   Toast.makeText(Photo_page.this,"Submit Success!", Toast.LENGTH_SHORT).show();
 	   }
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.photo_page, menu);
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

	
}
