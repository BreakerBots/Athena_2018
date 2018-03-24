package com.frc5104.main;

import java.io.File;
import java.util.Arrays;
import java.util.Calendar;

import com.frc5104.logging.CSVFileReader;
import com.frc5104.logging.CSVFileWriter;
import com.frc5104.logging.LogDouble;
import com.frc5104.main.subsystems.Drive;
import com.frc5104.main.subsystems.Elevator;
import com.frc5104.main.subsystems.Shifters;
import com.frc5104.main.subsystems.Squeezy;
import com.frc5104.main.subsystems.Squeezy.SqueezyState;
import com.frc5104.main.subsystems.SqueezySensors;
import com.frc5104.utilities.ControllerHandler;
import com.frc5104.utilities.ControllerHandler.Control;
import com.frc5104.utilities.Deadband;
import com.frc5104.utilities.HMI;
import com.frc5104.utilities.TalonFactory;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Timer;
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
	
	public double getDriveX() {
		return -Deadband.getDefault().get(controller.getAxis(HMI.kDriveX));
	}//getDriveX

	public double getDriveY() {
		return -Deadband.getDefault().get(controller.getAxis(HMI.kDriveY));
	}//getDriveX
	
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
				boolean success = loadPlaybackFile();

				if (success) {
					System.out.println("Started Playback");
					recorderState = RecorderState.kPlayback;
					getBatteryVoltage();
					
					playbackIndex = 0;
				} else {
					System.out.println("Failed to start playback!");
				}
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
			double x = getDriveX();
			double y = getDriveY();
			
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
		String pathName = SmartDashboard.getString("DB/String 5", "robot_path");
		if (pathName.equals("")) {
			pathName = "robot_path";
		}

		File dir = new File(root, pathName);
		
		if (!dir.exists()) dir.mkdir();
		
		int index = 0;
		File pathFile;
		do {
			pathFile = new File(dir, ""+index);
			index++;
		} while (pathFile.exists());
		
		recorder = new CSVFileWriter(pathFile);
		recorderFile = pathFile;
		
	}//initRecorderFile
	
	public File getPlaybackFile() {
		String pathName = SmartDashboard.getString("DB/String 5", "robot_path");
		if (pathName.equals("")) {
			pathName = "robot_path";
		}
		
		File dir = new File(root, pathName);
		
		if (!dir.exists()) {
			return null;
		}
		
		String[] files = dir.list();
		if (files.length == 0) return null;
		
		Arrays.sort(files);
		System.out.println("Files: ");
		for (String s: files)
			System.out.println(s);
		
		File playbackFile = new File(dir, files[files.length-1]);
		
		return playbackFile;
	}//initPlaybackFile
	
	public void setupRecorderData() {
		recorder.addLogDouble("joy_x", new LogDouble() {
			public double get() {
				return getDriveX();
			}
		});
		recorder.addLogDouble("joy_y", new LogDouble() {
			public double get() {
				return getDriveY();
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
	
	public boolean loadPlaybackFile() {
		File readFile = getPlaybackFile();
		if (readFile == null) {
			System.out.println("No valid file to read from!!!");
			return false;
		} else {
			reader = new CSVFileReader(readFile);
			reader.readFile();
			return true;
		}
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
