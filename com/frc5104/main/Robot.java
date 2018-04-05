package com.frc5104.main;

import com.frc5104.autopaths.AutoSelector;
import com.frc5104.main.subsystems.Drive;
import com.frc5104.main.subsystems.Elevator;
import com.frc5104.main.subsystems.Shifters;
import com.frc5104.main.subsystems.Squeezy;
import com.frc5104.main.subsystems.Squeezy.SqueezyState;
import com.frc5104.main.subsystems.SqueezySensors;
import com.frc5104.utilities.ControllerHandler;
import com.frc5104.utilities.Deadband;
import com.frc5104.utilities.HMI;
import com.frc5104.utilities.TalonFactory;

import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.command.Scheduler;

public class Robot extends IterativeRobot {
	
	int[] talonIDs = new int[] {11, 12, 13, 14 //drive
			,21, 22, 23    //squeezy
			,31, 32        //elevator
	};
	TalonFactory talonFactory = new TalonFactory(talonIDs);

	CommandGroup auto;

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
	
	DoubleSolenoid ptoSol = new DoubleSolenoid(4, 5);
	
	public DoubleSolenoid squeezyUpDown = new DoubleSolenoid(0, 1);
	
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
		
		CameraServer.getInstance().startAutomaticCapture();
		
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
		HMI.PutOnDashboard();
		Scheduler.getInstance().run();
		squeezy.update();
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
		
		if (controller.getHeldEvent(HMI.kPtoButton, 0.4)) { 
			System.out.println("Switching PTO!");
			ptoSol.set(ptoSol.get() == DoubleSolenoid.Value.kReverse ? DoubleSolenoid.Value.kForward : DoubleSolenoid.Value.kReverse);
			if (ptoSol.get() == Value.kForward)
				controller.rumbleSoftFor(0.5, 0.2);
			else
				controller.rumbleHardFor(1, 0.2);
		}
		
		if (drive != null) {
			double x = -controller.getAxis(HMI.kDriveX),
				   y = controller.getAxis(HMI.kDriveY);
			x = Deadband.getDefault().get(x);
			y = Deadband.getDefault().get(y);
			
			drive.arcadeDrive(y,x);
		}
		
		if (controller.getAxis(HMI.kShift) > 0.6)
			shifters.shiftHigh();
		else
			shifters.shiftLow();
		
		if (elevator != null) {
			elevator.userControl();
		}

		if (squeezy != null) {
			squeezy.updateState();
			squeezy.update();
		}
		
		if (controller.getPressed(HMI.kSqueezyDown)) {
			System.out.println("DOWN!");
			squeezyUpDown.set(DoubleSolenoid.Value.kForward);
		}
		if (controller.getPressed(HMI.kSqueezyUp)) {
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
		squeezy.postSqueezerData();
		squeezy.postState();
		squeezy.postUltrasonicData();
		
		elevator.updateTables();
	}//robotPeriodic
	
	public void testInit() {

	}
	
}//Robot
