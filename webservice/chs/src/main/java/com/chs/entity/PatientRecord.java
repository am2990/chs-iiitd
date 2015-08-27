package com.chs.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="patient_record")
public class PatientRecord {
	
	@Id
    @Column(name="id")
    @GeneratedValue
    private Integer id;
	
    @Column(name="p_name")
    private String patientName;
    
    @Column(name="p_uuid")
    private String patientUUID;
    
    @Column(name="p_age")
    private Integer patientAge;
    
    @Column(name="p_gender")
    private String patientGender;
    
    @Column(name="p_cellnumber")
    private String patientCellNumber;
    
    @Column(name="created_at")
    private Date createdAt;
    
    
    public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getpatientName() {
		return patientName;
	}
	public void setpatientName(String patientname) {
		this.patientName = patientname;
	}
	
	public String getpatientUUID() {
		return patientUUID;
	}
	public void setpatientUUID(String patientUUID) {
		this.patientUUID = patientUUID;
	}
	
	public Integer getpatientAge() {
		return patientAge;
	}
	public void setpatientAge(int patientAge) {
		this.patientAge = patientAge;
	}
	
	public String getpatientGender() {
		return patientGender;
	}
	public void setpatientGender(String patientGender) {
		this.patientGender = patientGender;
	}
	
	public String getpatientCellNumber() {
		return patientCellNumber;
	}
	public void setpatientCellNumber(String patientCellNumber) {
		this.patientCellNumber = patientCellNumber;
	}
	
	public Date getcreatedAt() {
		return createdAt;
	}
	public void setcreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

}
