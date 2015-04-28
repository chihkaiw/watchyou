package com.example.a55123;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.a55123.support.NewPersonalDataSQL;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class SignUp extends Activity implements OnClickListener{
	
	private Button back,signup,in;
	private Bundle bundle  = new Bundle() ,bundle2 = new Bundle();
	private EditText email, password;
	private CheckBox checkbox;
	private ArrayList<String> box = new ArrayList<String>();
	private String email_name = "", id="";
	private String[] reverse;
	private int check_exist=0;
	SQLiteDatabase db_personal;
	public String db_name_personal = "PersonalSQL";
	public String table_name_personal = "personaldata";
	private static final int create = 123;
	NewPersonalDataSQL personaldata_helper = new NewPersonalDataSQL(SignUp.this, db_name_personal);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sign_up);
		
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		
		email = (EditText)findViewById(R.id.signup_account_type);
		password = (EditText)findViewById(R.id.signup_password_type);
		back = (Button)findViewById(R.id.signup_back);
		back.setOnClickListener(this);
		back.setVisibility(View.INVISIBLE);
		signup = (Button)findViewById(R.id.signup_create);
		signup.setOnClickListener(this);
		in = (Button)findViewById(R.id.signup_signin);
		in.setOnClickListener(this);
		checkbox = (CheckBox)findViewById(R.id.signup_checkBox);
		
		db_personal = personaldata_helper.getReadableDatabase();
		
		bundle = getIntent().getExtras();
		try{
			box = bundle.getStringArrayList("Account");
			id=box.get(0);
			email_name = box.get(1);
			email.setText(email_name);
			check_exist=1;
		}catch(Exception e){
			e.printStackTrace();
		}
		boolean test = false;
		try{
			test = bundle.getBoolean("hide_back");	
		}catch(Exception e){
			e.printStackTrace();
		}
		if(test){
			back.setVisibility(View.VISIBLE);
		}
	}
	
	@Override
	public void onClick(View v) {
		switch(v.getId()){
			case R.id.signup_back:
				finish();
				break;
				
			case R.id.signup_create:
				Intent i = new Intent(SignUp.this,Create_Account.class);
				if(check_exist == 1){
					
					String id = box.get(0);
					Log.e("signup ID", id);
					bundle2.putString("ID", id);
					i.putExtras(bundle2);
				}
				
				startActivityForResult(i, create);
				break;
				
			case R.id.signup_signin:
				String str = email.getText().toString();
				String str_p = password.getText().toString();
				if(email_name == ""){
					Toast.makeText(SignUp.this,"Please Create New Account", Toast.LENGTH_SHORT).show();
				}
				else{//database have data
					String[][] tmp;// all data from webserver
					try {
						tmp = getJSONData();
						for(int x=0 ; x<tmp[0].length ; x++){
							if(str.equals(tmp[2][x])){
								if(str_p.equals(tmp[3][x])){
									long long0 = db_personal.delete(table_name_personal, "_ID=" + id, null);
									if (long0 == -1) {
					        		   Toast.makeText(SignUp.this,"fail to load！", Toast.LENGTH_SHORT).show();		   
					        	    }
					        	    else{    
					        		   Toast.makeText(SignUp.this,"Success delete !", Toast.LENGTH_SHORT).show();
					        	    }

									ContentValues cv = new ContentValues();
						        	cv.put("name", tmp[1][x]);
						        	cv.put("email", tmp[2][x]);
						        	cv.put("password", tmp[3][x]);
						        	cv.put("keeplogin", checkbox.isChecked());
						        	cv.put("webserverID", tmp[0][x]);
						        	long long1 = db_personal.insert(table_name_personal, "", cv);
						        	
					        	    if (long1 == -1) {
					        		   Toast.makeText(SignUp.this,"Update fail！", Toast.LENGTH_SHORT).show();
					        	    }
					        	    else{    
					        		   Toast.makeText(SignUp.this,"Update Success!", Toast.LENGTH_SHORT).show();
					        		   Intent intent = new Intent(SignUp.this, MainActivity.class);
					        		   setResult(Activity.RESULT_OK,intent);
					        		   finish();
					        	    }
								}
								else{
									Toast.makeText(SignUp.this,"Incorrect Password", Toast.LENGTH_SHORT).show();
								}
							}
						}
					} catch (IOException e) {
						Toast.makeText(SignUp.this,"Incorrect E-Mail/Password", Toast.LENGTH_SHORT).show();
						e.printStackTrace();
					}
					
				}
		}
		
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    if (requestCode == create) {
	        if (resultCode == Activity.RESULT_OK) {
	        	reverse = myData();
	        	id=reverse[0];
	        	email_name=reverse[1];
	        	email.setText(email_name);
	        }
	    }
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.sign_up, menu);
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
	// find the date from DataBase
	public String[] myData(){
    	String keeplogin = "select _ID, email, password, keeplogin from personaldata ";
		Cursor cursor = db_personal.rawQuery(keeplogin, null);
		String[] sNote = new String[4];
		  
		int rows_num = cursor.getCount();//取得資料表列數
		if(rows_num != 0) {
			  cursor.moveToFirst();   //將指標移至第一筆資料
			  for(int i=0; i<4; i++){
				  String strCr = cursor.getString(i);
				  sNote[i]=strCr;  
			  }
			  cursor.moveToNext();//將指標移至下一筆資料
		 }
		 cursor.close();
		 return sNote;
	}
	
	 // Get JSON Raw data
 	private String[][] getJSONData() throws IOException{
 		try{
 		String szUrl = "http://watchyou.herokuapp.com/users/index.json";
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
  		
  		String[][] final_ans = new String[4][jsonArray.length()];
        /*String[] name = new String[jsonArray.length()];
  		String[] email = new String[jsonArray.length()];
  		String[] password = new String[jsonArray.length()];
  		int[] ID = new int[jsonArray.length()];*/
  		
  		for(int i = 0 ; i<jsonArray.length(); i++){
  			JSONObject lib = jsonArray.getJSONObject(i);

  			final_ans[0][i] = Integer.toString(lib.getInt("id"));
			final_ans[1][i] = lib.getString("name");
			final_ans[2][i] = lib.getString("email");
			final_ans[3][i] = lib.getString("password");
  		}
  		return final_ans; 
      }

	
}
