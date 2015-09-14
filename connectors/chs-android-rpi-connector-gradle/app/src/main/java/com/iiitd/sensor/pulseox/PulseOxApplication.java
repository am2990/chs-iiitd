package com.iiitd.sensor.pulseox;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.json.JSONArray;
import org.json.JSONObject;
import org.opendatakit.sensors.service.BaseActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.UserManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.androidplot.ui.AnchorPosition;
import com.androidplot.ui.widget.Widget;
import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.PointLabelFormatter;
import com.androidplot.xy.SimpleXYSeries;
//import com.androidplot.xy.XLayoutStyle;
import com.androidplot.xy.XYPlot;
//import com.androidplot.xy.YLayoutStyle;
import com.androidplot.xy.XYSeries;
import com.iiitd.navigationexample.R;
import com.iiitd.networking.NetworkDevice;
import com.iiitd.networking.Sensor;
import com.iiitd.sqlite.helper.DatabaseHelper;
import com.iiitd.sqlite.helper.Pair;

public class PulseOxApplication extends Activity{

	// Tag for Logging
	private static final String TAG = "PulseOxApp";
	private static final String OX_SENSOR_ID_STR = "pluseOxSensorID";
	
	private static final String DATA_GOOD = "Acceptable to Record";
	private static final String DATA_INACCURATE = "Inaccurate Data";
	private static final String DEVICE_NOT_CONNECTED = "Device reads NOT CONNECTED";

	private static final int PULSE_COLOR = Color.rgb(64,128,64);
	
	private static final int MAX_DATAPOINTS = 300;

	private String pulseOxId;

	private Button probeConnectionButton;
	private Button recordPulseOxButton;
	
	private TextView pulseTxt;
	private TextView oxTxt;
	private TextView series3Reading;
	
	private Integer mAnswerOx;
	private Integer mAnswerPulse;

	private UDPSendTask send = null;;
	private AsyncTask<URL, Integer, Long> recv = null;
	private BlockingQueue<Pair> pulse_ox = new LinkedBlockingQueue<Pair>();

	private boolean getReadings;
	
	private Context mContext;
	
	// Used to only play the beep once when an getting an accurate reading
	private boolean playBeep = true;
	private boolean receiveRunning = false , bOx = false , bPulse = false;;
	//plots for plotting the data from the oxygen sensor
	private SimpleXYSeries plenthSeries;
	private XYPlot dataPlot;
	private int dataPointCounter = 0;
	public static String selected;

	private String[] deviceSpinner = new String[] {"Add A Network Device First"};
	private String[] sensorSpinner = new String[] {"Select Device First"};

	private ArrayAdapter<Sensor> sensorAdapter = null;
	private ArrayAdapter<NetworkDevice> deviceAdapter = null;

	private NetworkDevice selectedDevice;
	private Sensor selectedSensor;

	AsyncTask udpSend;
	AsyncTask udpReceive;

	List<Integer> series1 = new ArrayList<>();
	List<Integer> series2 = new ArrayList<>();
	List<Integer> series3 = new ArrayList<>();
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pulseox_layout);

		mContext = this;
		pulseTxt = (TextView) findViewById(R.id.pulseReading);
		oxTxt = (TextView) findViewById(R.id.oxygenReading);
		series3Reading = (TextView) findViewById(R.id.series3Reading);

		dataPlot = (XYPlot) findViewById (R.id.dataPlot);
		probeConnectionButton = (Button) findViewById (R.id.connect);
		recordPulseOxButton = (Button) findViewById (R.id.record);

		probeConnectionButton.setEnabled(false);


		populateDeviceSpinners();

		Spinner s = (Spinner) findViewById(R.id.sensor_spinner);
		s.setEnabled(false);

		s.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				Log.d(TAG, "Sensor selected at position " + position);
				selectedSensor = sensorAdapter.getItem(position);
				probeConnectionButton.setEnabled(true);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});

		recordPulseOxButton.setEnabled(false);


        dataPlot.setRangeBoundaries(0, 255, BoundaryMode.AUTO);
        // get rid of the visual aids for positioning:
