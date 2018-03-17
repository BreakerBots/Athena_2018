package com.frc5104.autopaths;

import java.io.File;

import com.frc5104.autocommands.MoveElevator;
import com.frc5104.autocommands.ProcessRecording;
import com.frc5104.logging.CSVFileReader;
import com.frc5104.main.subsystems.Elevator.Stage;

import edu.wpi.first.wpilibj.command.CommandGroup;

public class Recording extends CommandGroup {
	
	File file;
	CSVFileReader reader;
	
    public Recording(String recordingName) {    	
    	elevator(Stage.kLowerScale);
    	
    	file = new File("/home/lvuser/sanfrancisco/"+recordingName);
    	reader = new CSVFileReader(file);

    	reader.readFile();
    	
    	addSequential(new ProcessRecording(reader));
    }//Recording
    
    public void elevator(Stage stage) {
    	addParallel(new MoveElevator(stage));
    }
    
}//Recording
