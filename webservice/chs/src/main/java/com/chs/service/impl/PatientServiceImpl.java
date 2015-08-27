package com.chs.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.chs.dao.PatientDAO;
import com.chs.entity.PatientRecord;
import com.chs.service.PatientService;

@Service
public class PatientServiceImpl implements PatientService{
	
	@Autowired
    private PatientDAO patientDAO;

	@Override
	public void addNewPatientRecord(PatientRecord pRecord) {
		patientDAO.addNewPatientRecord(pRecord);
	}

	@Override
	public PatientRecord getPatientRecord(String name, String number) {
		return patientDAO.getPatientRecord(name, number);
	}

	@Override
	public List<PatientRecord> getPatientRecordByNumber(String number) {
		return patientDAO.getPatientRecordByNumber(number);
	}

}
