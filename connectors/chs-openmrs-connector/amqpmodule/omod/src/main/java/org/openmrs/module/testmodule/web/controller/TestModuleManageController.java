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
package org.openmrs.module.testmodule.web.controller;

import java.util.Date;
import java.util.List;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.openmrs.api.context.Context;
import org.openmrs.module.testmodule.TestModule;
import org.openmrs.module.testmodule.api.TestModuleService;
import org.openmrs.module.testmodule.utils.Publisher;
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
public class  TestModuleManageController {
	
	protected final Log log = LogFactory.getLog(getClass());
	
	public static BlockingDeque<String> incoming = new LinkedBlockingDeque<String>();
	
	
	@RequestMapping(value = "/module/testmodule/manage", method = RequestMethod.GET)
	public void manage(ModelMap model) {
//		ApplicationContext ctx = 
//				new AnnotationConfigApplicationContext(ConsumerConfiguration.class);
//		SimpleMessageListenerContainer container = 
//				ctx.getBean(SimpleMessageListenerContainer.class);
//		container.stop();
//		SaveRun sv = new SaveRun();
//		new Thread(sv).start();

//		String msg = incoming.poll();
//        if(msg != null)
//        	System.out.println("pulled to save" + msg);
//        	savePatientToDb(msg);
//		
//		System.out.println("srvice" + service.toString());
//		
//		TestModule person = new TestModule();
//		person.setName("herlo");
//		person.setIsVisited(0);
//		person.setUuid("dsahkdsah");
//		person.setDob("21-09-2012");
//		person.setGender("Male");
//		person.setObservation("list of obs");
//		service.addPerson(person);
//	
		model.addAttribute("user", Context.getAuthenticatedUser());
	}
	
	@RequestMapping(value = "/module/testmodule/patientQueue", method = RequestMethod.GET)
	public void patientQueue(ModelMap model) {
		
		TestModuleService amqpService = Context.getService(TestModuleService.class);
		
		List<TestModule> patients =  amqpService.listPersons();
		model.addAttribute("patients", patients);
		model.addAttribute("user", Context.getAuthenticatedUser());
	}
	
	@RequestMapping(value = "/module/testmodule/patientHistory", method = RequestMethod.GET)
	public void patientHistory(ModelMap model) {
		
		TestModuleService amqpService = Context.getService(TestModuleService.class);
		List<TestModule> patients =  amqpService.listPersonsVisited();
		model.addAttribute("patients", patients);
		model.addAttribute("user", Context.getAuthenticatedUser());
	}
	
	@RequestMapping(value = "/module/testmodule/viewPatient", method = RequestMethod.GET)
	public void viewPatient(ModelMap model, @RequestParam( value = "patient_id", required = false ) 
	Integer patient_id ) 
	{
		
		TestModuleService amqpService = Context.getService(TestModuleService.class);		
		TestModule patient =  amqpService.getPersonById(patient_id);
		String obsp = patient.getObservation();
		JSONObject obs = new JSONObject(obsp);
		int obs_id = obs.getInt("obs_id");
		String allergies = obs.getString("allergies");
		String sensorname = obs.getString("sensorname");
		int temp = obs.getInt("temperature");
		JSONArray sens_arr = new JSONArray(obs.get("sensor_readings").toString());
		model.addAttribute("patient", patient);
		model.addAttribute("allergies", allergies);
		model.addAttribute("sensorname", sensorname);
		model.addAttribute("temp", temp);
		model.addAttribute("obs_id", obs_id);
		model.addAttribute("readings", sens_arr.toString());
	}
	
	
	@RequestMapping(value = "/module/testmodule/profile", method = RequestMethod.GET )
	public @ResponseBody String processAJAXRequest(
		   @RequestParam("obs") String obs, @RequestParam("oid") Integer obs_id) {
		
		System.out.println("Received Notification" + obs);
		System.out.println("Received Notification for Obs Id" + obs_id);

//		TODO Get the name of queue to publish. Name of the queue should come from sender

		String response = obs_id+":"+obs;
		
		Publisher p = new Publisher("doc_hw", response);
		p.Publish();
		
		return response;


	}
	
	@RequestMapping(value = "/module/testmodule/settings", method = RequestMethod.GET)
	public void settings(ModelMap model) {
		System.out.println("\n\n comments:in setting function");
		
		
	}

	@RequestMapping(value = "/module/testmodule/savesettings", method = RequestMethod.GET)
	public String savesettings(@RequestParam( value = "url", required = false ) 
	String url, @RequestParam( value = "username", required = false ) 
	String username , @RequestParam( value = "password", required = false ) 
	String password )
	{
		    //Context.getUserContext().getAllRoles(user);
		   Context.getAdministrationService().addGlobalProperty(username, password);//url
		   Context.getAuthenticatedUser();
			System.out.println("\n\nsavesettings comments:"+url+username+password);
			String response = "sent";
			return response;
		
		
	}
	
//	public void savePatientToDb(String json){
//    	try {
//			
//    		JSONObject obj = new JSONObject(json);
//			String name = obj.getString("name");
//			String uuid = obj.getString("uuid");
//			String dob = obj.getString("dob");
//			String gender = obj.getString("gender");
//			System.out.println("Name:"+name);
//			System.out.println("UUID"+uuid);
//			System.out.println("DOB"+dob);
//			System.out.println("Gender"+gender);
//			JSONObject obs = new JSONObject(obj.get("obs").toString());
//			String allergies = obs.getString("allergies");
//			System.out.println("Allergies"+allergies);
//			String sensorname = obs.getString("sensorname");
//			System.out.println("Sensor Name"+sensorname);
//			int temp = obs.getInt("temperature");
//			System.out.println("Temp"+temp);
//			JSONArray sens_arr = new JSONArray(obs.get("sensor_readings").toString());
//			for(int i = 0 ; i < sens_arr.length(); i++ ){
//				System.out.println(sens_arr.get(i));
//			}
//			
//			TestModule patient = new TestModule();
//			patient.setName(name);
//			patient.setDob(dob);
//			patient.setGender(gender);
//			patient.setUuid(uuid);
//			patient.setIsVisited(0);
//			patient.setObservation(obj.get("obs").toString());
//			TestModuleService service = Context.getService(TestModuleService.class); 
//			service.addPerson(patient);
//			
//		} catch (JSONException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
////	
//    }
//
//	
//	
//	class SaveRun implements Runnable {
//        
//        public void run() {
//        	while(true){
//	            String msg = incoming.poll();
//	            if(msg != null)
//	            	System.out.println("pulled to save" + msg);
//	            	savePatientToDb(msg);
//        	}
//        }
//        
//    }
	
	
	
}
