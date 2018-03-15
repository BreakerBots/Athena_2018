package com.frc5104.autopaths;

import com.frc5104.autocommands.Delay;
import com.frc5104.autocommands.EjectSqueezy;
import com.frc5104.autocommands.Move;
import com.frc5104.autocommands.MoveElevator;
import com.frc5104.autocommands.PIDTurn;
import com.frc5104.main.subsystems.Elevator.Stage;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.command.CommandGroup;

public class Right extends CommandGroup {
	String gameData; 
	String position = DriverStation.getInstance().getLocation() == 0 ? "L" : (DriverStation.getInstance().getLocation() == 2 ? "R" : "M");
	
    public Right() {    	
    	elevator(Stage.kLowerScale);
    	move(100);
    	delay(100);
    	turn(-90);
    	move(20);
    	addSequential(new EjectSqueezy(0.15));
    }
    
    public void move(double inches) {
    	addSequential(new Move(inches));
    }
    
    public void turn(double degrees) {
    	addSequential(new PIDTurn(degrees));
    }
    
    public void elevator(Stage stage) {
    	addParallel(new MoveElevator(stage));
    }
    
    public void delay(int milliseconds) {
	    addSequential(new Delay(milliseconds));
    }
}
