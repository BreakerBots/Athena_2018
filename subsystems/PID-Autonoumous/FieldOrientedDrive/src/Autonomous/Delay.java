package Autonomous;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Command;

/**
 *
 */
public class Delay extends Command {
	long startTime;
	int delay;

    public Delay(int milliseconds) {
        delay = milliseconds;
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    	startTime = System.currentTimeMillis();
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
    	System.out.println("Delay");
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
    	return (System.currentTimeMillis() >= startTime + delay);
    }

    // Called once after isFinished returns true
    protected void end() {
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
    }
}
