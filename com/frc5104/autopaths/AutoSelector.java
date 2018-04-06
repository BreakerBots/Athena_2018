package com.frc5104.autopaths;

import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class AutoSelector {

	public enum AutonomousPaths {
		Baseline (kSwitchEject),
		CenterToLeft_NoElevator(kSwitchEject),
		CenterToRight_NoElevator(kSwitchEject),
		LeftToLeft_NoElevator(kSwitchEject),
		RightToRight_NoElevator(kSwitchEject);
		
		double ejectEffort;
		
		private AutonomousPaths(double ejectEffort) {
			this.ejectEffort = ejectEffort;
		}
		
		public CommandGroup getPath(boolean eject) {
			
			CommandGroup autoPath;
			if (eject) {
				autoPath = new DropSqueezyRecording(toString(), squeezySolenoid, ejectEffort);
			} else {
				autoPath = new Recording(toString());
			}
			
			return autoPath;
		}//getPath
	}//AutonomusPaths
	
	public static int kWaitForGameDataMillis = 3000;
	
	public enum Position {
		kLeft, kCenter, kRight
	}
	
	public static final double kSwitchEject = 0.6;
	public static final double kScaleEject = 1;
	public static DoubleSolenoid squeezySolenoid;
	
	public static volatile String gameData = null;
	public static Position robotPosition;
	//Choose to go for the same side scale over the opposite side switch
	public static boolean userDecision;
	
	public static CommandGroup getAuto(DoubleSolenoid squeezySol) {
		squeezySolenoid = squeezySol;
		
		CommandGroup auto = AutonomousPaths.Baseline.getPath(false);

		Thread gameDataThread = new Thread() {
			public void run() {
				while (!Thread.interrupted()) {
					gameData = DriverStation.getInstance().getGameSpecificMessage();
					if (gameData != null) {
						System.out.println("GameData: "+gameData);
					} else {
						System.out.println("No Game Data");
					}
					if (gameData != null) {
						System.out.println("Got Game Data: "+gameData);
						System.out.println("At: "+DriverStation.getInstance().getMatchTime());
						break;
					}
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		};
		gameDataThread.start();
		
		try {
			gameDataThread.join(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		if (gameData != null) {
			System.out.println("Provided Game Data: "+gameData);
			String position;
			
			position = NetworkTableInstance.getDefault().getTable("Autonomous").getEntry("AutoPos").getString("null");

			if (position.equals("null"))
				position = SmartDashboard.getString("autoposition", "null");

			if (!position.equals("null")) {
				switch (position) {
				case "Left":
					if (gameData.charAt(0) == 'L') {
						System.out.println("Left to Left!");
						auto = AutonomousPaths.LeftToLeft_NoElevator.getPath(false);
					} else if (gameData.charAt(1) == 'L') {
						System.out.println("Left to Left Scale!");
//						auto = AutonomousPaths.LeftToLeftScale.getPath(true);
					}
						
					break;
				case "Center":
					if (gameData.charAt(0) == 'L')
						System.out.println("Center To Left!");
//						auto = new Recording("CenterToLeft");
					else if (gameData.charAt(0) == 'R')
						System.out.println("Center To Right!");
//						auto = new Recording("CenterToRight");
					break;
				case "Right":
					if (gameData.charAt(0) == 'R')
						System.out.println("Right To Right!");
//						auto = new Recording("Right");
					break;
				}
			}
		} else { //else return default auto (new Baseline()) 
			System.out.println("No Game Data Provided!");
		}
		
		String[] paths = {
				"Baseline",
				"CenterToLeft_NoElevator",
				"CenterToRight_NoElevator",
				"LeftToLeft_NoElevator",
				"RightToRight_NoElevator"
		};
		
		//Left Position
		if (gameData.charAt(0) == 'L')
			auto = AutonomousPaths.LeftToLeft_NoElevator.getPath(true);
		else 
			auto = AutonomousPaths.LeftToLeft_NoElevator.getPath(false);

		//Center position
		if (gameData.charAt(0) == 'L')
			auto = AutonomousPaths.CenterToLeft_NoElevator.getPath(true);
		else 
			auto = AutonomousPaths.CenterToRight_NoElevator.getPath(true);

		return auto;
	}//CommandGroup

}//AutoSelector
