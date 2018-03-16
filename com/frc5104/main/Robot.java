package com.frc5104.main;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.frc5104.autopaths.AutoSelector;
import com.frc5104.autopaths.Baseline;
import com.frc5104.main.subsystems.Drive;
import com.frc5104.main.subsystems.Elevator;
import com.frc5104.main.subsystems.Elevator.Stage;
import com.frc5104.main.subsystems.Shifters;
import com.frc5104.main.subsystems.Squeezy;
import com.frc5104.main.subsystems.SqueezySensors;
import com.frc5104.utilities.ButtonS;
import com.frc5104.utilities.ControllerHandler;
import com.frc5104.utilities.Deadband;
import com.frc5104.utilities.TalonFactory;
import com.frc5104.utilities.ControllerHandler.Button;
import com.frc5104.utilities.ControllerHandler.Dpad;
import com.frc5104.vision.VisionThread;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Robot extends IterativeRobot {

	int[] talonIDs = new int[] {11, 12, 13, 14 //drive
			,21, 22, 23    //squeezy
			,31, 32        //elevator
	};
	TalonFactory talonFactory = new TalonFactory(talonIDs);

	CommandGroup auto;
	VisionThread vision;

	Joystick joy = new Joystick(0);
	Deadband deadband = new Deadband(0.05);
	
	//Drive Squeezy Elevator Climber
//	Drive drive = null;
	Drive drive = Drive.getInstance();
	Shifters shifters = Shifters.getInstance();
	ButtonS shifterButton = new ButtonS(9);
	
//	Squeezy squeezy = null;
	Squeezy squeezy = Squeezy.getInstance();
//	SqueezySensors squeezySensors = SqueezySensors.getInstance();
	SqueezySensors squeezySensors = SqueezySensors.getInstance();
	
//	Elevator elevator = null;
	Elevator elevator = Elevator.getInstance();
	
	PTO pto = null;
//	PTO pto = PTO.getInstance();
	long startTime = System.currentTimeMillis();
	TalonSRX ptoTalon = null;
//	TalonSRX ptoTalon = new TalonSRX(9);
//	TalonSRX ptoTalon = new TalonSRX(/*Athena/Ares*//*9*/  /*Babyboard*/11);
	
	ButtonS ptoShifter = new ButtonS(4);
	DoubleSolenoid ptoSol = new DoubleSolenoid(2,3);
	
	DoubleSolenoid squeezyUpDown = new DoubleSolenoid(4,5);
	
	ControllerHandler controller = ControllerHandler.getInstance();
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
		
		squeezyUpDown.set(DoubleSolenoid.Value.kReverse);
		
	    drive.resetEncoders();
		
	}//robotInit
	long autoStartTime;
	public void autonomousInit() {
//		auto = AutoSelector.getAuto();
//		Scheduler.getInstance().add(auto);
		autoStartTime = System.currentTimeMillis();
	}//autonomousInit
	
	public void autonomousPeriodic() {
//		Scheduler.getInstance().run();
		squeezy.update();
		if (System.currentTimeMillis() < autoStartTime + 4000) {
			drive.arcadeDrive(0.3, 0);
		} else {
			drive.arcadeDrive(0, 0);
		}
	}//autonomousPeriodic
	
	public void teleopInit() {
		if (shifters != null)
			shifters.shiftLow();
		
	}//teleopInit
	
	public void teleopPeriodic() {
		controller.update();
		
		if (controller.getPressed(Button.LB))
			elevator.goTo(Stage.kSwitch);
		else if (controller.getPressed(Button.RB))
			elevator.goTo(Stage.kTop);
		
//		System.out.println("Encoder Position: "+drive.getEncoderRight());
		if (controller.getHeldEvent(Dpad.E, 0.4)) { 
			ptoSol.set(ptoSol.get() == DoubleSolenoid.Value.kReverse ? DoubleSolenoid.Value.kForward : DoubleSolenoid.Value.kReverse);
			controller.rumbleHardFor(1, 0.2);
		}
		
		if (drive != null) {
			double x = joy.getRawAxis(0),
				   y = -joy.getRawAxis(1);
			
//			x = deadband.get(x);
//			y = deadband.get(y);
			drive.arcadeDrive(y,x);
		}
		
		shifterButton.update();
		if (shifterButton.Pressed)
			shifters.toggle();
		
		if (elevator != null) {
			elevator.userControl();
		}

		if (squeezy != null) {
			squeezy.pollButtons();
			squeezy.updateState();
			squeezy.update();
		}
		
		if (controller.getPressed(Dpad.S)) {
			System.out.println("DOWN!");
			squeezyUpDown.set(DoubleSolenoid.Value.kForward);
		}
		if (controller.getPressed(Dpad.N)) {
			System.out.println("UP!");
			squeezyUpDown.set(DoubleSolenoid.Value.kReverse);
		}
//		if (Math.abs(drive.getEncoderLeft()+drive.getEncoderRight())/2 > 1300)
//			shifters.shiftHigh();
//		else if (Math.abs(drive.getEncoderLeft()+drive.getEncoderRight())/2 < 800)
//			shifters.shiftLow();

		
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
