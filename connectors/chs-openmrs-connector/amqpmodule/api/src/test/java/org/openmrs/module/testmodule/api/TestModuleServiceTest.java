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

import static org.junit.Assert.assertNotNull;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.junit.Test;
import org.openmrs.Encounter;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.Person;
import org.openmrs.PersonName;
import org.openmrs.Visit;
import org.openmrs.api.context.Context;
import org.openmrs.module.testmodule.TestModule;
import org.openmrs.test.BaseModuleContextSensitiveTest;

/**
 * Tests {@link ${TestModuleService}}.
 */
public class  TestModuleServiceTest extends BaseModuleContextSensitiveTest {
	
	@Test
	public void shouldSetupContext() {
		assertNotNull(Context.getService(TestModuleService.class));
		TestModuleService service = Context.getService(TestModuleService.class);
		TestModule person = new TestModule();
		person.setName("herlo");
		person.setIsVisited(0);
		person.setUuid("dsahkdsah");
		person.setDob("21-09-2012");
		person.setGender("Male");
		person.setObservation("list of obs");
		
		System.out.println("Service Info " +  service.toString());

		service.addPerson(person);
		
		
//		Person per = new Person();
//		per.setGender("Male");
//	
//		
//		PersonName pn = new PersonName();
//		pn.setFamilyName("family Name");
//		pn.setGivenName("Given Name");
//		
//		Set<PersonName> pn_set = new TreeSet<PersonName>();
//		pn_set.add(pn);
//		
//		Context.getPersonService().savePerson(per);
//		
//		Patient p = new Patient();
//		p.setNames(pn_set);
//		p.setGender("Male");
//
//
//		PatientIdentifierType identType = Context.getPatientService().getPatientIdentifierType(2);
//		Location location = new Location(1);
//		
//		PatientIdentifier patientIdentifier = new PatientIdentifier("dhsakda-sdaks",
//				identType, location );
//		
//		
//		Set<PatientIdentifier> id_set = new TreeSet<PatientIdentifier>();
//		id_set.add(patientIdentifier);
//		p.setIdentifiers(id_set);
//		
//		Context.getPatientService().savePatient(p);
//		
//		Obs obs = new Obs();
//		obs.setConcept(Context.getConceptService().getConcept(21));
//		obs.setPerson(per);
//		Context.getObsService().saveObs(obs, "new");
//		
//		
//		Encounter e = new Encounter();
//		e.addObs(obs);
//		e.setPatient(p);
//		Context.getEncounterService().saveEncounter(e);
//		
//		Set<Encounter> en_set = new TreeSet<Encounter>();
//		en_set.add(e);
//		Visit v = new Visit();
//		v.setPatient(p);
//		v.setEncounters(en_set);
//		Context.getVisitService().saveVisit(v);
		

	}
}
