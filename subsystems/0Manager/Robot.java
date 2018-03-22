package org.usfirst.frc.team5104.robot;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Robot extends IterativeRobot {
	ExampleSubsystem es = new ExampleSubsystem();
	
	public void robotInit() {
		SubsystemHandler.getInstance().robotInit();
	}

	public void teleopInit() {
		SubsystemHandler.getInstance().init();
	}

	public void teleopPeriodic() {
		SubsystemHandler.getInstance().update();
	}
}
