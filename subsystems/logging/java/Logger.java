package org.usfirst.frc.team5104.robot.java;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Logger {

	private File root;
	private List<ObjectLog> logs;
	
	private int ticks;
	
	public Logger(String rootDirectory) {
		root = new File(rootDirectory);
		logs = new ArrayList<ObjectLog>();
		
		ticks = 0;
	}//Logger
	
	public void collect() {
		for (ObjectLog file : logs) {
			file.collect();
		}
		ticks++;
	}//collect
	
	public boolean log() {
		if (ticks == 0) return false;
		
		Calendar today = Calendar.getInstance();
		int month	= today.get(Calendar.MONTH),
			day		= today.get(Calendar.DAY_OF_MONTH),
			year	= today.get(Calendar.YEAR);
		int hour	= today.get(Calendar.HOUR),
			minute	= today.get(Calendar.MINUTE),
			second	= today.get(Calendar.SECOND);

		
		File date = new File(root, String.format("%d-%d-%d",month,day,year));
		if (date.mkdir())
			System.out.println("Successfully created this date's directory");
		else
			System.out.println("Failed to create this date's directory");
		
		File time = new File(date, String.format("%d.%d.%d",hour,minute,second));
		if (time.mkdir())
			System.out.println("Successfully created this time's directory");
		else
			System.out.println("Failed to create this time's directory");
		
		for (ObjectLog file : logs) {
			file.log(time);
			file.clear();
		}
		
		ticks = 0;
		return true;
	}//log

	public void logObject(String name, LogValue callback) {
		ObjectLog file = new ObjectLog(name, callback);
		logs.add(file);
	}//logObject
	
//	public void logString(String name, LogString callback) {
//		
//	}//logString
//	
	public void logInt(String name, LogValue callback) {
		ObjectLog file = new IntLog(name, callback);
		logs.add(file);
	}//logInt
	
	public void logLong(String name, LogValue callback) {
		ObjectLog file = new LongLog(name, callback);
		logs.add(file);
	}//logLong

	public void logDouble(String name, LogValue callback) {
		ObjectLog file = new DoubleLog(name, callback);
		logs.add(file);
	}//logDouble
	
}//Logging
