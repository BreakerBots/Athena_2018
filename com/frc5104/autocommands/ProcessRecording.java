package com.frc5104.autocommands;

import com.frc5104.logging.CSVFileReader;
import com.frc5104.main.subsystems.Drive;
import com.frc5104.main.subsystems.Elevator;
import com.frc5104.main.subsystems.Elevator.Stage;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.GenericHID.RumbleType;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Command;

public class ProcessRecording extends Command {

	CSVFileReader src;
	int index;
	
	double batteryVoltage;
	
	//---- Debug ----//
	int totalThisMs = 0,
		   totalDtMs = 0,
		   totalWaitMs = 0;

	public ProcessRecording(CSVFileReader reader) {
		src = reader;
		index = 0;
	}//ProcessRecording

    protected void initialize() {
    	getBatteryVoltage();

    	System.out.println("Reading has "+src.size()+" points.");
    }

    private int last2Time = 0;
    protected void execute() {
    	
		int thisMs = getDeltaTime();
		int dtMs = (int) src.get("time", index);
		if (last2Time != 0) {
			int temp = dtMs;
			dtMs = dtMs - last2Time;
			last2Time = temp;
		} else
			last2Time = dtMs;
    	double x = src.get("joy_x", index);
    	double y = src.get("joy_y", index);
    	
    	int waitMs = dtMs - thisMs;

//    	boolean squeezyEject = src.get("squeezyEject", index) == 1.0;

//    	System.out.printf("Playback! File: %4dms   This: %4dms   Waiting: %dms\n", dtMs, thisMs, waitMs);
    	
    	totalThisMs += thisMs;
    	totalDtMs	+= dtMs;
    	totalWaitMs += waitMs;

		if (waitMs> 0){
			Timer.delay(waitMs/1000.0);
		}
    	
//    	getDeltaTime(); //Reset Delta Time
		lastTime = System.currentTimeMillis();
    	
    	Drive.getInstance().arcadeDrive(y*10/batteryVoltage, x*10/batteryVoltage);
    	
    	index++;
    }//execute

    protected boolean isFinished() {
    	return index == src.size();
    }//isFinished

    protected void end() {
    	System.out.printf("Finished Playback {%s} Count: %3d  Averages{PlaybackDt: %3d  RecordingDt: %3d  WaitDt: %3d\n",
    		src.name(), src.size(),  totalThisMs/src.size(), totalDtMs/src.size(), totalWaitMs/src.size());
    	Drive.getInstance().arcadeDrive(0, 0);
//    	if (Elevator.getInstance().controlMode() == Control.kEffort)
//    		Elevator.getInstance().setEffort(0);
//    	if (elevatorStage != null)
//		Elevator.getInstance().goTo(elevatorStage);
    }

    protected void interrupted() {
    
    }
    
    private void getBatteryVoltage() {
		Drive.getInstance().arcadeDrive(0, 0);
		
		(new Joystick(0)).setRumble(RumbleType.kRightRumble, 1);
		Timer.delay(0.3);
		(new Joystick(0)).setRumble(RumbleType.kRightRumble, 0);

		batteryVoltage = DriverStation.getInstance().getBatteryVoltage();
		System.out.println("Measured Battery Voltage at: "+batteryVoltage);
	}
    
    long lastTime = 0;
	public int getDeltaTime() {
		if (lastTime == 0) lastTime = System.currentTimeMillis();
		long now = System.currentTimeMillis();
		int delta = (int)(now-lastTime);
		lastTime = now;
		return delta;
	}//getDeltaTime
	
}//getBatteryVoltage
