package com.example.a55123.support;

import java.util.ArrayList;
import java.util.List;

public class FixDataProvider {
	
	public static List<String> GetTypeList() {
		
		List<String> list = new ArrayList<String>();
		list.add("");
		list.add("Study Work");
		list.add("Club");
		list.add("Others");
		return list;
	}
	public static List<String> GetStarList() {
		/*char star = 17;
		String s1=Character.toString(star);
		//String s1= ""+star;
		String s2= star+"+"+star;
		String s3= ""+star+star+star;*/
		List<String> list = new ArrayList<String>();
		list.add("");
		list.add("*");
		list.add("**");
		list.add("***");
		return list;
	}
	public static List<String> GetRingList() {
		
		List<String> list = new ArrayList<String>();
		list.add("");
		list.add("Ring1");
		list.add("Ring2");
		list.add("Ring3");
		return list;
	}

}
