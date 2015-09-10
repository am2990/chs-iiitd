package com.iiitd.navigationexample;


import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.iiitd.chs.MainActivity;
import com.iiitd.networking.NetworkDevice;
import com.iiitd.networking.Sensor;
import com.iiitd.sqlite.helper.DatabaseHelper;

@SuppressWarnings("deprecation")
public class AddDeviceActivity extends ActionBarActivity {

	NetworkDevice device;
	TextView dv_name, dv_ip, dv_mac, dv_sensor;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_device);
		
		Intent i = getIntent();
		device = (NetworkDevice)i.getSerializableExtra("device");
		
		dv_name = (TextView)findViewById(R.id.device_name);
		dv_ip = (TextView)findViewById(R.id.device_ip);
		dv_mac = (TextView)findViewById(R.id.device_mac);
		dv_sensor = (TextView)findViewById(R.id.device_sensor1);


		dv_name.setText(device.getDeviceName());
		dv_ip.setText(device.getIpAddress());
		dv_mac.setText(device.getMacAddress());
		//TODO account for dynamic sensor fetching
		List<Sensor> sensor_list =  device.getSensorList();
		String sensor_disp = "";
		for(Sensor sensor : sensor_list){
			sensor_disp += sensor.toString() +"\n";
		}
		dv_sensor.setText(sensor_disp);
		
	}
	
	public void addDevice(View v){
		
		DatabaseHelper db = new DatabaseHelper(this);
		long response_device = db.addConnectedDevice(device);

		
		if(response_device == 0)
			Toast.makeText(this, "Device with MAC already Exits !!!", Toast.LENGTH_LONG).show();

		for(Sensor sensor: device.getSensorList()) {
			long response_sensor = db.addSensor(sensor);
		}

		db.closeDb();
		Toast.makeText(this, "Device Successfully Added !!!", Toast.LENGTH_LONG).show();

		Intent i = new Intent(this, MainActivity.class);
		startActivity(i);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.add_device, menu);
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
