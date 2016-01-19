package com.iiitd.chs;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.iiitd.navigationexample.R;
import com.iiitd.networking.Sensor;
import com.iiitd.sqlite.helper.DatabaseHelper;
import com.iiitd.sqlite.model.Notification;
import com.iiitd.sqlite.model.Patient;
import com.iiitd.sqlite.model.PatientObservation;

import java.util.List;

public class PatientHistory extends ActionBarActivity {

	public static final String TAG = "PatientHistory";

	private TextView tvUUID, tvName, tvObsID, tvTemperature, tvAllergies, tvSensorReading, tvNotification;
	Patient patient;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_patient_history);

		PatientObservation patientObservation = (PatientObservation) getIntent().getSerializableExtra(Constants.PATIENT_OBS);
		DatabaseHelper db = new DatabaseHelper(this);
		patient = db.getPatientById(patientObservation.getPatientId());
		Sensor sensor = db.getSensorByObsId(patientObservation.getId());
		Notification notification = db.getNotificationByObsId(patientObservation.getId());

		List<String> readingsList = sensor.getReadings();
		String readings = "";
		for(String s: readingsList){
			readings += s +" , ";
		}
		Log.d(TAG, readings);
//		List<PatientObservation> obs = db.getObsById(1);
		db.closeDb();

		tvName = (TextView)findViewById(R.id.pt_name1);
		tvUUID = (TextView)findViewById(R.id.pt_uuid);
		tvObsID = (TextView)findViewById(R.id.pt_obs_id);
		tvTemperature = (TextView)findViewById(R.id.pt_temp);
		tvAllergies = (TextView)findViewById(R.id.pt_allergies);
		tvSensorReading = (TextView)findViewById(R.id.pt_sensordata);
		tvNotification = (TextView)findViewById(R.id.pt_notifications);

		tvName.setText(patient.getName());
		tvUUID.setText(patient.getUUID());
		tvObsID.setText(" "+ patientObservation.getPatientId());
		tvTemperature.setText(patientObservation.getTemperature());
		tvAllergies.setText(patientObservation.getAllergies());
		tvSensorReading.setText(readings);
		tvNotification.setText(notification.getNotification());

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
