// Practice code for Squeezy Sanchez motor

package org.usfirst.frc.team5104.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Talon;



public class Squeezy_Sanchez extends IterativeRobot {
	Joystick stick = new Joystick(0);

	TalonSRX talonSS = new TalonSRX(21);
	int turns = 2;
	double enc;		// current encoder position
	double erev = 40897;    // ticks per revolutions
	double comp = erev*0.18;		// compensation value
	double speed = 0;    // (of talon)
	
	boolean ssp;
	
	Button Button = new Button(4);
	
	
/*	public void turn(int turns) {		//turn motor function -- requires encoder update to function properly
		
			if(enc < (erev*turns) - comp) { 
				
				speed = 0.1; 
			}
			else { 
				
				speed = 0; 
			}
			
		}
*/

	@Override
	public void robotInit() {
		
	}



	@Override
	public void teleopInit() {
		
		talonSS.setSelectedSensorPosition(0, 0, 0);
		
		enc = 0;
		speed = 0;
		ssp = false;
	}
	

	@Override
	public void teleopPeriodic() {
		
		Button.update();
		
// updates Button_Press class		
//------------------------------------------------------------		
		
		enc = talonSS.getSelectedSensorPosition(0);
		System.out.println(enc);

// updates encoder to read current encoder position		
//------------------------------------------------------------		
		
		if(stick.getRawButton(1)) {
			
			if(enc < (erev*turns) - comp) { 
				
				speed = 0.1; 
			}
			else { 
				
				speed = 0; 
			}
			
		}
			
	
		
// calls turn function and turns it the amount of times shown	
//------------------------------------------------------------		
			
		if(stick.getRawButton(3)) {
			
			talonSS.setSelectedSensorPosition(0, 0, 0);
		}

// resets encoder (used for testing)
//------------------------------------------------------------		
		
		
//		System.out.println(ssp);
		
		talonSS.set(ControlMode.PercentOutput, speed);

// sets talon to desired speed	
//------------------------------------------------------------		

		
	}

}
