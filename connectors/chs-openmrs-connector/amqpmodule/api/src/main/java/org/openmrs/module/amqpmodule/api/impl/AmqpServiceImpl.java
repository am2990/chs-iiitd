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
package org.openmrs.module.amqpmodule.api.impl;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.amqpmodule.AmqpModule;
import org.openmrs.module.amqpmodule.api.AmqpService;
import org.openmrs.module.amqpmodule.api.db.AmqpServiceDAO;
import org.springframework.transaction.annotation.Transactional;

/**
 * It is a default implementation of {@link AmqpService}.
 */
public class AmqpServiceImpl extends BaseOpenmrsService implements AmqpService {
	
	protected final Log log = LogFactory.getLog(this.getClass());
	
	private AmqpServiceDAO dao;
	
	/**
     * @param dao the dao to set
     */
    public void setDao(AmqpServiceDAO dao) {
	    this.dao = dao;
    }
    
    /**
     * @return the dao
     */
    public AmqpServiceDAO getDao() {
	    return dao;
    }
    
    
    @Override
    @Transactional
    public void addPerson(AmqpModule p) {
        this.dao.addPerson(p);
    }
 
    @Override
    @Transactional
    public void updatePerson(AmqpModule p) {
        this.dao.updatePerson(p);
    }
    
    @Override
    @Transactional
    public List<AmqpModule> listPersons() {
        return this.dao.listPersons();
    }
 
    @Override
    @Transactional
    public List<AmqpModule> listPersonsVisited() {
        return this.dao.listPersonsVisited();
    }
    
    @Override
    @Transactional
    public AmqpModule getPersonById(int id) {
        return this.dao.getPersonById(id);
    }
 
    @Override
    @Transactional
    public void removePerson(int id) {
        this.dao.removePerson(id);
    }
    
}