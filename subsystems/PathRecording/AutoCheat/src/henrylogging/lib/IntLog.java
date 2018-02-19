package henrylogging.lib;

import java.io.IOException;
import java.io.ObjectOutputStream;

public class IntLog extends ObjectLog{

	public IntLog(String fileName, LogValue log) {
		super(fileName, log);
	}

	public void writeData(ObjectOutputStream output) throws IOException{
		int[] array = new int[data.size()];
		
		for (int i=0; i<data.size(); i++) {
			array[i] = (int) data.get(i);
		}
		
		output.writeObject("int");
		output.writeObject(array);
	}//writeData
	
}//ObjectLog
