package com.example.a55123;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Calendar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.a55123.support.NewListDataSQL;
import com.example.a55123.support.NewPersonalDataSQL;

import android.support.v7.app.ActionBarActivity;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener {
	
	private Button camerabutton, rankbutton, mebutton,signupbutton;
	private ImageView init_view;
    SQLiteDatabase db;
    SQLiteDatabase db_personal;
	public String db_name = "MainPageSQL";
	public String table_name = "newtable";
	public String db_name_personal = "PersonalSQL";
	public String table_name_personal = "personaldata";
	private Bundle bundle = new Bundle();
	private int howmanydataintable = 0;
	private static final int signin_different_account = 456;
	private String[] person_data;
	
	NewListDataSQL helper = new NewListDataSQL(MainActivity.this, db_name);
	NewPersonalDataSQL personaldata_helper = new NewPersonalDataSQL(MainActivity.this, db_name_personal);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// Super Important!!!
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		
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
		db_personal = personaldata_helper.getReadableDatabase();
	
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
				String[] person_data2 = myData();
				ArrayList<String> info2 = new ArrayList<String>();
				info2.add(person_data2[0]);// ID in database
				info2.add(person_data2[2]);// email
				info2.add(person_data2[3]);// password
            	bundle.putStringArrayList("Account", info2);
            	boolean hide_back =true;
            	bundle.putBoolean("hide_back", hide_back);
            	i_test.putExtras(bundle);
				startActivityForResult(i_test,signin_different_account);
				break;
				
			case R.id.main_init_imagebutton:
				person_data = myData();
				if(howmanydataintable == 0){ // if there is no data in local database(first time user)
					Intent i_check_account = new Intent(this, SignUp.class);
					startActivity(i_check_account);
				}
				else{
					if(person_data[5].equals("0")){ // determine account is keep logining or not 0 => no, 1 => yes
						ArrayList<String> info = new ArrayList<String>();
						info.add(person_data[0]); // ID in database
						info.add(person_data[2]); // email
						info.add(person_data[3]); // password
		            	bundle.putStringArrayList("Account", info);
						Intent i_check_account = new Intent(this, SignUp.class);
						i_check_account.putExtras(bundle);
						startActivity(i_check_account);
					}
				}
				init_view.setVisibility(View.GONE);
				person_data = myData();
				try {
					initial_schedule_database();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
	}
	//  If Someone resign it, updata the Schedule
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    if (requestCode == signin_different_account) {
	        if (resultCode == Activity.RESULT_OK) {
	        	person_data = myData();
				try {
					initial_schedule_database();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        }
	    }
	}
	// get personal infomation data from local database
	public String[] myData(){
    	String keeplogin = "select _ID, name, email, password, webserverID, keeplogin from personaldata ";
		Cursor cursor = db_personal.rawQuery(keeplogin, null);
		String[] sNote = new String[6];
		  
		int rows_num = cursor.getCount();
		howmanydataintable = rows_num;
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
	
	// put schedule after delete old data for this user to local database
	public void initial_schedule_database() throws IOException, JSONException{
		
		String str1 = "delete from newtable ; update sqlite_sequence SET seq = 0 where name = newtable;";

		db.execSQL(str1); // clean table;
		Toast.makeText(MainActivity.this,"Delete Schedule database ok", Toast.LENGTH_SHORT).show();
		ArrayList<ArrayList<String>> data_from_web;
		data_from_web = schedule_delete_old_data();
		ContentValues cv;
		 for(int i=0 ; i<data_from_web.get(0).size() ; i++){
			  cv = new ContentValues();
			  cv.put("schedule_ID_web", data_from_web.get(0).get(i));
			  cv.put("title", data_from_web.get(1).get(i));
	      	  cv.put("year", Integer.parseInt(data_from_web.get(2).get(i)));
	      	  cv.put("month", Integer.parseInt(data_from_web.get(3).get(i)));
	      	  cv.put("day", Integer.parseInt(data_from_web.get(4).get(i)));
	      	  cv.put("reminddate", data_from_web.get(8).get(i));
	      	  cv.put("remindtime", data_from_web.get(9).get(i));
	      	  
	      	  cv.put("star",Integer.parseInt(data_from_web.get(5).get(i)));
	      	  cv.put("ring",Integer.parseInt(data_from_web.get(6).get(i)));
	      	  cv.put("type",Integer.parseInt(data_from_web.get(7).get(i)));
	      	  
	      	  cv.put("submit", data_from_web.get(10).get(i));
	      	  cv.put("accept", data_from_web.get(11).get(i));
	      	  //cv.put("image","null");
	      	  cv.put("note",data_from_web.get(13).get(i));
	      	  long long1 = db.insert(table_name, "", cv);
	      	   
	      	   if (long1 == -1) {
	      		   Toast.makeText(MainActivity.this,"fail！", Toast.LENGTH_SHORT).show();
	      	   }
	      	   else{    
	      		   Toast.makeText(MainActivity.this,"Success!", Toast.LENGTH_SHORT).show();
	      	   }
		 }
		
	}
	// get Raw Json Schedule of this user from webserver
	private String[][] getJSONData() throws IOException{
		try{
		String szUrl = "http://watchyou.herokuapp.com/schedules/index/"+ person_data[4] +".json";
		URL url = new URL(szUrl);
		URLConnection rulConnection = url.openConnection(); 
		HttpURLConnection httpcon = (HttpURLConnection) rulConnection;
		httpcon.setConnectTimeout(30000);
		httpcon.setReadTimeout(30000); 
		httpcon.setRequestMethod("GET");
		httpcon.setDoInput(true);  
		httpcon.setUseCaches(false);
		httpcon.setAllowUserInteraction(false);
		httpcon.connect();
		
		 BufferedReader reader = new BufferedReader(new InputStreamReader(httpcon.getInputStream(), "UTF-8"));
         String jsonString= reader.readLine();
         reader.close();
         
         try {
             return (getJson(jsonString));
         } catch (JSONException e) {
             e.printStackTrace();
             return null;
         }
 
		}catch (MalformedURLException ex) {
	        ex.printStackTrace();
	    } catch (IOException ex) {
	    	ex.printStackTrace();
	    }
	    return null;
	}
	
	// Transfer Raw JSON data to  readable data
	public String[][]  getJson(String jsonString) throws JSONException {
		JSONArray jsonArray = new JSONArray(jsonString);		
  		String[][] final_ans = new String[14][jsonArray.length()];
  		
  		for(int i = 0 ; i<jsonArray.length(); i++){
  			JSONObject lib = jsonArray.getJSONObject(i);

  			final_ans[0][i] = Integer.toString(lib.getInt("id"));
			final_ans[1][i] = lib.getString("title");
			final_ans[2][i] = lib.getString("year");
			final_ans[3][i] = lib.getString("month");
			final_ans[4][i] = lib.getString("day");
			final_ans[5][i] = lib.getString("star");
			final_ans[6][i] = lib.getString("ring");
			final_ans[7][i] = lib.getString("category"); // type
			final_ans[8][i] = lib.getString("remind_date");
			final_ans[9][i] = lib.getString("remind_time");
			final_ans[10][i] = lib.getString("submit");
			final_ans[11][i] = lib.getString("accept");
			final_ans[12][i] = lib.getString("image");
			final_ans[13][i] = lib.getString("note");
			Log.e("id",final_ans[0][i]);
			Log.e("title",final_ans[1][i]);
			Log.e("year",final_ans[2][i]);
			Log.e("month",final_ans[3][i]);
			Log.e("day",final_ans[4][i]);
			Log.e("star",final_ans[5][i]);
			Log.e("ring",final_ans[6][i]);
			Log.e("category",final_ans[7][i]);
			Log.e("remind_date",final_ans[8][i]);
			Log.e("remind_time",final_ans[9][i]);
			Log.e("submit",final_ans[10][i]);
			Log.e("accept",final_ans[11][i]);
			Log.e("image",final_ans[12][i]);
			Log.e("note",final_ans[13][i]);
  		}
  		return final_ans; 
    }
	// delete old schedule data at webserver before save it into local database
	public ArrayList<ArrayList<String>> schedule_delete_old_data() throws IOException, JSONException{
        final Calendar cal = Calendar.getInstance();
        int Year = cal.get(Calendar.YEAR);
        int Month = cal.get(Calendar.MONTH)+1;
        int Day = cal.get(Calendar.DAY_OF_MONTH);
		String[][] full_data = getJSONData();
		ArrayList<ArrayList<String>> final_ans = new ArrayList<ArrayList<String>>();
		for(int a = 0 ; a<14 ; a++){
			ArrayList<String> tmp = new ArrayList<String>();
			final_ans.add(tmp);
		}
		for(int i=0 ; i<full_data[0].length ; i++){
			if(Integer.parseInt(full_data[2][i]) < Year || Integer.parseInt(full_data[3][i]) < Month){
				Log.e("USERID", person_data[4]);
				Log.e("scheduleID", full_data[0][i]);
				delete_schedule(person_data[4],full_data[0][i]);
				
			}
			else{
				final_ans.get(0).add(full_data[0][i]);
				final_ans.get(1).add(full_data[1][i]);
				final_ans.get(2).add(full_data[2][i]);
				final_ans.get(3).add(full_data[3][i]);
				final_ans.get(4).add(full_data[4][i]);
				final_ans.get(5).add(full_data[5][i]);
				final_ans.get(6).add(full_data[6][i]);
				final_ans.get(7).add(full_data[7][i]);
				final_ans.get(8).add(full_data[8][i]);
				final_ans.get(9).add(full_data[9][i]);
				final_ans.get(10).add(full_data[10][i]);
				final_ans.get(11).add(full_data[11][i]);
				final_ans.get(12).add(full_data[12][i]);
				final_ans.get(13).add(full_data[13][i]);
			}
		}
		return final_ans;
	}
	// the place do delete function( delete data at webserver)
	public void delete_schedule(String UserID, String id) throws JSONException{
		StringBuilder sb = new StringBuilder();
		String http = "http://watchyou.herokuapp.com/schedules/delete"+ "/"+ UserID+ "/"+ id;
		HttpURLConnection urlConnection = null;
		try {
			URL url = new URL(http);
			urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.setDoOutput(true);
			urlConnection.setRequestMethod("DELETE");
			urlConnection.setUseCaches(false);
			urlConnection.setConnectTimeout(10000);
			urlConnection.setReadTimeout(10000);
			urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			urlConnection.connect();
			// Create JSONObject here
			int HttpResult = urlConnection.getResponseCode();
			if (HttpResult == HttpURLConnection.HTTP_OK) {
				BufferedReader br = new BufferedReader(new InputStreamReader(
						urlConnection.getInputStream(), "utf-8"));
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
    
}
