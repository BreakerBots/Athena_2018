package com.frc5104.autocommands;

import com.frc5104.main.subsystems.Elevator;

import edu.wpi.first.wpilibj.command.Command;

public class MoveElevator extends Command {
	double target;

    public MoveElevator(double ticks) {
        target = ticks;
    }

    protected void initialize() {
    	Elevator.getInstance().setPosition(target);
    }

    protected void execute() {
    	System.out.println("Elevator Error: " + Elevator.getInstance().getError());
    }

    protected boolean isFinished() {
        return Elevator.getInstance().onTarget();
    }

    protected void end() {

    }

    protected void interrupted() {
    	
    }
}
