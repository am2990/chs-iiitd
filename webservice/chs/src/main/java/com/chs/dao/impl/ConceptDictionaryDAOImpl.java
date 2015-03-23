package com.chs.dao.impl;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.chs.dao.ConceptDictionaryDAO;
import com.chs.entity.ConceptDictionary;

@Transactional 
@Repository
public class ConceptDictionaryDAOImpl implements ConceptDictionaryDAO{

	@Autowired
	private SessionFactory sessionFactory;
	
	
	@SuppressWarnings("unchecked")
	@Override
	public List<ConceptDictionary> getAllConcepts() {
		
		return this.sessionFactory.getCurrentSession().createQuery("from ConceptDictionary").list();
		
	}

	public ConceptDictionary getConceptByName(String name){
		
		Session session = this.sessionFactory.getCurrentSession();
	    Criteria cr = session.createCriteria(ConceptDictionary.class);
	    cr.add(Restrictions.eq("conceptName", name));
	    ConceptDictionary result = (ConceptDictionary)cr.uniqueResult();
	    
	    return result;
	}
}
