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
	double axisY;
	double axisX;
	double TPI = 231.21387283; //Archer
	//double TPI = 190.6005; // Ares ticks per inch
	boolean start = true;
	double encStart;
	int kforward;
	double curr;
	SigmSpeed s;
	double ang;
	double angStart;

	@Override
	public void autonomousInit() {
//		gyro.calibrate();
		talon1.setSelectedSensorPosition(0, 0, 10); 
		talon3.setSelectedSensorPosition(0, 0, 10);
		delay(100);
	}

	@Override
	public void autonomousPeriodic() {
		move(-50);
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
			dist = dist*TPI + encStart;
			kforward = (int) (dist/Math.abs(dist));
			start = false;
			s = new SigmSpeed(dist/4, dist/4, dist);
		}
		dist = Math.abs(dist*TPI + encStart);
		curr = Math.abs(talon1.getSelectedSensorPosition(0));
		if(curr < dist) {
			ang = angStart - gyro.getAngle();
			System.out.println(ang);
			drive.arcadeDrive(clamp(s.getSpeed(curr), 0.4, 1), -ang/5);
		} else {
			drive.arcadeDrive(0, 0);
		}
	}
	
	public void delay(int ms) {
		// delays by ms milliseconds
		int inTime = (int) System.currentTimeMillis();
		int nowTime = (int) System.currentTimeMillis();
		while (nowTime - inTime < ms)
			nowTime = (int) System.currentTimeMillis();
	}
	
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
}
