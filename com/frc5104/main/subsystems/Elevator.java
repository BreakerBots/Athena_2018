package com.frc5104.main.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.Joystick;

public class Elevator {

	public static final int TALON_ID = 31;
	
	public static final int AXIS_ID = 5;
	
	public static final double kRaiseEffort = 0.8;
	public static final double kLowerEffort = 0.5;
	public static final double kHoldEffort = 0;
		// Constant effort to hold up elevator
		// Might change w/ Power Cube
	
	static Elevator m_instance = null;
	
	public static Elevator getInstance() {
		if (m_instance == null)
			m_instance = new Elevator();
		return m_instance;
	}//getInstance

	private Joystick joy = new Joystick(0);
	private TalonSRX talon = new TalonSRX(TALON_ID);
	
	private double effort = kHoldEffort;
	
	public void poll() {
		double x = joy.getRawAxis(AXIS_ID);
		
//		Deadband d = new Deadband(0.1);
//		x = d.getValue(x);
//		x = Deadband.getDefault().getValue(x);

		effort = x;
	}//poll
	
	public void update() {
		talon.set(ControlMode.PercentOutput, effort);
	}//update
	
	//----- Elevator Actions ------//
	public void raise () {
		effort = kRaiseEffort;
	}//raise
	
	public void lower() {
		effort = kLowerEffort;
	}//lower
	
	public void hold() {
		effort = kHoldEffort;
	}//hold
	
}//Elevator
