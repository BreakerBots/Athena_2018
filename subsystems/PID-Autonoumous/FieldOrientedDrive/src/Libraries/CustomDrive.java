package Libraries;

import java.util.ArrayList;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

public class CustomDrive {
	ArrayList<TalonSRX> LT = new ArrayList<TalonSRX>();
	ArrayList<TalonSRX> RT = new ArrayList<TalonSRX>();
	
	public CustomDrive(TalonSRX L1, TalonSRX R1) {
		LT.add(L1);
		RT.add(R1);
	}
	public CustomDrive(TalonSRX L1, TalonSRX L2, TalonSRX R1, TalonSRX R2) {
		LT.add(L1);
		RT.add(R1);
		LT.add(L2);
		RT.add(R2);
	}
	public CustomDrive(int L1, int L2, int R1, int R2) {
		LT.add(new TalonSRX(L1));
		RT.add(new TalonSRX(R1));
		LT.add(new TalonSRX(L2));
		RT.add(new TalonSRX(R2));
	}
	public CustomDrive(TalonSRX L1, TalonSRX L2, TalonSRX L3, TalonSRX R1, TalonSRX R2, TalonSRX R3) {
		LT.add(L1);
		RT.add(R1);
		LT.add(L2);
		RT.add(R2);
		LT.add(L3);
		RT.add(R3);
	}
	
	public void arcadeDrive(double move, double angle) {
		for (int i = 0; i < LT.size(); i++) {
			LT.get(i).set(ControlMode.PercentOutput, -move-angle);
		}
		for (int i = 0; i < RT.size(); i++) {
			RT.get(i).set(ControlMode.PercentOutput,  move-angle);
		}
	}
}
