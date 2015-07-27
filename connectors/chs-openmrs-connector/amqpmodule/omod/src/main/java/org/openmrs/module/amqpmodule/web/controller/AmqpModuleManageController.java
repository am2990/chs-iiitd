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
package org.openmrs.module.amqpmodule.web.controller;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.amqpmodule.AmqpModule;
import org.openmrs.module.amqpmodule.api.AmqpService;
import org.openmrs.module.amqpmodule.utils.Publisher;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import ca.uhn.hl7v2.util.MessageQuery.Result;

/**
 * The main controller.
 */
@Controller
public class  AmqpModuleManageController {
	
	protected final Log log = LogFactory.getLog(getClass());
	
	@RequestMapping(value = "/module/amqpmodule/manage", method = RequestMethod.GET)
	public void manage(ModelMap model) {
		
		AmqpService amqpService = Context.getService(AmqpService.class);
//		
		List<AmqpModule> patients =  amqpService.listPersons();
//		AmqpModule amqp = new AmqpModule();
//		amqp.setName("Hello World");
//		amqp.setUuid("fhdskjgjhfgxvxvadmvdf");
//		amqp.setObs("Come visit the hospital");
//		amqpService.addPerson(amqp);
		model.addAttribute("patients", patients);
		model.addAttribute("user", Context.getAuthenticatedUser());
	}
	
	@RequestMapping(value = "/module/amqpmodule/viewPatient", method = RequestMethod.GET)
	public void viewPatient(ModelMap model, @RequestParam( value = "patient_id", required = false ) 
	Integer patient_id ) 
	{
		
		AmqpService amqpService = Context.getService(AmqpService.class);		
		AmqpModule patient =  amqpService.getPersonById(patient_id);

		model.addAttribute("patient", patient);
		model.addAttribute("user", Context.getAuthenticatedUser());
	}
	
	
	@RequestMapping(value = "/module/amqpmodule/profile", method = RequestMethod.GET )
	public @ResponseBody String processAJAXRequest(
			@RequestParam("obs") String obs) {
		
		Publisher p = new Publisher("doc_hw" , obs );
		
		System.out.println("Doctore Response "+obs);
		String response = "sent";
		return response;
	}

}
