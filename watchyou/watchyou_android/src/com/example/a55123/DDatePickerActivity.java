package com.example.a55123;

import java.util.ArrayList;
import java.util.Calendar;

import com.example.a55123.support.NewListDataSQL;

import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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


public class DDatePickerActivity extends ActionBarActivity {

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
    private String s1="";
    SQLiteDatabase db;
	public String db_name = "MainPageSQL";
	public String table_name = "newtable";
	private Bundle bundle = new Bundle();
	private ArrayList<Integer> box;
	private int checksum = 0; // 0=new one, 1=reverse
	
	NewListDataSQL helper = new NewListDataSQL(DDatePickerActivity.this, db_name);
    //private TextView text;

    /** This integer will uniquely define the dialog to be used for displaying date picker.*/

    static final int DONE_DIALOG_ID = 2;
    static final int ADD_DIALOG_ID = 3;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ddate_picker);
        
        db = helper.getReadableDatabase();
        
        text = (TextView)findViewById(R.id.launch_codes);
        pDone = (Button) findViewById(R.id.Doneforschedule);
        pAdd = (Button) findViewById(R.id.addnew);
        pback = (Button)findViewById(R.id.date_backbutton);
        
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

                text.setText(new StringBuilder().append(pYear).append(" / ").
                	      append(pMonth).append(" / ").append(pDay));
                s1=text.getText().toString();
                
                setlistview();
                Toast.makeText(DDatePickerActivity.this, s1, Toast.LENGTH_SHORT).show();
            }
        });

        pDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            	//DDatePickerActivity.this.finish();
            	setlistview();
            }
        });
        
        pback.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				DDatePickerActivity.this.finish();
			}
		});

        pAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            	
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
            	
                Intent i = new Intent();
                i.setClass(DDatePickerActivity.this, ScheduleContent.class);
                i.putExtras(bundle); 
                startActivity(i);
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
            	
            	 Intent i = new Intent();
                 i.setClass(DDatePickerActivity.this, ScheduleContent.class);
                 i.putExtras(bundle);
                 startActivity(i);
			}
        	
        });
    }
    
    public String[] myTitle(String target){
    	String quary_title = buildSQLstring(target, pYear, pMonth, pDay);
    	//"select title from newtable where (year = '2015' AND month = '4' AND day = '9')"
		Cursor cursor = db.rawQuery(quary_title, null);
		 //用陣列存資料
		String[] sNote = new String[cursor.getCount()];
		String ans = Integer.toString(cursor.getCount());
		Log.e("str length", ans);
		  
		int rows_num = cursor.getCount();//取得資料表列數
		if(rows_num != 0) {
			  cursor.moveToFirst();   //將指標移至第一筆資料
			  for(int i=0; i<rows_num; i++){
				  String strCr = cursor.getString(0);
				  sNote[i]=strCr;
				  cursor.moveToNext();//將指標移至下一筆資料
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
			 //用陣列存資料
			  
			int rows_num = cursor.getCount();//取得資料表列數
			if(rows_num != 0) {
				  cursor.moveToFirst();   //將指標移至第一筆資料
				  String strCr = cursor.getString(0);
				  if(strCr != null){
					  sNote[i]=strCr;
				  }
				  else{
					  sNote[i]="nnn";
				  }
				  cursor.moveToNext();//將指標移至下一筆資料
			 }
			Log.e("ans~"+i+"~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~", sNote[i]);
			 cursor.close(); //關閉Cursor
			 //dbHelper.close();//關閉資料庫，釋放記憶體，還需使用時不要關閉
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
    	Log.e("The SSSSSSSSSSSSSSSSSSSSSString IIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIII build",s);
    	
    	return s;
    }
    
    public void setlistview(){
    	str_title = myTitle("title");
    	str_time = myTitle("remindtime");
    	String[] final_string = new String[str_title.length];
    	for(int i=0 ; i<str_title.length ; i++){
    		if(str_time[i]=="nnn"){
    			final_string[i]=str_title[i];
    		}
    		else{
    			final_string[i]=str_time[i]+" "+str_title[i];
    		}
    	}
        ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(DDatePickerActivity.this,
        		android.R.layout.simple_expandable_list_item_1,final_string);
        listview.setAdapter(listAdapter);
    }
    
}