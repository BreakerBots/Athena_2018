/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.usfirst.frc.team5104.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import Libraries.ButtonS;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;

public class Robot extends IterativeRobot {
	Joystick controller = new Joystick(0);
	double axisX;
	double axisY;
	TalonSRX talon1 = new TalonSRX(11);
	TalonSRX talon2 = new TalonSRX(12);
	TalonSRX talon3 = new TalonSRX(13);
	TalonSRX talon4 = new TalonSRX(14);
	
	ButtonS gearS = new ButtonS(9);
	boolean gearState = false;
	DoubleSolenoid gearSol = new DoubleSolenoid(0, 1);
	
	@Override
	public void teleopPeriodic() {
		gearS.update();
		
		axisY = controller.getRawAxis(1);
		axisX = controller.getRawAxis(0);
		
		if (gearS.Pressed) {
			gearState = !gearState;
			if (gearState) { gearSol.set(DoubleSolenoid.Value.kForward); }
			else { gearSol.set(DoubleSolenoid.Value.kReverse); }
		}
			
		talon1.set(ControlMode.PercentOutput, -(-axisY-axisX));
		talon2.set(ControlMode.PercentOutput, -(-axisY-axisX));
		talon3.set(ControlMode.PercentOutput, -( axisY-axisX));
		talon4.set(ControlMode.PercentOutput, -( axisY-axisX));
	}
}
