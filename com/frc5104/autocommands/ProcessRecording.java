package com.frc5104.autocommands;

import java.lang.Thread;
import java.lang.InterruptedException;

import com.frc5104.logging.CSVFileReader;
import com.frc5104.main.subsystems.Drive;
import com.frc5104.main.subsystems.Elevator;
import com.frc5104.main.subsystems.Elevator.Control;
import com.frc5104.utilities.ControllerHandler;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.GenericHID.RumbleType;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class ProcessRecording extends Command {

	CSVFileReader src;
	int index;
	
	double batteryVoltage;
	
    public ProcessRecording(CSVFileReader reader) {
    	src = reader;
    	index = 0;
    }//ProcessRecording

    protected void initialize() {
    	getBatteryVoltage();

    	System.out.println("Reading has "+src.size()+" points.");
    }

    private long playbackLastTime = null;
    protected void execute() {
	long dt = (long) reader.get("time", playbackIndex);
    	double x = src.get("joy_x", index);
    	double y = src.get("joy_y", index);
    	
//    	boolean squeezyEject = src.get("squeezyEject", index) == 1.0;

	if (playbackLastTime != null){
		long delay = dt - (now-playbackLastTime);
		if (delay > 0){
			try {
				Thread.sleep(delay);
			} catch (InterruptedException e){
				e.printStackTrace();
			}
		}
	}
	playbackLastTime = now;

    	
    	Drive.getInstance().arcadeDrive(y*10/batteryVoltage, x*10/batteryVoltage);
    	
    	index++;
    }//execute

    protected boolean isFinished() {
    	return index == src.size();
    }//isFinished

    protected void end() {
    	Drive.getInstance().arcadeDrive(0, 0);
    	if (Elevator.getInstance().controlMode() == Control.kEffort)
    		Elevator.getInstance().setEffort(0);
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
}//getBatteryVoltage
