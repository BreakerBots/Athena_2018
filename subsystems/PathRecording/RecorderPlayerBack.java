package org.usfirst.frc.team5104.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;

public class RecorderPlayerBack extends IterativeRobot {
	TalonSRX talon1 = new TalonSRX(11);
	TalonSRX talon2 = new TalonSRX(12);
	TalonSRX talon3 = new TalonSRX(13);
	TalonSRX talon4 = new TalonSRX(14);

	Joystick controller = new Joystick(0);
	
	double axisY;
	double axisX;
	
	ButtonS btnA = new ButtonS(1);
	ButtonS btnB = new ButtonS(2);
	
	double recorder[][] = new double[1800][2];
	int index = 0;
	int state = 0;
	
	@Override
	public void teleopPeriodic() {
		btnA.update(); btnB.update();
		
		//State Machine
		if (btnA.Pressed) { 
			if (state == 0 || state == 2) { state = 1; recorder = new double[1800][2]; }
			else { state = 0; }
		}
		if (btnB.Pressed) {
			if (state == 0 || state == 1) { state = 2; index = 0; }
			else { state = 0; }
		}
		System.out.println(state);
		
		//State Handling
		if (state == 1) {
			if (index < recorder.length) {
				axisY = controller.getRawAxis(1);
				axisX = controller.getRawAxis(0);
				
				recorder[index][0] = axisX;
				recorder[index][1] = axisY;
				index++;
				
				talon1.set(ControlMode.PercentOutput, -axisY-axisX);
				talon2.set(ControlMode.PercentOutput, -axisY-axisX);
				talon3.set(ControlMode.PercentOutput, axisY-axisX);
				talon4.set(ControlMode.PercentOutput, axisY-axisX);
			}
			else { state = 0; }
		}
		else if (state == 0) {
			axisY = controller.getRawAxis(1);
			axisX = controller.getRawAxis(0);
			
			talon1.set(ControlMode.PercentOutput, -axisY-axisX);
			talon2.set(ControlMode.PercentOutput, -axisY-axisX);
			talon3.set(ControlMode.PercentOutput, axisY-axisX);
			talon4.set(ControlMode.PercentOutput, axisY-axisX);
		}
		else if (state == 2) {
			if (index < recorder.length) {
				//axisX = -recorder[index][0];
				//axisY = -recorder[index][1];
				//index--;
				axisX = recorder[index][0];
				axisY = recorder[index][1];
				index++;
				
				talon1.set(ControlMode.PercentOutput, -axisY-axisX);
				talon2.set(ControlMode.PercentOutput, -axisY-axisX);
				talon3.set(ControlMode.PercentOutput, axisY-axisX);
				talon4.set(ControlMode.PercentOutput, axisY-axisX);
			}
			else { state = 0; }
		}
	}
}