//        dataPlot.disableAllMarkup();
		dataPlot.setTicksPerRangeLabel(Integer.MAX_VALUE);
		dataPlot.setTicksPerDomainLabel(Integer.MAX_VALUE);

 		dataPointCounter = 0;
 		
		// restore stored preferences if any
		SharedPreferences appPreferences = getPreferences(MODE_PRIVATE);
		if (appPreferences.contains(OX_SENSOR_ID_STR)) {
			pulseOxId = appPreferences.getString(OX_SENSOR_ID_STR, null);
			if (pulseOxId != null) {
				Log.d(TAG, "restored pulseOxId: " + pulseOxId);
			}
		}


		Log.d(TAG, "on create");
	}

	private void populateDeviceSpinners() {

		DatabaseHelper db = new DatabaseHelper(this);

		final List<NetworkDevice> nd = db.getAllConnectedDevices();
		Spinner d = (Spinner) findViewById(R.id.device_spinner);
		if(nd.size() > 0) {


			deviceAdapter = new ArrayAdapter<NetworkDevice>(this,
					android.R.layout.simple_spinner_item, nd);
			d.setAdapter(deviceAdapter);
		}
		else{

			ArrayAdapter<String> deviceAdapter = new ArrayAdapter<String>(this,
					android.R.layout.simple_spinner_item, deviceSpinner);
			d.setAdapter(deviceAdapter);
		}

		d.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

				Log.d(TAG, "Network Device selected on position " + position);
				//get the device
				NetworkDevice device = nd.get(0);
				selectedDevice = device;
				//get list of sensors on device
				List<Sensor> sensors = device.getSensorList();
				// populate sensor spinner
				Spinner s = (Spinner) findViewById(R.id.sensor_spinner);
				sensorAdapter = new ArrayAdapter<Sensor>(mContext,
						android.R.layout.simple_spinner_item, sensors);
				s.setAdapter(sensorAdapter);
				//enable the spinner
				s.setEnabled(true);


			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});

	}

	protected void onResume() {
		super.onResume();
		
	}
	
    protected void onPause() {
        super.onPause();

    }
    
	@Override
	public void onDestroy() {
		Log.d(TAG, "onDestroy");

		if(udpReceive != null && (udpReceive.getStatus() == AsyncTask.Status.RUNNING))
			udpReceive.cancel(true);
		if(udpSend != null && (udpSend.getStatus() == AsyncTask.Status.RUNNING))
			udpSend.cancel(true);

		super.onDestroy();
	}

	public void connectButtonAction(View view) {
		Log.d(TAG, "connect button pressed");
		Spinner spinner = (Spinner)findViewById(R.id.sensor_spinner);
		this.selected = spinner.getSelectedItem().toString();
		pulseOxId = "yes";

		series1 = new ArrayList<>();
		series2 = new ArrayList<>();
		series3 = new ArrayList<>();

		getSensorReadings();

	}

	public void recordButtonAction(View view) {
		Log.d(TAG, "record button pressed");
		getReadings = false;

		CharSequence text = recordPulseOxButton.getText();
		if(text.toString().equalsIgnoreCase("Press Here to Record Results")){
			Toast.makeText(mContext, "Closing Sensor Connections ...", Toast.LENGTH_SHORT);
			recordPulseOxButton.setText("Save Values !!!");
			recordPulseOxButton.setEnabled(false);
			return;
		}
			new AlertDialog.Builder(this)
					.setTitle("Save Values")
					.setMessage("Are you sure you want to save the results ?")
					.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {

							returnValuetoCaller();
						}
					})
					.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							// do nothing
						}
					})
					.setIcon(android.R.drawable.ic_dialog_alert)
					.show();
