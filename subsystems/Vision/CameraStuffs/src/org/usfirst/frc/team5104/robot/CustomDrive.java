package org.usfirst.frc.team5104.robot;

import java.util.ArrayList;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

public class CustomDrive {
	ArrayList<TalonSpeedHandler> LT = new ArrayList<TalonSpeedHandler>();
	ArrayList<TalonSpeedHandler> RT = new ArrayList<TalonSpeedHandler>();
	
	public CustomDrive(int L1, int L2, int R1, int R2) {
		LT.add(new TalonSpeedHandler(L1));
		RT.add(new TalonSpeedHandler(R1));
		LT.add(new TalonSpeedHandler(L2));
		RT.add(new TalonSpeedHandler(R2));
	}
	
	public void arcadeDrive(double move, double angle) {
		for (int i = 0; i < LT.size(); i++) {
			LT.get(i).set(-move-angle, 500);
		}
		for (int i = 0; i < RT.size(); i++) {
			RT.get(i).set(move-angle, 500);
		}
	}
	
	public void update() {
		for (int i = 0; i < LT.size(); i++) {
			LT.get(i).update();
		}
		for (int i = 0; i < RT.size(); i++) {
			RT.get(i).update();
		}
	}
}
