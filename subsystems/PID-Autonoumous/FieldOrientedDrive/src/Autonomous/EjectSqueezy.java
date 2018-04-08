package Autonomous;

import Subsystems.Squeezy;

import edu.wpi.first.wpilibj.command.Command;

public class EjectSqueezy extends Command {

	Squeezy squeezy = Squeezy.getInstance();
	
    public EjectSqueezy() {

    }

    protected void initialize() {
    	squeezy.forceState(Squeezy.SqueezyState.EJECT);
    }

    protected void execute() {
    	
    }

    protected boolean isFinished() {
        return true;
    }

    protected void end() {
    	
    }

    protected void interrupted() {
    
    }
}
