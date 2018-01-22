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
		
	ADXRS450_Gyro Gyro = new ADXRS450_Gyro();
	
	double targetAngle = 0;
	double currentAngle;
	
	float rots;
	float TPI = 231.21387283f;
	
	double speed;
	boolean driveForward = true;
	
	Joystick controller = new Joystick(0);
	double axisX;
	double axisY;
	
	@Override
	public void teleopInit() {
		talon1.setSelectedSensorPosition(0, 0, 0);
		
		move(30, 0.5);
		move(-30, 0.5);
		/*turn(45, 0.5);
		move(24, 0.5);
		turn(-90, 0.5);
		move(24, 0.5);
		turn(-135, 0.5);
		move(48, 0.5);*/
	}
	
	@Override
	public void teleopPeriodic() {
		axisY = controller.getRawAxis(1);
		axisX = controller.getRawAxis(0);
			
		talon1.set(ControlMode.PercentOutput, -axisY-axisX);
		talon2.set(ControlMode.PercentOutput, -axisY-axisX);
		talon3.set(ControlMode.PercentOutput, axisY-axisX);
		talon4.set(ControlMode.PercentOutput, axisY-axisX);
	}

	//Turning
	public void turn(double degrees, double maxSpeed) {
		if (driveForward) { targetAngle += degrees; }
		else { targetAngle -= degrees; }
		currentAngle = Gyro.getAngle();
		while (!(Math.abs(targetAngle - currentAngle) < 5)) {
			currentAngle = Gyro.getAngle();
			//System.out.println(currentAngle);
			double turnVal = clamp(1, -maxSpeed, maxSpeed);
			
			if (driveForward) { turnVal = -turnVal; }
			
			talon1.set(ControlMode.PercentOutput,  turnVal);
			talon2.set(ControlMode.PercentOutput,  turnVal);
			talon3.set(ControlMode.PercentOutput,  turnVal);
			talon4.set(ControlMode.PercentOutput,  turnVal);
		}
		talon1.set(ControlMode.PercentOutput,  0);
		talon2.set(ControlMode.PercentOutput,  0);
		talon3.set(ControlMode.PercentOutput,  0);
		talon4.set(ControlMode.PercentOutput,  0);
	}
	
	//Moving
	public void move(double inches, double maxSpeed) {
		//talon1.setSelectedSensorPosition(0, 0, 0);
		rots = (Math.abs(talon1.getSelectedSensorPosition(0))/TPI);
		System.out.println(rots);
		if (inches >= 0) {
			float accRots = rots;
			while (rots < inches) { 
				rots = (Math.abs(talon1.getSelectedSensorPosition(0))/TPI) - accRots;
				speed = -clamp(getSpeed(rots, inches, 50, 50),-maxSpeed, maxSpeed);	
				//System.out.println(speed);
				if (driveForward) { speed = -speed; }
				
				talon1.set(ControlMode.PercentOutput,  -speed);
				talon2.set(ControlMode.PercentOutput,  -speed);
				talon3.set(ControlMode.PercentOutput,   speed);
				talon4.set(ControlMode.PercentOutput,   speed);
			}
		}
		else {
			while (rots > inches) { 
				float accRots = (float) inches;
				rots = (Math.abs(talon1.getSelectedSensorPosition(0))/TPI) - accRots;
				speed = clamp(getSpeed(rots, inches, 50, 50),-maxSpeed, maxSpeed);	
				//System.out.println(speed);
				if (driveForward) { speed = -speed; }
				
				talon1.set(ControlMode.PercentOutput,  -speed);
				talon2.set(ControlMode.PercentOutput,  -speed);
				talon3.set(ControlMode.PercentOutput,   speed);
				talon4.set(ControlMode.PercentOutput,   speed);
			}
		}
		talon1.set(ControlMode.PercentOutput,  0);
		talon2.set(ControlMode.PercentOutput,  0);
		talon3.set(ControlMode.PercentOutput,  0);
		talon4.set(ControlMode.PercentOutput,  0); 
	}
	
	private double getSpeed(double current, double target, double accIn, double decIn) {
		double x;
		if (current < accIn) {
			x = current/accIn;
			return (Math.pow(x-1, 2)/-1.12)+1;
		}
		else if ((target - current) < decIn) {
			x = (target - current) / decIn;
			return (Math.pow(x-1, 2)/-1.12)+1;
		}
		else {
			return 1;
		}
	}
	private double clamp(double num, double min, double max) {
		if (num < min) { return min; }
		else if (num > max) { return max; }
		else { return num; }
	}
}
