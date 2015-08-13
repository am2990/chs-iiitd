package com.iiitd.sqlite.model;

public final class PatientObservation {
	
	int id;
	int patient_id;
	String temperature;
	String allergies;
	String created_at;
	
	public PatientObservation(){
		
	}
	
	public PatientObservation(int patient_id, String temperature, String allergies){
		this.patient_id = patient_id;
		this.temperature = temperature;
		this.allergies = allergies;
	}
	
	public void setId(int id){
		this.id = id;
	}
	
	public void setPatientId(int patient_id){
		this.patient_id = patient_id;
	}
	
	public void setTemperature(String temperature){
		this.temperature = temperature;
	}
	
	public void setAllergies(String allergies){
		this.allergies = allergies;
	}
	
	public int getId(){
		return this.id;
	}
	
	public int getPatientId(){
		return this.patient_id;
	}
	
	public String getTemperature(){
		return this.temperature;
	}
	
	public String getAllergies(){
		return this.allergies;
	}
	
	public void setCreatedAt(String created_at){
		this.created_at = created_at;
	}
	
	public String getCreatedAt(){
		return this.created_at;
	}
	
}
