package com.iiitd.chs;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.iiitd.navigationexample.R;
import com.iiitd.sqlite.helper.DatabaseHelper;
import com.iiitd.sqlite.model.Patient;
import com.iiitd.sqlite.model.PatientObservation;

@SuppressWarnings("deprecation")
public class PatientActivity extends ActionBarActivity {

	private TextView tvUUID, tvName, tvDob, tvGender, tvTemperature, tvAllergies;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_patient);
		
		Intent i = getIntent();
		Integer patient_id = i.getIntExtra(Patient.PATIENT_ID, 0);
		
		DatabaseHelper db = new DatabaseHelper(this);
		Patient patient = db.getPatientById(patient_id);
		List<PatientObservation> obs = db.getObsById(patient_id);
		db.closeDb();
		
		tvName = (TextView)findViewById(R.id.pt_name);
		tvUUID = (TextView)findViewById(R.id.pt_uuid);
		tvDob = (TextView)findViewById(R.id.pt_dob);
		tvGender = (TextView)findViewById(R.id.pt_gender);
		tvTemperature = (TextView)findViewById(R.id.pt_temperature);
		tvAllergies = (TextView)findViewById(R.id.pt_allergies);
		
		tvName.setText(patient.getName());
		tvUUID.setText(patient.getUUID());
		tvDob.setText(patient.getDob());
		tvGender.setText(patient.getGender());
		
		//TODO dynamically display list of observations with date
		
		tvTemperature.setText(obs.get(0).getTemperature());
		tvAllergies.setText(obs.get(0).getAllergies());
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.patient, menu);
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
}
