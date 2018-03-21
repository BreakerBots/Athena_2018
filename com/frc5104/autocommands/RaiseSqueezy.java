package com.frc5104.autocommands;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.command.Command;

public class RaiseSqueezy extends Command {

	DoubleSolenoid squeezy;
	long start;
	
    public RaiseSqueezy(DoubleSolenoid squeezySol) {
    	squeezy = squeezySol;
    }

    protected void initialize() {
    	squeezy.set(Value.kReverse);
    	start = System.currentTimeMillis();
    }

    protected void execute() {
    }

    protected boolean isFinished() {
    	return System.currentTimeMillis()-start>1000;
    }

    protected void end() {
    }

    protected void interrupted() {
    
    }
}
