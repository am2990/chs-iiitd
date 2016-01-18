package com.iiitd.sqlite.model;

import android.provider.BaseColumns;

import java.io.Serializable;

public final class Patient implements BaseColumns , Serializable {

	int id;
	String uuid;
	String name;
	String  gender;
	String  dob;
	String created_at;
	
	public final static String PATIENT_ID= "com.iiitd.model.patient_id";

	public Patient(){
		
	}
	
	public Patient( String uuid, String name, String gender, String dob){
		
		this.uuid = uuid;
		this.name = name;
		this.gender = gender;
		this.dob = dob;
		
	}
	
	public void setGender(String gender){
		this.gender = gender;
	}

	public String getGender(){
		return this.gender;
	}

	public String getUUID() {
		
		return this.uuid;
	}

	public String getName() {
		
		return this.name;
	}

	public String getDob() {
		
		return this.dob;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public Integer getId() {
		return this.id;
	}

	public void setUUID(String uuid) {
		this.uuid = uuid;
	}

	public void setCreatedAt(String created_at) {
		this.created_at = created_at;
	}

	public void setName(String name) {
		this.name = name;
		
	}

	public void setDob(String dob) {
		this.dob = dob;
	}
	
	public String toString(){
		return id + ".  "+ name +"  -  " + created_at;		
	}
}
