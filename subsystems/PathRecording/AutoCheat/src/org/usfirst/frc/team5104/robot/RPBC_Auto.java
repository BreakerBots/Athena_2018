package org.usfirst.frc.team5104.robot;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import Libraries.ButtonS;
import Libraries.UltraS;
import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import henrylogging.lib.LogValue;
import henrylogging.lib.Logger;

public class RPBC_Auto extends IterativeRobot {
	
	Logger logger = new Logger("/media/sda/paths");
	ADXRS450_Gyro gyro = new ADXRS450_Gyro();
	
	//Declaring Talons and Setting Device Ids
	TalonSRX talon1 = new TalonSRX(11);
	TalonSRX talon2 = new TalonSRX(12);
	TalonSRX talon3 = new TalonSRX(13);
	TalonSRX talon4 = new TalonSRX(14);

	//Declaring Controller and setting port;
	Joystick controller = new Joystick(0);
	
	//Declaring Variables for controller's joystick's axis'.
	double axisY;
	double axisX;
	
	/*Dynamic array used for recording and playbacks
		- 1800 Long allows for ~36 seconds of recording
		- 3 Long for the following
			1) for saving "axisX" of controller
			2) for saving "axisY" of controller
			3) for recording front facing ultrasanic.
	*/
	double recorder[][] = new double[1800][3];
	//Index used for moving through the "recorder" array.
	double index = 0;
	double vSpeed = 10;
	
	//Declaring custom ultrasanic object and double for saving it's distance.
	UltraS sanic = new UltraS(1, 0);
	double sanDist;
	
	//Streams used for accessing saved recording and writing to new recording.
	FileInputStream fi;
	ObjectInputStream oi;
	
	//Path used to declare directory and name of saved recoding file.
	String path = "/media/sda/" + "PathRecording.txt";
	
	public void robotInit() {
		//Initiate custom sanic object
		sanic.RInit();	
		
		//Set Loggic Values
		logger.logLong("time", new LogValue() { public Object get() { return System.currentTimeMillis(); } });
		logger.logDouble("gyro", new LogValue() {public Object get() {return gyro.getAngle();}});
		logger.logInt("left_encoder", new LogValue() {public Object get() {return talon1.getSelectedSensorPosition(0);}});
		logger.logInt("right_encoder", new LogValue() {public Object get() {return talon3.getSelectedSensorPosition(0);}});
		logger.logDouble("joy_x", new LogValue() {public Object get() {return controller.getRawAxis(0);}});
		logger.logDouble("joy_y", new LogValue() {public Object get() {return controller.getRawAxis(1);}});
		logger.logInt("index", new LogValue() {public Object get() {return index;}});
		
		//Put Down Default Name For File
		SmartDashboard.putString("DB/String 0", "LeftTo");
		SmartDashboard.putString("DB/String 1", "Switch");
		
		//Put down time for a place holder
		SmartDashboard.putString("DB/String 3", "Time: " + index / 50);
	}
	
	public void autonomousInit() {
		String LoadFileName = DriverStation.getInstance().getGameSpecificMessage();
		LoadFileName = "LLL";
		
		System.out.println("Map Is: " + LoadFileName);
		
		//Looking for Scale
		if (SmartDashboard.getString("DB/String 1", "") == "Scale") {
			if (LoadFileName.charAt(1) == 'L') { LoadFileName = "LeftScale"; }
			else { LoadFileName = "RightScale"; }
		}
		//Looking for Switch
		else {
			if (LoadFileName.charAt(0) == 'L') { LoadFileName = "LeftSwitch"; }
			else { LoadFileName = "RightSwitch"; }
		}
		
		System.out.println("Targeting" + LoadFileName);
		
		//Get Name of File from Dashboard and Set it to Save File Location. Then Read From That File.
		path = "/media/sda/" + SmartDashboard.getString("DB/String 0", SmartDashboard.getString("DB/String 0", "") + LoadFileName) + ".txt";		
		ReadFile();
		
		//Start the playback from the beginning.
		index = 0;
	}
		
	public void autonomousPeriodic() {
		//Collect data from the logger
		logger.collect();
		
		//Update all buttons and ultrasanic 
		sanic.Update();
		
		//Convert Sanic Distance To Varible to Reduce Times it is Called.
		sanDist = sanic.getDistance(); 
		
		//Add Current Time To Dashboard
		SmartDashboard.putString("DB/String 3", "Time: " + index / 50);
				
		//Playback
		if (index < recorder.length) {
			if (sanDist < (recorder[(int) index][2] - /* Range */   5   /*  */) && sanDist < 30) {
				talon1.set(ControlMode.PercentOutput, 0);
				talon2.set(ControlMode.PercentOutput, 0);
				talon3.set(ControlMode.PercentOutput, 0);
				talon4.set(ControlMode.PercentOutput, 0);
				System.out.println("UltraSanic: Stopped  due to object detection.");
			}
			else {
				axisX = recorder[(int) index][0] * vSpeed;
				axisY = recorder[(int) index][1] * vSpeed;
				index++;
				
				talon1.set(ControlMode.PercentOutput, (-axisY-axisX)  / talon1.getBusVoltage());
				talon2.set(ControlMode.PercentOutput, (-axisY-axisX)  / talon1.getBusVoltage());
				talon3.set(ControlMode.PercentOutput, ( axisY-axisX)  / talon1.getBusVoltage());
				talon4.set(ControlMode.PercentOutput, ( axisY-axisX)  / talon1.getBusVoltage());   
			}
		}
	}

	public void ReadFile() {
		//Open
		try {
			//---------------------------------------------------
			fi = new FileInputStream(new File(path));
			oi = new ObjectInputStream(fi);
			DoubleArray DA;
			DA = (DoubleArray) oi.readObject();
			recorder = DA.Array;
			System.out.println("Completed Reading from File...");
			fi.close();
			oi.close();
			//---------------------------------------------------
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.out.println("File Not Found, " + "\n" + "Recording may have not been recorded, file may have been named wrong, or the usb may not have been working.");
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public void disabledInit() {
		logger.log();
		index = 0;
	}
}
