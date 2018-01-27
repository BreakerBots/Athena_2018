package org.usfirst.frc.team5104.robot.java;

import java.io.IOException;
import java.io.ObjectOutputStream;

public class DoubleLog extends ObjectLog {

	public DoubleLog(String fileName, LogValue log) {
		super(fileName, log);
	}//DoubleLog

	@Override
	public void writeData(ObjectOutputStream out) throws IOException{
		double[] array = new double[data.size()];
		for (int i=0; i<data.size(); i++)
			array[i] = (double) data.get(i);
		
		out.writeObject("double");
		out.writeObject(array);
	}//writeData
	
}//DoubleLog