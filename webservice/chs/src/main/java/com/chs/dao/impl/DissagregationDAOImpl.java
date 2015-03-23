package com.chs.dao.impl;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.chs.dao.DissagregationDAO;
import com.chs.entity.DissagregationDictionary;

@Transactional 
@Repository
public class DissagregationDAOImpl implements DissagregationDAO{

	@Autowired
	private SessionFactory sessionFactory;
	
	@SuppressWarnings("unchecked")
	@Override
	public List<DissagregationDictionary> getAllDissags() {
		
		return this.sessionFactory.getCurrentSession().createQuery("from DissagregationDictionary").list();
		
	}
	
public DissagregationDictionary getDissagregationByName(String name){
		
		Session session = this.sessionFactory.getCurrentSession();
	    Criteria cr = session.createCriteria(DissagregationDictionary.class);
	    cr.add(Restrictions.eq("dissagName", name));
	    DissagregationDictionary result = (DissagregationDictionary)cr.uniqueResult();
	    
	    return result;
	}

}
