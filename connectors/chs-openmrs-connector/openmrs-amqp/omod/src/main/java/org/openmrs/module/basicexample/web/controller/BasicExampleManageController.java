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
package org.openmrs.module.basicexample.web.controller;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
//import org.openmrs.module.dhisreport.api.utils.Publisher;
//import org.openmrs.module.dhisreport.api.utils.Subscriber;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * The main controller.
 */
@Controller
public class  BasicExampleManageController {
	
	protected final Log log = LogFactory.getLog(getClass());
	
	@RequestMapping(value = "/module/basicexample/manage", method = RequestMethod.GET)
	public void manage(ModelMap model) {
		model.addAttribute("user", Context.getAuthenticatedUser());
	}
	
	@RequestMapping(value = "/module/basicexample/profile", method = RequestMethod.GET)
	public @ResponseBody String processAJAXRequest(
			@RequestParam("firstname") String firstname,
			@RequestParam("lastname") String lastname	) {
		String response = "hello baby";
		System.out.println("my first name--"+firstname);
		
////		Publisher p  = new Publisher("doc_hw", firstname);
////		String[] subs = {"hw_doc", "malaria"};
////		Subscriber s  = new Subscriber(subs);
//		s.start();
//		try {
//			p.Publish();
//		} catch (IOException e) {
//			System.out.println("Error in publish");
//			e.printStackTrace();
//		}
		// Process the request
		// Prepare the response string
		return response;
	}
}
