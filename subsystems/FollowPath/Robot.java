//Moves Ares forward and backward with encoder values from 1 talon

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

	// ADXRS450_Gyro gyro = new ADXRS450_Gyro();

	// double targetAngle = 0;
	// double currentAngle = 0;

	double ticks = 20000; // number of ticks
	// double ticks2;
	double position;

	double TPI = 183; // ticks per inch
	double startEncoderValue; // compensation
	// double comp1;

	double maxSpeed = 0.5; // percent of maximum speed
	// double accDis; //distance to accelerate and decelerate
	double pos; // current position of robot

	// double speed;
	// boolean driveForward = true;

	Joystick controller = new Joystick(0);
	double axisX;
	double axisY;

	int stage; //stage of movement
	boolean start; // start of stage

	// ---------------------------------------------------------

	@Override
	public void autonomousInit() {
		// reset stage and encoder
		talon1.setSelectedSensorPosition(0, 0, 0);
		stage = 0;
		start = true;
		System.out.println("**********AUTO INIT************");
	}

	// -------------------------------------------------------------

	@Override
	public void autonomousPeriodic() {
		// go through each stage of motion
		switch(stage) {
		case 0: move(36); break;
		case 1: delay(3000); break;
		case 2: move(-24); break;
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
	// Moving
	public void move(double dist) {
		dist = dist*TPI;
		int kforward = 1; // change to -1 if the robot moves in reverse

		if (start) {
			startEncoderValue = talon1.getSelectedSensorPosition(0);
			// comp1 = talon3.getSelectedSensorPosition(0); //resetting position
			// remember to fix roll over
			// accDis = dist/4; //distance to accelerate and decelerate
			start = false;
		}
		if(dist < 0) {
			kforward = -1;
			dist = Math.abs(dist);
		}

		position = Math.abs(talon1.getSelectedSensorPosition(0) - startEncoderValue);
		if (Math.abs(position) < dist) {
			// moving forward with acc/dec
			position = Math.abs(talon1.getSelectedSensorPosition(0) - startEncoderValue);
			System.out.println("position (ticks): " + position);
			talon1.set(ControlMode.PercentOutput, -1 * kforward * 0.5);
			talon2.set(ControlMode.PercentOutput, -1 * kforward * 0.5);
			talon3.set(ControlMode.PercentOutput, kforward * 0.5);
			talon4.set(ControlMode.PercentOutput, kforward * 0.5);
		} else {
			talon1.set(ControlMode.PercentOutput, 0);
			talon2.set(ControlMode.PercentOutput, 0);
			talon3.set(ControlMode.PercentOutput, 0);
			talon4.set(ControlMode.PercentOutput, 0);
			stage++;
			start = true;
		}
	}

	// ------------------------------------------------------------------------------------
	private double getSpeed(double pos, double target, double acc, double dec) {
		pos = Math.abs(pos);
		target = Math.abs(target);
		acc = Math.abs(acc);
		dec = Math.abs(acc);
		if (pos < acc) {
			double p = pos / acc;
			return clamp((Math.pow(p - 1, 2) / -1.12) + 1, .2, 1);
		} else if ((target - pos) < dec) {
			double p = (target - pos) / dec;
			return 1 - clamp((Math.pow(p, 2) / -1.12) + 1, 0, .8);
		} else {
			return 1;
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
}
