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

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;

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

	File file_joy, file_gyro, file_encoder;
	FileWriter out_joy, out_gyro, out_encoder;
	boolean writing = false;
	
	Joystick joy = new Joystick(0);
	WPI_TalonSRX left1 = new WPI_TalonSRX(11), left2= new WPI_TalonSRX(12);
	WPI_TalonSRX right1 = new WPI_TalonSRX(13), right2 = new WPI_TalonSRX(14);
	
	SpeedControllerGroup left = new SpeedControllerGroup(left1, left2);
	SpeedControllerGroup right = new SpeedControllerGroup(right1, right2);
	
	DifferentialDrive drive = new DifferentialDrive(left, right);
	
	AHRS gyro = new AHRS(I2C.Port.kMXP);
	
	@Override
	public void robotInit() {
		
		long time = System.currentTimeMillis();
		file_joy = new File("/media/sda/joy-"+time+".txt");
		file_gyro = new File("/media/sda/gyro-"+time+".txt");
		file_encoder = new File("/media/sda/encoder-"+time+".txt");
		
		try {
			out_joy = new FileWriter(file_joy);
			out_gyro = new FileWriter(file_gyro);
			out_encoder = new FileWriter(file_encoder);

			writing = true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		gyro.enableLogging(true);
		
	}//robotInit

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
		if (writing) {
			try {
				out_joy.close();
				out_gyro.close();
				out_encoder.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
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
		double x = joy.getRawAxis(0);
		double y = joy.getRawAxis(1);
		
		double angle = gyro.getAngle();
		int encoder = left1.getSelectedSensorPosition(0);
		
		drive.arcadeDrive(y, x);
		
		if (writing) {
			try {
				long time = System.currentTimeMillis();
				out_joy.write(time+"--"+String.format("%f,%f\n", x, y));
				out_gyro.write(time+"--"+String.format("%f\n", angle));
				out_encoder.write(time+"--"+String.format("%d\n", encoder));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}//teleopPeriodic
	
	public void disabledInit() {
		if (writing) {
			try {
				out_joy.write("\n");
				out_gyro.write("\n");
				out_encoder.write("\n");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}//disabledInit

	/**
	 * This function is called periodically during test mode.
	 */
	@Override
	public void testPeriodic() {
	}
}
