package org.usfirst.frc.team9104.robot;

import edu.wpi.first.wpilibj.IterativeRobot;


public class Robot extends IterativeRobot {
	LidarS lidar1 = new LidarS();
	
	
	
	@Override
	public void teleopInit() {
		lidar1.begin();
	}
	
	@Override
	public void teleopPeriodic() {
		lidar1.updateLidar();
		
		System.out.println(lidar1.getDistance());
	}
}
