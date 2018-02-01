package org.usfirst.frc.team5104.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;

public class AresSimple extends IterativeRobot {
	TalonSRX talon1 = new TalonSRX(11);
	TalonSRX talon2 = new TalonSRX(12);
	TalonSRX talon3 = new TalonSRX(13);
	TalonSRX talon4 = new TalonSRX(14);

	ADXRS450_Gyro gyro = new ADXRS450_Gyro();
	
	double ticks = 20000; // number of ticks
	double position;

	double TPI = 231.21387283; //183; // Archer, Ares ticks per inch
	double startEncoderValue; // compensation
	double startGyroValue;
	
	double currentAng;
	double targetAng;
	
	double maxSpeed = 0.5; // percent of maximum speed
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

		talon1.setSelectedSensorPosition(0, 0, 50);  // trying a 50 ms delay so we reset correctly
		
		System.out.println("***************   Start of auto:  "  + talon1.getSelectedSensorPosition(0));
		stage = 0;
		start = true;
		System.out.println("**********AUTO INIT************");
	}

	// -------------------------------------------------------------

	@Override
	public void autonomousPeriodic() {
		// go through each stage of motion
		// move(24); // outside of the loop
		switch (stage) {
		case 0:
			turn(90);
			break;
		case 1:
			System.out.println("*****STAGE 1*****");
			delay(800);
			break;
		//case 2: 
			//System.out.println("*****STAGE 2*****");
			//move(12);
			//break;
		case 2:
			System.out.println("*****STAGE 3*****");
			turn(-90);
			break;
		case 3:
			end();
			break;
		}
	}

	// ---------------------------------------------------------

	@Override
	public void teleopInit() {
		talon1.setSelectedSensorPosition(0, 0, 0);
		talon3.setSelectedSensorPosition(0, 0, 0);
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

		System.out
				.println("tp 1: " + talon1.getSelectedSensorPosition(0) + " 3: " + talon3.getSelectedSensorPosition(0));
	}

	// -----------------------------------------------------------------------------
	// Moving with no acceleration or deceleration
	
	public void move(double dist) {
		dist = dist * TPI;
		int kforward = (int) (dist/(Math.abs(dist)));; // changes to move forward or backward

		if (start) {
			startEncoderValue = talon1.getSelectedSensorPosition(0);
		s = new SigmSpeed(dist/4, dist/4, dist);
			// straightAngle = gyro.getAngle();
			// comp1 = talon3.getSelectedSensorPosition(0); //resetting position
			// remember to fix roll over
			// accDis = dist/4; //distance to accelerate and decelerate
			start = false;
		}
		if(dist > 0)
			position = Math.abs(talon1.getSelectedSensorPosition(0) - startEncoderValue);
		else
			position = Math.abs(talon1.getSelectedSensorPosition(0) + startEncoderValue);
		if (Math.abs(position) < Math.abs(dist)) {
			// moving with acc/dec
			System.out.println("position (ticks): " + position);
			talon1.set(ControlMode.PercentOutput, -1 * kforward * s.getSpeed(position));
			talon2.set(ControlMode.PercentOutput, -1 * kforward * s.getSpeed(position));
			talon3.set(ControlMode.PercentOutput, kforward * s.getSpeed(position));
			talon4.set(ControlMode.PercentOutput, kforward * s.getSpeed(position));
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
		int kforward = (int) (deg/(Math.abs(deg))); // changes to move forward or backward
		
		maxSpeed = 0.5;
		
		if (start) {
			s = new SigmSpeed(deg/4, deg/4, deg);
			startGyroValue  = gyro.getAngle();
			start = false;
		}
		
		currentAng = Math.abs(gyro.getAngle() - startGyroValue);
		
		if (Math.abs(currentAng) < Math.abs(deg)) {
			// turning with acc/dec
			double speed = kforward * clamp(maxSpeed * s.getSpeed(currentAng), .4, 1);
			System.out.println("angle: " + currentAng + "speed: " + speed);
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
		// System.out.println(stage);
	}

	private void end()  {
		talon1.set(ControlMode.PercentOutput, 0);
		talon2.set(ControlMode.PercentOutput, 0);
		talon3.set(ControlMode.PercentOutput, 0);
		talon4.set(ControlMode.PercentOutput, 0);
	
	}
}
