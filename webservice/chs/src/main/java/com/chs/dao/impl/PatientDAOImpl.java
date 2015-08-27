package com.chs.dao.impl;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.chs.dao.PatientDAO;
import com.chs.entity.PatientRecord;

@Transactional
@Repository
public class PatientDAOImpl implements PatientDAO{

	@Autowired
    private SessionFactory sessionFactory;
	
	
	@Override
	public void addNewPatientRecord(PatientRecord pRecord) {
		
		this.sessionFactory.getCurrentSession().saveOrUpdate(pRecord);
		
	}

	@Override
	public PatientRecord getPatientRecord(String name, String number) {
		
		Session session = this.sessionFactory.getCurrentSession();
	    Criteria cr = session.createCriteria(PatientRecord.class);
	    cr.add(Restrictions.eq("patientName", name)).add(Restrictions.eq("patientCellNumber", number));
	    PatientRecord result = (PatientRecord) cr.uniqueResult();
	    return result;
		
	}

	@Override
	public List<PatientRecord> getPatientRecordByNumber(String number) {
		
		Session session = this.sessionFactory.getCurrentSession();
	    Criteria cr = session.createCriteria(PatientRecord.class);
	    cr.add(Restrictions.eq("patientCellNumber", number));
	    List<PatientRecord> result = (List<PatientRecord>) cr.uniqueResult();
		return result;
	}

}
