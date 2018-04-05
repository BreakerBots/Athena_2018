package com.frc5104.logging;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CSVFileWriter {

	private File file;
	private FileWriter writer;
	
	private String separator;
	private String endline;

	final long startTime = System.currentTimeMillis();
	long tickTime = startTime;
	List<Column> columns;
	
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
		
		columns = new ArrayList<Column>();
		LogDouble timeCallback = new LogDouble() {
			public double get() {
				return (double)(tickTime-startTime);
			}
		};
		columns.add(new Column("time", timeCallback));
	}//CSVFile
	
	public void setFile(String fileName) {
		file = new File(fileName);
		
		try {
			writer = new FileWriter(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}//setFile
	
	private void writeTitles() throws IOException{
		boolean first = true;
		for (int i=0; i<columns.size(); i++) {
			if (!first) writer.write(separator);
			writer.write(columns.get(i).name);
			first = false;
		}
		writer.write(endline);
	}//writeTitles
	
	public void collectAtTime(long time) {
		tickTime = time;
		
		for (Column d: columns) {
			d.collect();
		}
		
	}//updateAtTime
		
	public void writeValuesToFile() {

		System.out.println("Writing to file: "+file.getAbsolutePath());
		
		try {
			writeTitles();
	
			//For each line
			for (int i=0; i<columns.get(0).size(); i++) {
				//Scan across the Column callbacks, and 
				//  write each recorded value for that timestamp
				for (int j=0; j<columns.size(); j++) {
					if (j != 0)
						writer.write(separator);
					writer.write(""+columns.get(j).getValue(i));
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
		columns.add(new Column(name, value));
	}//addLogDouble
	
	public String getAbsolutePath() {
		return file.getAbsolutePath();
	}
	
	public List<Column> getColumns(){
		return columns;
	}//getColumn
	
	public void setColumns(List<Column> newColumns) {
		columns = newColumns;
	}//setColumn

}//CSVFile