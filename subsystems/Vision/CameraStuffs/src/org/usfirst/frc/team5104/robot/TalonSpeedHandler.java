package org.usfirst.frc.team5104.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

public class TalonSpeedHandler {
	public double travelSpeed = 0.0;
	
	public TalonSRX talon;
	
	private double targetPercentOutput = 0.0;
	private double currentPercentOutput = 0.0;
	
	public TalonSpeedHandler(int talonDeviceID) {
		talon = new TalonSRX(talonDeviceID);
	}
	
	public void update() {
		talon.set(ControlMode.PercentOutput, currentPercentOutput);
		if (targetPercentOutput > currentPercentOutput) {
			//Positive
			if (((targetPercentOutput - currentPercentOutput) / travelSpeed) >= 1) { 
				currentPercentOutput += travelSpeed; 
			}
			else { currentPercentOutput = targetPercentOutput; }
		} 
		else if (targetPercentOutput < currentPercentOutput) {
			//Negative 
			if (((currentPercentOutput - targetPercentOutput) / travelSpeed) >= 1) { 
				currentPercentOutput -= travelSpeed; 
			}
			else { currentPercentOutput = targetPercentOutput; }
		}
	}
	
	public void set(double PercentOutput, double TravelTimeInMilliSeconds) {
		targetPercentOutput = PercentOutput;
		travelSpeed = (20 / TravelTimeInMilliSeconds);
	}
}
