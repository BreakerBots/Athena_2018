/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.usfirst.frc.team5104.robot;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;

public class Robot extends IterativeRobot {
	private Joystick controller = new Joystick(0);
	WPI_TalonSRX talon1 = new WPI_TalonSRX(11);
	WPI_TalonSRX talon2 = new WPI_TalonSRX(12);
	WPI_TalonSRX talon3 = new WPI_TalonSRX(13);
	WPI_TalonSRX talon4 = new WPI_TalonSRX(14);
	SpeedControllerGroup m_left = new SpeedControllerGroup(talon1, talon2);
	SpeedControllerGroup m_right = new SpeedControllerGroup(talon3, talon4);
	DifferentialDrive drive = new DifferentialDrive(m_left, m_right);
	ADXRS450_Gyro gyro = new ADXRS450_Gyro();
	double axisY, axisX;
	double TPI = 231.21387283; //Archer
	//double TPI = 190.6005; // Ares ticks per inch
	boolean start;
	int kforward, kturn, stage;
	double curr, encStart, ang, angStart, curPos, curAng;
	SigmSpeed s, a;

	@Override
	public void autonomousInit() {
//		gyro.calibrate();
		talon1.setSelectedSensorPosition(0, 0, 10); 
		talon3.setSelectedSensorPosition(0, 0, 10);
		start = true;
		stage = -1;
		delay(100);
	}

	@Override
	public void autonomousPeriodic() {
		switch(stage) {
		case 0:turn(180);
		break;
		case 1: move(50);
		break;
		case 2: move(-50);
		break;
		case 3: end(); 
		break;
		}
	}

	@Override
	public void teleopPeriodic() {
		axisY = controller.getRawAxis(1);
		axisX = controller.getRawAxis(0);

		drive.arcadeDrive(axisY, axisX);
	}
	
	public void move(double dist) {
		if(start) {
			encStart = talon1.getSelectedSensorPosition(0);
			angStart = gyro.getAngle();
			kforward = (int) (dist/Math.abs(dist));
			dist = Math.abs(dist*TPI);
			s = new SigmSpeed(dist/4, dist/4, dist);
			start = false;		
		}
		dist = Math.abs(dist*TPI);
		curr = Math.abs(talon1.getSelectedSensorPosition(0) - encStart);
		if(curr < dist) {
			System.out.println(curr);
			ang = angStart - gyro.getAngle();
			drive.arcadeDrive(kforward*clamp(s.getSpeed(curr), 0.4, 1), -ang/10);
		} else {
			System.out.println("Next Stage");
			drive.arcadeDrive(0, 0);
			start = true;
			delay(100);
		}
	}
	public void turn(double angle) {
		if(start) {
			angStart = gyro.getAngle();
			kforward = (int) (angle/Math.abs(angle));
			angle = Math.abs(angle);
			s = new SigmSpeed(angle/4, angle/4, angle);
			start = false;		
		}
		angle = Math.abs(angle);
		curr = Math.abs(gyro.getAngle() - angStart);
		if(curr < angle) {
			System.out.println(curr);
			drive.arcadeDrive(0, kforward*clamp(s.getSpeed(curr), 0.4, 1));
		} else {
			System.out.println("Next Stage");
			drive.arcadeDrive(0, 0);
			start = true;
			delay(100);
		}
	}
	
	public void move(double dist, double turn) {
		if(start) {
			encStart = Math.abs(talon1.getSelectedSensorPosition(0));
			angStart = Math.abs(gyro.getAngle());
			dist = dist*TPI + encStart;
			kforward = (int) (dist/Math.abs(dist));
			kturn = (int) (turn/Math.abs(turn));
			s = new SigmSpeed(dist/4, dist/4, dist);
			a = new SigmSpeed(0, turn*0.1, turn);
			start = false;
		}
		dist = Math.abs(dist*TPI);
		turn = Math.abs(turn);
		curPos = Math.abs(talon1.getSelectedSensorPosition(0) - encStart);
		curAng = Math.abs(gyro.getAngle() - angStart);
		if(curPos < dist && curAng < turn) {
			curPos = curPos/Math.tan(turn);
			System.out.println(turn + " " + curAng);
			drive.arcadeDrive(kforward*clamp(s.getSpeed(curPos), 0.4, 1), kturn*clamp(s.getSpeed(curAng), 0.4, 1));
		} else if(curPos < dist) {
			System.out.println(turn + " " + curAng + "**Maintaining Angle");
			ang = angStart - gyro.getAngle();
			drive.arcadeDrive(kforward*clamp(s.getSpeed(curPos), 0.4, 1), -ang/10);
		} else {
			System.out.println("Next Stage");
			drive.arcadeDrive(0, 0);
			start = true;
			delay(100);
		}
	}
	
	
	public void delay(int ms) {
		// delays by ms milliseconds
		int inTime = (int) System.currentTimeMillis();
		int nowTime = (int) System.currentTimeMillis();
		while (nowTime - inTime < ms)
			nowTime = (int) System.currentTimeMillis();
		stage++;
	}
	
	public double clamp(double num, double min, double max) {
		// ensures speed is within min and max
		if (num < min) {
			return min;
		} else if (num > max) {
			return max;
		} else {
			return num;
		}
	} 
	
	public void end() {
		drive.arcadeDrive(0, 0);
	}
}
