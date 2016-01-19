package org.openmrs.module.testmodule.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.openmrs.api.context.Context;
import org.openmrs.module.testmodule.TestModule;
import org.openmrs.module.testmodule.api.TestModuleService;

public class HelloWorldHandler {

	public void handleMessage(byte[] text) {
		String msg = new String(text);
		System.out.println("Received : " + msg);
		savePatientToDb(msg);
//		TestModuleManageController.incoming.add(msg);
	}
	
	public void savePatientToDb(String json){
    	try {
			
    		JSONObject obj = new JSONObject(json);
			String name = obj.getString("name");
			String uuid = obj.getString("uuid");
			String dob = obj.getString("dob");
			String gender = obj.getString("gender");
			System.out.println("Name:"+name);
			System.out.println("UUID"+uuid);
			System.out.println("DOB"+dob);
			System.out.println("Gender"+gender);
			JSONObject obs = new JSONObject(obj.get("obs").toString());
			int obsId = obs.getInt("obs_id");
			System.out.println("OBS ID"+obsId);
			String allergies = obs.getString("allergies");
			System.out.println("Allergies"+allergies);
			String sensorname = obs.getString("sensorname");
			System.out.println("Sensor Name"+sensorname);
			int temp = obs.getInt("temperature");
			System.out.println("Temp"+temp);
			JSONArray sens_arr = new JSONArray(obs.get("sensor_readings").toString());
			for(int i = 0 ; i < sens_arr.length(); i++ ){
				System.out.println(sens_arr.get(i));
			}
			
			TestModule patient = new TestModule();
			patient.setName(name);
			patient.setDob(dob);
			patient.setGender(gender);
			patient.setUuid(uuid);
			patient.setIsVisited(0);
			patient.setObservation(obj.get("obs").toString());
			Context.openSession();
			Context.authenticate("admin", "Admin123");
			TestModuleService service = Context.getService(TestModuleService.class); 
			System.out.println("Service Info " +  service.toString());
			service.addPerson(patient);
			Context.closeSession();
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//	
    }


}
