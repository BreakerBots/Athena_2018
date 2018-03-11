package com.frc5104.main;

import java.io.File;
import java.util.Calendar;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.frc5104.logging.CSVFileReader;
import com.frc5104.logging.CSVFileWriter;
import com.frc5104.logging.LogDouble;
import com.frc5104.main.subsystems.Drive;
import com.frc5104.main.subsystems.Elevator;
import com.frc5104.main.subsystems.Shifters;
import com.frc5104.main.subsystems.Squeezy;
import com.frc5104.main.subsystems.SqueezySensors;
import com.frc5104.utilities.ButtonS;
import com.frc5104.utilities.Deadband;
import com.frc5104.vision.VisionThread;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class RobotRecorder extends IterativeRobot {

	
	//------------ Recording ----------------//
	public static final String root = "/home/lvuser/aresPaths";
	enum RecorderState {
		kUser, kRecording, kPlayback
	}
	RecorderState recorderState = RecorderState.kUser;

	File recorderFile;
	CSVFileWriter recorder;
	
	//------------- Playback ----------------//
	CSVFileReader reader;
	
	//---------------------------------------//
	
	BasicAuto auto;
	VisionThread vision;

	Joystick joy = new Joystick(0);
	Deadband deadband = new Deadband(0.05);
	
	//Drive Squeezy Elevator Climber
//	Drive drive = null;
	Drive drive = Drive.getInstance();
	Shifters shifters = Shifters.getInstance();
	ButtonS shifterButton = new ButtonS(5);
	
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
	DoubleSolenoid ptoSol = new DoubleSolenoid(4, 5);
	
	
	DoubleSolenoid squeezyUpDown = new DoubleSolenoid(0,1);
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
		
		squeezyUpDown.set(DoubleSolenoid.Value.kForward);
		
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
		if (shifters != null)
			shifters.shiftLow();
		
	}//teleopInit
	
	public void teleopPeriodic() {
		int pov = joy.getPOV();
		
		SmartDashboard.putString("String 0", "POV: "+joy.getPOV());
		SmartDashboard.putString("String 1", recorderState.toString());
		
		switch (recorderState) {
		case kUser:
			userTeleop();
			if (pov == 270) {
				System.out.println("Started Recording");
				recorderState = RecorderState.kRecording;
				initRecorderFile();
				setupRecorderData();
			}
			if (pov == 90) {
				System.out.println("Started Playback");
				recorderState = RecorderState.kPlayback;
				loadPlaybackFile();
			}
			break;
		case kRecording:
			userTeleop();
			recorder.collectAtTime(System.currentTimeMillis());
			
			if (pov == 180) {
				System.out.println("Stopped Recording");
				recorderState = RecorderState.kUser;
				closeRecorderFile();
			}
			break;
		case kPlayback:
			playback();
			break;
		}
	}//teleopPeriodic
	
	public void userTeleop() {
//		System.out.println("Encoder Position: "+drive.getEncoderRight());
		
		ptoShifter.update(); if (ptoShifter.Pressed) { 
			ptoSol.set(ptoSol.get() == DoubleSolenoid.Value.kReverse ? DoubleSolenoid.Value.kForward : DoubleSolenoid.Value.kReverse);
		}
		
		if (drive != null) {
			double x = -joy.getRawAxis(0),
					y = joy.getRawAxis(1);
			
//			x = deadband.get(x);
//			y = deadband.get(y);
			drive.arcadeDrive(y,x);
		}
		
		shifterButton.update();
		if (shifterButton.Pressed)
			shifters.toggle();
		
		if (elevator != null) {
			elevator.update();
		}

		if (squeezy != null) {
			squeezy.poll();
			squeezy.updateState();
		}
		
		if (joy.getPOV() == 90) {
			System.out.println("DOWN!");
			squeezyUpDown.set(DoubleSolenoid.Value.kForward);
		}
		if (joy.getPOV() == 180) {
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
	
	public void initRecorderFile() {
		Calendar today = Calendar.getInstance();
		int month	= today.get(Calendar.MONTH),
			day		= today.get(Calendar.DAY_OF_MONTH),
			year	= today.get(Calendar.YEAR);
		int hour	= today.get(Calendar.HOUR),
			minute	= today.get(Calendar.MINUTE),
			second	= today.get(Calendar.SECOND);

		/* Creates a new directory for the day */
		File date = new File(root, String.format("%d-%d-%d",month,day,year));
		if (date.mkdir())
			System.out.println("Successfully created this date's directory");
		else
			System.out.println("Failed to create this date's directory");

		String fileName = SmartDashboard.getString("DB/String 5", "robotPath");
		if (fileName.equals("")) {
			fileName = "robot_path";
		}
		
		int index = 0;
		File pathFile = new File(date, fileName);
		while (pathFile.exists()) {
			pathFile = new File(date, fileName+"_"+index);
			index++;
		}
		
		SmartDashboard.putString("DB/String 5", pathFile.getName());
		
		recorder = new CSVFileWriter(pathFile);
		
	}//initRecorderFile
	
	public void setupRecorderData() {
		recorder.addLogDouble("joy_x", new LogDouble() {
			public double get() {
				return joy.getRawAxis(0);
			}
		});
		recorder.addLogDouble("joy_y", new LogDouble() {
			public double get() {
				return -joy.getRawAxis(1);
			}
		});
	}//setupRecorderData
	
	public void closeRecorderFile() {
		recorder.writeValuesToFile();
	}//closeRecorderFile
	
	public void loadPlaybackFile() {
		reader = new CSVFileReader(recorderFile);
	}//loadPlaybackFile
	
	public void playback() {
		reader.readLine();
		double x = reader.get("joy_x");
		double y = reader.get("joy_y");
		
		drive.arcadeDrive(y, x);
	}//playback
	
}//Robot
