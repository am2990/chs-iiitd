package com.chs.dao;

import java.util.List;

import com.chs.entity.ConceptDictionary;

public interface ConceptDictionaryDAO {
	
	   public List<ConceptDictionary> getAllConcepts();
	   
	   public ConceptDictionary getConceptByName(String name);

}
