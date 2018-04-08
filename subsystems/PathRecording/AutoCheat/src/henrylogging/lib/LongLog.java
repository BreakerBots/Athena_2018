package henrylogging.lib;

import java.io.IOException;
import java.io.ObjectOutputStream;

public class LongLog extends ObjectLog{

	public LongLog(String fileName, LogValue log) {
		super(fileName, log);
	}

	public void writeData(ObjectOutputStream output) throws IOException{
		long[] array = new long[data.size()];
		
		for (int i=0; i<data.size(); i++) {
			array[i] = (long) data.get(i);
		}

		output.writeObject("long");
		output.writeObject(array);
	}//writeData
	
}//ObjectLog
