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
import com.frc5104.main.subsystems.Elevator.Stage;
import com.frc5104.main.subsystems.Squeezy.SqueezyState;
import com.frc5104.utilities.ButtonS;
import com.frc5104.utilities.ControllerHandler;
import com.frc5104.utilities.ControllerHandler.Button;
import com.frc5104.utilities.ControllerHandler.Dpad;
import com.frc5104.utilities.Deadband;
import com.frc5104.utilities.TalonFactory;
import com.frc5104.vision.VisionThread;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.GenericHID.RumbleType;
import edu.wpi.first.wpilibj.command.CommandGroup;
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
	double batteryVoltage;
	
	//------------- Playback ----------------//
	CSVFileReader reader;
	int playbackIndex;
	
	//---------------------------------------//
	
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
	
	public void robotInit() {
		System.out.println("Running Athena Recorder");
		
		if (squeezy != null)
			squeezy.initTable(null);
		
		if (elevator != null)
			elevator.initTable(null);
		
		squeezyUpDown.set(DoubleSolenoid.Value.kReverse);
		
		drive.resetEncoders();
		
	}//robotInit
	
	public void autonomousInit() {

	}//autonomousInit
	
	public void autonomousPeriodic() {
		
	}//autonomousPeriodic
	
	public void teleopInit() {
		if (shifters != null)
			shifters.shiftLow();
		
		recorderState = RecorderState.kUser;
		
		getBatteryVoltage();
		
	}//teleopInit
	
	public void teleopPeriodic() {
		controller.update();
		
		SmartDashboard.putString("DB/String 1", recorderState.toString());
		
//		System.out.println(recorderState.toString() + "\t" + pov);
		
		switch (recorderState) {
		case kUser:
			userTeleop();
			if (controller.getPressed(Button.Y)) {
				System.out.println("Started Recording");
				recorderState = RecorderState.kRecording;
				getBatteryVoltage();
				initRecorderFile();
				setupRecorderData();
			}
			if (controller.getPressed(Button.LIST)) {
				System.out.println("Started Playback");
				recorderState = RecorderState.kPlayback;
				getBatteryVoltage();
				loadPlaybackFile();
				
				playbackIndex = 0;
			}
			break;
		case kRecording:
			userTeleop();
			recorder.collectAtTime(System.currentTimeMillis());
			
			if (controller.getPressed(Button.Y)) {
				System.out.println("Stopped Recording");
				recorderState = RecorderState.kUser;
				closeRecorderFile();
			}
			break;
		case kPlayback:
			if (!playback()) {
				recorderState = RecorderState.kUser;
				getBatteryVoltage();
			}
			break;
		}
	}//teleopPeriodic
	
	public void userTeleop() {
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
			if (!squeezy.isInState(SqueezyState.INTAKE)) {
				System.out.println("UP!");
				squeezyUpDown.set(DoubleSolenoid.Value.kReverse);
			} else {
				System.out.println("Will not pull up squeezy in intake mode!!!");
			}
		}

//		if (Math.abs(drive.getEncoderLeft()+drive.getEncoderRight())/2 > 1300)
//			shifters.shiftHigh();
//		else if (Math.abs(drive.getEncoderLeft()+drive.getEncoderRight())/2 < 800)
//			shifters.shiftLow();

		
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
		recorderFile = pathFile;
		
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
		recorder.addLogDouble("elevator_effort", new LogDouble() {
			public double get() {
				return joy.getRawAxis(3);
			}
		});
		recorder.addLogDouble("buttons", new LogDouble() {
			public double get() {
				for (int i=0; i<Button.values().length; i++)
					if (controller.getPressed(Button.values()[i]))
						return i;
				return -1;
			}
		});
	}//setupRecorderData
	
	public void closeRecorderFile() {
		recorder.writeValuesToFile();
	}//closeRecorderFile
	
	public void loadPlaybackFile() {
		reader = new CSVFileReader(recorderFile);
	}//loadPlaybackFile
	
	public boolean playback() {
		System.out.println("Playback!");
		
		double x = reader.get("joy_x", playbackIndex);
		double y = reader.get("joy_y", playbackIndex);
		double elevatorEffort = reader.get("elevator_effort", playbackIndex);
		
		drive.arcadeDrive(y*10/batteryVoltage, x*10/batteryVoltage);
		elevator.setEffort(elevatorEffort);
		
		playbackIndex++;
		return playbackIndex == reader.size();
	}//playback
	
	private void getBatteryVoltage() {
		drive.arcadeDrive(0, 0);
		
		joy.setRumble(RumbleType.kRightRumble, 1);
		Timer.delay(0.1);
		joy.setRumble(RumbleType.kRightRumble, 0);

		batteryVoltage = DriverStation.getInstance().getBatteryVoltage();
		SmartDashboard.putString("DB/String 4", ""+batteryVoltage);
		System.out.println("Measured Battery Voltage at: "+batteryVoltage);
	}
	
}//Robot
