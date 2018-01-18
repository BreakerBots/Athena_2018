package org.usfirst.frc.team9104.robot;

import edu.wpi.first.wpilibj.IterativeRobot;

public class Robot extends IterativeRobot {
	UltraS sanic = new UltraS();
	
	public void robotInit() {
		sanic.RInit();
	}

	public void teleopPeriodic() {
		sanic.Update();
		
		System.out.println(sanic.getDistance());
	}
}
