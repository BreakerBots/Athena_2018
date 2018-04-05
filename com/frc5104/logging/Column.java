package com.frc5104.logging;

import java.util.ArrayList;
import java.util.List;

public class Column {
	public final String name;
	public final LogDouble callback;
	
	public List<Double> values;
	
	public Column (String name, LogDouble callback) {
		this.name = name;
		this.callback = callback;
		
		values = new ArrayList<Double>();
	}//Column
	
	public Column (String name, LogDouble callback, List<Double>values) {
		this.name = name;
		this.callback = callback;
		
		this.values = values;
	}//Column
	
	public String getName() {
		return name;
	}//getName
	
	public LogDouble getCallback() {
		return callback;
	}//getCallback
	
	public void collect() {
		values.add(callback.get());
	}//collect
	
	public int size() {
		return values.size();
	}//age
	
	public double getValue(int index) {
		return values.get(index);
	}//getValue
	
}//Column