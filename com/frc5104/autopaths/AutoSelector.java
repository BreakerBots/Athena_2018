package com.frc5104.autopaths;

import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class AutoSelector {

	public static int kWaitForGameDataMillis = 3000;
	
	public enum Position {
		kLeft, kCenter, kRight
	}
	
	public static volatile String gameData = null;
	public static Position robotPosition;
	//Choose to go for the same side scale over the opposite side switch
	public static boolean userDecision;
	
	public static CommandGroup getAuto(DoubleSolenoid squeezySol) {
		
		CommandGroup auto = new Recording("Baseline");

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
					if (gameData.charAt(0) == 'L')
						System.out.println("Left to Left!");
//						auto = new Recording("Left");
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
		
		if (gameData.charAt(0) == 'L')
			auto = new DropSqueezyRecording("LeftToLeft_NoElevator", squeezySol);
		else 
			auto = new Recording("LeftToLeft_NoElevator");
		
		return auto;
	}//CommandGroup

}//AutoSelector
