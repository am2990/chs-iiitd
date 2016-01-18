package com.iiitd.chs;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.iiitd.form.ObservationActivity;
import com.iiitd.navigationexample.R;
import com.iiitd.sqlite.helper.DatabaseHelper;
import com.iiitd.sqlite.model.Patient;
import com.iiitd.sqlite.model.PatientObservation;

import java.util.List;

@SuppressWarnings("deprecation")
public class PatientActivity extends ActionBarActivity {

	private TextView tvName;
	private static List<PatientObservation> obs;
	private static Context mContext;
	private static String[] obs_list;
	Patient patient;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_patient);

		Intent i = getIntent();
		Integer patient_id = i.getIntExtra(Patient.PATIENT_ID, 0);
		
		DatabaseHelper db = new DatabaseHelper(this);
		patient = db.getPatientById(patient_id);
		obs = db.getObsById(patient_id);
		obs_list = new String[obs.size()];
		int j = 0;
		for( PatientObservation p : obs) {
			obs_list[j++] = p.toString();
		}
		db.closeDb();


		android.support.v4.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		SimpleListFragment fragment = new SimpleListFragment();
		transaction.replace(R.id.fragment, fragment);
		transaction.commit();

		tvName = (TextView)findViewById(R.id.pt_name);


		tvName.setText(patient.getName());
//		//TODO dynamically display list of observations with date

		mContext = this;
	}

	public void addObservation(View v){

		Intent intent=new Intent(this, ObservationActivity.class);
		String[] vals = {patient.getUUID(), patient.getName(), patient.getGender(), patient.getDob()};
		intent.putExtra(Constants.PATIENT, patient);
		intent.putExtra(Constants.NEW_PATIENT, false);
//		intent.putExtra("PATIENT", vals);
		startActivity(intent);
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

	public static class SimpleListFragment extends ListFragment
	{

		String[] numbers_text = new String[] { "No Recorded Observations" };

		@Override
		public void onListItemClick(ListView l, View v, int position, long id) {

			Intent i = new Intent(mContext, PatientHistory.class);
			startActivity(i);
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
								 Bundle savedInstanceState) {


			//TODO create the fragment class and do it in the more elegant way
			//Check if the patient has observations recorded else display default list
			if(obs_list ==  null){
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(
					inflater.getContext(), android.R.layout.simple_list_item_1,
					numbers_text);
				setListAdapter(adapter);
			}
			else{
				ArrayAdapter<String> adapter = new ArrayAdapter<String>(
						inflater.getContext(), android.R.layout.simple_list_item_1,
						obs_list);
				setListAdapter(adapter);
			}

			return super.onCreateView(inflater, container, savedInstanceState);
		}
	}

}
