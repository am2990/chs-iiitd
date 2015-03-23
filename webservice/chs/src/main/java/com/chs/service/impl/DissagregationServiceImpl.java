package com.chs.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.chs.dao.DissagregationDAO;
import com.chs.entity.DissagregationDictionary;
import com.chs.service.DissagregationService;

@Service
public class DissagregationServiceImpl implements DissagregationService{

	@Autowired
	private DissagregationDAO dissagregationDAO;
	@Override
	public List<DissagregationDictionary> getAllDissagregations() {
		
		return this.dissagregationDAO.getAllDissags();
	}
	
	public DissagregationDictionary getDissagregationByName(String name){
		return this.dissagregationDAO.getDissagregationByName(name);
	}

	
}
