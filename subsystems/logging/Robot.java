/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.usfirst.frc.team5104.robot;

import org.usfirst.frc.team5104.robot.java.LogValue;
import org.usfirst.frc.team5104.robot.java.Logger;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;

public class Robot extends IterativeRobot {

	Logger logger;

	Joystick joy = new Joystick(0);
	WPI_TalonSRX left1 = new WPI_TalonSRX(11), left2= new WPI_TalonSRX(12);
	WPI_TalonSRX right1 = new WPI_TalonSRX(13), right2 = new WPI_TalonSRX(14);
	
	SpeedControllerGroup left = new SpeedControllerGroup(left1, left2);
	SpeedControllerGroup right = new SpeedControllerGroup(right1, right2);
	
	DifferentialDrive drive = new DifferentialDrive(left, right);
	
//	AHRS gyro = new AHRS(I2C.Port.kMXP);
	
	@Override
	public void robotInit() {
		
		System.out.println("Running Robot Logging Program");
		
		logger = new Logger("/media/sda/logs");
		
//		logger.logDouble("gyro", new LogValue() {
//			public Object get() {
//				return gyro.getAngle();
//			}
//		});
		logger.logDouble("left_encoder", new LogValue() {
			public Object get() {
				return left1.getSelectedSensorPosition(0);
			}
		});
		logger.logDouble("right_encoder", new LogValue() {
			public Object get() {
				return right1.getSelectedSensorPosition(0);
			}
		});
		logger.logDouble("joy_x", new LogValue() {
			public Object get() {
				return joy.getRawAxis(0);
			}
		});
		logger.logDouble("joy_y", new LogValue() {
			public Object get() {
				return joy.getRawAxis(1);
			}
		});
		
//		gyro.enableLogging(true);
		
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
	}//autonomousInit

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
		System.out.println("logging");
		logger.collect();
		
		double x = joy.getRawAxis(0);
		double y = joy.getRawAxis(1);
		
		drive.arcadeDrive(y, x);
		
	}//teleopPeriodic
	
	@Override
	public void disabledInit() {
		System.out.println("Saving Log Files");
		logger.log();
		
	}//disabledInit

	/**
	 * This function is called periodically during test mode.
	 */
	@Override
	public void testPeriodic() {
	}
}
