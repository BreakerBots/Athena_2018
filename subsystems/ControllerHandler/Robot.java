package org.usfirst.frc.team5104.robot;

import org.usfirst.frc.team5104.robot.ControllerHandler.Control;

import edu.wpi.first.wpilibj.IterativeRobot;

public class Robot extends IterativeRobot {
	ControllerHandler controller = ControllerHandler.getInstance();
	
	//For an easier control in main robot programs I would recomend a control sceme like this:
	Control SqueezyIntake = Control.A;
	//and then:
	//controller.getPressed(SqueezyIntake);
	
	public void autonomousPeriodic() {
		//Make Sure to Call this functions
		controller.update();
	
		//Control Functions [ inputs can be anythings under "Control.", "Control." or "Control." ]
			controller.getHeld(Control.B);
			controller.getHeldTime(Control.X);
			controller.getPressed(Control.N);
			controller.getReleased(Control.RT);
			controller.getClickTime(Control.A);
			controller.getHeldEvent(Control.A, 0.4);
			
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
			if ( controller.getPressed(Control.N) ) {
				//Control has been pressed
				controller.rumbleHardFor(1, 0.2);
			}
			
			//Released
			if ( controller.getReleased(Control.RT) ) {
				//Control has been released
				controller.rumbleSoftFor(1, 0.2);
			}
			
			//Click Time
			if ( controller.getClickTime(Control.A) >= 0.4 ) {
				//Long Click
			} else {
				//Short Click				
			}
			
			//Hold Event
			if ( controller.getHeldEvent(Control.A, 0.4) ) {
				//Control has been held for 0.4 seconds
			}
	}
}
