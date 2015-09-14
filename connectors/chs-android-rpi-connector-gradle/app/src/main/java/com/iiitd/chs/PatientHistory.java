package com.iiitd.chs;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.iiitd.navigationexample.R;
import com.iiitd.sqlite.helper.DatabaseHelper;
import com.iiitd.sqlite.model.Patient;
import com.iiitd.sqlite.model.PatientObservation;

import java.util.List;

public class PatientHistory extends ActionBarActivity {

	private TextView tvUUID, tvName, tvDob, tvGender, tvTemperature, tvAllergies;
	Patient patient;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_patient_history);

		DatabaseHelper db = new DatabaseHelper(this);
		patient = db.getPatientById(1);
		List<PatientObservation> obs = db.getObsById(1);
		db.closeDb();

		tvName = (TextView)findViewById(R.id.pt_name1);
		tvUUID = (TextView)findViewById(R.id.pt_uuid);
		tvDob = (TextView)findViewById(R.id.pt_dob);
		tvGender = (TextView)findViewById(R.id.pt_gender);
		tvTemperature = (TextView)findViewById(R.id.pt_temperature);
		tvAllergies = (TextView)findViewById(R.id.pt_allergies);

		tvName.setText(patient.getName());
		tvUUID.setText(patient.getUUID());
		tvDob.setText(patient.getDob());
		tvGender.setText(patient.getGender());

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_patient_history, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if (id == R.id.action_settings) {
			return true;
		}

		return super.onOptionsItemSelected(item);
	}
}
