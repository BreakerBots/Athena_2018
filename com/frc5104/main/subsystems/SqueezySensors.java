package com.frc5104.main.subsystems;

import edu.wpi.first.wpilibj.Ultrasonic;

public class SqueezySensors {

	
	
	static SqueezySensors instance = null;
	
	public static SqueezySensors getInstance() {
		if (instance == null) {
			instance = new SqueezySensors();
		}
		return instance;
	}//getInstance
	
	
	Ultrasonic centerUltra = new Ultrasonic(0, 1);
	Ultrasonic leftUltra = new Ultrasonic(2, 3);
	Ultrasonic rightUltra = new Ultrasonic(4, 5);
	
	public void updateSensors() {
		centerUltra.update();
		leftUltra.update();
		rightUltra.update();
	}//updateSensors

	public boolean detectBox() {
		if (leftUltra.get() < 20 || rightUltra.get() < 20)
			return true;
		else
			return false;
	}//detectBox
	
	public boolean detectBoxGone() {
		if (centerUltra.get() > 10)
			return true;
		else
			return false;
	}//detectBoxGone
	
	
}//SqueezySensors
