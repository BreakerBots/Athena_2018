package com.frc5104.autocommands;

import com.frc5104.main.subsystems.Squeezy;
import com.frc5104.main.subsystems.Squeezy.SqueezyState;

import edu.wpi.first.wpilibj.command.Command;

public class EjectSqueezy extends Command {

	Squeezy squeezy;
	double ejectEffort;
	double previousEffort;
	
    public EjectSqueezy(double squeezyEffort) {
    	squeezy = Squeezy.getInstance();
    	ejectEffort = squeezyEffort;
    }

    protected void initialize() {
    	squeezy.forceState(Squeezy.SqueezyState.EJECT);
    	
    	previousEffort = Squeezy.kEjectEffort;
    	Squeezy.kEjectEffort = ejectEffort;
    }

    protected void execute() {
    	squeezy.update();
    	squeezy.updateState();
    }

    protected boolean isFinished() {
    	
        return squeezy.isInState(SqueezyState.EMPTY) ||
        		squeezy.isInState(SqueezyState.UNJAM) ||
        		squeezy.isInState(SqueezyState.INTAKE);
    }

    protected void end() {
    	squeezy.forceState(SqueezyState.EMPTY);
    	Squeezy.kEjectEffort = previousEffort;
    }

    protected void interrupted() {
    
    }
}
