//Encoders
package org.usfirst.frc.team5104.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Timer;

public class Robot extends IterativeRobot {
	TalonSRX talon1 = new TalonSRX(11);
	TalonSRX talon2 = new TalonSRX(12);
	TalonSRX talon3 = new TalonSRX(13);
	TalonSRX talon4 = new TalonSRX(14);
		
	ADXRS450_Gyro Gyro = new ADXRS450_Gyro();
	
	double targetAngle = 0;
	double currentAngle = 0;
	
	double rots;  //number of ticks
	double TPI = 231.21387283;  // ticks per inch
	double comp; //compensation
	
	double speed;
	boolean driveForward = true;
	
	Joystick controller = new Joystick(0);
	double axisX;
	double axisY;
	
	int stage; //stage of movement

//---------------------------------------------------------
	
	@Override
	public void autonomousInit() {
		//reset stage and encoder
		talon1.setSelectedSensorPosition(0, 0, 0);
		stage = 0;
	}
	
	@Override
	public void autonomousPeriodic() {
		//go through each stage of motion
		switch(stage) {
			case 0: move(120); break;
			case 1: delay(3000); break;
			case 2: move(-120); break;
		}
	}
	
//---------------------------------------------------------
	
	
	@Override
	public void teleopInit() {
		talon1.setSelectedSensorPosition(0, 0, 0);
	}
	
	
	//------------------------------------------------------------------
	
	@Override
	public void teleopPeriodic() {
		//move with controller
		axisY = controller.getRawAxis(1);
		axisX = controller.getRawAxis(0);
		
		talon1.set(ControlMode.PercentOutput, -axisY-axisX);
		talon2.set(ControlMode.PercentOutput, -axisY-axisX);
		talon3.set(ControlMode.PercentOutput, axisY-axisX);
		talon4.set(ControlMode.PercentOutput, axisY-axisX);
	}

	
	//-----------------------------------------------------------------------------
	//Moving
	public void move(double dist) {
		comp = talon1.getSelectedSensorPosition(0); //resetting position
		double maxSpeed = 1; //percent of maximum speed 
		double accDis = dist/4; //distance to accelerate and decelerate
		double pos; //current position of robot
		if(dist > 0) {
			//moving forward with acc/dec
			dist = -dist;
			rots = talon1.getSelectedSensorPosition(0) + comp;
			pos = rots/TPI;
			while((rots/TPI) > dist) {
				pos = rots/TPI;
				talon1.set(ControlMode.PercentOutput, maxSpeed*getSpeed(pos, dist, accDis, accDis));
				talon2.set(ControlMode.PercentOutput, maxSpeed*getSpeed(pos, dist, accDis, accDis));
				talon3.set(ControlMode.PercentOutput, -maxSpeed*getSpeed(pos, dist, accDis, accDis));
				talon4.set(ControlMode.PercentOutput, -maxSpeed*getSpeed(pos, dist, accDis, accDis));
				rots = talon1.getSelectedSensorPosition(0);
			}
		} else { 
			//moving backward with acc/dec
			rots = -talon1.getSelectedSensorPosition(0) + comp;
			pos = -rots/TPI;
			System.out.println(rots);
			while((rots/TPI) > dist) {
				pos = -rots/TPI;
				talon1.set(ControlMode.PercentOutput, -maxSpeed*getSpeed(pos, dist, accDis, accDis));
				talon2.set(ControlMode.PercentOutput, -maxSpeed*getSpeed(pos, dist, accDis, accDis));
				talon3.set(ControlMode.PercentOutput, maxSpeed*getSpeed(pos, dist, accDis, accDis));
				talon4.set(ControlMode.PercentOutput, maxSpeed*getSpeed(pos, dist, accDis, accDis));
				rots = -talon1.getSelectedSensorPosition(0) + comp;
			}
		}
		//stop and next stage
		talon1.set(ControlMode.PercentOutput, 0);
		talon2.set(ControlMode.PercentOutput, 0);
		talon3.set(ControlMode.PercentOutput, 0);
		talon4.set(ControlMode.PercentOutput, 0);
		stage++;
	}
	
	//------------------------------------------------------------------------------------
	private double getSpeed(double current, double target, double accIn, double decIn) {
		current = Math.abs(current); 
		target = Math.abs(target);
		accIn = Math.abs(accIn); //inches to accelerate
		decIn = Math.abs(decIn); //inches to decelerate
		double x;
		if (current < accIn) {
			x = current/accIn;
			return clamp((Math.pow(x-1, 2)/-1.12)+1, .1, 1);
			//returns speed from parabola, minimum of 10%
		} else if ((target - current) < decIn) {
			x = (target - current) / decIn;
			return clamp((Math.pow(x-1, 2)/-1.12)+1, .1, 1);
			//returns speed form parabola, minimum of 10%
		} else {
			return 1;
		}
	}
	private double clamp(double num, double min, double max) {
		//ensures speed is within min and max
		if (num < min) { 
			return min; 
		} else if (num > max) { 
			return max; 
		} else { 
			return num; 
		}
	}
	
	private void delay(int ms) {
		//delays by ms milliseconds
		int inTime = (int) System.currentTimeMillis();
		int nowTime = (int) System.currentTimeMillis();
		while(nowTime - inTime < ms) 
			nowTime = (int) System.currentTimeMillis();
		stage++;
		System.out.println(stage);
	}
}
