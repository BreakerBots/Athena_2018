package com.frc5104.autonomous;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.command.CommandGroup;

public class CenterToLeft extends CommandGroup {
	String gameData; 
	String position = DriverStation.getInstance().getLocation() == 0 ? "L" : (DriverStation.getInstance().getLocation() == 2 ? "R" : "M");
	
    public CenterToLeft() {    	
    	elevator(-5000);
    	move(15);
    	delay(100);
    	turn(-40);
    	delay(100);
    	move(50);
    	delay(100);
    	turn(40);
    	move(25);
    	addSequential(new EjectSqueezy(0.15));
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
