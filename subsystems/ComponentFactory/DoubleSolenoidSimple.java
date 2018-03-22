package org.usfirst.frc.team5104.robot;

import org.usfirst.frc.team5104.robot.ControllerHandler.Control;

import edu.wpi.first.wpilibj.DoubleSolenoid;

public class DoubleSolenoidSimple implements Component {
	DoubleSolenoid sol;
	Control control;
	boolean toggle = true;

	public DoubleSolenoidSimple(int forwardChannel, int reverseChannel, Control control) {
		sol = new DoubleSolenoid(forwardChannel, reverseChannel);
		this.control = control;
	}
	
	public DoubleSolenoidSimple(int forwardChannel, int reverseChannel, Control control, boolean toggle) {
		sol = new DoubleSolenoid(forwardChannel, reverseChannel);
		this.control = control;
		this.toggle = toggle;
	}
	
	public void init() {
		
	}

	public void update() {
		if (toggle) {
			if (ControllerHandler.getInstance().getPressed(control) ) { sol.set(DoubleSolenoid.Value.kForward); }
			if (ControllerHandler.getInstance().getReleased(control)) { sol.set(DoubleSolenoid.Value.kReverse); }
		}
		else {
			sol.set(ControllerHandler.getInstance().getHeld(control) ? DoubleSolenoid.Value.kForward : DoubleSolenoid.Value.kReverse);
		}
	}
}
