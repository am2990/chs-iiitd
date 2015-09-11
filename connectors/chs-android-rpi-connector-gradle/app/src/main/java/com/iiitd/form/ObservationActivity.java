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
import android.widget.Toast;

import com.iiitd.chs.Constants;
import com.iiitd.chs.MainActivity;
import com.iiitd.navigationexample.R;
import com.iiitd.sensor.pulseox.NoninPacket;
import com.iiitd.sensor.pulseox.PulseOxApplication;
import com.iiitd.sqlite.helper.DatabaseHelper;
import com.iiitd.sqlite.model.Patient;
import com.iiitd.sqlite.model.PatientObservation;

@SuppressWarnings("deprecation")
public class ObservationActivity extends ActionBarActivity {

	public static final String Tag = "ObservationActivity";

	private CharSequence mTitle;

	private EditText etTemperature, etAllergies;
	private TextView pulseTxt, oxTxt;
	
	public static final int PULSEOX_VALUE_REQUEST = 1;
	
	int pulse = 0, ox = 0; 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.observation_layout);
		
		mTitle = getTitle();
		etAllergies = (EditText) findViewById(R.id.Allergies);
		etTemperature = (EditText) findViewById(R.id.Temperature);
		pulseTxt = (TextView) findViewById(R.id.obs_pulseReading);
		oxTxt = (TextView) findViewById(R.id.obs_oxygenReading);
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
		
		//TODO  get observation data from the Activity
		Intent intent = getIntent();
		
//		Integer patient_id = intent.getIntExtra(Patient.PATIENT_ID,0);
		String[] vals = intent.getStringArrayExtra("PATIENT");
		String allergies = etAllergies.getText().toString();
		String temperature = etTemperature.getText().toString();
		
		DatabaseHelper db = new DatabaseHelper(this);
		
		
		int patient_id = (int) db.createPatient(new Patient(vals[0], vals[1], vals[2], vals[3]));
		
		db.addPatientObservation(new PatientObservation(patient_id, temperature, allergies));

		String msg = "";
		for(String s: vals){
			msg += s + ":";
		}
		msg += temperature+":"+allergies+":"+pulse+":"+ox;
		//AMQPImpl.addToPublish(msg);
		MainActivity.amqpIntent.putExtra(Constants.AMQP_PUBLISH_MESSAGE, msg);
		startService(MainActivity.amqpIntent);
		//TODO Validate Form
		
		//TODO Save Form into SQLite Database

		Intent backToMainIntent = new Intent(this, MainActivity.class);
		startActivity(backToMainIntent);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    // Check which request we're responding to
    	Toast toast = Toast.makeText(this, "received the intent", Toast.LENGTH_LONG);
    	toast.show();
	    if (requestCode == PULSEOX_VALUE_REQUEST) {
	        // Make sure the request was successful
	        if (resultCode == RESULT_OK) {
	            // The user picked a correct reading.
	        	
	        	ox = data.getIntExtra(NoninPacket.OX, 0);
	        	pulse = data.getIntExtra(NoninPacket.PULSE, 0);
	        	
	        	pulseTxt.setText(Integer.toString(pulse));
	        	oxTxt.setText(Integer.toString(ox));
	        	
//	        	Toast toast2 = Toast.makeText(this, "oxygen :"+ox+"pulse: "+pulse, Toast.LENGTH_LONG);
//	        	toast2.show();
	        }
	    }
	}
	
}
