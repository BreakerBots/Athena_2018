package org.usfirst.frc.team5104.robot;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CSVFileWriter {

	private String path;
	private String name;
	
	private File file;
	private FileWriter writer;
	private int lineCount;
	
	private String separator;
	private String endline;
	
	List<String> titles;
	List<LogValue> values;
	
	public CSVFileWriter (String directory, String filename) throws IOException {
		path = directory;
		name = filename;
		
		file = new File(path+"/"+name);
		writer = new FileWriter(file);
		lineCount = 0;
		
		separator = ", ";
		endline = "\n";
		
		titles = new ArrayList<String>();
		titles.add("time");
		values = new ArrayList<LogValue>();
	}//CSVFile
	
	private void writeTitles() throws IOException{
		boolean first = true;
		for (String title: titles) {
			if (!first) writer.write(separator);
			writer.write(title);
		}
	}//writeTitles
	
	public void update(long time) throws IOException{
		
		if (lineCount == 0)
			writeTitles();
		
		boolean first = true;
		for (LogValue value : values) {
			if (!first) writer.write(separator);
			writer.write(value.getText());
		}
		
		if (values.size() > 0) {
			lineCount ++;
			writer.write(endline);
			writer.flush();
		}
		
	}//update
	
	public void addLogValue(String name, LogValue value) {
		titles.add(name);
		values.add(value);
	}//addLogValue
	
	public String getAbsolutePath() {
		return path+"/"+name;
	}

}//CSVFile
