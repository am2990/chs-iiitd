package com.iiitd.form;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.UUID;

import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.iiitd.chs.Constants;
import com.iiitd.chs.MainActivity;
import com.iiitd.navigationexample.R;
import com.iiitd.sqlite.helper.DatabaseHelper;
import com.iiitd.sqlite.model.Patient;

@SuppressWarnings("deprecation")
public class RegistrationActivity extends ActionBarActivity {

	private CharSequence mTitle;
	private EditText etName, etDob;
	private TextView uuidTextView;
	private RadioGroup rg;
	
	
	Calendar myCalendar = Calendar.getInstance();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.encounter_layout);
		mTitle = getTitle();
		
		
		etDob = (EditText) findViewById(R.id.dob);
		etName = (EditText) findViewById(R.id.PatientName);
		rg = (RadioGroup) findViewById(R.id.radio_gender);
		
		
		etDob.setOnClickListener(new View.OnClickListener() {
			
	        public void onClick(View v) {
	           
	            new DatePickerDialog(RegistrationActivity.this, date, myCalendar
	                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
	                    myCalendar.get(Calendar.DAY_OF_MONTH)).show();
	        }
	    });
		
		
		uuidTextView = (TextView) findViewById(R.id.textViewUUID);
	    UUID id = UUID.randomUUID();
	    uuidTextView.setText(id.toString());

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.registration, menu);
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
	
	public void showDatePickerDialog(View v) {
	    DialogFragment newFragment = new DatePickerFragment();
	    newFragment.show(getFragmentManager(), "datePicker");
	}
	
	public void saveForm(View v){
		
		System.out.println("Clicked Add Observation");
		
		//TODO Validate Form

		String gender = "";
		if(rg.getCheckedRadioButtonId()!=-1){
		    int id= rg.getCheckedRadioButtonId();
		    View radioButton = rg.findViewById(id);
		    int radioId = rg.indexOfChild(radioButton);
		    RadioButton btn = (RadioButton) rg.getChildAt(radioId);
		    gender = (String) btn.getText();
		}
		
		String patientName = etName.getText().toString();
		String patientDob = etDob.getText().toString();
		String uuid = uuidTextView.getText().toString();

//		DatabaseHelper db = new DatabaseHelper(this);
//		int patient_id = (int) db.createPatient(new Patient(uuid, patientName, patientDob, gender));

		//TODO Send some data to Obs Form link to the patient
		Intent intent = new Intent(this, ObservationActivity.class);
//		intent.putExtra(Patient.PATIENT_ID, patient_id);
		
		//TODO get rid of this code
		String[] vals = {uuid, patientName, gender, patientDob};
		Patient patient = new Patient(uuid, patientName, gender, patientDob);


		intent.putExtra(Constants.PATIENT, patient);
		startActivity(intent);

	}

	DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

	    @Override
	    public void onDateSet(DatePicker view, int year, int monthOfYear,
	            int dayOfMonth) {

	        myCalendar.set(Calendar.YEAR, year);
	        myCalendar.set(Calendar.MONTH, monthOfYear);
	        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
	        updateLabel();
	    }

		

	};

	 
	 private void updateLabel() {

		 String myFormat = "MM/dd/yy"; //In which you need put here
		 SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

		 etDob.setText(sdf.format(myCalendar.getTime()));
	 }
	
	
}
