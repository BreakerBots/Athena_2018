package com.frc5104.logging;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class CSVFileReader {

	File file;
	Scanner scanner;
	String separator;
	
	List<String> titles;
	Map<String, Double> map;
	double[] rawValues;
	
	public CSVFileReader (File readFile, String splitString) {
		file = readFile;
		try {
			scanner = new Scanner(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.out.println("File failed: "+file.getAbsolutePath());
		}
		separator = splitString;
		
		titles = new ArrayList<String>();
		
		map = new HashMap<String, Double>();
		String firstLine = scanner.nextLine();
		for (String title: firstLine.split(separator)) {
			titles.add(title);
			map.put(title, -1.0);
		}
		
		rawValues = new double[titles.size()];
		
	}//CSVFileReader
	
	public CSVFileReader (File readFile) {
		this(readFile, ", ");
	}//CSVFileReader
	
	public void readLine() {
		String line = scanner.nextLine();
		String[] values = line.split(separator);
		for (int i=0; i<values.length; i++) {
			double value = Double.parseDouble(values[i]);
			map.put(titles.get(i), value);
			rawValues[i] = value;
		}
	}//readLine
	
	public double get(String key) {
		return map.get(key);
	}//get by key
	
	public double get(int index) {
		return rawValues[index];
	}//get by index
	
}//CSVFileReader
