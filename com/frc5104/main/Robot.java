package com.frc5104.main;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.frc5104.autopaths.AutoSelector;
import com.frc5104.main.subsystems.Drive;
import com.frc5104.main.subsystems.Elevator;
import com.frc5104.main.subsystems.Shifters;
import com.frc5104.main.subsystems.Squeezy;
import com.frc5104.main.subsystems.Squeezy.SqueezyState;
import com.frc5104.main.subsystems.SqueezySensors;
import com.frc5104.utilities.ButtonS;
import com.frc5104.utilities.ControllerHandler;
import com.frc5104.utilities.ControllerHandler.Control;
import com.frc5104.utilities.Deadband;
import com.frc5104.utilities.TalonFactory;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.DriverStation;
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

	Joystick joy = new Joystick(0);
	Deadband deadband = new Deadband(0.05);
	
	//Drive Squeezy Elevator Climber
//	Drive drive = null;
	Drive drive = Drive.getInstance();
	Shifters shifters = Shifters.getInstance();
	
//	Squeezy squeezy = null;
	Squeezy squeezy = Squeezy.getInstance();
//	SqueezySensors squeezySensors = SqueezySensors.getInstance();
	SqueezySensors squeezySensors = SqueezySensors.getInstance();
	
//	Elevator elevator = null;
	Elevator elevator = Elevator.getInstance();
	
	long startTime = System.currentTimeMillis();
	TalonSRX ptoTalon = null;
//	TalonSRX ptoTalon = new TalonSRX(9);
//	TalonSRX ptoTalon = new TalonSRX(/*Athena/Ares*//*9*/  /*Babyboard*/11);
	
	ButtonS ptoShifter = new ButtonS(4);
	DoubleSolenoid ptoSol = new DoubleSolenoid(2,3);
	
	public DoubleSolenoid squeezyUpDown = new DoubleSolenoid(4,5);
	
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
		
	}//robotInit
	long autoStartTime;
	public void autonomousInit() {
		squeezy.forceState(SqueezyState.HOLDING);
		squeezyUpDown.set(Value.kReverse);//UP
		
		auto = AutoSelector.getAuto(squeezyUpDown);
		Scheduler.getInstance().add(auto);
		
//		autoStartTime = System.currentTimeMillis();
	}//autonomousInit
	
	public void autonomousPeriodic() {
		Scheduler.getInstance().run();
		squeezy.update();
//		if (System.currentTimeMillis() < autoStartTime + 10000) {
//			drive.arcadeDrive(0.3, 0);
//		} else {
//			drive.arcadeDrive(0, 0);
//		}
		SmartDashboard.putString("DB/String 7", DriverStation.getInstance().getGameSpecificMessage());
	}//autonomousPeriodic
	
	public void teleopInit() {
		if (shifters != null)
			shifters.shiftLow();
		
	}//teleopInit
	
	public void teleopPeriodic() {
		controller.update();
		
//		if (controller.getPressed(Button.LB))
//			elevator.goTo(Stage.kSwitch);
//		else if (controller.getPressed(Button.RB))
//			elevator.goTo(Stage.kTop);
		
//		System.out.println("Encoder Position: "+drive.getEncoderRight());
		if (controller.getHeldEvent(Control.X, 0.4)) { 
//		if (controller.getPressed(Button.X))
			System.out.println("Switching PTO!");
			ptoSol.set(ptoSol.get() == DoubleSolenoid.Value.kReverse ? DoubleSolenoid.Value.kForward : DoubleSolenoid.Value.kReverse);
			if (ptoSol.get() == Value.kForward)
				controller.rumbleSoftFor(0.5, 0.2);
			else
				controller.rumbleHardFor(1, 0.2);
		}
		
		if (drive != null) {
			double x = joy.getRawAxis(0),
				   y = -joy.getRawAxis(1);
			x = Deadband.getDefault().get(x);
			y = Deadband.getDefault().get(y);
			
			drive.arcadeDrive(y,x);
		}
		
		if (controller.getAxis(Control.RT) > 0.6)
			shifters.shiftHigh();
		else
			shifters.shiftLow();
		
		if (elevator != null) {
			elevator.userControl();
		}

		if (squeezy != null) {
			squeezy.pollButtons();
			squeezy.updateState();
			squeezy.update();
		}
		
		if (controller.getPressed(Control.S)) {
			System.out.println("DOWN!");
			squeezyUpDown.set(DoubleSolenoid.Value.kForward);
		}
		if (controller.getPressed(Control.N)) {
			if (!squeezy.isInState(SqueezyState.INTAKE)) {
				System.out.println("UP!");
				squeezyUpDown.set(DoubleSolenoid.Value.kReverse);
			} else {
				System.out.println("Will not pull up squeezy in intake mode!!!");
				squeezy.forceState(SqueezyState.HOLDING);
			}
		}
		
	}//teleopPeriodic
	
	public void robotPeriodic() {
	}
	
	public void testInit() {

	}
	
}//Robot
