package com.example.a55123;

import java.io.FileNotFoundException;
import java.util.Calendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class Me extends Activity {
	
	private ImageButton me_photo;
	private TextView date_view, text1, text2, text3;
	private Button schedule_button, supervise_button, record_button, me_back;
	private int pYear, pMonth, pDay;
	private int choosewhich=Integer.MAX_VALUE;
	private static final int CAMERA_REQUEST = 1888;
	private static final int ALBUM_REQUEST = 999;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_me);
		
		me_back = (Button)findViewById(R.id.me_back);
		me_back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Me.this.finish();
			}
		});
		
		me_photo = (ImageButton)findViewById(R.id.me_imageButton);
		me_photo.setImageResource(R.drawable.ad);
		me_photo.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				AlertDialog.Builder dialog = new AlertDialog.Builder(Me.this);
		        dialog.setTitle("Choose Photo Source");
		        dialog.setItems(R.array.photo_oprion, new DialogInterface.OnClickListener() {
		               public void onClick(DialogInterface dialog, int which) {
		                   // The 'which' argument contains the index position
		                   // of the selected item
		            	   choosewhich=which;
		            	   if(choosewhich == 0){
		            		   Intent intent = new Intent();
	            	           intent.setType("image/*");                                         //�|�}�ҿ�����ɵ������z������������
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
			}
		});
		
		date_view = (TextView)findViewById(R.id.me_date);
		final Calendar cal = Calendar.getInstance();
        pYear = cal.get(Calendar.YEAR);
        pMonth = cal.get(Calendar.MONTH)+1;
        pDay = cal.get(Calendar.DAY_OF_MONTH);
        date_view.setText(new StringBuilder().append(pYear).append(" / ").
      	      append(pMonth).append(" / ").append(pDay));
        
		text1 = (TextView)findViewById(R.id.me_textView1);
		text2 = (TextView)findViewById(R.id.me_textView2);
		text3 = (TextView)findViewById(R.id.me_textView3);
		//-----------------------schedule button-----------------------------------------------
		schedule_button = (Button)findViewById(R.id.me_schedule_button);
		schedule_button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(Me.this, DDatePickerActivity.class);
				startActivity(i);
			}
		});
		//--------------------------------------------------------------------------
		//---------------------------------supervise button-------------------------
		supervise_button = (Button)findViewById(R.id.me_supervise_button);
		supervise_button.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(Me.this, Me_supervise_main.class);
				startActivity(i);	
			}
		});
		//--------------------------------------------------------------------------
		//-------------------------------record button-----------------------------
		record_button = (Button)findViewById(R.id.me_record_button);
		//--------------------------------------------------------------------------
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.me, menu);
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
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    if (requestCode == CAMERA_REQUEST) {
	        if (resultCode == Activity.RESULT_OK) {
	            Bitmap photo = (Bitmap) data.getExtras().get("data");
	            me_photo.setImageBitmap(photo);
	        }
	    }
	    if(requestCode == ALBUM_REQUEST){
		    if (resultCode == RESULT_OK) {
		        Uri uri = data.getData();
		        Log.e("uri", uri.toString());
		        ContentResolver cr = this.getContentResolver();
		        try {
		        Bitmap bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));
		        me_photo.setImageBitmap(bitmap);
		        } catch (FileNotFoundException e) {
		         Log.e("Exception", e.getMessage(),e);
		        }
		     }
	    }
	       super.onActivityResult(requestCode, resultCode, data);
     }
}
