package com.frc5104.recording;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CSVFileWriter {

	static class Data {
		public final String name;
		public final LogDouble callback;
		
		public List<Double> values;
		
		public Data (String name, LogDouble callback) {
			this.name = name;
			this.callback = callback;
			
			values = new ArrayList<Double>();
		}//Data
		
		public void collect() {
			values.add(callback.get());
		}//collect
		
		public int age() {
			return values.size();
		}//age
		
		public double getValue(int index) {
			return values.get(index);
		}//getValue
	}//Data
	
	private File file;
	private FileWriter writer;
	
	private String separator;
	private String endline;

	final long startTime = System.currentTimeMillis();
	long tickTime = startTime;
	List<Data> data;
	
	public CSVFileWriter (File file) {
		this.file = file;
		
		//Create necessary parent directories
		String warningText = "Failed to create parent directories";
				warningText += "\\nThis might just mean that they are already created";
		if (!file.getParentFile().mkdirs()) System.out.println(warningText);
		
		try {
			writer = new FileWriter(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		separator = ", ";
		endline = "\n";
		
		data = new ArrayList<Data>();
		LogDouble timeCallback = new LogDouble() {
			public double get() {
				return (double)(tickTime-startTime);
			}
		};
		data.add(new Data("time", timeCallback));
	}//CSVFile
	
	private void writeTitles() throws IOException{
		boolean first = true;
		for (int i=0; i<data.size(); i++) {
			if (!first) writer.write(separator);
			writer.write(data.get(i).name);
			first = false;
		}
		writer.write(endline);
	}//writeTitles
	
	public void updateAtTime(long time) throws IOException{
		tickTime = time;
		
		for (Data d: data) {
			d.collect();
		}
		
	}//updateAtTime
		
	public void writeValuesToFile() {

		System.out.println("Writing to file: "+file.getAbsolutePath());
		
		try {
			writeTitles();
	
			//For each line
			for (int i=0; i<data.get(0).age(); i++) {
				//Scan across the Data callbacks, and 
				//  write each recorded value for that timestamp
				for (int j=0; j<data.size(); j++) {
					if (j != 0)
						writer.write(separator);
					writer.write(""+data.get(j).getValue(i));
				}//j
				writer.write(endline);
			}//i
			
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Failed to write to file");
		}
	
	}//writeValuesToFile
	
	public void addLogDouble(String name, LogDouble value) {
		data.add(new Data(name, value));
	}//addLogDouble
	
	public String getAbsolutePath() {
		return file.getAbsolutePath();
	}

}//CSVFile