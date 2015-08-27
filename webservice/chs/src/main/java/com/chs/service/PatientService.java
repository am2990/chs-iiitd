package com.chs.service;

import java.util.List;

import com.chs.entity.PatientRecord;

public interface PatientService {

	public void addNewPatientRecord(PatientRecord pRecord);

	public PatientRecord getPatientRecord(String name, String number);

	public List<PatientRecord> getPatientRecordByNumber(String number);
}
