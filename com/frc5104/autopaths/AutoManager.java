package com.frc5104.autopaths;

import edu.wpi.first.wpilibj.command.CommandGroup;

public class AutoManager {

	public static CommandGroup getAuto() {
		
		CommandGroup auto = new Baseline();
		
		
		
		return auto;
	}//CommandGroup

}//AutoSelector
