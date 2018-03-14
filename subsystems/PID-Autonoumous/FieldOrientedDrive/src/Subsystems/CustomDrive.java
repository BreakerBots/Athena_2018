package Subsystems;

import java.util.ArrayList;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

public class CustomDrive {
	static CustomDrive m_instance;
	public static CustomDrive getInstance() {
		if (m_instance == null) {
			m_instance = new CustomDrive();
		}
		return m_instance;
	} private CustomDrive() {  }
	
	TalonSRX[] LT = {new TalonSRX(11), new TalonSRX(12)};
	TalonSRX[] RT = {new TalonSRX(13), new TalonSRX(14)};
	
	public void arcadeDrive(double move, double angle) {
		for (int i = 0; i < LT.length; i++) {
			LT[i].set(ControlMode.PercentOutput, -move-angle);
		}
		for (int i = 0; i < RT.length; i++) {
			RT[i].set(ControlMode.PercentOutput,  move-angle);
		}
	}
}
