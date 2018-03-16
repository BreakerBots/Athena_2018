package com.frc5104.autopaths;

import com.frc5104.autocommands.Delay;
import com.frc5104.autocommands.Move;
import com.frc5104.autocommands.MoveElevator;
import com.frc5104.autocommands.PIDTurn;
import com.frc5104.main.subsystems.Elevator;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.command.CommandGroup;

public class Baseline extends CommandGroup {
	String gameData; 
	String position = DriverStation.getInstance().getLocation() == 0 ? "L" : (DriverStation.getInstance().getLocation() == 2 ? "R" : "M");
	
    public Baseline() {
    	elevator(Elevator.Stage.kLowerScale);
    	move(100);
    }
    
    public void move(double inches) {
    	addSequential(new Move(inches));
    }
    
    public void turn(double degrees) {
    	addSequential(new PIDTurn(degrees));
    }
    
    public void elevator(Elevator.Stage stage) {
    	addParallel(new MoveElevator(stage));
    }
    
    public void delay(int milliseconds) {
	    addSequential(new Delay(milliseconds));
    }
}
