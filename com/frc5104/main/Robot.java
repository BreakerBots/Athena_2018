package com.frc5104.main;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.frc5104.main.subsystems.Drive;
import com.frc5104.main.subsystems.Squeezy;
import com.frc5104.main.subsystems.SqueezySensors;
import com.frc5104.vision.VisionThread;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Robot extends IterativeRobot {

	BasicAuto auto;
	VisionThread vision;
	//New comment/
	Joystick joy = new Joystick(0);
	
	//Drive Squeezy Elevator Climber
	Drive drive = null;
//	Drive drive = Drive.getInstance();
//	Shifters shifters = Shifters.getInstance();
	
	Squeezy squeezy = null;
	SqueezySensors squeezySensors = null;
//	Squeezy squeezy = Squeezy.getInstance();
//	SqueezySensors squeezySensors = SqueezySensors.getInstance();
	
//	Elevator elevator = Elevator.getInstance();
	
	PTO pto = null;
//	PTO pto = PTO.getInstance();
	long startTime = System.currentTimeMillis();
//	Talon ptoTalon = null;
	TalonSRX ptoTalon = new TalonSRX(9);
	
	public void robotInit() {
		System.out.println("Running Athena code");
		
		if (squeezy != null)
			squeezy.initTable(null);

		vision = new VisionThread();
		vision.start();
		
	}//robotInit
	
	public void autonomousInit() {
		SmartDashboard.putNumber("DB/Slider 0", 4);
		
//		auto = new AutoPickupCube();
//		
//		auto.init();
	}//autonomousInit
	
	public void autonomousPeriodic() {
		
		
	}//autonomousPeriodic
	
	public void teleopInit() {
//		shifters.shiftLow();
	}//teleopInit
	
	public void teleopPeriodic() {
//		System.out.println("Encoder Position: "+drive.getEncoderRight());
		
		double x = joy.getRawAxis(0),
				y = joy.getRawAxis(1);
		
		if (drive != null)
			drive.arcadeDrive(y,-x);
		
//		elevator.poll();
//		elevator.update();
		
//		if (Math.abs(drive.getEncoderLeft()+drive.getEncoderRight())/2 > 1300)
//			shifters.shiftHigh();
//		else if (Math.abs(drive.getEncoderLeft()+drive.getEncoderRight())/2 < 800)
//			shifters.shiftLow();

		
		if (squeezySensors != null) {
			squeezySensors.updateSensors();
		}
		if (squeezy != null) {
			squeezy.poll();
			squeezy.updateState();
		}
		
//		if (joy.getRawAxis(3) > 0.2) {
		if (pto != null) {
			if ((System.currentTimeMillis() - startTime)%2000 > 1000) {
	//			elevator.disable();
				pto.powerClimber();
				System.out.println("Powering climber");
			} else {
	//			elevator.enable();
				pto.powerElevator();
				System.out.println("Powering elevator");
			}
		}
		if (ptoTalon != null) {
			double elevatorEffort = joy.getRawAxis(5);
			if (-0.1 < elevatorEffort && elevatorEffort < 0.1)
				elevatorEffort = 0;
			ptoTalon.set(ControlMode.PercentOutput, elevatorEffort);
		}
		
	}//teleopPeriodic
	
	public void robotPeriodic() {
	}
	
}//Robot
