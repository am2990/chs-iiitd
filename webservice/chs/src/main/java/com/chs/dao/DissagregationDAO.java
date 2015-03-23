package com.chs.dao;

import java.util.List;

import com.chs.entity.DissagregationDictionary;

public interface DissagregationDAO {

	 public List<DissagregationDictionary> getAllDissags();
	 
	 public DissagregationDictionary getDissagregationByName(String name);
}
