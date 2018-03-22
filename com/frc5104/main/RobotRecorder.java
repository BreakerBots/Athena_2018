package com.frc5104.main;

import java.io.File;
import java.util.Calendar;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.frc5104.logging.CSVFileReader;
import com.frc5104.logging.CSVFileWriter;
import com.frc5104.logging.LogDouble;
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
import edu.wpi.first.wpilibj.GenericHID.RumbleType;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class RobotRecorder extends IterativeRobot {

	static class HMI{
		public static final Control kStartRecording = Control.MENU;
		public static final Control kStopRecording = Control.LIST;
		public static final Control kPlayback = Control.LIST;
		
		public static final Control kDriveX = Control.LX;
		public static final Control kDriveY = Control.LY;
		public static final Control kShift = Control.RT;
		
		public static final Control kPtoButton = Control.X;
		public static final Control kElevator = Control.RY;
		
		public static final Control kSqueezyUp = Control.N;
		public static final Control kSqueezyDown = Control.S;
	}
	
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
			if (controller.getPressed(HMI.kStartRecording)) {
				System.out.println("Started Recording");
				recorderState = RecorderState.kRecording;
				getBatteryVoltage();
				initRecorderFile();
				setupRecorderData();
			}
			if (controller.getPressed(HMI.kPlayback)) {
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
			
			if (controller.getPressed(HMI.kStopRecording)) {
				System.out.println("Stopped Recording");
				recorderState = RecorderState.kUser;
				closeRecorderFile();
			}
			break;
		case kPlayback:
			if (playback()) {
				recorderState = RecorderState.kUser;
				getBatteryVoltage();
			}
			break;
		}
	}//teleopPeriodic
	
	public void userTeleop() {
//		controller.update();
		
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
			double x = controller.getAxis(HMI.kDriveX),
				   y = -controller.getAxis(HMI.kDriveY);
			x = Deadband.getDefault().get(x);
			y = Deadband.getDefault().get(y);
			
			drive.arcadeDrive(y*10/batteryVoltage,x*10/batteryVoltage);
		}
		
		if (controller.getAxis(Control.RT) > 0.6)
			shifters.shiftHigh();
		else
			shifters.shiftLow();
		
		if (elevator != null) {
//			elevator.userControl();
			elevator.setEffort(controller.getAxis(HMI.kElevator));
		}

		if (squeezy != null) {
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
				return Deadband.getDefault().get(controller.getAxis(HMI.kDriveX));
			}
		});
		recorder.addLogDouble("joy_y", new LogDouble() {
			public double get() {
				return -Deadband.getDefault().get(controller.getAxis(HMI.kDriveY));
			}
		});
		recorder.addLogDouble("elevator_effort", new LogDouble() {
			public double get() {
				return controller.getAxis(HMI.kElevator);
			}
		});
//		recorder.addLogDouble("buttons", new LogDouble() {
//			public double get() {
//				for (int i=0; i<Button.values().length; i++)
//					if (controller.getPressed(Button.values()[i]))
//						return i;
//				return -1;
//			}
//		});
	}//setupRecorderData
	
	public void closeRecorderFile() {
		recorder.writeValuesToFile();
	}//closeRecorderFile
	
	public void loadPlaybackFile() {
		reader = new CSVFileReader(recorderFile);
		reader.readFile();
	}//loadPlaybackFile
	
	public boolean playback() {
		System.out.println("Playback!");
		
		double x = reader.get("joy_x", playbackIndex);
		double y = reader.get("joy_y", playbackIndex);
		double elev = reader.get("elevator_effort", playbackIndex);
		
		drive.arcadeDrive(y*10/batteryVoltage, x*10/batteryVoltage);
		elevator.setEffort(elev);
		
		playbackIndex++;

		return playbackIndex == reader.size();
	}//playback
	
	private void getBatteryVoltage() {
		drive.arcadeDrive(0, 0);
		
		controller.rumbleHard(1);
		Timer.delay(0.5);
		controller.rumbleHard(0);

		batteryVoltage = DriverStation.getInstance().getBatteryVoltage();
		SmartDashboard.putString("DB/String 4", ""+batteryVoltage);
		System.out.println("Measured Battery Voltage at: "+batteryVoltage);
	}
	
}//Robot
