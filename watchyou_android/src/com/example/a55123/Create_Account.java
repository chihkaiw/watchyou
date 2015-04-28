package com.example.a55123;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;




import com.example.a55123.support.NewPersonalDataSQL;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Create_Account extends Activity implements OnClickListener{
	
	private Button back, create;
	private EditText name,email, password, repassword;
	private String name_s, email_s, password_s, repassword_s;
	private Bundle bundle = new Bundle();
	private int check_exist=0;
	private String id="";
	
	SQLiteDatabase db_personal;
	public String db_name_personal = "PersonalSQL";
	public String table_name_personal = "personaldata";
	NewPersonalDataSQL personaldata_helper = new NewPersonalDataSQL(Create_Account.this, db_name_personal);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create__account);
		
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		
		db_personal = personaldata_helper.getReadableDatabase();
		
		back = (Button)findViewById(R.id.create_account_back);
		back.setOnClickListener(this);
		create = (Button)findViewById(R.id.create_account_confirm);
		create.setOnClickListener(this);
		name = (EditText)findViewById(R.id.create_account_name_type);
		email = (EditText)findViewById(R.id.create_account_email_type);
		password = (EditText)findViewById(R.id.create_account_password_type);
		repassword = (EditText)findViewById(R.id.create_account_repassword_type);
		
		bundle = getIntent().getExtras();
		try{
			id = bundle.getString("ID");
			check_exist=1;
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	@Override
	public void onClick(View v) {
		switch(v.getId()){
			case R.id.create_account_back:
				finish();
				break;
				
			case R.id.create_account_confirm:
				name_s = name.getText().toString();
				email_s = email.getText().toString();
				password_s = password.getText().toString();
				//----------------------------------------------------------------------------------------------------------
				repassword_s = password.getText().toString();  //HAVE TO CHANGE!!!!!!
				//----------------------------------------------------------------
				if(password_s.equals(repassword_s)){
					if(check_exist == 1){
						Log.e("Create ID = ", id);
						long long0 = db_personal.delete(table_name_personal, "_ID=" + id, null);
						if (long0 == -1) {
		        		   Toast.makeText(Create_Account.this,"fail to load！", Toast.LENGTH_SHORT).show();		   
		        	    }
		        	    else{    
		        		   Toast.makeText(Create_Account.this,"Success delete !", Toast.LENGTH_SHORT).show();
		        	    }
					}
					Log.e("check_exist", ""+check_exist);
					ContentValues cv = new ContentValues();
		        	cv.put("name", name_s);
		        	cv.put("email", email_s);
		        	cv.put("password", password_s);
		        	cv.put("keeplogin", false);
		        	cv.put("webserverID", "null");
		        	long long1 = db_personal.insert(table_name_personal, "", cv);
		        	
		        	   
	        	    if (long1 == -1) {
	        		   Toast.makeText(Create_Account.this,"Create fail！", Toast.LENGTH_SHORT).show();
	        	    }
	        	    else{    
	        		   Toast.makeText(Create_Account.this,"Create Success!", Toast.LENGTH_SHORT).show();
	        		   post();
	        	    }
	        	    Intent intent = new Intent(Create_Account.this,SignUp.class);
		        	setResult(Activity.RESULT_OK,intent);
		        	finish();
				}
				else{
					Toast.makeText(Create_Account.this,"Password don't match", Toast.LENGTH_SHORT).show();
				}
		}
		
	}
	
	 private void post() {
	    	
	    	StringBuilder sb = new StringBuilder();

			String http = "http://watchyou.herokuapp.com/users";
			HttpURLConnection urlConnection = null;
			try {
				URL url = new URL(http);
				urlConnection = (HttpURLConnection) url.openConnection();
				urlConnection.setDoOutput(true);
				urlConnection.setRequestMethod("POST");
				urlConnection.setUseCaches(false);
				urlConnection.setConnectTimeout(10000);
				urlConnection.setReadTimeout(10000);
				urlConnection.setRequestProperty("Content-Type", "application/json");

				urlConnection.connect();

				// Create JSONObject here
				OutputStreamWriter out = new OutputStreamWriter(urlConnection.getOutputStream());
				JSONObject jsonParam = formJSON();
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
				} 
				else {
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
	    
	    public JSONObject formJSON(){
	    	Map<String, JSONObject> params = new HashMap<String, JSONObject>();
			Map<String, String> params_nested = new HashMap<String, String>();
			params_nested.put("name", name_s);
			params_nested.put("email", email_s);
			params_nested.put("password", password_s);
			params_nested.put("image", null);
			JSONObject json_nested = new JSONObject(params_nested);
			params.put("user", json_nested);
			JSONObject json_f = new JSONObject(params);
			
			return json_f;
	    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.create__account, menu);
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
