package org.usfirst.frc.team9104.robot;

import edu.wpi.first.wpilibj.Ultrasonic;

public class UltraS {
	Ultrasonic ultra; 
	
	private double avgDistance = 0;
	private double distance2 = 0;
	private double pdistance[] = new double[20];
	private int index2 = 1;
	
	public UltraS(int ping, int echo) {
		ultra = new Ultrasonic(ping, echo);
	}
	
	public void RInit() {
		ultra.setAutomaticMode(true);
		ultra.setEnabled(true);
		//CameraServer.getInstance().startAutomaticCapture();
		
		distance2 = ultra.getRangeInches();
		avgDistance = distance2;
	}
	
	public void Update() {
		distance2 = ultra.getRangeInches();
		
		if (index2 > pdistance.length-1) {
			index2 = 0;
		}
		pdistance[index2] = distance2;
		index2++;
				
		avgDistance = avg(pdistance);
	}
	
	public double getDistance() {
		return avgDistance;
	}
	
	private double avg(double[] avgA) {
		double cAvg = 0;
		for (int i = 0; i < avgA.length; i++) {
			cAvg += avgA[i];
		}
		return cAvg/avgA.length;
	}
}
