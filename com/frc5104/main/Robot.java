package com.frc5104.main;

import com.frc5104.main.subsystems.Drive;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;

public class Robot extends IterativeRobot {

	Joystick joy = new Joystick(0);
	
	//Drive Squeezy Elevator Climber
	Drive drive = Drive.getInstance();
	Squeezy squeezy = Squeezy.getInstance();
	
	public void robotInit() {
	}//robotInit
	
	public void autonomousInit() {
		
	}//autonomousInit
	
	public void autonomousPeriodic() {
		
		
	}//autonomousPeriodic
	
	public void teleopInit() {
		
	}//teleopInit
	
	public void teleopPeriodic() {
		System.out.println("Encoder Position: "+drive.getEncoderRight());
		
		double x = joy.getRawAxis(0),
				y = joy.getRawAxis(1);
		
		drive.arcadeDrive(y,x);
		
		squeezy.poll();
		squeezy.updateState();
		
	}//teleopPeriodic
	
}//Robot
