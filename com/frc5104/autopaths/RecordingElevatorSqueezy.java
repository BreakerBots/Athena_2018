package com.frc5104.autopaths;

import java.io.File;

import com.frc5104.autocommands.DropSqueezy;
import com.frc5104.autocommands.EjectSqueezy;
import com.frc5104.autocommands.MoveElevator;
import com.frc5104.autocommands.ProcessRecording;
import com.frc5104.logging.CSVFileReader;
import com.frc5104.main.subsystems.Elevator.Stage;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.command.CommandGroup;

public class RecordingElevatorSqueezy extends CommandGroup {
	
	File file;
	CSVFileReader reader;
	
    public RecordingElevatorSqueezy(String recordingName, DoubleSolenoid squeezySol, double ejectEffort, Stage stage) {    	
    	
		file = new File("/home/lvuser/good/"+recordingName);
		reader = new CSVFileReader(file);
	
		reader.readFile();
    	
    	elevator(Stage.kBottom);
    	addSequential(new ProcessRecording(reader, Stage.kLowerScale));
    	addSequential(new MoveElevator(stage));
    	addSequential(new DropSqueezy(squeezySol));
    	addSequential(new EjectSqueezy(ejectEffort));
    }//Recording
    
    public void elevator(Stage stage) {
    	addParallel(new MoveElevator(stage));
    }
    
}//Recording
