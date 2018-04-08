package com.frc5104.main;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.frc5104.autopaths.AutoSelector;
import com.frc5104.logging.CSVFileReader;
import com.frc5104.logging.CSVFileWriter;
import com.frc5104.logging.Column;
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

import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.command.Scheduler;
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
	
	Solenoid ptoSol = new Solenoid(4);
	Servo hookHolder = new Servo(0);
	double hookStartingPosition = 0;
	
	DoubleSolenoid squeezyUpDown = new DoubleSolenoid(0, 1);
	
	ControllerHandler controller = ControllerHandler.getInstance();
	
	public void robotInit() {
		System.out.println("Running Athena Recorder");
		
		if (squeezy != null)
			squeezy.initTable(null);
		
		if (elevator != null)
			elevator.initTable(null);
		hookHolder.setPosition(hookStartingPosition);
		
		squeezyUpDown.set(DoubleSolenoid.Value.kReverse);
		
		ptoSol.set(true);
		
//		UsbCamera cam = CameraServer.getInstance().startAutomaticCapture();
//		cam.setResolution(320, 240);
//		cam.setFPS(60);
		HMI.PutOnDashboard();

	}//robotInit
	
	public double getDriveX() {
		return Deadband.getDefault().get(controller.getAxis(HMI.kDriveX));
	}//getDriveX

	public double getDriveY() {
		return -Deadband.getDefault().get(controller.getAxis(HMI.kDriveY));
	}//getDriveX
	
	public void teleopInit() {
		hookHolder.setPosition(hookStartingPosition);
		if (shifters != null)
			shifters.shiftLow();
		
		recorderState = RecorderState.kUser;
		
		ptoSol.set(true);
		
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
//			System.out.println("Recording -- Delta: "+getDeltaTime());
			SmartDashboard.putNumber("recordingDelta", getDeltaTime());
			userTeleop();
			recorder.collectAtTime(System.currentTimeMillis());
			
			if (controller.getPressed(HMI.kStopRecording)) {
				System.out.println("Stopped Recording");
				recorderState = RecorderState.kUser;
//				closeRecorderFile();
				cropRecorderFile();
//				recorder.setFile("/home/lvuser/aresPaths/test");
				closeRecorderFile();
				getBatteryVoltage();
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
	
	long autoStartTime;
	public void autonomousInit() {
		squeezy.forceState(SqueezyState.HOLDING);
		squeezyUpDown.set(Value.kReverse);//UP
		
//		elevator.calibrateBottom();
		
		auto = AutoSelector.getAuto(squeezyUpDown);
		Scheduler.getInstance().add(auto);
		
		hookHolder.setPosition(hookStartingPosition);
		ptoSol.set(true);
		
//		autoStartTime = System.currentTimeMillis();
		HMI.PutOnDashboard();
		
		Timer.delay(1);
		
	}//autonomousInit
	
	public void autonomousPeriodic() {
		Scheduler.getInstance().run();
		squeezy.update();
	}//autonomousPeriodic
	
	public void userTeleop() {
		
//		if (controller.getPressed(Button.LB))
//			elevator.goTo(Stage.kSwitch);
//		else if (controller.getPressed(Button.RB))
//			elevator.goTo(Stage.kTop);
		
//		System.out.println("Encoder Position: "+drive.getEncoderRight());
		if (controller.getHeldEvent(HMI.kPtoHoldAndHookPressButton, 0.4)) { 
			System.out.println("Switching PTO!");
			ptoSol.set(!ptoSol.get());
//			if (ptoSol.get())
//				controller.rumbleSoftFor(0.5, 0.2);
//			else
//				controller.rumbleHardFor(1, 0.2);
		}
		if (!ptoSol.get()) {
			controller.rumbleHardFor(0.7, 0.2);
		}
		if (controller.getPressed(HMI.kOpenHookHolder)) {
			hookHolder.setPosition(1 - hookHolder.getPosition());
		}
		
		if (drive != null) {
			double x = getDriveX();
			double y = getDriveY();
			
			//ONLY DAMPEN DRIVE IF WE ARE RECORDING!!!!
			if (recorderState == RecorderState.kRecording) {
				y *= 10/batteryVoltage;
				x *= 10/batteryVoltage;
			}
			
			drive.arcadeDrive(y, x);
		}
		
		if (controller.getAxis(HMI.kDriveShift) > 0.6)
			shifters.shiftHigh();
		else
			shifters.shiftLow();
		
		if (elevator != null) {
			
//			elevator.setEffort(controller.getAxis(HMI.kElevatorUpDown));
			elevator.userControl();
			
			if (controller.getPressed(HMI.kElevatorDown)) {
//				if (elevator.getStage() == Elevator.Stage.kBottom) {
//					elevator.goTo(Stage.kSwitch);
//				} else
//					elevator.goTo(Stage.kBottom);
				elevator.goDown();
			}
			if (controller.getPressed(HMI.kElevatorUp)) {
				elevator.goUp();
			}
		}

		
		if (controller.getPressed(HMI.kSqueezyDown)) {
			System.out.println("DOWN!");
			squeezyUpDown.set(DoubleSolenoid.Value.kForward);
		}
		if (controller.getPressed(HMI.kSqueezyUp)) {
////			if (!squeezy.isInState(SqueezyState.INTAKE) && !squeezy.isInState(SqueezyState.MANUAL_OPEN)) {
//			if (!squeezy.getOpenLimitSwitch()) {
				System.out.println("UP!");
				squeezyUpDown.set(DoubleSolenoid.Value.kReverse);
//			} else {
//				System.out.println("Will not pull up squeezy if the limit switch is tripped!!!");
//				squeezy.forceState(SqueezyState.HOLDING);
//			}
		}
		
		if (squeezy != null) {
			squeezy.updateState();
			squeezy.update(squeezyUpDown.get() == DoubleSolenoid.Value.kReverse);
		}
	}//teleopPeriodic
	
	public void robotPeriodic() {
		squeezySensors.updateSensors();
		
		squeezy.postData();
		
		elevator.updateTables();
	}//robotPeriodic

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
				return controller.getAxis(HMI.kElevatorUpDown);
			}
		});
		recorder.addLogDouble("buttons", new LogDouble() {
			public double get() {
				for (int i=0; i<Control.values().length; i++)
					if (controller.getPressed(Control.values()[i]))
						return i;
				return -1;
			}
		});
	}//setupRecorderData
	
	public void cropRecorderFile() {
		List<Column> oldData = recorder.getColumns();
		List<Column> newData = new ArrayList<Column>();
		
		boolean flag = true;
		int beginning, end;
		
		beginning = 0;
		while (flag) {
			//Check Joystick X+Y
			Column[] cols = new Column[]{oldData.get(1), oldData.get(2)};
			for (Column c: cols)
				if (c.getValue(beginning) != 0) {
					flag = false;
					break;
				}
			if (flag)
				beginning++;
			if (beginning == cols[0].size())
				flag = false;
		}
		
		end = oldData.get(0).size()-1;
		flag = true;
		while (flag && end >= 0) {
			//Check Joystick X+Y
			Column[] cols = new Column[]{oldData.get(1), oldData.get(2)};
			for (Column c: cols)
				if (c.getValue(end) != 0) {
					flag = false;
					break;
				}
			if (flag)
				end--;
		}
		
		//Transfer over Columns, cropping out bounds
		for (Column c: oldData) {
			List<Double> data = new ArrayList<Double>();
			for (int i=beginning; i<=end; i++)
				data.add(c.getValue(i));
			newData.add(new Column(c.getName(),c.getCallback(),data));
		}
		
		recorder.setColumns(newData);
	}//cropRecorderFile
	
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
	
	int last2Time = 0;
	public boolean playback() {
		int thisMs = getDeltaTime();
		
		int dtMs = (int) reader.get("time", playbackIndex);
		if (last2Time != 0) {
			int temp = dtMs;
			dtMs = dtMs - last2Time;
			last2Time = temp;
		} else
			last2Time = dtMs;
		double x = reader.get("joy_x", playbackIndex);
		double y = reader.get("joy_y", playbackIndex);
		double elev = reader.get("elevator_effort", playbackIndex);
		
		int waitMs = dtMs - thisMs;
		
//		System.out.printf("Playback! File: %4dms   This: %4dms   Waiting: %4dms\n", dtMs, thisMs, waitMs);
		
		if (waitMs> 0){
			Timer.delay(waitMs/1000.0);
		}
		
		getDeltaTime(); //Reset Delta Time

		drive.arcadeDrive(y*10/batteryVoltage, x*10/batteryVoltage);
		boolean driveElevator = false;
		if (driveElevator)
			elevator.setEffort(elev);
		
		playbackIndex++;

		return playbackIndex == reader.size();
	}//playback
	
	long lastTime = 0;
	public int getDeltaTime() {
		if (lastTime == 0) lastTime = System.currentTimeMillis();
		long now = System.currentTimeMillis();
		int delta = (int)(now-lastTime);
		lastTime = now;
		return delta;
	}//getDeltaTime
	
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
