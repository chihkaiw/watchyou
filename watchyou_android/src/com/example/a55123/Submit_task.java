package com.example.a55123;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.example.a55123.support.NewListDataSQL;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class Submit_task extends Activity implements OnClickListener{
	
	private DatePicker datepicker;
	private Button back;
	private ListView task;
	private int Year, Month, Day;
	private String[] title;
	private String[] ID;
	private String[] webID;
	private SQLiteDatabase db;
	public String db_name = "MainPageSQL";
	public String table_name = "newtable";
	private Bundle bundle = new Bundle();
	private List<String> final_string, final_ID, final_webID;
	private static final int submittask = 012345;
	
	private NewListDataSQL helper = new NewListDataSQL(Submit_task.this, db_name);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_submit_task);
		
		db = helper.getReadableDatabase();
		
		final Calendar cal = Calendar.getInstance();
		Year = cal.get(Calendar.YEAR);
		Month = cal.get(Calendar.MONTH)+1;
		Day = cal.get(Calendar.DAY_OF_MONTH);

		
		
		back = (Button)findViewById(R.id.submit_task_back);
		back.setOnClickListener(this);
		
		datepicker = (DatePicker)findViewById(R.id.submit_task_datePicker);
		datepicker.init(Year, Month-1, Day, new OnDateChangedListener(){

			@Override
			public void onDateChanged(DatePicker view, int year,
					int monthOfYear, int dayOfMonth) {
				// TODO Auto-generated method stub
				Year = year;
				Month = monthOfYear+1;
				Day = dayOfMonth;
				StringBuilder text = new StringBuilder().append(Year).append(" / ").append(Month).append(" / ").append(Day);
				
	             setlistview();
	             Toast.makeText(Submit_task.this, text, Toast.LENGTH_SHORT).show();
			}
			
		});
		
		task = (ListView)findViewById(R.id.submit_task_listview);
		task.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
				String[] titleandid = new String[3];
				titleandid[0]=final_string.get(arg2);
				titleandid[1]=final_ID.get(arg2); 
				titleandid[2]=final_webID.get(arg2);
				bundle.putStringArray("Title and ID", titleandid);
				
				ArrayList<Integer> box = new ArrayList<Integer>();
				box.add(Year);
            	box.add(Month);
            	box.add(Day);
            	bundle.putIntegerArrayList("dateinfomation", box);
            	
            	 /*Intent i = new Intent();
                 i.setClass(Submit_task.this, Photo_page.class);
                 i.putExtras(bundle);
                 startActivity(i);*/
            	Intent i = new Intent(Submit_task.this, Photo_page.class);
            	i.putExtras(bundle); 
            	startActivityForResult(i, submittask);
			}
        });
		setlistview();
	}
	
	@Override
	public void onClick(View v) {
		switch(v.getId()){
			
			case R.id.submit_task_back:
				Submit_task.this.finish();
				break;
		}
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    if (requestCode == submittask) {
	        if (resultCode == Activity.RESULT_OK) {
	        	setlistview();
	        }
	    }
	}
	
	public String buildSQLstring(String target, int year, int month, int day){
    	String s1 = "select ";
    	String s2 = " from newtable where (year = '";
    	String final_quary = s1 + target + s2 + year + "' AND month = '" + month +"' AND day = '"+ day + "')"; 
    	return final_quary;
    }
	
	public void setlistview(){
    	
    	title = myTitle("title");
    	ID = myTitle("_ID");
    	webID = myTitle("schedule_ID_web");
    	String[] checkcheck = myTitle("submit");
    	final_string = new ArrayList<String>();
    	final_ID = new ArrayList<String>();
    	final_webID = new ArrayList<String>();
    	for(int i=0 ; i<title.length ; i++){
    	
    		if(checkcheck[i].equals("false")){ // 1 = true (submit already), 0 = false (haven't submit)
    			final_string.add(title[i]);
    			final_ID.add(ID[i]);
    			final_webID.add(webID[i]);
    		}

    	}
 
    	ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(Submit_task.this,
        		android.R.layout.simple_expandable_list_item_1,final_string);
        task.setAdapter(listAdapter);
    }
	
	public String[] myTitle(String target){
    	String quary_title = buildSQLstring(target, Year, Month, Day);
		Cursor cursor = db.rawQuery(quary_title, null);
		String[] sNote = new String[cursor.getCount()];
		
		int rows_num = cursor.getCount();//取得資料表列數
		
		if(rows_num != 0) {
			  cursor.moveToFirst();   //將指標移至第一筆資料
			  for(int i=0; i<rows_num; i++){
				  String strCr = cursor.getString(0);
				  sNote[i]=strCr;
				  cursor.moveToNext();//將指標移至下一筆資料
			  }
		 }
		 cursor.close();
		 return sNote;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.submit_task, menu);
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
