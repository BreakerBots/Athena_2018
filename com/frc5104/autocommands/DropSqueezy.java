package com.frc5104.autocommands;

import com.frc5104.main.Robot;
import com.frc5104.main.subsystems.Squeezy;
import com.frc5104.main.subsystems.Squeezy.SqueezyState;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.command.Command;

public class DropSqueezy extends Command {

	DoubleSolenoid squeezy;
	long start;
	
    public DropSqueezy(DoubleSolenoid squeezySol) {
    	squeezy = squeezySol;
    }

    protected void initialize() {
    	squeezy.set(Value.kForward);
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
