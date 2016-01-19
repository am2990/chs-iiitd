/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.testmodule.api.impl;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.testmodule.TestModule;
import org.openmrs.module.testmodule.api.TestModuleService;
import org.openmrs.module.testmodule.api.db.TestModuleDAO;
import org.springframework.transaction.annotation.Transactional;

/**
 * It is a default implementation of {@link TestModuleService}.
 */
public class TestModuleServiceImpl extends BaseOpenmrsService implements TestModuleService {
	
	protected final Log log = LogFactory.getLog(this.getClass());
	
	
	private TestModuleDAO dao;
	
	/**
     * @param dao the dao to set
     */
    public void setDao(TestModuleDAO dao) {
	    this.dao = dao;
    }
    
    /**
     * @return the dao
     */
    public TestModuleDAO getDao() {
	    return dao;
    }
    
    
    @Override
    @Transactional
    public void addPerson(TestModule p) {
        this.dao.addPerson(p);
    }
 
    
    @Override
    @Transactional
    public void updatePerson(TestModule p) {
        this.dao.updatePerson(p);
    }
    
    @Override
    @Transactional
    public List<TestModule> listPersons() {
        return this.dao.listPersons();
    }
 
    @Override
    @Transactional
    public List<TestModule> listPersonsVisited() {
        return this.dao.listPersonsVisited();
    }
    
    @Override
    @Transactional
    public TestModule getPersonById(int id) {
        return this.dao.getPersonById(id);
    }
 
    @Override
    @Transactional
    public void removePerson(int id) {
        this.dao.removePerson(id);
    }
 
    
    public String toString(){
    	return "Test Module Service";
    }
}