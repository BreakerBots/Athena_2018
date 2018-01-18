package org.usfirst.frc.team9104.robot;

import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Ultrasonic;

public class Robot extends IterativeRobot {
	Ultrasonic ultra = new Ultrasonic(1,0); 
	
	private double avgDistance = 0;
	private double distance2 = 0;
	private double pdistance[] = new double[20];
	private int index2 = 0;
	
	public void robotInit() {
		ultra.setAutomaticMode(true);
		CameraServer.getInstance().startAutomaticCapture();
		
		distance2 = ultra.getRangeInches();
		avgDistance = distance2;
	}

	public void teleopPeriodic() {
		distance2 = ultra.getRangeInches();
		
		if (index2 > pdistance.length-1) {
			index2 = 0;
		}
		pdistance[index2] = distance2;
		index2++;
				
		avgDistance = avg(pdistance);
		System.out.println(roundPlaces(avgDistance,0));
	}
	
	private double avg(double[] avgA) {
		double cAvg = 0;
		for (int i = 0; i < avgA.length; i++) {
			cAvg += avgA[i];
		}
		return cAvg/avgA.length;
	}
	private double roundPlaces(double a, double places) {
		return Math.round(a * Math.pow(10,places)) / Math.pow(10, places);
	}
}
