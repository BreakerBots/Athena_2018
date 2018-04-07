package com.frc5104.autocommands;

import com.frc5104.main.subsystems.Elevator;
import com.frc5104.main.subsystems.Elevator.Stage;

import edu.wpi.first.wpilibj.command.Command;

public class MoveElevator extends Command {

	Stage stage;
	
    public MoveElevator(Stage stage) {
    	this.stage = stage;
    }//ProcessRecording

    protected void initialize() {
    	System.out.println("FakeElevator");
    	Elevator.getInstance().goTo(stage);
    	Elevator.getInstance().clearIaccum();
    }

    protected void execute() {
    	Elevator.getInstance().goTo(stage);
    }//execute

    protected boolean isFinished() {
    	return Elevator.getInstance().onTarget();
    }//isFinished

    protected void end() {
    	System.out.println("Finished Elevating");
    }

    protected void interrupted() {
    
    }
    
}//getBatteryVoltage
