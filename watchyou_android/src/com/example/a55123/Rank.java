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
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
	
	SQLiteDatabase db_personal;
	public String db_name_personal = "PersonalSQL";
	public String table_name_personal = "personaldata";
	NewPersonalDataSQL personaldata_helper = new NewPersonalDataSQL(Rank.this, db_name_personal);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_rank);
		// Super Important!!!
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		
		db_personal = personaldata_helper.getReadableDatabase();
		
		rank_back_button = (Button)findViewById(R.id.rank_back_button);
		rank_back_button.setOnClickListener(this);
		
		rank_listivew = (ListView)findViewById(R.id.ranklistview);
		rank_listivew.setBackgroundResource(R.drawable.shape);
		chkNetwork();
		String[][] str=null;
		ArrayList<String> str_show = new ArrayList<String>();
		ArrayList<String> str_show_id = new ArrayList<String>();
		try {
			str = getJSONData();
			String[] data_in_base = myData();
			for(int i=0 ; i<str[0].length ; i++){
				if(!str[0][i].equals(data_in_base[4]) || !str[2][i].equals(data_in_base[2])){
					str_show.add(str[1][i]);
					str_show_id.add(str[0][i]);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(Rank.this,
				android.R.layout.simple_expandable_list_item_1,
				str_show);
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
  		
  		for(int i = 0 ; i<jsonArray.length(); i++){
  			JSONObject lib = jsonArray.getJSONObject(i);

  			final_ans[0][i] = Integer.toString(lib.getInt("id"));
			final_ans[1][i] = lib.getString("name");
			final_ans[2][i] = lib.getString("email");
			final_ans[3][i] = lib.getString("password");
  		}
  		return final_ans; 
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
	
	public String[] myData(){
    	String keeplogin = "select _ID, name, email, password, webserverID from personaldata ";
		Cursor cursor = db_personal.rawQuery(keeplogin, null);
		String[] sNote = new String[5];
		  
		int rows_num = cursor.getCount();//取得資料表列數
		if(rows_num != 0) {
			  cursor.moveToFirst();   //將指標移至第一筆資料
			  for(int i=0; i<5; i++){
				  String strCr = cursor.getString(i);
				  sNote[i]=strCr;  
			  }
			  cursor.moveToNext();//將指標移至下一筆資料
		 }
		 cursor.close();
		 return sNote;
	}

}
