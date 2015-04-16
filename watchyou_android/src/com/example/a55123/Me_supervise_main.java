package com.example.a55123;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class Me_supervise_main extends Activity {

	private Button me_supervisemain_back_button;
	private ListView me_supervisemain_listivew;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_me_supervise_main);
		// Super Important!!!
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		
		me_supervisemain_back_button = (Button)findViewById(R.id.me_supervisemain_back_button);
		me_supervisemain_back_button.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v) {
				Me_supervise_main.this.finish();
			}
			
		});
		
		me_supervisemain_listivew = (ListView)findViewById(R.id.me_supervisemainlistview);
		chkNetwork();
		String[] str=null;
		try {
			str = getJSONData();
		} catch (IOException e) {
			e.printStackTrace();
		}
		ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(Me_supervise_main.this,
				android.R.layout.simple_expandable_list_item_1,
				str);
		me_supervisemain_listivew.setAdapter(listAdapter);
		me_supervisemain_listivew.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (position == 0){
					Intent i = new Intent(Me_supervise_main.this,Me_supervise_personal.class);
					startActivity(i);	
				}
				
			}
			
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.me_supervise_main, menu);
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
	
	private String[] getJSONData() throws IOException{
		try{
		String szUrl = "http://watchyou.herokuapp.com/users/json";
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
	 public String[]  getJson(String jsonString) throws JSONException {
 		JSONArray jsonObject = new JSONArray(jsonString);
 		  
        String[] name = new String[jsonObject.length()];
 		String[] email = new String[jsonObject.length()];
 		String[] password = new String[jsonObject.length()];
 		String[] schedule = new String[jsonObject.length()];
 		
 		for(int i = 0 ; i<jsonObject.length(); i++){
 			JSONObject lib = jsonObject.getJSONObject(i);
 			name[i] = lib.getString("name");
 			email[i] = lib.getString("email");
 			password[i] = lib.getString("password");
 			schedule[i] = lib.getString("scheduleID");
 			Log.e("name", lib.getString("name"));
 			Log.e("email", lib.getString("email"));
 			Log.e("password", lib.getString("password"));
 			Log.e("scheduleID", lib.getString("scheduleID"));
 		}
 		return name; 
     }
	
	public void chkNetwork(){
        ConnectivityManager connMgr = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            Toast.makeText(Me_supervise_main.this, "DataBase Connecting", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(Me_supervise_main.this, "DataBase Connected Failed", Toast.LENGTH_SHORT).show();
        }
    }
}
