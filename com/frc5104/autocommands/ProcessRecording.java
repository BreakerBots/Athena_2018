package com.frc5104.autocommands;

import com.frc5104.logging.CSVFileReader;
import com.frc5104.main.subsystems.Drive;
import com.frc5104.main.subsystems.Elevator;
import com.frc5104.main.subsystems.Elevator.Control;

import edu.wpi.first.wpilibj.command.Command;

public class ProcessRecording extends Command {

	CSVFileReader src;
	int index;
	
    public ProcessRecording(CSVFileReader reader) {
    	src = reader;
    	index = 0;
    }//ProcessRecording

    protected void initialize() {

    }

    protected void execute() {
    	double x = src.get("x", index);
    	double y = src.get("y", index);
    	
    	boolean squeezyEject = src.get("squeezyEject", index) == 1.0;
    
    	
    	
    	
    	index++;
    }//execute

    protected boolean isFinished() {
    	return index == src.size();
    }//isFinished

    protected void end() {
    	Drive.getInstance().arcadeDrive(0, 0);
    	if (Elevator.getInstance().controlMode() == Control.kEffort)
    		Elevator.getInstance().setEffort(0);
    }

    protected void interrupted() {
    
    }
}
