package com.frc5104.main.subsystems;

public class TimedButton {

	long startTime;
	boolean pressed;
	
	public void update(boolean val) {
		
		if (!pressed && val) {
			startTime = System.currentTimeMillis();
		}
	
		pressed = val;
	}//update
	
	public boolean get(int millis) {
		boolean bool = false;
		
		boolean time_passed = System.currentTimeMillis() - startTime > millis;
		if (pressed && time_passed)
			bool = true;
		return bool;
	}//get
	
	public void reset() {
		pressed = false;
	}
}//TimedButton
