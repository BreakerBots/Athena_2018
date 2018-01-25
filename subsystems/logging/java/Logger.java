package org.usfirst.frc.team5104.robot.java;

import java.io.File;
import java.util.Calendar;

import org.usfirst.frc.team5104.robot.LogString;

public class Logger {

	private File root;
	
	public Logger(String rootDirectory) {
		root = new File(rootDirectory);
	}
	
	public void collect() {
		for (LogFile file : logFiles) {
			file.collect();
		}
	}//collect
	
	public void log() {
		Calendar today = Calendar.getInstance();
		int month	= today.get(Calendar.MONTH),
			day		= today.get(Calendar.DAY_OF_MONTH),
			year	= today.get(Calendar.YEAR);
		int hour	= today.get(Calendar.HOUR),
			minute	= today.get(Calendar.MINUTE),
			second	= today.get(Calendar.SECOND);
							
		
		File date = new File(root, String.format("%d-%d-%d",month,day,year));
		date.mkdir();
		File time = new File(date, String.format("%d:%d:%d",hour,minute,second));
		time.mkdir();
		
		for (LogFile file : logFiles) {
			file.log(time);
		}
	}//log
	
	
	public void logString(String name, LogString callback) {
		
	}//logString
	
	public void logInt(String name, LogInt callback) {
		
	}//logInt
	
	public void logDouble(String name, LogDouble callback) {
		
	}//logDouble
	
	
	
	
}//Logging
