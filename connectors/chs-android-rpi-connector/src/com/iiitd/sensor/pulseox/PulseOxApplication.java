package com.iiitd.sensor.pulseox;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.URL;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.json.JSONArray;
import org.json.JSONObject;
import org.opendatakit.sensors.service.BaseActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.androidplot.ui.AnchorPosition;
import com.androidplot.ui.widget.Widget;
import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XLayoutStyle;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.YLayoutStyle;
import com.iiitd.navigationexample.R;
import com.iiitd.networking.NetworkDevice;
import com.iiitd.sqlite.helper.DatabaseHelper;
import com.iiitd.sqlite.helper.Pair;

public class PulseOxApplication extends BaseActivity {

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
	
	private Context mContext;
	
	// Used to only play the beep once when an getting an accurate reading
	private boolean playBeep = true;
	private boolean receiveRunning = false , bOx = false , bPulse = false;;
	//plots for plotting the data from the oxygen sensor
	private SimpleXYSeries plenthSeries;
	private XYPlot dataPlot;
	private int dataPointCounter = 0;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pulseox_layout);
		pulseTxt = (TextView) findViewById(R.id.pulseReading);
		oxTxt = (TextView) findViewById(R.id.oxygenReading);
		statusTxt = (TextView) findViewById(R.id.probeStatus);
		dataPlot = (XYPlot) findViewById (R.id.dataPlot);
		probeConnectionButton = (Button) findViewById (R.id.connect);
		recordPulseOxButton = (Button) findViewById (R.id.record);
		
		recordPulseOxButton.setEnabled(false);
	
		mContext = this;
		
 	    Widget domainLabelWidget = dataPlot.getDomainLabelWidget();
 		
        dataPlot.position(domainLabelWidget,                     // the widget to position
                                 0,                                    // x position value
                                 XLayoutStyle.ABSOLUTE_FROM_LEFT,       // how the x position value is applied, in this case from the left
                                 0,                                     // y position value
                                 YLayoutStyle.ABSOLUTE_FROM_BOTTOM,     // how the y position is applied, in this case from the bottom
                                 AnchorPosition.LEFT_BOTTOM);           // point to use as the origin of the widget being positioned

        dataPlot.setRangeBoundaries(0, 255, BoundaryMode.AUTO);
        // get rid of the visual aids for positioning:
        dataPlot.disableAllMarkup();
		dataPlot.setTicksPerRangeLabel(Integer.MAX_VALUE);
		dataPlot.setTicksPerDomainLabel(Integer.MAX_VALUE);
        
		plenthSeries = new SimpleXYSeries("HeartWaveform");
 		dataPlot.addSeries(plenthSeries,new LineAndPointFormatter(Color.GREEN, Color.BLUE, null));
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
		new UDPReceiveTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);		
		
		Log.d(TAG, "on create");
	}
	
	protected void onResume() {
		super.onResume();
		
	}
	
    protected void onPause() {
        super.onPause();
        
		// stop the processor of data
		
    }
    
	@Override
	public void onDestroy() {
		Log.d(TAG, "onDestroy");
		super.onDestroy();
	}

	public void connectButtonAction(View view) {
		Log.d(TAG, "connect button pressed");
		pulseOxId = "yes";
		if (pulseOxId == null) {
			//TODO check for online devices
			launchSensorDiscovery();
		} else {
			connectPulseOx();
		}
	}

	public void recordButtonAction(View view) {
		Log.d(TAG, "record button pressed");
		returnValuetoCaller();
	}
	
	private void connectPulseOx() {
		
		if (pulseOxId == null) {
			Log.e(TAG, "ERROR: Somehow tried to connect when no ID is present");
			return;
		}
		new UDPSendTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		isConnected = true;
//		try {
//			if (!isConnected(pulseOxId)) {
//				Log.d(TAG, "connecting to sensor: " + pulseOxId);
//				sensorConnect(pulseOxId, null);
//				pulseTxt.setText("IN CONNECTING");
//				probeConnectionButton.setText("PROBLEM DETECTING PROBE\n Retry PulseOx Probe Connect");
//				
//			}
//			if (isConnected(pulseOxId)) {
//				Log.d(TAG, "starting pulse ox sensor: " + pulseOxId);
//				isConnected = true;
//				startSensor(pulseOxId);
//				pulseTxt.setText("IN STARTING");
//				probeConnectionButton.setText("Restart PulseOx Probe Connection");
//				
//				
//				
//			} else {
//				Log.d(TAG, "Trouble in connecting to pulseOx sensor");
//			}
//		} catch (RemoteException rex) {
//			rex.printStackTrace();
//		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		super.onActivityResult(requestCode, resultCode, data);

		Log.d(TAG, "onActivityResult resultCode" + resultCode
				+ "  and  requestCode" + requestCode);

		if (requestCode == SENSOR_DISCOVERY_RETURN) {
			// from addSensorActvitity
			if (resultCode == RESULT_OK) {
				// Get sensor id and state from result
				if (data.hasExtra("sensor_id")) {
					pulseOxId = data.getStringExtra("sensor_id");

					// update sensor id stored in preferences
					SharedPreferences.Editor prefsEditor = getPreferences(
							MODE_PRIVATE).edit();
					prefsEditor.putString(OX_SENSOR_ID_STR, pulseOxId);
					prefsEditor.commit();

					// connect to sensor
					connectPulseOx();
				} else {
					Log.d(TAG, "activity result returned without sensorID");
				}
			}
		}
	}

	private void processData() {

		// ensure sensor has been connected
		if (!isConnected) {
			return;
		}
		runOnUiThread(new Runnable() {
			public void run() {
				try {

					String text;
					int server_port = 10002;
					byte[] message = new byte[1500];
					DatagramPacket dp = new DatagramPacket(message, message.length);
					DatagramSocket receive = new DatagramSocket(server_port);
					receive.receive(dp);
					text = new String(message, 0, dp.getLength());
					Log.d(TAG,"message:" + text);
					receive.close();
					parseMessage(text);


					if (pulse_ox != null) {

						for (Pair p : pulse_ox) {
							try {
								if (p.getLeft()!= null) {
									//								//TODO figure out how to display device not connected
									//								boolean connected = b.getBoolean(NoninPacket.CONNECTED);
									//								
									//								if (!connected) {
									//									pulseTxt.setText(DEVICE_NOT_CONNECTED);
									//									oxTxt.setText(DEVICE_NOT_CONNECTED);
									//									statusTxt.setText(DEVICE_NOT_CONNECTED);
									//									continue;
									//								}
								}
								if (true) {
									int pulse = (int) p.getLeft();
									if(pulse == 511) {
										pulseTxt.setText("Error");
										Toast toast = Toast.makeText(mContext, "error 511", Toast.LENGTH_SHORT);
										toast.show();
									} else {
										pulseTxt.setText(Integer.toString(pulse));

									}
									bPulse = true;
									mAnswerPulse = pulse;

									Log.d(TAG, "Got new pulse: " + pulse);
								}
								if (true) {
									int ox = (int) p.getRight();
									if(ox == 127) {
										mAnswerOx = -1;
										oxTxt.setText("Error");
										Toast toast = Toast.makeText(mContext, "error 127", Toast.LENGTH_SHORT);
										toast.show();
									} else {
										mAnswerOx = 99;
										oxTxt.setText("99");
										oxTxt.setText(Integer.toString(ox));
									}
									bOx = true;								
									mAnswerOx = ox;

									Log.d(TAG, "Got new oxygen: " + ox);
								}

								if (true) {
									int[] pleths;
									for (int i = 0; i < pulse_ox.size(); i++) {
										plenthSeries.addLast(dataPointCounter,
												(int)pulse_ox.poll().getRight());
										dataPointCounter++;

										if (dataPointCounter > MAX_DATAPOINTS) {
											plenthSeries.removeFirst();
										}
									}
									dataPlot.redraw();

									//returnValuetoCaller();
								}
								
							}catch(RuntimeException e){
								
							}

						
						}

					}
					
				}	catch (IOException e) {
					// TODO Auto-generated catch block
					Log.d(TAG, "Entered Async Task Catch Exception");
					System.out.println("caught exception");
					e.printStackTrace();
				}
				}

			
		});
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
	     protected Long doInBackground(String... ips) {
//	    	 UDPMulticast udp = new UDPMulticast(mContext, "224.3.29.71" , 10000);
	    	 try{
	    		 String messageStr=" type: \"request\", sdr:\"192.168.48.21\"  , rcv:\"192.168.17.72\" , port: 10002, mac:\"11:22:33:44:55:66\", time:1234 , argv:[{sensorid:1,readings:5}]";
	    		 int server_port = 10000;
	    		 DatagramSocket send = new DatagramSocket();
	    		 
	    		 DatabaseHelper db = new DatabaseHelper(mContext);
	    		 List<NetworkDevice> device_list = db.getAllConnectedDevices();
//	    		 InetAddress local = InetAddress.getByName("192.168.43.123");
	    		 
	    		 for(NetworkDevice d: device_list){
	    			 InetAddress local = InetAddress.getByName(d.getIpAddress());
		    		 int msg_length=messageStr.length();
		    		 byte[] message = messageStr.getBytes();
		    		 DatagramPacket p = new DatagramPacket(message, msg_length,local,server_port);
		    		 isConnected = true;
		    		 for(int i =0;i<10;i++){
		    			 send.send(p);
		    		 }
		    		 Log.d(TAG, "sending request");
		    		 send.close();
	    		 }
	    	 }catch(Exception e){
	    		 e.printStackTrace();
	    	 }

//	    	 udp.sendMessage("discover");
	    	 return 0L;
	     }

	     protected void onProgressUpdate(Integer... progress) {
//	         setProgressPercent(progress[0]);
	     }

	     protected void onPostExecute(Long result) {
//	         showDialog("Downloaded " + result + " bytes");
	    	 try {
	    		
				Thread.sleep(500);
				
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
	     }
	 }

	private class UDPReceiveTask extends AsyncTask<URL, Integer, Long> {
		DatagramSocket receive = null; 
		protected Long doInBackground(URL... urls) {
//	    	 UDPMulticast udp = new UDPMulticast(mContext, "224.3.29.71" , 10000);
	    	 
	    	 try{
	    		 while(true){
	    			 if(!isConnected)
	    				 continue;
		    		 String text;
		    		 int server_port = 10002;
		    		 byte[] message = new byte[1500];
		    		 DatagramPacket p = new DatagramPacket(message, message.length);
		    		 receive = new DatagramSocket(server_port);
		    		 receive.setSoTimeout(3000);
//		    		 receive.setReuseAddress(true);
		    		 Log.d(TAG, "Waiting to Receive");
		    		 receive.receive(p);
		    		 text = new String(message, 0, p.getLength());
		    		 Log.d(TAG,"message:" + text);
	                 receive.close();
	                 parseMessage(text);
	                 
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
	     }

	     protected void onPostExecute(Long result) {	    	 
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

					Log.d(TAG, "Got new oxygen: " + ox);
				}
				dataPlot.redraw();
				pulse_ox.remove(p);
	      }
	     }
	 }

	private String parseMessage(String msg){

		try{

			JSONObject obj = new JSONObject(msg);
			String packet_type = obj.getString("type");

			if(packet_type.equalsIgnoreCase("request_reply")){
				
				JSONArray arr = obj.getJSONArray("argv");
				
				for (int i = 0; i < arr.length(); i++){
					Object c = arr.get(i);
					JSONObject sens = new JSONObject(c.toString());
					JSONArray points_arr = sens.getJSONArray("readings");
					for(int j = 0 ; j < points_arr.length() ; j++){
						JSONArray point_tuple = (JSONArray) points_arr.get(j);
						Pair<Integer,Integer> p = new Pair(point_tuple.get(0),point_tuple.get(1));
						pulse_ox.add(p);
					}
					
				}



				}	
			

		}catch(Exception e){
			e.printStackTrace();
		}

		return msg;

	}

}