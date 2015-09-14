package com.iiitd.form;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.iiitd.chs.Constants;
import com.iiitd.chs.MainActivity;
import com.iiitd.navigationexample.R;
import com.iiitd.networking.Sensor;
import com.iiitd.sensor.pulseox.PulseOxApplication;
import com.iiitd.sqlite.helper.DatabaseHelper;
import com.iiitd.sqlite.model.Patient;
import com.iiitd.sqlite.model.PatientObservation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("deprecation")
public class ObservationActivity extends ActionBarActivity {

	public static final String Tag = "ObservationActivity";

	private CharSequence mTitle;

	private EditText etTemperature, etAllergies;
	private TextView sensor1Txt, sensor2Txt, sensor3Txt;
	String sensor = "default";

	public static final int PULSEOX_VALUE_REQUEST = 1;
	
	Integer s1 = 0, s2 = 0 , s3 = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.observation_layout);
		
		mTitle = getTitle();
		etAllergies = (EditText) findViewById(R.id.Allergies);
		etTemperature = (EditText) findViewById(R.id.Temperature);
		sensor1Txt = (TextView) findViewById(R.id.obs_pulseReading);
		sensor2Txt = (TextView) findViewById(R.id.obs_oxygenReading);
		sensor3Txt = (TextView) findViewById(R.id.obs_series3Reading);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.observation, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void restoreActionBar() {
		ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle(mTitle);
	}
	
	public void addSensor(View v){

		Log.v(Tag,"Add Sensor Pressed");

		Intent intent = new Intent(this, PulseOxApplication.class);
		startActivityForResult(intent, PULSEOX_VALUE_REQUEST);

	}
	
	public void saveObservation(View v){

		Intent intent = getIntent();
//		Integer patient_id = intent.getIntExtra(Patient.PATIENT_ID,0);
		String[] vals = intent.getStringArrayExtra("PATIENT");
		String allergies = etAllergies.getText().toString();
		String temperature = etTemperature.getText().toString();
		
		DatabaseHelper db = new DatabaseHelper(this);
		
		
		int patient_id = (int) db.createPatient(new Patient(vals[0], vals[1], vals[2], vals[3]));
		
		int obs_id = (int)db.addPatientObservation(new PatientObservation(patient_id, temperature, allergies));

		List<String> reading_list = new ArrayList<String>();
		reading_list.add(s1.toString());
		reading_list.add(s2.toString());

		if(sensor.equalsIgnoreCase("bp"))
			reading_list.add(s3.toString());

		Sensor s = new Sensor();
		s.setSensorName(sensor);
		s.setReading(reading_list);
		s.setObsId(obs_id);
		s.setPatientId(patient_id);
		int sensor_id = (int) db.addSensor(s);
		//Create Json to send to hospitals
		JSONObject obj = new JSONObject();
		try {
			obj.put("uuid", vals[0]);
			obj.put("name", vals[1]);
			obj.put("dob", vals[3]);
			obj.put("gender", vals[2]);

			JSONObject obs = new JSONObject();

			obs.put("temperature", Double.parseDouble(temperature));
			obs.put("allergies", allergies);
			obs.put("sensorname", sensor);
			JSONArray sensor_readings = new JSONArray();

			sensor_readings.put(s1);
			sensor_readings.put(s2);

			if(sensor.equalsIgnoreCase("bp"))
				sensor_readings.put(s3);
			obs.put("sensor_readings", sensor_readings);

			obj.put("obs", obs);

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Log.d(Tag,"Publishing Json" + obj.toString());




		String msg = "";
//		for(String s: vals){
//			msg += s + ":";
//		}
//		msg += temperature+":"+allergies+":"+s1+":"+s2;
		msg = obj.toString();
		Log.d(Tag,"Sending Message" + msg);
		//AMQPImpl.addToPublish(msg);
		MainActivity.amqpIntent.putExtra(Constants.AMQP_PUBLISH_MESSAGE, msg);
		startService(MainActivity.amqpIntent);
		//TODO Validate Form
		
		//TODO Save Form into SQLite Database

		System.out.println("ADDING INFO TO DATABASE");
//		Intent backToMainIntent=new Intent("com.hayes.android.MyService");
		Intent backToMainIntent = new Intent(this, MainActivity.class);
		startActivity(backToMainIntent);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    // Check which request we're responding to

	    if (requestCode == PULSEOX_VALUE_REQUEST) {
	        // Make sure the request was successful
	        if (resultCode == RESULT_OK) {
	            // The user picked a correct reading.

				sensor = data.getStringExtra("Sensor");
	        	s1 = data.getIntExtra("Reading1", 0);
	        	s2 = data.getIntExtra("Reading2", 0);
				sensor1Txt.setText(Integer.toString(s1));
				sensor2Txt.setText(Integer.toString(s2));

				if(sensor.equalsIgnoreCase("bp")) {
					s3 = data.getIntExtra("Reading3", 0);
					sensor3Txt.setText(Integer.toString(s3));
				}
				else{
					sensor3Txt.setText("N/A");
				}

	        }
	    }
	}
	
}
