package org.usfirst.frc.team5104.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;

public class Recorder extends IterativeRobot {
	TalonSRX talon1 = new TalonSRX(11);
	TalonSRX talon2 = new TalonSRX(12);
	TalonSRX talon3 = new TalonSRX(13);
	TalonSRX talon4 = new TalonSRX(14);

	Joystick controller = new Joystick(0);
	
	double axisY;
	double axisX;
	
	double recorder[][] = new double[1800][2];
	
	int index = 0;
	
	boolean hasPrinted = false;
	
	@Override
	public void teleopPeriodic() {
		if (index < 900) {
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
		else { 
			if (!hasPrinted) { 
				for (int i = 0; i < recorder.length; i++) {
			         if (i > 0) {
			            System.out.print(", ");
			         }
			         System.out.print("{" + recorder[i][0] + ", " + recorder[i][1] + "}");
				}
				hasPrinted = true;
				talon1.set(ControlMode.PercentOutput, 0);
				talon2.set(ControlMode.PercentOutput, 0);
				talon3.set(ControlMode.PercentOutput, 0);
				talon4.set(ControlMode.PercentOutput, 0);
			} 
		}
	}
}
