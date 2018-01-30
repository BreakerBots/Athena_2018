//encoders for Archer - commented TPI for Ares

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

	double TPI = 231.21387283; //183; // ticks per inch
	double startEncoderValue; // compensation
	
	double currentAng;
	double targetAng;
	
	double maxSpeed = 0.5; // percent of maximum speed
	double pos; // current position of robot

	Joystick controller = new Joystick(0);
	double axisX;
	double axisY;

	int stage; // stage of movement
	boolean start=true; // start of stage

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
		turn(-90);
		/*switch (stage) {
		case 0:
			move(120);
			break;
		case 1:
			delay(800);
			break;
		case 2:
			move(-100);
			break;
		case 3:
			end();
			break;
		}
*/
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
		int kforward = 1; // change to -1 if the robot moves in reverse
		SigmSpeed s = new SigmSpeed(dist/4, dist/4, dist);

		if (start) {
			startEncoderValue = talon1.getSelectedSensorPosition(0);
			// remember to fix roll over
			start = false;
		}
		if (dist < 0) {
			kforward = -1;
			dist = Math.abs(dist);
		}

		position = Math.abs(talon1.getSelectedSensorPosition(0) - startEncoderValue);
		if (Math.abs(position) < dist) {
			// moving forward with acc/dec
			position = Math.abs(talon1.getSelectedSensorPosition(0) - startEncoderValue);
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
		if(start) {
			startEncoderValue = gyro.getAngle(); //resetting angle
			maxSpeed = .5; //percent of maximum speed 
			currentAng = gyro.getAngle(); //current angle of robot
			start = false;
		}
		currentAng = gyro.getAngle();
		if(deg > 0) {
			System.out.println("Pos ang; gyro = " + currentAng);
			targetAng = -deg + startEncoderValue;
			if(currentAng > targetAng) {
				talon1.set(ControlMode.PercentOutput, maxSpeed);
				talon2.set(ControlMode.PercentOutput, maxSpeed);
				talon3.set(ControlMode.PercentOutput, maxSpeed);
				talon4.set(ControlMode.PercentOutput, maxSpeed);
			} else {
				//stop and next stage
				talon1.set(ControlMode.PercentOutput, 0);
				talon2.set(ControlMode.PercentOutput, 0);
				talon3.set(ControlMode.PercentOutput, 0);
				talon4.set(ControlMode.PercentOutput, 0);
				stage++;
			}
		} else { 
			System.out.println("Neg ang; gyro = " + currentAng);
			targetAng = deg - startEncoderValue;
			if(currentAng < Math.abs(targetAng)) {
				talon1.set(ControlMode.PercentOutput, -maxSpeed);
				talon2.set(ControlMode.PercentOutput, -maxSpeed);
				talon3.set(ControlMode.PercentOutput, -maxSpeed);
				talon4.set(ControlMode.PercentOutput, -maxSpeed);
			} else {
				//stop and next stage
				talon1.set(ControlMode.PercentOutput, 0);
				talon2.set(ControlMode.PercentOutput, 0);
				talon3.set(ControlMode.PercentOutput, 0);
				talon4.set(ControlMode.PercentOutput, 0);
				stage++;
			}
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
