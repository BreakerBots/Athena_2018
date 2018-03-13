/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.usfirst.frc.team5104.robot;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.kauailabs.navx.frc.AHRS;

import Libraries.ButtonS;
import Libraries.CustomDrive;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.PIDOutput;
import edu.wpi.first.wpilibj.PIDSource;
import edu.wpi.first.wpilibj.PIDSourceType;
import edu.wpi.first.wpilibj.SPI.Port;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Robot extends IterativeRobot {
	AHRS ahrs = new AHRS(Port.kMXP);
	CustomDrive drive = new CustomDrive(11, 12, 13, 14);
	Joystick controller = new Joystick(0); ButtonS x = new ButtonS(3), b = new ButtonS(2); boolean driving = true;
	PIDController turnController;
	PIDController moveController;

	
	//Archer
//	double TPI = 231;
	//Ares
	double TPI = 270;
	
	double rotateToAngleRate;
	double moveToDistance;
	
	//Turning PID Values
	static double tP = 0.07;
	static double tI = 0.0;
	static double tD = 0.0;
	static double tF = 0.00;
	static double tToleranceDegrees = /*2.5*/ 2;
	
	//Moving Forward PID Values
	static double mP = 0.07;
	static double mI = 0.000002;
	static double mD = 0.00002;
	static double mF = 0.00;
	static double mToleranceTicks = 80.0f;
	
	TalonSRX talonL = new TalonSRX(11);
    TalonSRX talonR = new TalonSRX(13);
	
	public void robotInit() {
		ahrs.reset();
		
		turnController = new PIDController(tP, tI, tD, tF, new PIDSource() {
			public void setPIDSourceType(PIDSourceType pidSource) {
			}
			public PIDSourceType getPIDSourceType() {
				return PIDSourceType.kDisplacement;
			}
			public double pidGet() {
				return ahrs.getAngle();
			}}, new PIDOutput() {
			public void pidWrite(double output) {
				rotateToAngleRate = output;
			}
		});
	    turnController.setInputRange(-180.0f,  180.0f);
	    turnController.setOutputRange(-1.0, 1.0);
	    turnController.setAbsoluteTolerance(tToleranceDegrees);
	    turnController.setContinuous(true);
	    
	    
	    moveController = new PIDController(mP, mI, mD, mF, new PIDSource() {
			public void setPIDSourceType(PIDSourceType pidSource) {
			}
			public PIDSourceType getPIDSourceType() {
				return PIDSourceType.kDisplacement;
			}
			public double pidGet() {
				return talonL.getSelectedSensorPosition(0);
			}}, new PIDOutput() {
				@Override
				public void pidWrite(double output) {
					moveToDistance = output;
				}
			});
	    moveController.setOutputRange(-1.0, 1.0);
	    moveController.setAbsoluteTolerance(mToleranceTicks);
	    
	    talonL.setSelectedSensorPosition(0,0,0);
		talonR.setSelectedSensorPosition(0,0,0);
		
	}
	
	public void teleopPeriodic() {
		drive.arcadeDrive(controller.getRawAxis(1), controller.getRawAxis(0));
	}
	
	public void autonomousInit() {
		//Pull up paths
		Scheduler.getInstance().add(new AutoBasic(this));
		
		tP = SmartDashboard.getNumber("DB/Slider 0", tP);
		tI = SmartDashboard.getNumber("DB/Slider 1", tI);
		tD = SmartDashboard.getNumber("DB/Slider 2", tD);
	}
	
	public void autonomousPeriodic() {
		Scheduler.getInstance().run();
	}
}
