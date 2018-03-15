package com.frc5104.autopaths;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.command.CommandGroup;

public class AutoSelector {

	public static int kWaitForGameDataMillis = 3000;
	
	public enum Position {
		kLeft, kCenter, kRight
	}
	
	public static String gameData;
	public static Position robotPosition;
	//Choose to go for the same side scale over the opposite side switch
	public static boolean userDecision;
	
	public static CommandGroup getAuto() {
		
		CommandGroup auto = new Baseline();

		Thread gameDataThread = new Thread() {
			public void run() {
				while (Thread.interrupted()) {
					gameData = DriverStation.getInstance().getGameSpecificMessage();
					if (!gameData.equals("")) {
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
		
		
		if (!gameDataThread.equals("")) {
			
			
			
		} //else return default auto (new Baseline())
		
		return auto;
	}//CommandGroup

}//AutoSelector
