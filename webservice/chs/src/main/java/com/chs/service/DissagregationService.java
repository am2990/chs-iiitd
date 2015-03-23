package com.chs.service;

import java.util.List;

import com.chs.entity.DissagregationDictionary;

public interface DissagregationService {
	
    public List<DissagregationDictionary> getAllDissagregations();

    public DissagregationDictionary getDissagregationByName(String name);
}
