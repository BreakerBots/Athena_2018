package org.usfirst.frc.team5104.robot;

import org.usfirst.frc.team5104.robot.ControllerHandler.Axis;
import org.usfirst.frc.team5104.robot.ControllerHandler.Button;
import org.usfirst.frc.team5104.robot.ControllerHandler.Dpad;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.IterativeRobot;

public class Robot extends IterativeRobot {
	ControllerHandler controller = ControllerHandler.getInstance();
	
	public void autonomousPeriodic() {
		//Make Sure to Call this functions
		controller.update();
	
		//Button Functions [ inputs can be anythings under "Button.", "Dpad." or "Axis." ]
			controller.getHeld(Button.B);
			controller.getHeldTime(Button.X);
			controller.getPressed(Dpad.N);
			controller.getReleased(Axis.RT);
			controller.getClickTime(Button.A);
			controller.getHeldEvent(Button.A, 0.4);
		
		//Axis Functions [ inputs can be anything under "Axis." ]
			controller.getAxis(Axis.LY);
			
		//Rumble Function [ hard rumble is a deep rumble and soft rumble is lighter rumble ]
			controller.rumbleHard(1);
			controller.rumbleSoft(1);
			controller.rumbleHardFor(1, 0.5);
			controller.rumbleSoftFor(1, 0.5);
	}
	
	public void teleopPeriodic() {
		//Make Sure to Call this functions
			controller.update();
		
		//Examples			
			//Pressed
			if ( controller.getPressed(Dpad.N) ) {
				//Button has been pressed
				controller.rumbleHardFor(1, 0.2);
			}
			
			//Released
			if ( controller.getReleased(Axis.RT) ) {
				//Button has been released
				controller.rumbleSoftFor(1, 0.2);
			}
			
			//Click Time
			if ( controller.getClickTime(Button.A) >= 0.4 ) {
				//Long Click
			} else {
				//Short Click				
			}
			
			//Hold Event
			if ( controller.getHeldEvent(Button.A, 0.4) ) {
				//Button has been held for 0.4 seconds
			}
	}
}
