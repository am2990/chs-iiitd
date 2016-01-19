/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.testmodule;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Table;

import org.openmrs.BaseOpenmrsMetadata;
import org.openmrs.BaseOpenmrsObject;

/**
 * It is a model class. It should extend either {@link BaseOpenmrsObject} or {@link BaseOpenmrsMetadata}.
 */

@Entity
@Table(name = "test_module_patients")
public class TestModule extends BaseOpenmrsObject implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue
	@Column(name = "test_id")
	private Integer id;
	
	@Column(name = "test_name")
	private String Name; 
	
	@Column(name = "test_uuid")
	private String Uuid;   
	
	@Column(name = "test_is_visited")
	private Integer isVisited;
	
	@Column(name = "test_notification")
	private String Notification;
	
	@Column(name = "test_observation")
	private String Observation;
	
	@Column(name = "test_gender")
	private String Gender;
	
	@Column(name = "test_dob")
	private String Dob;
	
//	@OneToOne
//	@MapsId
//	@JoinColumn(name = "test_encounter_id")
//	private Encounter encounter;
//	
//	@OneToOne
//	@MapsId
//	@JoinColumn(name = "test_patient_id")
//	private Patient patient;
//	
//	@OneToOne
//	@MapsId
//	@JoinColumn(name = "test_visit_id")
//	private Visit visit;

	public TestModule() {}
	
	@Override
	public Integer getId() {
		return id;
	}
	
	@Override
	public void setId( Integer id ) {
		this.id = id;
	}
	
	public String getName() {
		return Name;
	}
	public void setName( String name ) {
		this.Name = name;
	}
	
	
	public String getUuid() {
		return Uuid;
	}
	public void setUuid( String uuid ) {
		this.Uuid = uuid;
	}
	
	
	public Integer getIsVisited() {
		return isVisited;
	}
	public void setIsVisited( Integer isVisited ) {
		this.isVisited = isVisited;
	}
	
	
	public String getNotification(){
		return this.Notification;
	}
	
	public void setNotification(String notification){
		this.Notification = notification;
	}
	
	
	public String getGender(){
		return this.Gender;
	}
	
	public void setGender(String gender){
		this.Gender = gender;
	}
	
	public String getDob(){
		return this.Dob;
	}
	
	public void setDob(String dob){
		this.Dob = dob;
	}
	
	public String getObservation(){
		return this.Observation;
	}
	
	public void setObservation(String observation){
		this.Observation = observation;
	}
//	public Encounter getEncounter(){
//		return this.encounter;
//	}
//	public void setEncounter(Encounter encounter){
//		this.encounter = encounter;
//	}
//	
//	
//	public Patient getPatient(){
//		return this.patient;
//	}
//	public void setPatient(Patient patient){
//		this.patient = patient;
//	}
//	
//	
//	public Visit getVisit(){
//		return this.visit;
//	}
//	public void setVisit(Visit visit){
//		this.visit = visit;
//	}
}