package com.chs.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.chs.dao.ConceptDictionaryDAO;
import com.chs.entity.ConceptDictionary;
import com.chs.service.ConceptService;

@Service
public class ConceptServiceImpl implements ConceptService{

	
	@Autowired
    private ConceptDictionaryDAO conceptDAO;
    
    
	@Override
	@Transactional
	public List<ConceptDictionary> getAllConcepts() {
		
		return this.conceptDAO.getAllConcepts();
		
	}


	@Override
	public ConceptDictionary getConceptByName(String conceptname) {
		return this.conceptDAO.getConceptByName(conceptname);
	}
	

}
