package org.usfirst.frc.team5104.robot.java;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.usfirst.frc.team5104.robot.LogValue;

public abstract class LogFile {
	
	String name;

	FileOutputStream fout;
	ObjectOutputStream out;
	
	LogValue log;
	List<Object> data;
	
	public LogFile(String fileName, LogValue log) {
		name = fileName;
		this.log = log;
	}//LogFile
	
	public void start() {
		if (data != null) data.clear();
		
		data = new ArrayList<Object>();
	}//start
	
	public void collect() {
		data.add(log.get());
	}
	
	public void log(File directory) {
		try {
			File writeFile = new File(directory,name);
			fout = new FileOutputStream(writeFile);
			out = new ObjectOutputStream(fout);
			
			writeData(out);
			
			out.close();
			fout.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}//log
	
	protected void writeData(ObjectOutputStream output) throws IOException {
		output.writeObject(data);
	}//writeData
	
	
	
}//LogFile
