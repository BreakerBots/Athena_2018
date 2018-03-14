package com.frc5104.autonomous;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.command.CommandGroup;

public class AutoBasic extends CommandGroup {
	String gameData; 
	String position = DriverStation.getInstance().getLocation() == 0 ? "L" : (DriverStation.getInstance().getLocation() == 2 ? "R" : "M");
	
    public AutoBasic() {    	
    	elevator(-3000);
    	move(70);
    	delay(100);
    	turn(90);
    }
    
    public void move(double inches) {
    	addSequential(new Move(inches));
    }
    
    public void turn(double degrees) {
    	addSequential(new Turn(degrees));
    }
    
    public void elevator(double ticks) {
    	addParallel(new MoveElevator(ticks));
    }
    
    public void delay(int milliseconds) {
	    addSequential(new Delay(milliseconds));
    }
}
