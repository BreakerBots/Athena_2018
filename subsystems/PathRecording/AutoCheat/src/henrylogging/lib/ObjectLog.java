package henrylogging.lib;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class ObjectLog {
	
	String name;

	FileOutputStream fout;
	ObjectOutputStream out;
	
	LogValue log;
	List<Object> data;
	
	public ObjectLog(String fileName, LogValue log) {
		name = fileName;
		this.log = log;
		this.data = new ArrayList<Object>();
	}//LogFile
	
	public void start() {
		if (data != null) data.clear();
		
		data = new ArrayList<Object>();
	}//start
	
	public void collect() {
		data.add(log.get());
	}//collect
	
	public void log(File directory) {
		try {
			File writeFile = new File(directory,name);
			fout = new FileOutputStream(writeFile);
			out = new ObjectOutputStream(fout);
			
			writeData(out);
			System.out.println("Finished writing "+name+" data");
			
			out.close();
			fout.close();
		} catch (IOException e) {
			System.out.println("Failed to write data out");
			e.printStackTrace();
		}
	}//log
	
	public void clear() {
		data.clear();
		System.out.println("Cleared accumulated "+name+" data");
	}//clear
	
	protected void writeData(ObjectOutputStream output) throws IOException {
		output.writeObject("object");
		output.writeObject(data);
	}//writeData
	
}//LogFile
