package com.frc5104.main;

import com.frc5104.main.subsystems.Drive;
import com.frc5104.main.subsystems.Elevator;
import com.frc5104.main.subsystems.Squeezy;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Robot extends IterativeRobot {

	Joystick joy = new Joystick(0);
	
	//Drive Squeezy Elevator Climber
	Drive drive = Drive.getInstance();
//	Squeezy squeezy = null;
	Squeezy squeezy = Squeezy.getInstance();
	Elevator elevator = Elevator.getInstance();
	
	public void robotInit() {
	}//robotInit
	
	public void autonomousInit() {
		SmartDashboard.putNumber("DB/Slider 0", 4);
	}//autonomousInit
	
	public void autonomousPeriodic() {
		
		
	}//autonomousPeriodic
	
	public void teleopInit() {
		drive.shiftLow();
	}//teleopInit
	
	public void teleopPeriodic() {
		System.out.println("Encoder Position: "+drive.getEncoderRight());
		
		double x = joy.getRawAxis(0),
				y = joy.getRawAxis(1);
		
		drive.arcadeDrive(y,-x);
		
		if (squeezy != null) {
			squeezy.poll();
			squeezy.updateState();
		}
		
		elevator.poll();
		elevator.update();
		
		if (Math.abs(drive.getEncoderLeft()+drive.getEncoderRight())/2 > 1300)
			drive.shiftHigh();
		else if (Math.abs(drive.getEncoderLeft()+drive.getEncoderRight())/2 < 800)
			drive.shiftLow();
		
	}//teleopPeriodic
	
}//Robot
