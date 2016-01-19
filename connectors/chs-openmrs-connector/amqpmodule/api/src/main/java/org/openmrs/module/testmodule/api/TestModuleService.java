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
package org.openmrs.module.testmodule.api;

import java.util.List;

import org.openmrs.api.OpenmrsService;
import org.openmrs.module.testmodule.TestModule;
import org.springframework.transaction.annotation.Transactional;

/**
 * This service exposes module's core functionality. It is a Spring managed bean which is configured in moduleApplicationContext.xml.
 * <p>
 * It can be accessed only via Context:<br>
 * <code>
 * Context.getService(TestModuleService.class).someMethod();
 * </code>
 * 
 * @see org.openmrs.api.context.Context
 */
@Transactional
public interface TestModuleService extends OpenmrsService {
     
	/*
	 * Add service methods here
	 * 
	 */
	public void addPerson(TestModule person);
	
	public void updatePerson(TestModule p);
    public List<TestModule> listPersons();
    public List<TestModule> listPersonsVisited();
    public TestModule getPersonById(int id);
    public void removePerson(int id);
	
	public String toString();
}