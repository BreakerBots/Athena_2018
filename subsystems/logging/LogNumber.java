package org.usfirst.frc.team5104.robot;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class LogNumber {

	private String dir;
	private String filename;
	
	private File file;
	private FileWriter writer;
	private boolean fileOpen;
	
	public LogNumber(String directoryPath, String name){
		dir = directoryPath;
		filename = name;
		
		file = new File(dir+"/"+filename);
		try {
			writer = new FileWriter(file.getAbsolutePath());
			fileOpen = true;
		} catch (IOException e) {
			e.printStackTrace();
			fileOpen = false;
		}
	}//LogString

	public boolean recordValue(double num, String ending) {
		if (!fileOpen) return false;
		
		try {
			writer.write(String.format("%f", num)+ending);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}//recordValue
	
	public boolean recordValue(double num) {
		return recordValue(num, "\n");
	}//recordValue
	
	public boolean flush() {
		if (!fileOpen) return false;
		
		try {
			writer.flush();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}//flush
	
	public boolean close() {
		try {
			writer.close();
			fileOpen = false;
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}//close

}//LogString
