package com.chs.dao;

import java.util.List;

import com.chs.entity.PatientRecord;

public interface PatientDAO {
	
	public void addNewPatientRecord(PatientRecord pRecord);
	
	public PatientRecord getPatientRecord(String name, String number);
	
	public List<PatientRecord> getPatientRecordByNumber(String number);

}
