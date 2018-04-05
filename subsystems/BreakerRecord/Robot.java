package org.usfirst.frc.team5104.robot;

import org.usfirst.frc.team5104.robot.ControllerHandler.Button;

import edu.wpi.first.wpilibj.IterativeRobot;

public class Robot extends IterativeRobot {
	ControllerHandler controller = ControllerHandler.getInstance();
	
	public void autonomousPeriodic() {
		controller.update();
	
		//Starts The Playback
		controller.startPlayback("/lvuser/a.txt");
		
		//Will return if the button "A" was pressed at this time in the recording
		controller.getPressed(Button.A);
	}
	
	public void teleopPeriodic() {
		controller.update();
		
		//Start The Reocording
		controller.startRecording("/lvuser/a.txt");
		
		//Will return if the button "A" is pressed
		controller.getPressed(Button.A);
	}
}
