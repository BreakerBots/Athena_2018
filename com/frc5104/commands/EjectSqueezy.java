package com.frc5104.commands;

import com.frc5104.main.subsystems.Squeezy;

import edu.wpi.first.wpilibj.command.Command;

/**
 *
 */
public class EjectSqueezy extends Command {

	Squeezy squeezy = Squeezy.getInstance();
	
    public EjectSqueezy() {
        // Use requires() here to declare subsystem dependencies
        // eg. requires(chassis);
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    	squeezy.forceState(Squeezy.SqueezyState.EJECT);
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
    	
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
        return true;
    }

    // Called once after isFinished returns true
    protected void end() {
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
    }
}
