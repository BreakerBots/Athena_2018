package com.frc5104.main.subsystems;

import edu.wpi.first.wpilibj.DoubleSolenoid;

public class Shifters {

	public static final int FORWARD_ID = 2;
	public static final int REVERSE_ID = 3;
	
	static Shifters instance = null;
	
	public static Shifters getInstance() {
		if (instance == null) {
			instance = new Shifters();
		}
		return instance;
	}//getInstance
	
	DoubleSolenoid gearShifters = new DoubleSolenoid(FORWARD_ID, REVERSE_ID);

	public boolean inHighGear() {
		return gearShifters.get() == DoubleSolenoid.Value.kForward;
	}//inHighGear
	
	public boolean inLowGear() {
		return !inHighGear();
	}//inLowGear
	
	public void shiftHigh() {
		gearShifters.set(DoubleSolenoid.Value.kForward);
	}//shiftHigh
	
	public void shiftLow() {
		gearShifters.set(DoubleSolenoid.Value.kReverse);
	}//setLow
	
	public void toggle() {
		if (inHighGear())
			shiftLow();
		else
			shiftHigh();
	}//toggle
		
	public void shiftHigh(boolean high) {
		if (high)
			shiftHigh();
		else
			shiftLow();
	}//shiftHigh
	
}//Shifters
