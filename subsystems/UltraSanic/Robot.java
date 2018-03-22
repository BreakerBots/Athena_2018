package org.usfirst.frc.team5104.robot;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Robot extends IterativeRobot {
	//Makes filtered ultrasonic at ports (0, 1) taking only readings from (0 - 10)
	SanicCache sc = new SanicCache(0, 1, 0, 10);
	
	public void teleopPeriodic() {
		sc.collect();
		
		//Get an average from the last five filtered readings
		sc.getAvg(5);
		
		//Gets the last filtered reading
		sc.getLast();
		
		//Gets the last unfiltered reading
		sc.getRaw();
	}
}
