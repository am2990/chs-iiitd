package com.iiitd.networking;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.BlockingDeque;


public class Sensor implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3999551814311729112L;
	private int id;
	private int obs_id = -1;
	private int patient_id = -1;
	private String sensorName;
	private SensorType sensorType = SensorType.GENERIC;
	private List<String> readings;
	private int readingCount = 0;
	private String datetime;

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
	
	public void setSensorType(String sType){
		this.sensorType = SensorType.valueOf(sType);;
	}
	
	public void setReading(List<String> readings){
		
		this.readings = readings;
	}
	
	public List<String> getReadings(){
		
		return readings;
	}

	public void setReadingCount(int count){
		this.readingCount = count;
	}

	public int getReadingCount(){
		return this.readingCount;
	}

	public void setDatetime(String datetime){
		this.datetime = datetime;
	}

	public String getDatetime(){
		return this.datetime;
	}

	public void setPatientId(int patient_id){
		this.patient_id = patient_id;
	}

	public int getPatientId(){
		return this.patient_id;
	}

	public void setObsId(int obs_id){
		this.obs_id = obs_id;
	}

	public int getObsId(){
		return this.obs_id;
	}

	//TODO add getter and setter for obs_id

	public String toString(){
		return this.getSensorName();
	}


}
