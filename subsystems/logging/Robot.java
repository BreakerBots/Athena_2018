/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.usfirst.frc.team5104.robot;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import edu.wpi.first.wpilibj.IterativeRobot;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.properties file in the
 * project.
 */
public class Robot extends IterativeRobot {

	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	@Override
	public void robotInit() {
		File file = new File("/home/lvuser/Log.txt");
		
		try {
			FileWriter out = new FileWriter(file);
			out.write("Hello Birk, I am on the RoboRIO");
			
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		/*
		FileOutputStream out;
		try {
			out = new FileOutputStream(file);
			byte[] bytes = {(byte)'H',(byte)'E',(byte)'L',(byte)'L',(byte)'O'};
			out.write(bytes);
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		*/
		
		
	}

	/**
	 * This autonomous (along with the chooser code above) shows how to select
	 * between different autonomous modes using the dashboard. The sendable
	 * chooser code works with the Java SmartDashboard. If you prefer the
	 * LabVIEW Dashboard, remove all of the chooser code and uncomment the
	 * getString line to get the auto name from the text box below the Gyro
	 *
	 * <p>You can add additional auto modes by adding additional comparisons to
	 * the switch structure below with additional strings. If using the
	 * SendableChooser make sure to add them to the chooser code above as well.
	 */
	@Override
	public void autonomousInit() {
	}

	/**
	 * This function is called periodically during autonomous.
	 */
	@Override
	public void autonomousPeriodic() {
	}

	/**
	 * This function is called periodically during operator control.
	 */
	@Override
	public void teleopPeriodic() {
	}

	/**
	 * This function is called periodically during test mode.
	 */
	@Override
	public void testPeriodic() {
	}
}
