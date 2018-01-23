package org.usfirst.frc.team5104.robot;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;
import java.util.Scanner;

public class CSVFileReader {

	File file;
	Scanner scanner;
	String separator;
	
	List<String> titles;
	Dictionary<String, Double> dictionary;
	
	public CSVFileReader (String directory, String name, String splitString) throws FileNotFoundException, IOException{
		file = new File(directory+"/"+name);
		scanner = new Scanner(file);
		separator = splitString;
		
		titles = new ArrayList<String>();
		
		String firstLine = scanner.nextLine();
		for (String title: firstLine.split(separator)) {
			titles.add(title);
			dictionary.put(title, -1.0);
		}
		
	}//CSVFileReader
	
	public CSVFileReader (String directory, String name) throws FileNotFoundException, IOException{
		this(directory, name, ", ");
	}//CSVFileReader
	
	public void readLine() {
		String line = scanner.nextLine();
		String[] values = line.split(separator);
		for (int i=0; i<values.length; i++) 
			dictionary.put(titles.get(i), Double.parseDouble(values[i]));
	}//readLine
	
	public double get(String key) {
		return dictionary.get(key);
	}//getValue
	
}//CSVFileReader
