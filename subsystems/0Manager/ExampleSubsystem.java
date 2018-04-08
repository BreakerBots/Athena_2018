package org.usfirst.frc.team5104.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

//This is an example subsystem class
public class ExampleSubsystem implements Subsystem {
	TalonSRX talon1, talon2, talon3, talon4;
	
	public ExampleSubsystem() {
		//This is the only function call neededfor an actual subsystem
		SubsystemHandler.getInstance().addSystem(this);
	}
	
	public void robotInit() {
		talon1 = new TalonSRX(11);
		talon2 = new TalonSRX(12);
		talon3 = new TalonSRX(13);
		talon4 = new TalonSRX(14);
	}

	public void init() {
		talon2.set(ControlMode.Follower, talon1.getDeviceID());
		talon4.set(ControlMode.Follower, talon3.getDeviceID());
	}

	public void update() {
		talon1.set(ControlMode.PercentOutput, 0.1);
		talon4.set(ControlMode.PercentOutput, 0.1);
	}
	
}
