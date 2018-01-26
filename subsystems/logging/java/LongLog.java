package org.usfirst.frc.team5104.robot.java;

import java.io.IOException;
import java.io.ObjectOutputStream;

public class LongLog extends ObjectLog{

	public LongLog(String fileName, LogValue log) {
		super(fileName, log);
	}

	public void writeData(ObjectOutputStream output) {
		long[] array = new long[data.size()];
		
		for (int i=0; i<data.size(); i++) {
			array[i] = (long) data.get(i);
		}
		
		try {
			output.writeObject(array);
		} catch (IOException e) {
			System.out.println("Failed to write object "+name+" data");
			e.printStackTrace();
		}
	}//writeData
	
}//ObjectLog
