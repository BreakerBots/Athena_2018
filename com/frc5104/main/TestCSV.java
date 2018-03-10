package com.frc5104.main;

import java.io.File;

import com.frc5104.logging.CSVFileReader;
import com.frc5104.logging.CSVFileWriter;
import com.frc5104.logging.LogDouble;

public class TestCSV {

	static File file = new File("C:/Users/breakerbots/Desktop/test.csv");
	
	static CSVFileWriter writer = new CSVFileWriter(file);
	static CSVFileReader reader;
	
	static int x = 1;
	static int y = 1;
	
	public static void main(String[] args) {
		
		writer.addLogDouble("x", new LogDouble() {
			public double get() {
				return x;
			}
		});
		writer.addLogDouble("y", new LogDouble() {
			public double get() {
				return y;
			}
		});
		
		for (int i=0; i<100; i++) {
			int temp = x;
			x = y;
			y = temp+y;
			writer.collectAtTime(i);
			System.out.println("X: "+x+"\tY: "+y);
		}
		
		writer.writeValuesToFile();
		
		reader = new CSVFileReader(file);

		
		for (int i=0; i<100; i++) {
			reader.readLine();

			System.out.printf("X: %12.0f   Y: %12.0f\n", reader.get("x"), reader.get("y"));
		}
		
		
	}//main
}//TestCSV
