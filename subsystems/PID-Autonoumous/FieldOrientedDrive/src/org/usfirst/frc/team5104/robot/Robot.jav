package org.usfirst.frc.team5104.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import Autonomous.AutoBasic;
import Autonomous.PIDauto;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.command.Scheduler;
import utilities.*;
import Subsystems.*;

public class Robot extends IterativeRobot {
	
	//For Implementing Autonomous This is All You need in the robot class, the rest you should already have...
	public void autonomousInit() {
		Elevator.getInstance().start();
		Scheduler.getInstance().add(new AutoBasic());
	}
	
	public void autonomousPeriodic() {
		Elevator.getInstance().update();
		Scheduler.getInstance().run();
	}
	
	public void robotInit() {
		PIDauto.getInstance().init();
		Elevator.getInstance().robotInit();
	}
	//--
	
	
	
	
	
	
	
	
	
	
	
	
	//The Extras Stuffs ---
		TalonFactory TalonFactory = new TalonFactory( new int[]{ 11, 12, 13, 14, 21, 22, 23, 31, 32 } );
		Joystick controller = new Joystick(0);
		
		//Drive
		TalonSRX talonL = new TalonSRX(11);
	    TalonSRX talonR = new TalonSRX(13);
	    
	    //Elevator
	    TalonSRX talonE1 = new TalonSRX(31);
	    TalonSRX talonE2 = new TalonSRX(32);
		
		public void teleopInit() {
	    	talonE2.set(ControlMode.Follower, talonE1.getDeviceID());
		}
		
		public void teleopPeriodic() {
			CustomDrive.getInstance().arcadeDrive(controller.getRawAxis(1), controller.getRawAxis(0));
			talonE1.set(ControlMode.PercentOutput, controller.getRawAxis(5));
		}
}
