package com.chs.service;

import java.util.List;

import com.chs.entity.ConceptDictionary;


public interface ConceptService {
	
    public List<ConceptDictionary> getAllConcepts();
    public ConceptDictionary getConceptByName(String conceptname);

}
