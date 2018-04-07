package org.usfirst.frc.team5104.robot;

import org.usfirst.frc.team5104.robot.ControllerHandler.Control;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

public class TalonSRXSimple implements Component {
	TalonSRX talon;
	Control axis;
	boolean Inverted = false;
	
	public TalonSRXSimple(int deviceID, Control axis) {
		talon = new TalonSRX(deviceID);
		this.axis = axis;
	}
	
	public TalonSRXSimple(int deviceID, Control axis, boolean Inverted) {
		talon = new TalonSRX(deviceID);
		this.axis = axis;
		this.Inverted = Inverted;
	}
	
	public void init() {
		
	}

	public void update() {
		talon.set(ControlMode.PercentOutput, Inverted ? -ControllerHandler.getInstance().getAxis(axis) : ControllerHandler.getInstance().getAxis(axis));
	}
}
