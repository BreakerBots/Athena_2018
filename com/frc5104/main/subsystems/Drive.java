package com.frc5104.main.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Drive {

	static Drive m_instance = null;
	
	public static Drive getInstance() {
		if (m_instance == null) {
			m_instance = new Drive();
		}
		return m_instance;
	}//getInstance

	WPI_TalonSRX talonLeftMain = new WPI_TalonSRX(11),
			 talonRightMain = new WPI_TalonSRX(13),
			 talonLeftFollower = new WPI_TalonSRX(12),
			 talonRightFollower = new WPI_TalonSRX(14);
	
//	SpeedControllerGroup leftDrive = new SpeedControllerGroup(talonLeftMain, talonLeftFollower),
//						rightDrive = new SpeedControllerGroup(talonRightMain, talonRightFollower);
	
	DifferentialDrive drive = new DifferentialDrive(talonLeftMain, talonRightMain);	

	private Drive() {
		drive.setDeadband(0);
//		drive.setExpiration(0.1);
		
		talonLeftFollower.set(ControlMode.Follower, 11);
		talonRightFollower.set(ControlMode.Follower, 13);
		
	}
	
	public void arcadeDrive(double moveVal, double rotateVal) {
		drive.arcadeDrive(moveVal, rotateVal);
	}//arcadeDrive
	
	public void postValuesToNetworkTable() {

	}//postValuesToNetworkTable
	
}//Drive
