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
	Map<String, ArrayList<Double>> map;
	int size;
//	double[] rawValues;
	
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
		
		map = new HashMap<String, ArrayList<Double>>();
		String firstLine = scanner.nextLine();
		for (String title: firstLine.split(separator)) {
			titles.add(title);
			map.put(title, new ArrayList<Double>());
		}
		
//		rawValues = new double[titles.size()];
		
	}//CSVFileReader
	
	public CSVFileReader (File readFile) {
		this(readFile, ", ");
	}//CSVFileReader
	
	public void readFile() {
		String line;
		size = 0;
		
		long start = System.currentTimeMillis();
		
		while (scanner.hasNextLine()) {
			System.out.println("Reading line "+(size+1));
			line = scanner.nextLine();
			String[] values = line.split(separator);
			for (int i=0; i<values.length; i++) {
				double value = Double.parseDouble(values[i]);
				map.get(titles.get(i)).add(value);
			}
			size++;
		}
		
		long end = System.currentTimeMillis();
		
		System.out.printf("Reading from {%s} took %.2f seconds\n", file.getName(), (end-start)/1000.0);
		
	}//readFile
	/*
	public boolean readLine() {
//		if (!scanner.hasNextLine()) return false;
//		
//		String line = scanner.nextLine();
//		String[] values = line.split(separator);
//		for (int i=0; i<values.length; i++) {
//			double value = Double.parseDouble(values[i]);
//			map.put(titles.get(i), value);
//			rawValues[i] = value;
//		}
		return true;
	}//readLine
	*/
	
	public double get(String key, int index) {
		return map.get(key).get(index);
	}//get by key
	
	public int size() {
//		return size;
		return map.get("time").size();
	}//size
	
//	public double get(int index, int arrayIndex) {
//		return rawValues[index];
//	}//get by index
	
}//CSVFileReader
