package org.usfirst.frc.team5104.robot;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.List;

public class Deserialize {
	
	public static void main(String[] args) {

		File file = new File("C:\\Users\\BreakerBots\\Desktop\\robot_logs\\0-26-2018\\8.27.25");
		
		File[] files = file.listFiles();
		
		for (File f: files) {
			System.out.println(f.getAbsolutePath());
			if (f.getAbsolutePath().endsWith(".txt")) continue;

			try {
				ObjectInputStream input = new ObjectInputStream(new FileInputStream(f));
				
				String type = (String) input.readObject();
				Object data = input.readObject();
				
				switch (type) {
				case "object":
					writeObject(f, data);
					break;
				case "double":
					writeDouble(f, data);
					break;
				case "long":
					writeLong(f, data);
					break;
				case "int":
					writeInt(f, data);
					break;
				default:
					System.out.println("Unknown data type");
				}//switch type
				
				input.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}//main
	
	@SuppressWarnings("unchecked")
	public static void writeObject (File out, Object data) {
		List<Object> writeData;
		writeData = (List<Object>)data;
		
		File writeFile = new File(out.getAbsolutePath()+".txt");
		
		try {
			FileWriter writer = new FileWriter(writeFile);
		
			for (Object o : writeData)
				writer.write(o.toString()+"\n");
		
			writer.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}//writeObject
	
	public static void writeDouble (File out, Object data) {
		double[] writeData = (double[])data;
		
		File writeFile = new File(out.getAbsolutePath()+".txt");
		
		try {
			FileWriter writer = new FileWriter(writeFile);
		
			for (double d : writeData)
				writer.write(d + "\n");
		
			writer.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}//writeDouble
	
	public static void writeLong (File out, Object data) {
		long[] writeData = (long[])data;
		
		File writeFile = new File(out.getAbsolutePath()+".txt");
		
		try {
			FileWriter writer = new FileWriter(writeFile);
		
			for (long l : writeData)
				writer.write(l + "\n");
		
			writer.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}//writeLong
	
	public static void writeInt (File out, Object data) {
		int[] writeData = (int[]) data;
		
		File writeFile = new File(out.getAbsolutePath()+".txt");
		
		try {
			FileWriter writer = new FileWriter(writeFile);
		
			for (int i : writeData)
				writer.write(i + "\n");
		
			writer.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}//writeInt
	
	
	
	
	
}//Deserialize
