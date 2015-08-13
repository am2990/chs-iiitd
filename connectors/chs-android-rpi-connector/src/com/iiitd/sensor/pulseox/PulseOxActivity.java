package com.iiitd.sensor.pulseox;

import java.util.List;

import org.opendatakit.sensors.service.BaseActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
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

public class PulseOxActivity extends BaseActivity{
	
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

		private DataProcessor pulseOxProcessor;

		private boolean isConnected;
		
		private Context mContext;
		
		// Used to only play the beep once when an getting an accurate reading
		private boolean playBeep = true;

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

			isConnected = false;
			mAnswerOx = 0;
			mAnswerPulse = 0;
			
		

			Log.d(TAG, "on create");
		}
		
		private void returnValuetoCaller() {
//			Toast toast = Toast.makeText(this, "returning value", Toast.LENGTH_LONG);
//			toast.show();
			Intent intent = new Intent();
			intent.putExtra(NoninPacket.OX, mAnswerOx);
			intent.putExtra(NoninPacket.PULSE, mAnswerPulse);
			setResult(RESULT_OK, intent);
			finish();
		}

		
		private void processData() {

			// ensure sensor has been connected
			if (!isConnected) {
				return;
			}
			runOnUiThread(new Runnable() {
				public void run() {
					try {
						List<Bundle> data = getSensorData(pulseOxId, 1);
						if (data != null) {
							
							for (Bundle b : data) {
								try {
									Thread.sleep(100);
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								if (b.containsKey(NoninPacket.CONNECTED)) {
									boolean connected = b.getBoolean(NoninPacket.CONNECTED);
									
									if (!connected) {
										pulseTxt.setText(DEVICE_NOT_CONNECTED);
										oxTxt.setText(DEVICE_NOT_CONNECTED);
										statusTxt.setText(DEVICE_NOT_CONNECTED);
										continue;
									}
								}
								if (b.containsKey(NoninPacket.UNUSABLE)) {
									boolean unusable = b.getBoolean(NoninPacket.UNUSABLE);
									if (unusable) {
										pulseTxt.setTextColor(Color.MAGENTA);
										oxTxt.setTextColor(Color.MAGENTA);
										statusTxt.setTextColor(Color.RED);
										statusTxt.setText(DATA_INACCURATE);
										recordPulseOxButton.setEnabled(false);
										recordPulseOxButton.setTextColor(Color.GRAY);
										playBeep=true;
										Toast toast = Toast.makeText(mContext, "unusable", Toast.LENGTH_LONG);
										toast.show();
									} else {
										pulseTxt.setTextColor(PULSE_COLOR);
										oxTxt.setTextColor(Color.BLUE);
										statusTxt.setTextColor(Color.BLACK);
										statusTxt.setText(DATA_GOOD);
										recordPulseOxButton.setEnabled(true);
										recordPulseOxButton.setTextColor(Color.BLUE);
										Toast toast = Toast.makeText(mContext, "data good", Toast.LENGTH_SHORT);
										toast.show();
										if (playBeep) {
											MediaPlayer mediaPlayer = MediaPlayer.create(
													PulseOxActivity.this, R.raw.beep);
											mediaPlayer.start();
											playBeep = false;
											Toast toast2 = Toast.makeText(mContext, "play beep", Toast.LENGTH_LONG);
											toast2.show();
											continue;
										}
									}
		    						
								}
									
								if (b.containsKey(NoninPacket.PULSE)) {
									int pulse = b.getInt(NoninPacket.PULSE);
									if(pulse == 511) {
										pulseTxt.setText("Error");
										Toast toast = Toast.makeText(mContext, "error 511", Toast.LENGTH_SHORT);
										toast.show();
									} else {
										pulseTxt.setText(Integer.toString(pulse));
									}
									mAnswerPulse = pulse;
									Log.d(TAG, "Got new pulse: " + pulse);
								}
								if (b.containsKey(NoninPacket.OX)) {
									int ox = b.getInt(NoninPacket.OX);
									if(ox == 127) {
										mAnswerOx = -1;
										oxTxt.setText("Error");
										Toast toast = Toast.makeText(mContext, "error 127", Toast.LENGTH_SHORT);
										toast.show();
									} else {
										mAnswerOx = 99;
										oxTxt.setText("99");
//										oxTxt.setText(Integer.toString(ox));
//										mAnswerOx = ox;
									}
									
									//vibrator.vibrate(75);
									Log.d(TAG, "Got new oxygen: " + ox);
								}
								
								if (b.containsKey(NoninPacket.PLETHYSMOGRAPHIC)) {
									int[] pleths = b
											.getIntArray(NoninPacket.PLETHYSMOGRAPHIC);
									for (int i = 0; i < NoninPacket.PACKET_SIZE; i++) {
										plenthSeries.addLast(dataPointCounter,
												pleths[i]);
										dataPointCounter++;

										if (dataPointCounter > MAX_DATAPOINTS) {
											plenthSeries.removeFirst();
										}
									}
									dataPlot.redraw();

									mAnswerPulse = b.getInt(NoninPacket.PULSE);
									mAnswerOx = b.getInt(NoninPacket.OX);
									
									returnValuetoCaller();

								}
							}

							
							Toast toast = Toast.makeText(mContext, "loop exit", Toast.LENGTH_SHORT);
							toast.show();
							
							return;
						}
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						System.out.println("caught exception");
						e.printStackTrace();
					}
				}
			});
		}

		
		private class DataProcessor extends AsyncTask<Void, Void, Void> {

			private PulseOxActivity app;

			public DataProcessor(PulseOxActivity app) {
				this.app = app;
				Log.d(TAG, "Data Processor Construtor");
			}

			@Override
			protected Void doInBackground(Void... params) {
				while (!isCancelled()) {
					app.processData();
					try {
						Thread.sleep(250);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				return null;
			}

		}

}
