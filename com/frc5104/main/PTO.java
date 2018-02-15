package com.frc5104.main;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;

public class PTO {

	public static final int SWITCH_ID = 2;
	public static final int NULL_ID = 3;
	
	static PTO m_instance = null;
	
	public static PTO getInstance() {
		if (m_instance == null)
			m_instance = new PTO();
		return m_instance;
	}//getInstance
	
	DoubleSolenoid solenoid = new DoubleSolenoid(SWITCH_ID, NULL_ID);
	
	
	public void powerElevator () {
		solenoid.set(Value.kForward);
	}//powerElevator
	
	public void powerClimber() {
		solenoid.set(Value.kReverse);
	}//powerClimber
	
}//PTO
