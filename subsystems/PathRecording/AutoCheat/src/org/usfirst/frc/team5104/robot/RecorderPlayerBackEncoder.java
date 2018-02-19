 package org.usfirst.frc.team5104.robot;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import Libraries.ButtonS;
import Libraries.UltraS;
import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import henrylogging.lib.LogValue;
import henrylogging.lib.Logger;

public class RecorderPlayerBackEncoder extends IterativeRobot {
	
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
	
	//Creating Button Classes for State machine control
	ButtonS btnA = new ButtonS(1);
	ButtonS btnB = new ButtonS(2);
	ButtonS btnX = new ButtonS(3);
	
	/*Dynamic array used for recording and playbacks
		- 1800 Long allows for 36 seconds of recording
		- 3 Long for the following
			1) for saving "axisX" of controller
			2) for saving "axisY" of controller
			3) for recording front facing ultrasanic.
	*/
	double recorder[][] = new double[1800][5];
	//Index used for moving through the "recorder" array.
	double index = 0;
	double vSpeed = 10;
	//State variable used for representing posistion in state machine
	int state = 0;
	//Boolean used to not start recording unless there is something to record. (Stops the idle and the beginning of the recording)
	boolean sRcd = false;
	
	//Encoder Variables
	int esumL = 0, esumR = 0;
	int CurrentTick = 0;
	ArrayList<Double> xSpeed = new ArrayList<Double>();
	ArrayList<Double> ySpeed = new ArrayList<Double>();
	
	//Declaring custom ultrasanic object and double for saving it's distance.
	UltraS sanic = new UltraS(1, 0);
	double sanDist;
	
	//Streams used for accessing saved recording and writing to new recording.
	FileOutputStream f;
	ObjectOutputStream o;
	FileInputStream fi;
	ObjectInputStream oi;
	//Path used to declare directory and name of saved recoding file.
	String path = "/media/sda/" + "PathRecording.txt";
	
	public void robotInit() {
		//Initiate custom sanic object
		sanic.RInit();	
		
		logger.logLong("time", new LogValue() { public Object get() { return System.currentTimeMillis(); } });
		logger.logDouble("gyro", new LogValue() {public Object get() {return gyro.getAngle();}});
		logger.logInt("left_encoder", new LogValue() {public Object get() {return talon1.getSelectedSensorPosition(0);}});
		logger.logInt("right_encoder", new LogValue() {public Object get() {return talon3.getSelectedSensorPosition(0);}});
		logger.logDouble("joy_x", new LogValue() {public Object get() {return controller.getRawAxis(0);}});
		logger.logDouble("joy_y", new LogValue() {public Object get() {return controller.getRawAxis(1);}});
		logger.logInt("index", new LogValue() {public Object get() {return index;}});
	}
	
	public void teleopInit() {
		state = 0;
		SmartDashboard.putString("DB/String 0", "PathRecording");
		
		talon1.setSelectedSensorPosition(0, 0, 0);
		talon3.setSelectedSensorPosition(0, 0, 0);
		
		SmartDashboard.putString("DB/String 7", "10");
	}
		
