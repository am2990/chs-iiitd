package com.iiitd.sensor.pulseox;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.URL;
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
import com.androidplot.xy.SimpleXYSeries;
//import com.androidplot.xy.XLayoutStyle;
import com.androidplot.xy.XYPlot;
//import com.androidplot.xy.YLayoutStyle;
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
	private TextView statusTxt;
	
	private Integer mAnswerOx;
	private Integer mAnswerPulse;

	private UDPSendTask send = null;;
	private AsyncTask<URL, Integer, Long> recv = null;
	private BlockingQueue<Pair> pulse_ox = new LinkedBlockingQueue<Pair>();
	
	private boolean isConnected;
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

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pulseox_layout);

		mContext = this;
		pulseTxt = (TextView) findViewById(R.id.pulseReading);
		oxTxt = (TextView) findViewById(R.id.oxygenReading);
		statusTxt = (TextView) findViewById(R.id.probeStatus);
		dataPlot = (XYPlot) findViewById (R.id.dataPlot);
		probeConnectionButton = (Button) findViewById (R.id.connect);
		recordPulseOxButton = (Button) findViewById (R.id.record);

		probeConnectionButton.setEnabled(false);

		populateDeviceSpinners();

		Spinner s = (Spinner) findViewById(R.id.sensor_spinner);
//		ArrayAdapter<String> sensorAdapter = new ArrayAdapter<String>(this,
//				android.R.layout.simple_spinner_item, sensorSpinner);
//		s.setAdapter(sensorAdapter);
		s.setEnabled(false);

		s.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				Log.d(TAG, "Sensor selected at position "+position);
				selectedSensor = sensorAdapter.getItem(position);
				probeConnectionButton.setEnabled(true);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});

		recordPulseOxButton.setEnabled(false);
	

		
 	    Widget domainLabelWidget = dataPlot.getDomainLabelWidget();
 		
//        dataPlot.position(domainLabelWidget,                     // the widget to position
//                                 0,                                    // x position value
//                                 XLayoutStyle.ABSOLUTE_FROM_LEFT,       // how the x position value is applied, in this case from the left
//                                 0,                                     // y position value
//                                 YLayoutStyle.ABSOLUTE_FROM_BOTTOM,     // how the y position is applied, in this case from the bottom
//                                 AnchorPosition.LEFT_BOTTOM);           // point to use as the origin of the widget being positioned

        dataPlot.setRangeBoundaries(0, 255, BoundaryMode.AUTO);
        // get rid of the visual aids for positioning:
//        dataPlot.disableAllMarkup();
		dataPlot.setTicksPerRangeLabel(Integer.MAX_VALUE);
		dataPlot.setTicksPerDomainLabel(Integer.MAX_VALUE);
        
		plenthSeries = new SimpleXYSeries("HeartWaveform");
// 		dataPlot.addSeries(plenthSeries,new LineAndPointFormatter(Color.GREEN, Color.BLUE, null));
 		dataPointCounter = 0;
 		
		// restore stored preferences if any
		SharedPreferences appPreferences = getPreferences(MODE_PRIVATE);
		if (appPreferences.contains(OX_SENSOR_ID_STR)) {
			pulseOxId = appPreferences.getString(OX_SENSOR_ID_STR, null);
			if (pulseOxId != null) {
				Log.d(TAG, "restored pulseOxId: " + pulseOxId);
			}
		}

		isConnected = true;
		mAnswerOx = 0;
		mAnswerPulse = 0;
//		
//		UDPReceiveTask udpReceive = new UDPReceiveTask();
//		udpReceive.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
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
		super.onDestroy();
	}

	public void connectButtonAction(View view) {
		Log.d(TAG, "connect button pressed");
		Spinner spinner = (Spinner)findViewById(R.id.sensor_spinner);
		this.selected = spinner.getSelectedItem().toString();
		pulseOxId = "yes";
		getSensorReadings();

	}

	public void recordButtonAction(View view) {
		Log.d(TAG, "record button pressed");
		getReadings = false;
//		if(udpSend.isCancelled() && udpReceive.isCancelled()){
			new AlertDialog.Builder(this)
					.setTitle("Save Values")
					.setMessage("Are you sure you want to save this entry?")
					.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							// continue with delete
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

		isConnected = true;
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

		Log.d(TAG, "Entered Return Value to Caller");
		
		Intent intent = new Intent();
		intent.putExtra(NoninPacket.OX, mAnswerOx);
		intent.putExtra(NoninPacket.PULSE, mAnswerPulse);
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

					 InetAddress local = InetAddress.getByName(selectedDevice.getIpAddress());
					 //TODO Remove
//					 local = InetAddress.getByName("192.168.43.238");
		    		 int msg_length=messageStr.length();
		    		 byte[] message = messageStr.getBytes();
		    		 DatagramPacket p = new DatagramPacket(message, msg_length,local,server_port);
		    		 isConnected = true;
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
		protected Long doInBackground(URL... urls) {
//	    	 UDPMulticast udp = new UDPMulticast(mContext, "224.3.29.71" , 10000);
	    	 
	    	 try{
	    		 while(getReadings){

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
	    	 }catch(Exception e){
	    		 receive.close();
	    		 e.printStackTrace();
	    	 }


	    	 return 0L;
	     }
	     
	     @Override
	     protected void onPreExecute() {
	    	 isConnected = false;
	     }

	     protected void onProgressUpdate(Integer... progress) {
//	         setProgressPercent(progress[0]);
			 recordPulseOxButton.setEnabled(true);
			 Log.d(TAG,"Time to update things ");
	     }

	     protected void onPostExecute(Long result) {
			 Log.d(TAG,"Exiting UDP Send Thread");
			 receive.close();

	    	 	for(Pair p : pulse_ox ){
	    	 	int pulse = (int) p.getLeft();
	    	 	int ox = (int) p.getRight();
	    	 	
	    	 	pulseTxt.setTextColor(PULSE_COLOR);
				oxTxt.setTextColor(Color.BLUE);
				statusTxt.setTextColor(Color.BLACK);
				statusTxt.setText(DATA_GOOD);
				recordPulseOxButton.setEnabled(true);
				recordPulseOxButton.setTextColor(Color.BLUE);

				if (playBeep) {
					MediaPlayer mediaPlayer = MediaPlayer.create(
							PulseOxApplication.this, R.raw.beep);
					mediaPlayer.start();
					playBeep = false;

				}

				if (pulse > 0) {
					//						pulse = b.getInt(NoninPacket.PULSE);
					if(pulse == 511) {
						pulseTxt.setText("Error");

					} else {
						pulseTxt.setText(Integer.toString(pulse));

					}
					bPulse = true;
					mAnswerPulse = pulse;

					Log.d(TAG, "Got new pulse: " + pulse);
				}
				if (ox > 0) {
					//						int ox = b.getInt(NoninPacket.OX);
					if(ox == 127) {
						mAnswerOx = -1;
						oxTxt.setText("Error");

					} else {
						mAnswerOx = 99;
						oxTxt.setText("99");
						oxTxt.setText(Integer.toString(ox));
					}
					bOx = true;								
					mAnswerOx = ox;
					//vibrator.vibrate(75);


				}
//				dataPlot.redraw();
				pulse_ox.remove(p);
	      }
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
						for (int k = 0; k < point_tuple.length(); k++) {
							//get individual points
//							System.out.println(point_tuple.get(k));
						}
					}
				}
			}


		}catch(Exception e){
			e.printStackTrace();
		}

		return msg;

	}

}