//		returnValuetoCaller();
//		}

	}
	
	private void getSensorReadings() {

		getReadings = true;
		while(true) {
			udpSend = new UDPSendTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			if (udpSend.getStatus() == AsyncTask.Status.RUNNING) {
				udpReceive = new UDPReceiveTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
				break;
			}
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			Log.d(TAG, "KeyDown");
			returnValuetoCaller();
			break;
		}

		return super.onKeyDown(keyCode, event);
	}

	private void returnValuetoCaller() {

		Intent intent = new Intent();
		//TODO push back to be dynamic
		// possible way get send the sensor through a fixed field then the number of values are dependent on the sensor type and will be dynamic
		intent.putExtra("Sensor", selectedSensor.getSensorName());
		intent.putExtra("Reading1", series1.get(series1.size()-1));
		intent.putExtra("Reading2", series2.get(series2.size()-1));
		if(selectedSensor.getSensorName().equalsIgnoreCase("bp"))
			intent.putExtra("Reading3", series3.get(series3.size()-1));
		setResult(RESULT_OK, intent);
		finish();
	}

	
	private class UDPSendTask extends AsyncTask<String, Integer, Long> {
		DatagramSocket send;
	     protected Long doInBackground(String... ips) {

	    	 try{
				 String messageStr=" type: \"request\", sdr:\"192.168.48.21\"  , rcv:\"192.168.17.72\" , port: 10002, mac:\"11:22:33:44:55:66\", time:1234 , argv:[{sensor:\""+selectedSensor.getSensorName()+"\"}]";
	    		 int server_port = 10000;
	    		 send = new DatagramSocket();

//	    		 InetAddress local = InetAddress.getByName("192.168.43.123");
	    		 
	    		 while(getReadings){

//					 if(udpSend.isCancelled())
//						 break;
					 InetAddress local = InetAddress.getByName(selectedDevice.getIpAddress());
					 //TODO Remove
//					 local = InetAddress.getByName("192.168.43.238");
		    		 int msg_length=messageStr.length();
		    		 byte[] message = messageStr.getBytes();
		    		 DatagramPacket p = new DatagramPacket(message, msg_length,local,server_port);
					 Log.d(TAG, "sending request :" + messageStr);
					 send.send(p);

					 int samplingFrequency = 1000;
					 Thread.sleep(samplingFrequency);

	    		 }
	    	 }catch(Exception e){
				 send.close();
				 e.printStackTrace();
	    	 }

	    	 return 0L;
	     }

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			probeConnectionButton.setEnabled(false);
		}

		protected void onProgressUpdate(Integer... progress) {
//	         setProgressPercent(progress[0]);
	     }

	     protected void onPostExecute(Long result) {
//	         showDialog("Downloaded " + result + " bytes");
			 Log.d(TAG,"Exiting UDP Send Thread");
			 probeConnectionButton.setEnabled(true);
			 send.close();
	     }
	 }

	private class UDPReceiveTask extends AsyncTask<URL, Integer, Long> {
		DatagramSocket receive = null;
		private int exception = 0;
		protected Long doInBackground(URL... urls) {
//	    	 UDPMulticast udp = new UDPMulticast(mContext, "224.3.29.71" , 10000);
	    	 
	    	 try{
	    		 while(getReadings){
//					 if(udpReceive.isCancelled())
//						 break;
		    		 String text;
		    		 int server_port = 10002;
		    		 byte[] message = new byte[1500];
		    		 DatagramPacket p = new DatagramPacket(message, message.length);
		    		 receive = new DatagramSocket(server_port);
		    		 receive.setSoTimeout(3000);
		    		 Log.d(TAG, "Waiting to Receive");
		    		 receive.receive(p);
		    		 text = new String(message, 0, p.getLength());
		    		 Log.d(TAG,"message:" + text);
	                 receive.close();
	                 parseMessage(text);
	                 publishProgress(0);
	    		 }
	    	 }catch(SocketTimeoutException e){
	    		 receive.close();
				 //TODO if socket timeout occurs but getReadings is active this means device is not online
				 //Promppt the user and close the UDPSend
				 exception = 1;
	    	 }
			catch(Exception e){
				receive.close();
				e.printStackTrace();
			}


	    	 return 0L;
	     }
	     
	     @Override
	     protected void onPreExecute() {

	     }

	     protected void onProgressUpdate(Integer... progress) {
//	         setProgressPercent(progress[0]);
			 recordPulseOxButton.setEnabled(true);
			 Log.d(TAG,"Time to update things ");
			 //update the text views
			 int sensor1_current = series1.get(series1.size()-1);
			 int sensor2_current = series2.get(series2.size()-1);

			 if (sensor1_current > 0) {
				 //
				 switch (sensor1_current) {
					 case 1:
						 pulseTxt.setText("Sensor Offline");
					 case 511:
						 pulseTxt.setText("Error");
					 default:
						 pulseTxt.setText(Integer.toString(sensor1_current));
				 }

			 }
			 if (sensor2_current > 0) {

				 switch (sensor2_current) {
					 case 1:
						 oxTxt.setText("Sensor Offline");
					 case 127:
						 oxTxt.setText("Error");
					 default:
						 oxTxt.setText(Integer.toString(sensor2_current));
				 }
			 }

			 if(selectedSensor.getSensorName().equalsIgnoreCase("bp")){
				 int sensor3_current = series3.get(series3.size()-1);
				 if (sensor3_current > 0) {

					 switch (sensor3_current) {
						 case 1:
							 series3Reading.setText("Sensor Offline");
						 case 127:
							 series3Reading.setText("Error");
						 default:
							 series3Reading.setText(Integer.toString(sensor3_current));
					 }
				 }
			 }

			 //TODO from static XYSeries Plot move to Dynamic Plot updated regularly
//			 dataPlot.redraw();

		 }

	     protected void onPostExecute(Long result) {
			 Log.d(TAG, "Exiting UDP Receiver Thread");
			 receive.close();

			 if(getReadings && udpSend != null && exception == 1){
				 Toast.makeText(mContext,"Device Not Responding !!!",Toast.LENGTH_LONG).show();
				 udpSend.cancel(true);
				 probeConnectionButton.setEnabled(true);
				 return;
			 }
			 //depending on the type of sensor update the plot
			 // Turn the above arrays into XYSeries':
			 XYSeries series_one = new SimpleXYSeries(
					 series1,          // SimpleXYSeries takes a List so turn our array into a List
					 SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, // Y_VALS_ONLY means use the element index as the x value
					 "Series1");                             // Set the display title of the series

			 // same as above
			 XYSeries series_two = new SimpleXYSeries(
					 series2,
					 SimpleXYSeries.ArrayFormat.Y_VALS_ONLY,
					 "Series2");

			 // Create a formatter to use for drawing a series using LineAndPointRenderer:
			 LineAndPointFormatter series1Format = new LineAndPointFormatter(
					 Color.rgb(0, 200, 0),                   // line color
					 Color.rgb(0, 100, 0),                   // point color
					 null,                                   // fill color (none)
					 new PointLabelFormatter(Color.WHITE));                           // text color

			 // add a new series' to the xyplot:
			 dataPlot.addSeries(series_one, series1Format);

			 // same as above:
			 dataPlot.addSeries(series_two,
					 new LineAndPointFormatter(
							 Color.rgb(0, 0, 200),
							 Color.rgb(0, 0, 100),
							 null,
							 new PointLabelFormatter(Color.WHITE)));

			 if(selectedSensor.getSensorName().equalsIgnoreCase("bp")){
				 LineAndPointFormatter series3Format = new LineAndPointFormatter(
						 Color.rgb(251, 255, 118),                   // line color
						 Color.rgb(100, 0, 0),                   // point color
						 null,                                   // fill color (none)
						 new PointLabelFormatter(Color.WHITE));                           // text color

				 XYSeries series_three = new SimpleXYSeries(
						 series3,
						 SimpleXYSeries.ArrayFormat.Y_VALS_ONLY,
						 "Series3");
				 dataPlot.addSeries(series_three, series3Format);
			 }
			 // reduce the number of range labels
			 dataPlot.setTicksPerRangeLabel(3);
			 dataPlot.redraw();
			 recordPulseOxButton.setEnabled(true);
		 }
	 }

	private String parseMessage(String msg){

		try{

			JSONObject obj = new JSONObject(msg);
			String packet_type = obj.getString("type");

			if(packet_type.equalsIgnoreCase("request_reply")){

				//get argv array
				JSONArray arr = obj.getJSONArray("argv");

				for (int i = 0; i < arr.length(); i++) {

					Object c = arr.get(i);
					JSONObject sens = new JSONObject(c.toString());
					String sensorname = sens.getString("sensorname");
					Log.d(TAG,"Sensor Name " + sensorname);

					//get readings array
					JSONArray points_arr = sens.getJSONArray("readings");
					for (int j = 0; j < points_arr.length(); j++) {
						//get array of data points
						JSONArray point_tuple = (JSONArray) points_arr.get(j);
						Log.d(TAG,points_arr.get(j).toString());
//						for (int k = 0; k < point_tuple.length(); k++) {
							//get individual points
//							System.out.println(point_tuple.get(k));
						if(sensorname.equalsIgnoreCase("bp")){
								series1.add(Integer.parseInt(point_tuple.get(0).toString()));
								series2.add(Integer.parseInt(point_tuple.get(1).toString()));
								series3.add(Integer.parseInt(point_tuple.get(2).toString()));
							}
						if(sensorname.equalsIgnoreCase("nonin")){
							series1.add(Integer.parseInt(point_tuple.get(0).toString()));
							series2.add(Integer.parseInt(point_tuple.get(1).toString()));
						}
//						}
					}
				}
			}


		}catch(Exception e){
			e.printStackTrace();
		}

		return msg;

	}

}