	public void teleopPeriodic() {
		logger.collect();
		
		//Update all buttons and ultrasanic 
		btnA.update(); btnB.update(); btnX.update();
		sanic.Update();
		
		//Convert Sanic Distance To Varible to Reduce Times it is Called.
		sanDist = sanic.getDistance(); 
		
		//Get Name of File from Dashboard and Set it to Save File Location.
		path = "/media/sda/" + SmartDashboard.getString("DB/String 0", "PathRecording") + ".txt";
		
		//Convert State Integer To State Name. -- Change to Enum...
		String stateName = "";
		if (state == 0) { stateName = "Default"; }
		if (state == 1) { stateName = "Recording"; }
		if (state == 2) { stateName = "Playback"; }
		if (state == 3) { stateName = "Reverse Playback"; }
		
		//State Machine
		if (btnA.Pressed) { 
			if (state == 0 || state == 2 || state == 3) { state = 1; recorder = new double[1800][5]; index = 0; sRcd = false; esumL = 0; esumR = 0; xSpeed.clear(); ySpeed.clear();}
			else { state = 0; WriteFile(); }
		}
		if (btnB.Pressed) {
			if (state == 0 || state == 1 || state == 3) { state = 2; index = 0; ReadFile(); esumL = 0; esumR = 0;
				if(state==1){WriteFile();}}
			else { state = 0; }
		}
		if (btnX.Pressed) {
			if (state == 0 || state == 1 || state == 2) { state = 3; ReadFile(); esumL = 0; esumR = 0;
				if(state==1){WriteFile();}}
			else { state = 0; }
		}
		
		
		//Add Time and State To Dashboard.
		SmartDashboard.putString("DB/String 5", stateName);
		SmartDashboard.putString("DB/String 2", "Time: " + index / 50);
		
		vSpeed = Double.parseDouble(SmartDashboard.getString("DB/String 7", "10"));
		
		//vSpeed = SmartDashboard.getNumber("DB/Slider 0", 1);
		
		//State Handling
		if (state == 1) {
			if (index < recorder.length && sRcd) {  
				axisY = -controller.getRawAxis(1) * vSpeed;
				axisX =  controller.getRawAxis(0) * vSpeed;
				
				esumL += talon1.getSelectedSensorPosition(0);  esumR += talon3.getSelectedSensorPosition(0);
				xSpeed.add(axisX / vSpeed); ySpeed.add(axisY / vSpeed);
				
				CurrentTick++; if (CurrentTick >= 1) {
					CurrentTick = 0;
					
					recorder[(int) index][0] = avg(xSpeed);
					recorder[(int) index][1] = avg(ySpeed);
					recorder[(int) index][2] = 0;
					recorder[(int) index][3] = esumL;
					recorder[(int) index][4] = esumR;
					index++;
					
					esumL = 0; esumR = 0; xSpeed.clear(); ySpeed.clear();
				}
				
				
				talon1.set(ControlMode.PercentOutput, (-axisY-axisX)  / talon1.getBusVoltage());
				talon2.set(ControlMode.PercentOutput, (-axisY-axisX)  / talon1.getBusVoltage());
				talon3.set(ControlMode.PercentOutput, ( axisY-axisX)  / talon1.getBusVoltage());
				talon4.set(ControlMode.PercentOutput, ( axisY-axisX)  / talon1.getBusVoltage());
			}
			else if (!sRcd) { 
				if (controller.getRawAxis(1) != 0 && controller.getRawAxis(0) != 0) {
					sRcd = true;
				}
			}
			else { state = 0; }
		}
		else if (state == 0) {
			axisY = -controller.getRawAxis(1) * vSpeed;
			axisX =  controller.getRawAxis(0) * vSpeed;
				
			talon1.set(ControlMode.PercentOutput, (-axisY-axisX)  / talon1.getBusVoltage());
			talon2.set(ControlMode.PercentOutput, (-axisY-axisX)  / talon1.getBusVoltage());
			talon3.set(ControlMode.PercentOutput, ( axisY-axisX)  / talon1.getBusVoltage());
			talon4.set(ControlMode.PercentOutput, ( axisY-axisX)  / talon1.getBusVoltage());
		}
		else if (state == 2) {
			if (index < recorder.length) {
//				if (sanDist < (recorder[(int) index][2] - /* Range */   5   /*  */) && sanDist < 30) {
//					talon1.set(ControlMode.PercentOutput, 0);
//					talon2.set(ControlMode.PercentOutput, 0);
//					talon3.set(ControlMode.PercentOutput, 0);
//					talon4.set(ControlMode.PercentOutput, 0);
//					System.out.println("UltraSanic: Stopped  due to object detection.");
//				}
//				else {
					axisX = recorder[(int) index][0] * vSpeed;
					axisY = recorder[(int) index][1] * vSpeed;
					
					esumL += talon1.getSelectedSensorPosition(0);  esumR += talon3.getSelectedSensorPosition(0);

					if (Math.abs(esumL) >= Math.abs(recorder[(int) index][3])) {
//						talon1.set(ControlMode.PercentOutput, 0);
//						talon2.set(ControlMode.PercentOutput, 0);
					}
					else {
						talon1.set(ControlMode.PercentOutput, (-axisY-axisX)  / talon1.getBusVoltage());
						talon2.set(ControlMode.PercentOutput, (-axisY-axisX)  / talon1.getBusVoltage());
					} 
					if (Math.abs(esumR) >= Math.abs(recorder[(int) index][4])) {
//						talon3.set(ControlMode.PercentOutput, 0);
//						talon4.set(ControlMode.PercentOutput, 0);
					}
					else {
						talon3.set(ControlMode.PercentOutput, ( axisY-axisX)  / talon1.getBusVoltage());
						talon4.set(ControlMode.PercentOutput, ( axisY-axisX)  / talon1.getBusVoltage());   
					}
					
					if ((Math.abs(esumL) >= Math.abs(recorder[(int) index][3])) && (Math.abs(esumR) >= Math.abs(recorder[(int) index][4]))) {
						index++;  
						esumL = 0; esumR = 0;
					}
//				}
			}
			else { state = 0; }
		}
		else if (state == 3) {
			//Deleted
		}
		talon1.setSelectedSensorPosition(0, 0, 0);
		talon3.setSelectedSensorPosition(0, 0, 0);
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
			//System.out.println("Completed Reading from File...");
			fi.close();
			oi.close();
			//---------------------------------------------------
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			//System.out.println("file not found");
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public void WriteFile() {
		//Open
		try {
			//---------------------------------------------------
			f = new FileOutputStream(new File(path));
			o = new ObjectOutputStream(f);
			DoubleArray DA;
			DA = new DoubleArray(recorder);
			o.writeObject(DA);
			//System.out.println("Completed Writing to File...");
			f.close();
			o.close();
			//----------------------------------------------------
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			//System.out.println("file not found");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void disabledInit() {
		logger.log();
	}
	
	private double avg(ArrayList<Double>  avgA) {
		double cAvg = 0;
		for (int i = 0; i < avgA.size(); i++) {
			cAvg += avgA.get(i);
		}
		return cAvg/avgA.size();
	}
}
