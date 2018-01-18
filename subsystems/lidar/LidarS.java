package org.usfirst.frc.team9104.robot;

import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.I2C.Port;

public class LidarS{

	private static final int LIDAR_ADDR = 0x62;
	private static final int LIDAR_CONFIG_REGISTER = 0x00;
	private static final int LIDAR_DISTANCE_REGISTER = 0x8f;

	private I2C i2c;
	private byte[] distArray;

	private long[] time_buffer;
	private int index = 0;
	private int count = 0;
	
	private double avgDistance = 0;
	private double distance2 = 0;
	private double pdistance[] = new double[10];
	private int index2 = 0;
		
	public LidarS () {
		i2c = new I2C(Port.kMXP, LIDAR_ADDR);
			
		distArray = new byte[2];

		time_buffer = new long[100];
		index = 0;
		count = 0;
	}
	
	public void begin() {
		//begin the lidar averaging
		updateLidar();
		distance2 = getDistanceUnfiltered();
		avgDistance = distance2;
	}
		
	public void updateLidar() {
		long now = System.currentTimeMillis();
		time_buffer[index] = now;
		index++;
		if (index == 100) index = 0;
		
		if (count % 100 == 0){
			//Take distance measurement with receiver bias correction
			i2c.write(LIDAR_CONFIG_REGISTER, 0x04);
		} else {
			//Take distance measurement without receiver bias correction
			i2c.write(LIDAR_CONFIG_REGISTER, 0x03);
		}
//		printf("Requested Distance Measurement...\n");

		byte[] status = new byte[1];

		byte[] statusRegister = {(byte)0x01};
		do {
			i2c.writeBulk(statusRegister, 1);
			i2c.readOnly(status, 1);

		//System.out.printf("Status: %d\t(status & 1)== %d\n",status[0],status[0]&1);
		} 
		while ((status[0] & 1) == 1);

		byte[] readRegister = {(byte) LIDAR_DISTANCE_REGISTER};

		i2c.writeBulk(readRegister, 1);
		i2c.readOnly(distArray, 2);

		count++;
		 
		distance2 = getDistanceUnfiltered();
		
		if (index2 > pdistance.length-1) {
			index2 = 0;
		}
		pdistance[index2] = distance2;
		index2++;
				
		avgDistance = avg(pdistance);
	}
	
	private double getDistanceUnfiltered() {
		//return (256*distArray[0]+distArray[1])/2.54;
		return (Integer.toUnsignedLong(distArray[0] << 8) + Byte.toUnsignedInt(distArray[1]) * 0.393701) - 4.6;
	}
	
	public double getDistance() {
		return roundPlaces(avgDistance,2);
	}
	
	public double getDistancePlaces(double places) {
		return roundPlaces(avgDistance,places);
	}
	
	private double roundPlaces(double a, double places) {
		return Math.round(a * Math.pow(10,places)) / Math.pow(10, places);
	}
	
	private double avg(double[] avgA) {
		double cAvg = 0;
		for (int i = 0; i < avgA.length; i++) {
			cAvg += avgA[i];
		}
		return cAvg/avgA.length;
	}
}
