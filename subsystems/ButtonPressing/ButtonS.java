package org.usfirst.frc.team5104.robot;

import edu.wpi.first.wpilibj.Joystick;

public class ButtonS {
	private boolean Val = false;
	private boolean LastVal = false;
	
	public boolean Pressed = false;
	
	Joystick controller = new Joystick(0);
	
	private int btnSlot = 0;
	
	public ButtonS(int ButtonSlot) {
		btnSlot = ButtonSlot;
	}
	
	public void update() {
		Pressed = false;
		Val = controller.getRawButton(btnSlot);
		if (Val != LastVal) {
			LastVal = Val;
			if (Val == true) { Pressed = true; }
		}
	}
}
