/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                       */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.
 * 
 *                                                                */
/*-PixyCam - William Bennett---------------------------------------------------------------------------*/

package org.usfirst.frc.team9104.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.I2C.Port;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Robot extends IterativeRobot {


	// Vision Variables
	int x = 0;
	int y = 0;
	int height = 0;
	int width = 0;

	Vision pixy1 = new Vision();
	PixyPacket visionPacket = new PixyPacket();

	@Override
	public void robotInit() {

	}

	@Override
	public void autonomousInit() {

	}

	@Override
	public void autonomousPeriodic() {

	}

	@Override
	public void teleopInit() {

	}

	@Override
	public void teleopPeriodic() {


		visionPacket = pixy1.usePixy();
		if (visionPacket != null) {
			System.out.print("X:  " + visionPacket.X);
			System.out.print("   Y: " + visionPacket.Y);
			System.out.print("   Width:" + visionPacket.Width);
			System.out.println("   Height:" + visionPacket.Height);
		}
;
	}

	@Override
	public void testPeriodic() {
	}
}
