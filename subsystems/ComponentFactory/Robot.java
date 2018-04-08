package org.usfirst.frc.team5104.robot;

import org.usfirst.frc.team5104.robot.ControllerHandler.Control;

import edu.wpi.first.wpilibj.IterativeRobot;

public class Robot extends IterativeRobot {
	DoubleSolenoidSimple ds1 = new DoubleSolenoidSimple(0, 1, Control.A);
	TalonSRXSimple t1 = new TalonSRXSimple(12, Control.LY);
	
	public void robotInit() {
		ComponentHandler.getInstance().addSubsystems(new Component[] { 
				ds1, t1
		});
	}

	public void teleopInit() {
		ComponentHandler.getInstance().init();
	}

	public void teleopPeriodic() {
		ComponentHandler.getInstance().update();
	}
}
