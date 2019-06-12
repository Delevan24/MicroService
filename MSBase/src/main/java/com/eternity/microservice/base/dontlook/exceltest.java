package com.eternity.microservice.base.dontlook;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class exceltest {
	
	public static final Map<String, Object> keyMap = new HashMap<>();
	
	
	public static void main(String[] args) {
		/*System.out.println(keyMap);
		keyMap.put("11", 11);
		keyMap.put("22", 22);
		keyMap.put("33", 33);
		System.out.println(keyMap);
		modifyMap();
		System.out.println(keyMap);*/
		// new HashMap<>();
		// new ArrayList<Integer>();
		// new Hashtable<>();
		// new ConcurrentHashMap<>();
		Date date = new Date();
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
		String format = df.format(date);
		System.out.println(format);
		LocalDateTime now = LocalDateTime.now();
		DateTimeFormatter df1 = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
		System.out.println(now.format(df1));
		// ExecutorService executorService = Executors.newFixedThreadPool(5);
	}
	
	public static void modifyMap(){
		
		keyMap.put("44", 44);
	}
}
