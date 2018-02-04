//Working movement with encoders
package org.usfirst.frc.team5104.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;

public class Robot extends IterativeRobot {
	TalonSRX talon1 = new TalonSRX(11);
	TalonSRX talon2 = new TalonSRX(12);
	TalonSRX talon3 = new TalonSRX(13);
	TalonSRX talon4 = new TalonSRX(14);

	ADXRS450_Gyro gyro = new ADXRS450_Gyro();
	
	double ticks = 20000; // number of ticks
	double position;

	double TPI = 190.6005; //231.21387283; // Archer, Ares ticks per inch
	double startEncoderValue; // compensation
	double startGyroValue;
	
	double currentAng;
	double targetAng;
	
	double min = 0.2;
	double max = 0.7;
	double maxSpeed = 0.75; // percent of maximum speed
	double pos; // current position of robot
	
	Joystick controller = new Joystick(0);
	double axisX;
	double axisY;

	int stage; // stage of movement
	boolean start=true; // start of stage
	
	SigmSpeed s;

	// ---------------------------------------------------------

	@Override
	public void autonomousInit() {
		// reset stage and encoder

		talon1.setSelectedSensorPosition(0, 0, 10); 
		stage = 0;
		start = true;
		System.out.println("**********AUTO INIT************");
	}

	// -------------------------------------------------------------

	@Override
	public void autonomousPeriodic() {
		// go through each stage of motion - Center to Left path 
		switch (stage) {
		case 0: delay(1000); break;
		case 1: move(50); break;
		case 2: turn(90); break;
		case 3: move(65); break;
		case 4: turn(-90); break;
		case 5: move(55); break;
		case 6: end(); break;
		}
	}

	// ---------------------------------------------------------

	@Override
	public void teleopInit() {
		talon1.setSelectedSensorPosition(0, 0, 10);
		talon3.setSelectedSensorPosition(0, 0, 10);
	}

	// ------------------------------------------------------------------

	@Override
	public void teleopPeriodic() {
		// move with controller
		axisY = controller.getRawAxis(1);
		axisX = controller.getRawAxis(0);

		talon1.set(ControlMode.PercentOutput, axisY - axisX);
		talon2.set(ControlMode.PercentOutput, axisY - axisX);
		talon3.set(ControlMode.PercentOutput, -axisY - axisX);
		talon4.set(ControlMode.PercentOutput, -axisY - axisX);
	}

	// -----------------------------------------------------------------------------
	// Moving with no acceleration or deceleration
	
	public void move(double dist) {
		dist = dist * TPI;
		int kforward = (int) (dist/(Math.abs(dist))); // changes to move forward or backward

		if (start) {
			startEncoderValue = talon1.getSelectedSensorPosition(0);
			s = new SigmSpeed(dist/4, dist/4, dist);
			start = false;
		}
		position = Math.abs(talon1.getSelectedSensorPosition(0) - startEncoderValue);
		if (Math.abs(position) < Math.abs(dist)) {
			// moving with acc/dec
			double speed = clamp(kforward * s.getSpeed(position), min, max);
			talon1.set(ControlMode.PercentOutput, -1 * speed);
			talon2.set(ControlMode.PercentOutput, -1 * speed);
			talon3.set(ControlMode.PercentOutput, speed);
			talon4.set(ControlMode.PercentOutput, speed);
		} else {
			talon1.set(ControlMode.PercentOutput, 0);
			talon2.set(ControlMode.PercentOutput, 0);
			talon3.set(ControlMode.PercentOutput, 0);
			talon4.set(ControlMode.PercentOutput, 0);
			stage++;
			start = true;
		}
	}
	
	//turning
	public void turn(double deg) {
		int kforward = (int) (deg/(Math.abs(deg))); // changes to move left or right
		if (start) {
			s = new SigmSpeed(deg/4, deg/4, deg);
			startGyroValue  = gyro.getAngle();
			start = false;
		}
		
		currentAng = Math.abs(gyro.getAngle() - startGyroValue);
		
		if (Math.abs(currentAng) < Math.abs(deg)) {
			// turning with acc/dec
			double speed = kforward*s.getSpeed(currentAng/deg);
			talon1.set(ControlMode.PercentOutput, speed);
			talon2.set(ControlMode.PercentOutput, speed);
			talon3.set(ControlMode.PercentOutput, speed);
			talon4.set(ControlMode.PercentOutput, speed);
		} else {
			talon1.set(ControlMode.PercentOutput, 0);
			talon2.set(ControlMode.PercentOutput, 0);
			talon3.set(ControlMode.PercentOutput, 0);
			talon4.set(ControlMode.PercentOutput, 0);
			stage++;
			start = true;
		}
	}
		
	// -----------------------------------------------------------

	private double clamp(double num, double min, double max) {
		// ensures speed is within min and max
		if (num < min) {
			return min;
		} else if (num > max) {
			return max;
		} else {
			return num;
		}
	} 
	// -------------------------------------------------------------

	private void delay(int ms) {
		// delays by ms milliseconds
		int inTime = (int) System.currentTimeMillis();
		int nowTime = (int) System.currentTimeMillis();
		while (nowTime - inTime < ms)
			nowTime = (int) System.currentTimeMillis();
		stage++;
	}

	private void end()  {
		talon1.set(ControlMode.PercentOutput, 0);
		talon2.set(ControlMode.PercentOutput, 0);
		talon3.set(ControlMode.PercentOutput, 0);
		talon4.set(ControlMode.PercentOutput, 0);
	
	}
}
