// Practice code for Squeezy Sanchez motor


/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.usfirst.frc.team5104.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Talon;

public class Robot extends IterativeRobot {
	Joystick stick = new Joystick(0);

	TalonSRX talonSS = new TalonSRX(21);
	int turns = 5;
	double enc;
	double erev = 410299;    // ticks per revolutions
	double speed = 0;    // (of talon)

	@Override
	public void robotInit() {
	}



	@Override
	public void teleopInit() {
		talonSS.setSelectedSensorPosition(0, 0, 0);
		
		speed = 0;
	}


	@Override
	public void teleopPeriodic() {
		
		/*if(stick.getRawButton(1)) {
			
			talonSS.set(ControlMode.PercentOutput, 0.2);
			
		}
		
		else {
			talonSS.set(ControlMode.PercentOutput, 0);
			
		}
				
		talonSS.set(ControlMode.PercentOutput, speed);
		*/	
		
// pull trigger to spin
		
//-----------------------------------------------------------		
		
/*		enc = talonSS.getSelectedSensorPosition(0);
		
		if(stick.getRawButton(3)) {
			
			talonSS.setSelectedSensorPosition(0, 0, 0);
			
		}
*/
// resets encoder (used for testing)
		
//-----------------------------------------------------------
		
		if(stick.getRawButton(1)) {
			
			if (enc < 0.25*erev) {
				
				speed = 0.1;
				speed = -0.9*(Math.pow((enc/(0.25*erev)), 2) - 1) + 1;
				
			}
			
			if (enc*turns - 0.25*erev < enc) {
				
				speed = 0.9;
				speed = -0.9*(((enc - ((turns - 1)*erev) - 0.75*erev)/(0.25*erev)) - 1) +1;		
			}
			
		talonSS.set(ControlMode.PercentOutput, speed);
		
		}

//weird parabolic thing (don't touch it)		

//-----------------------------------------------------------		
		
/*		if(stick.getRawButton(0)) {
			
			speed = (stick.getRawAxis(1));
			
		}
*/
//motor spin control
	
//------------------------------------------------------------
		
		
	}

}
