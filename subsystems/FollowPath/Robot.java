package org.usfirst.frc.team5104.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.IterativeRobot;

public class Robot extends IterativeRobot {
	TalonSRX talon1 = new TalonSRX(11);
	TalonSRX talon2 = new TalonSRX(12);
	TalonSRX talon3 = new TalonSRX(13);
	TalonSRX talon4 = new TalonSRX(14);
	
	ADXRS450_Gyro Gyro = new ADXRS450_Gyro();
	
	double targetAngle = 0;
	double currentAngle;
	
	float rots;
	float TPI = 231.21387283f;
	double speed;

	@Override
	public void teleopInit() {
		talon1.setSelectedSensorPosition(0, 0, 0);
		
		move(90);
		turn(90);
		move(12);
	}
	
	@Override
	public void teleopPeriodic() {
		
	}
	
	public void turn(double degrees) {
		targetAngle -= degrees;
		currentAngle = Gyro.getAngle();

		while (!(Math.abs(targetAngle - currentAngle) < 1)) {
			currentAngle = Gyro.getAngle();
			
			talon1.set(ControlMode.PercentOutput,  -(targetAngle - currentAngle)/40);
			talon2.set(ControlMode.PercentOutput,  -(targetAngle - currentAngle)/40);
			talon3.set(ControlMode.PercentOutput,  -(targetAngle - currentAngle)/40);
			talon4.set(ControlMode.PercentOutput,  -(targetAngle - currentAngle)/40);
		}
	}
	
	public void move(double inches) {
		rots = (Math.abs(talon1.getSelectedSensorPosition(0))/TPI);
		while (rots < inches) { 
			rots = (Math.abs(talon1.getSelectedSensorPosition(0))/TPI);
			speed = getSpeed(rots, inches);
			System.out.println(speed);
			
			talon1.set(ControlMode.PercentOutput,  speed);
			talon2.set(ControlMode.PercentOutput,  speed);
			talon3.set(ControlMode.PercentOutput,  -speed);
			talon4.set(ControlMode.PercentOutput,  -speed);
		}
		talon1.set(ControlMode.PercentOutput,  0);
		talon2.set(ControlMode.PercentOutput,  0);
		talon3.set(ControlMode.PercentOutput,  -0);
		talon4.set(ControlMode.PercentOutput,  -0);
	}
	private double getSpeed(double current, double target) {
		double x;
		if (current < 50) {
			x = current/50;
			return (Math.pow(x-1, 2)/-1.12)+1;
		}
		else if ((target - current) < 50) {
			x = (target - current) / 50;
			return (Math.pow(x-1, 2)/-1.12)+1;
		}
		else {
			return 1;
		}
	}
}
