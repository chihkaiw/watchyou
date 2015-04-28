package com.example.a55123;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.example.a55123.support.NewListDataSQL;
import com.example.a55123.support.NewPersonalDataSQL;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
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
	private static final int CAMERA_REQUEST = 188;
	private static final int ALBUM_REQUEST = 999;
	
	private Bundle bundle;
	private String _ID, title,webID;
	private int Year, Month, Day;
	private String[] persinal_data;
	
	SQLiteDatabase db;
	SQLiteDatabase db_personal;
    public String db_name = "MainPageSQL";
	public String table_name = "newtable";
	NewListDataSQL helper = new NewListDataSQL(Photo_page.this, db_name);
	public String db_name_personal = "PersonalSQL";
	public String table_name_personal = "personaldata";
	NewPersonalDataSQL personaldata_helper = new NewPersonalDataSQL(Photo_page.this, db_name_personal);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_photo_page);
		
		db = helper.getWritableDatabase();
		db_personal = personaldata_helper.getWritableDatabase();
		 bundle = getIntent().getExtras();
		 ArrayList<Integer> box;
		 box = bundle.getIntegerArrayList("dateinfomation");
		 Year=box.get(0);
		 Month=box.get(1);
		 Day=box.get(2);
		 
		 String[] str = bundle.getStringArray("Title and ID");
		 title = str[0];
		 _ID = str[1];
		 webID = str[2];
		 
		 persinal_data = myData();
		
		
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
			try {
				ComfirmSubmitStatusAtTable();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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
	
	private void ComfirmSubmitStatusAtTable() throws JSONException{
		JSONObject x = formatUpDateJSON();
		put_schedule(x);
		
		/*ContentValues cv = new ContentValues();
		cv.put("submit", "true");
		long long1 = db.update(table_name, cv, "_ID=" + _ID, null);
		
	   if (long1 == -1) {
 		   Toast.makeText(Photo_page.this,"Submit Fail！", Toast.LENGTH_SHORT).show();
 	   }
 	   else{    
 		   Toast.makeText(Photo_page.this,"Submit Success!", Toast.LENGTH_SHORT).show();
 	   }*/
	}
	//--------------------update data at Webserver---------------------------------------------------------
	public JSONObject formatUpDateJSON() throws JSONException{
		Map<String, JSONObject> params = new HashMap<String, JSONObject>();
		Map<String, String> params_nested = new HashMap<String, String>();
		params_nested.put("submit", "true");
		params_nested.put("userID", persinal_data[4]);
		//params_nested.put("id", "34");
		JSONObject json_nested = new JSONObject(params_nested);
		params.put("schedule", json_nested);
		JSONObject json_f = new JSONObject(params);
		json_f.put("id", webID);
		return json_f;
		
	}
	
	public void put_schedule(JSONObject jsonParam) throws JSONException {
		StringBuilder sb = new StringBuilder();
		String http = "http://watchyou.herokuapp.com/schedules";
		HttpURLConnection urlConnection = null;
		try {
			URL url = new URL(http);
			urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.setDoOutput(true);
			urlConnection.setRequestMethod("PUT");
			urlConnection.setUseCaches(false);
			urlConnection.setConnectTimeout(10000);
			urlConnection.setReadTimeout(10000);
			urlConnection.setRequestProperty("Content-Type", "application/json");
			urlConnection.connect();
			// Create JSONObject here
			OutputStreamWriter out = new OutputStreamWriter(
					urlConnection.getOutputStream());
			out.write(jsonParam.toString());
			out.close();
			int HttpResult = urlConnection.getResponseCode();
			if (HttpResult == HttpURLConnection.HTTP_OK) {
				BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "utf-8"));
				String line = null;
				while ((line = br.readLine()) != null) {
					sb.append(line + "\n");
				}
				br.close();
				System.out.println("" + sb.toString());
			} else {
				System.out.println(urlConnection.getResponseMessage());
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (urlConnection != null)
				urlConnection.disconnect();
		}
	}
	//--------------------------------------------------------------------------------------------------
	//----------------------get personal data from local database-------------------------------------
	public String[] myData(){
    	String keeplogin = "select _ID, name, email, password, webserverID, keeplogin from personaldata ";
		Cursor cursor = db_personal.rawQuery(keeplogin, null);
		String[] sNote = new String[6];
		  
		int rows_num = cursor.getCount();
		if(rows_num != 0) {
			  cursor.moveToFirst();  
			  for(int i=0; i<6; i++){
				  String strCr = cursor.getString(i);
				  sNote[i]=strCr;  
			  }
			  cursor.moveToNext();
		 }
		 cursor.close();

		 return sNote;
	}
	//--------------------------------------------------------------------------------------------------
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
