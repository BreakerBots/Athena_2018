/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.usfirst.frc.team5104.robot;

import java.io.IOException;
import java.util.Calendar;

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

	CSVFileWriter dataFile;
	
	Joystick joy = new Joystick(0);
	WPI_TalonSRX left1 = new WPI_TalonSRX(13), left2= new WPI_TalonSRX(14);
	WPI_TalonSRX right1 = new WPI_TalonSRX(11), right2 = new WPI_TalonSRX(12);
	
	SpeedControllerGroup left = new SpeedControllerGroup(left1, left2);
	SpeedControllerGroup right = new SpeedControllerGroup(right1, right2);
	
	DifferentialDrive drive = new DifferentialDrive(left, right);
	
	AHRS gyro = new AHRS(I2C.Port.kMXP);
	
	@Override
	public void robotInit() {
		
		System.out.println("Running Robot Logging Program");
		
		Calendar today = Calendar.getInstance();
		String directory = "/media/sda/";
//							+today.get(Calendar.MONTH)+"-"
//							+today.get(Calendar.DAY_OF_MONTH)+"-"
//							+today.get(Calendar.YEAR)+"--"
//							+today.get(Calendar.HOUR)+":"
//							+today.get(Calendar.MINUTE)+":"
//							+today.get(Calendar.SECOND);
		
		try {
			dataFile = new CSVFileWriter(directory,"robot_data");
			System.out.println("Successfully created log file at: "+dataFile.getAbsolutePath());
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Failed to create log file at: "+dataFile.getAbsolutePath());
		}
		
		dataFile.addLogValue("gyro", new LogValue() {
			public String getText() {
				return ""+gyro.getAngle();
			}
		});
		dataFile.addLogValue("left_encoder", new LogValue() {
			public String getText() {
				return ""+right1.getSelectedSensorPosition(0);
			}
		});
		dataFile.addLogValue("joy_x", new LogValue() {
			public String getText() {
				return ""+joy.getRawAxis(0);
			}
		});
		dataFile.addLogValue("joy_y", new LogValue() {
			public String getText() {
				return ""+joy.getRawAxis(1);
			}
		});
		
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
		try {
			dataFile.update(System.currentTimeMillis());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		double x = joy.getRawAxis(0);
		double y = joy.getRawAxis(1);
		
		drive.arcadeDrive(y, x);
		
	}//teleopPeriodic
	
	public void disabledInit() {
		
	}//disabledInit

	/**
	 * This function is called periodically during test mode.
	 */
	@Override
	public void testPeriodic() {
	}
}
