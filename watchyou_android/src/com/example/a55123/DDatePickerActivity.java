package com.example.a55123;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Calendar;

import com.example.a55123.support.NewListDataSQL;

import android.support.v7.app.ActionBarActivity;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView.OnDateChangeListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.CalendarView;


public class DDatePickerActivity extends Activity implements OnClickListener{

    /** Private members of the class */
    private CalendarView pC;
    private TextView text;
    private Button pDone, pAdd, pback;
    private int pYear;
    private int pMonth;
    private int pDay;
    private ListView listview;
    private String[] str_title;
    private String[] str_time;
    private String[] str_check;
    private String s1="";
    private SQLiteDatabase db;
	public String db_name = "MainPageSQL";
	public String table_name = "newtable";
	private Bundle bundle = new Bundle();
	private ArrayList<Integer> box;
	private int checksum = 0; // 0=new one, 1=reverse
	
	private static final int addnewone = 123;
	private static final int clicktoreverse = 456;
	
	NewListDataSQL helper = new NewListDataSQL(DDatePickerActivity.this, db_name);
    //private TextView text;

    /** This integer will uniquely define the dialog to be used for displaying date picker.*/
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ddate_picker);
        
        db = helper.getReadableDatabase();
        
        text = (TextView)findViewById(R.id.launch_codes);
        pDone = (Button) findViewById(R.id.Doneforschedule);
        pDone.setOnClickListener(this);
        pAdd = (Button) findViewById(R.id.addnew);
        pAdd.setOnClickListener(this);
        pback = (Button)findViewById(R.id.date_backbutton);
        pback.setOnClickListener(this);
        
        pC = (CalendarView) findViewById(R.id.calendarView1);
        final Calendar cal = Calendar.getInstance();
        pYear = cal.get(Calendar.YEAR);
        pMonth = cal.get(Calendar.MONTH)+1;
        pDay = cal.get(Calendar.DAY_OF_MONTH);
        
        listview = (ListView) findViewById(R.id.listview);
        setlistview();

        pC.setOnDateChangeListener(new OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month,
                                            int dayOfMonth) {
                pYear = year;
                pMonth = month+1;
                pDay = dayOfMonth;


                s1="" + pYear + " / " + pMonth + " / " + pDay;
                
                setlistview();
                Toast.makeText(DDatePickerActivity.this, s1, Toast.LENGTH_SHORT).show();
            }
        });

        listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
				String str = str_title[arg2];
				String[] item_detail = reverseRow(str);
				Toast.makeText(DDatePickerActivity.this, str, Toast.LENGTH_SHORT).show();
				
				box = new ArrayList<Integer>();
				box.add(pYear);
            	box.add(pMonth);
            	box.add(pDay);
            	bundle.putIntegerArrayList("dateinfo", box);
            	
            	checksum=1;
            	bundle.putInt("CheckSum", checksum);
            	
            	bundle.putStringArray("Item_Detail", item_detail);
            	
            	 /*Intent i = new Intent();
                 i.setClass(DDatePickerActivity.this, ScheduleContent.class);
                 i.putExtras(bundle);
                 startActivity(i);*/
            	Intent i = new Intent(DDatePickerActivity.this, ScheduleContent.class);
            	i.putExtras(bundle); 
            	startActivityForResult(i, clicktoreverse); 
			}
        	
        });
    }
    
    @Override
	public void onClick(View v) {
    	switch(v.getId()){
			case R.id.Doneforschedule:
				setlistview();
				break;
				
			case R.id.date_backbutton:
				DDatePickerActivity.this.finish();
				break;
				
			case R.id.addnew:
				box = new ArrayList<Integer>();
            	box.add(pYear);
            	box.add(pMonth);
            	box.add(pDay);
            	bundle.putIntegerArrayList("dateinfo", box);
            	
            	checksum=0;
            	bundle.putInt("CheckSum", checksum);
            	
            	ArrayList<String> quary_string = new ArrayList<String>();
            	quary_string.add(buildSQLstring("title", pYear, pMonth, pDay));
            	quary_string.add(buildSQLstring("remindtime", pYear, pMonth, pDay));
            	bundle.putStringArrayList("Quaryinfo", quary_string);
            	
            	
            	Intent i = new Intent(DDatePickerActivity.this, ScheduleContent.class);
            	i.putExtras(bundle); 
            	startActivityForResult(i, addnewone); 
                break;
    	}
    	
    	setlistview();
    	
	}
    
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    if (requestCode == addnewone) {
	        if (resultCode == Activity.RESULT_OK) {
	        	setlistview();
	        }
	    }
	    if (requestCode == clicktoreverse) {
	        if (resultCode == Activity.RESULT_OK) {
	        	setlistview();
	        }
	    }
	       super.onActivityResult(requestCode, resultCode, data);
     }
    
    public String[] myData(String target){
    	String quary_title = buildSQLstring(target, pYear, pMonth, pDay);
    	// Sample "select title from newtable where (year = '2015' AND month = '4' AND day = '9')"
		Cursor cursor = db.rawQuery(quary_title, null);
		String[] sNote = new String[cursor.getCount()];
		String ans = Integer.toString(cursor.getCount());
		//Log.e("str length", ans);
		  
		int rows_num = cursor.getCount();
		if(rows_num != 0) {
			  cursor.moveToFirst();   
			  for(int i=0; i<rows_num; i++){
				  String strCr = cursor.getString(0);
				  sNote[i]=strCr;
				  cursor.moveToNext();
			  }
		 }
		 cursor.close(); //關閉Cursor
		 //dbHelper.close();//關閉資料庫，釋放記憶體，還需使用時不要關閉
		 return sNote;
	}
    
    public String[] reverseRow(String title){
    	String[] demo = {"title", "reminddate", "remindtime", "note", "type", "star", "ring", "_ID"};
    	String[] sNote = new String[8];
    	for(int i=0 ; i<8 ; i++){
    		String quary_time = reverseItemSQL(demo[i],title, pYear, pMonth, pDay);
    		Cursor cursor = db.rawQuery(quary_time, null);
			  
			int rows_num = cursor.getCount();
			if(rows_num != 0) {
				  cursor.moveToFirst();   
				  String strCr = cursor.getString(0);
				  if(strCr != null){
					  sNote[i]=strCr;
				  }
				  else{
					  sNote[i]="nnn";
				  }
				  cursor.moveToNext();
			 }
			 cursor.close(); 
			 //dbHelper.close();
    	}
		 return sNote;
	}
    
    
    public String buildSQLstring(String target, int year, int month, int day){
    	String s1 = "select ";
    	String s2 = " from newtable where (year = '";
    	String final_quary = s1 + target + s2 + year + "' AND month = '" + month +"' AND day = '"+ day + "')"; 
    	
    	return final_quary;
    }
    
    public String reverseItemSQL(String target, String title, int year, int month, int day){
    	String s = "select "+target+" from newtable where (year = '"+year+"' AND month = '"+ month 
    			+"' AND day = '"+ day +"' AND title = '"+ title +"')";
    	
    	return s;
    }
    
    public void setlistview(){
    	str_title = myData("title");
    	str_time = myData("remindtime");
    	str_check = myData("submit");
    	String[] final_string = new String[str_title.length];
    	for(int i=0 ; i<str_title.length ; i++){
    		if(str_time[i]=="nnn"){ // if they didn't give time of schedule
    			final_string[i]=str_title[i];
    		}
    		else{
    			final_string[i]=str_time[i]+" "+str_title[i];
    		}
    		if(str_check[i].equals("true")){
    			final_string[i] = " √   " + final_string[i];
    		}
    	}
        ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(DDatePickerActivity.this,
        		android.R.layout.simple_expandable_list_item_1,final_string);
        listview.setAdapter(listAdapter);
    }

	
    
}