package com.frc5104.main;

import com.frc5104.main.subsystems.Squeezy;
import com.frc5104.main.subsystems.SqueezySensors;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Robot extends IterativeRobot {

	Joystick joy = new Joystick(0);
	
	//Drive Squeezy Elevator Climber
//	Drive drive = Drive.getInstance();
//	Shifters shifters = Shifters.getInstance();
	
//	Squeezy squeezy = null;
	Squeezy squeezy = Squeezy.getInstance();
	SqueezySensors squeezySensors = SqueezySensors.getInstance();
	
//	Elevator elevator = Elevator.getInstance();
	
	public void robotInit() {
		System.out.println("Running Athena code");
		
		squeezy.initTable(null);
		
	}//robotInit
	
	public void autonomousInit() {
		SmartDashboard.putNumber("DB/Slider 0", 4);
	}//autonomousInit
	
	public void autonomousPeriodic() {
		
		
	}//autonomousPeriodic
	
	public void teleopInit() {
//		shifters.shiftLow();
	}//teleopInit
	
	public void teleopPeriodic() {
		squeezySensors.updateSensors();
//		System.out.println("Encoder Position: "+drive.getEncoderRight());
		
		double x = joy.getRawAxis(0),
				y = joy.getRawAxis(1);
		
//		drive.arcadeDrive(y,-x);
		
//		elevator.poll();
//		elevator.update();
		
//		if (Math.abs(drive.getEncoderLeft()+drive.getEncoderRight())/2 > 1300)
//			shifters.shiftHigh();
//		else if (Math.abs(drive.getEncoderLeft()+drive.getEncoderRight())/2 < 800)
//			shifters.shiftLow();

		
		if (squeezy != null) {
			squeezy.poll();
			squeezy.updateState();
		}
		
		if (squeezySensors != null) {
			squeezySensors.updateSensors();
		}
		
	}//teleopPeriodic
	
	public void robotPeriodic() {
	}
	
}//Robot
