package com.frc5104.autonomous;

import com.frc5104.main.subsystems.Squeezy;
import com.frc5104.main.subsystems.Squeezy.SqueezyState;

import edu.wpi.first.wpilibj.command.Command;

public class EjectSqueezy extends Command {

	Squeezy squeezy;
	
    public EjectSqueezy() {
    	squeezy = Squeezy.getInstance();
    }

    protected void initialize() {
    	squeezy.forceState(Squeezy.SqueezyState.EJECT);
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
    }

    protected void interrupted() {
    
    }
}
