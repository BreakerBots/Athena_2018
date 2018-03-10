package com.frc5104.main;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.frc5104.main.subsystems.Drive;
import com.frc5104.main.subsystems.Elevator;
import com.frc5104.main.subsystems.Squeezy;
import com.frc5104.main.subsystems.SqueezySensors;
import com.frc5104.utilities.ButtonS;
import com.frc5104.utilities.Deadband;
import com.frc5104.vision.VisionThread;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Robot extends IterativeRobot {

	BasicAuto auto;
	VisionThread vision;

	Joystick joy = new Joystick(0);
	Deadband deadband = new Deadband(0.05);
	
	//Drive Squeezy Elevator Climber
	Drive drive = null;
//	Drive drive = Drive.getInstance();
//	Shifters shifters = Shifters.getInstance();
	
	Squeezy squeezy = null;
//	SqueezySensors squeezySensors = SqueezySensors.getInstance();
//	Squeezy squeezy = Squeezy.getInstance();
//	SqueezySensors squeezySensors = SqueezySensors.getInstance();
	
//	Elevator elevator = null;
	Elevator elevator = Elevator.getInstance();
	
	PTO pto = null;
//	PTO pto = PTO.getInstance();
	long startTime = System.currentTimeMillis();
	TalonSRX ptoTalon = null;
//	TalonSRX ptoTalon = new TalonSRX(9);
//	TalonSRX ptoTalon = new TalonSRX(/*Athena/Ares*//*9*/  /*Babyboard*/11);
	
	ButtonS ptoShifter = new ButtonS(4);
	DoubleSolenoid ptoSol = new DoubleSolenoid(4, 5);
	
	/* ------- PTO PID Values for Elevator -------
	 * 
	 * p == 0.16
	 * i == 0.00002
	 * d == 0.15
	 * izone == 1000
	 * 
	 * fwd soft limit == 0
	 * rev soft limit == -16150
	 * 
	 * ------- PTO PID Values for Squeezy -------
	 * 
	 * p == 0.01
	 * i == 0.0001
	 * d == 
	 * izone == 3000
	 * 
	 * fwd soft limit == 0
	 * rev soft limit == -100000
	 * 
	 * -------   						  -------
	 */
	
	public void robotInit() {
		System.out.println("Running Athena code");
		
		if (squeezy != null)
			squeezy.initTable(null);
		
		if (elevator != null)
			elevator.initTable(null);
		
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
		ptoShifter.update(); if (ptoShifter.Pressed) { 
			ptoSol.set(ptoSol.get() == DoubleSolenoid.Value.kReverse ? DoubleSolenoid.Value.kForward : DoubleSolenoid.Value.kReverse);
		}
		
		if (drive != null) {
			double x = joy.getRawAxis(0),
					y = joy.getRawAxis(1);
			
			x = deadband.get(x);
			y = deadband.get(y);
			drive.arcadeDrive(y,-x);
		}
		
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
		
		if (elevator != null) {
			elevator.update();
		}
		
		if (ptoTalon != null) {
			double elevatorEffort = ptoTalon.getMotorOutputPercent();
			if (SmartDashboard.getBoolean("pto_driven_by_joystick", true)) {
				elevatorEffort = -(joy.getRawButton(5) ? 1: -1) + (joy.getRawButton(6) ? 1 : -1);
				elevatorEffort = deadband.get(elevatorEffort);
				ptoTalon.set(ControlMode.PercentOutput, elevatorEffort);
			} else {
				ptoTalon.set(ControlMode.Position, SmartDashboard.getNumber("elevator_setpoint", 
						ptoTalon.getSelectedSensorPosition(0)));
			}

			boolean lower = ptoTalon.getSensorCollection().isFwdLimitSwitchClosed();
			boolean upper = ptoTalon.getSensorCollection().isRevLimitSwitchClosed();
			if (lower) {
				ptoTalon.setSelectedSensorPosition(0, 0, 10);
			}
			SmartDashboard.putBoolean("limits/lower-fwd", lower);
			SmartDashboard.putBoolean("limits/upper-rev", upper);
			
			SmartDashboard.putNumber(""
					+ "", elevatorEffort);
			SmartDashboard.putNumber("pto_current", ptoTalon.getOutputCurrent());
			SmartDashboard.putNumber("pto_voltage", ptoTalon.getMotorOutputVoltage());
			
			SmartDashboard.putNumber("elevator_pos", ptoTalon.getSelectedSensorPosition(0));
			SmartDashboard.putNumber("elevator_vel", ptoTalon.getSelectedSensorVelocity(0));
			
			SmartDashboard.putNumber("i_accum", ptoTalon.getIntegralAccumulator(0));
			if (SmartDashboard.getBoolean("clear_i_accum", false)) {
				SmartDashboard.putBoolean("clear_i_accum", false);
				ptoTalon.setIntegralAccumulator(0, 0, 10);
			}
		}
		
		
	}//teleopPeriodic
	
	public void robotPeriodic() {
	}
	
	public void testInit() {

	}
	
}//Robot
