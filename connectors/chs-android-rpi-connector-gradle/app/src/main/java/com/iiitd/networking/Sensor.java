package com.iiitd.networking;

import java.io.Serializable;
import java.util.concurrent.BlockingDeque;


public class Sensor implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3999551814311729112L;
	private int id;
	private String sensorName;
	private SensorType sensorType;
	private BlockingDeque<String> readings;

//	public Sensor(Integer id, String sensorName, SensorType sensorType){
//		this.sensorId = id;
//		this.sensorName = sensorName;
//		this.sensorType = sensorType;
//		
//		readings = new LinkedBlockingDeque<String>();
//		
//	}
	
	public int getId(){
		return id;
	}
	
	public void setId(int id){
		this.id = id;
	}
	
	public String getSensorName(){
		return this.sensorName;
	}
	
	public void setSensorName(String sensorName){
		this.sensorName = sensorName;
	}
	
	public SensorType getSensorType(){
		return this.sensorType;
	}
	
	public void setSensorType(SensorType sType){
		this.sensorType = sType;
	}
	
	public void setReading(BlockingDeque<String> readings){
		
		this.readings = readings;
	}
	
	public BlockingDeque<String> getReadings(){
		
		return readings;
	}
}
