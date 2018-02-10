package com.frc5104.main.subsystems;

import edu.wpi.first.wpilibj.DigitalInput;

public class SqueezySensors {

	static SqueezySensors instance = null;
	
	public static SqueezySensors getInstance() {
		if (instance == null) {
			instance = new SqueezySensors();
		}
		return instance;
	}//getInstance
	
	FilteredUltraSonic centerUltra = new FilteredUltraSonic(0, 1);
	FilteredUltraSonic leftUltra = new FilteredUltraSonic(2, 3);
	FilteredUltraSonic rightUltra = new FilteredUltraSonic(4, 5);
	
	private SqueezySensors() {
		centerUltra.init();
		leftUltra.init();
		rightUltra.init();
	}//SqueezySensors
	
	public void updateSensors() {
		centerUltra.update();
		leftUltra.update();
		rightUltra.update();
		
	}//updateSensors

	public boolean detectBox() {
		if (leftUltra.getDistance() < 6 && rightUltra.getDistance() < 6)
			return true;
		else
			return false;
	}//detectBox
	
	public boolean detectBoxGone() {
		if (centerUltra.getDistance() > 10)
			return true;
		else
			return false;
//		if (leftUltra.getDistance() > 4 && rightUltra.getDistance() > 4)
//			return true;
//		else
//			return false;
	}//detectBoxGone
	
	public boolean detectBoxHeld() {
		if (centerUltra.getDistance() < 6)
//			leftUltra.getDistance() < 2 &&
//			rightUltra.getDistance() < 2)
			return true;
		else
			return false;
	}//detectBoxHeld
	
	public double[] getDistances() {
		double[] distances = new double[3];
		distances[0] = centerUltra.getDistance();
		distances[1] = leftUltra.getDistance();
		distances[2] = rightUltra.getDistance();
		
		return distances;
	}//getInstantDistances
	
}//SqueezySensors
