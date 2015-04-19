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
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class Rank extends Activity implements OnClickListener{
	
	private Button rank_back_button;
	private ListView rank_listivew;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_rank);
		// Super Important!!!
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		
		rank_back_button = (Button)findViewById(R.id.rank_back_button);
		rank_back_button.setOnClickListener(this);
		
		rank_listivew = (ListView)findViewById(R.id.ranklistview);
		chkNetwork();
		String[] str=null;
		try {
			str = getJSONData();
		} catch (IOException e) {
			e.printStackTrace();
		}
		ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(Rank.this,
				android.R.layout.simple_expandable_list_item_1,
				str);
		rank_listivew.setAdapter(listAdapter);
	}
	
	@Override
	public void onClick(View v) {
		switch(v.getId()){
			case R.id.rank_back_button:
				Rank.this.finish();
				break;
		}
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.rank, menu);
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
	
	// Get JSON Raw data
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
	
	// Transfer Raw JSON data to  readable data
	public String[]  getJson(String jsonString) throws JSONException {
 		JSONArray jsonObject = new JSONArray(jsonString);
 		  
        String[] name = new String[jsonObject.length()];
 		String[] email = new String[jsonObject.length()];
 		String[] password = new String[jsonObject.length()];
 		int[] ID = new int[jsonObject.length()];
 		
 		for(int i = 0 ; i<jsonObject.length(); i++){
 			JSONObject lib = jsonObject.getJSONObject(i);
 			name[i] = lib.getString("name");
 			email[i] = lib.getString("email");
 			password[i] = lib.getString("password");
 			ID[i] = lib.getInt("id");
 			Log.e("name", lib.getString("name"));
 			Log.e("email", lib.getString("email"));
 			Log.e("password", lib.getString("password"));
 			Log.e("ID", ""+lib.getInt("id"));
 		}
 		return name; 
     }
	
	public void chkNetwork(){
        ConnectivityManager connMgr = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            Toast.makeText(Rank.this, "DataBase Connecting ", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(Rank.this, "DataBase Connected Failed ", Toast.LENGTH_SHORT).show();
        }
    }

	
	
	//呼叫AsyncTask
	/*private class DownloadWebpageTask extends AsyncTask<Void,Integer,String[]>
    {
     
        @Override
        //要在背景中做的事
        protected String[]  doInBackground(Void... params) {
            try {
                return getWebData();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }        
        }
        //@Override
        //背景工作處理完"後"需作的事
       protected void onPostExecute(String[] result) {
            super.onPostExecute(result);
            rank_back_button.setText(result[1]);
        
           //進度BAR消失
           //ProgressBar pb=(ProgressBar)findViewById(R.id.progressBar);
           //pb.setVisibility(-1);       
        }

        //取得網路資料
        public String[] getWebData() throws IOException{
            URL url=new URL("http://watchyou.herokuapp.com/users/json");
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setReadTimeout(10000 );// milliseconds 
            conn.setConnectTimeout(15000 );// milliseconds 
            conn.setRequestMethod("GET");
            conn.setDoInput(true);        
            conn.connect();
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            String jsonString= reader.readLine();
            reader.close();
      
            try {
                return (getJson(jsonString));
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        
        }
        public String[]  getJson(String jsonString) throws JSONException {
            //如果是巢狀JSON字串,須分兩次來取資料
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
    			Log.e("name", lib.getString("email"));
    			Log.e("name", lib.getString("password"));
    			Log.e("name", lib.getString("scheduleID"));
    		}
    		
    		return name;
            
        }
    }*/
}
