package com.iiitd.amqp;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.iiitd.chs.Constants;
import com.iiitd.chs.SettingsActivity;
import com.iiitd.sqlite.helper.Pair;
import com.rabbitmq.client.AMQP.Queue.DeclareOk;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;
import com.rabbitmq.client.QueueingConsumer;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

public class AMQPService extends IntentService{

	private  BlockingDeque<Pair<String, String>> publishQueue ;
	private  BlockingDeque<String> subscribeQueue ;

	public static String[] publishList;
	public static ArrayList<String> stopicList = new ArrayList<>();
	public static ArrayList<String> ptopicList = new ArrayList<>();

	Thread subscribeThread;
	Thread publishThread;

	private ConnectionFactory factory;

	private static String TAG = "com.iiitd.amqp.AMQPService";

	private static boolean isRunning;
	private Context mContext;


	public AMQPService(){
		super("amqpService");
	}


	@Override
	public void onCreate() {
		super.onCreate();
		Log.v(TAG, "AMQP Service Created");

//		mContext = AMQPService.this;
		publishQueue = new LinkedBlockingDeque<Pair<String, String>>();
		subscribeQueue = new LinkedBlockingDeque<String>();

		isRunning = false;

		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
		String username = sharedPref.getString(SettingsActivity.SETTINGS_USERNAME, "");
		String password = sharedPref.getString(SettingsActivity.SETTINGS_PASSWORD, "");
		String ipaddress = sharedPref.getString(SettingsActivity.SETTINGS_IPADDRESS, "");

		String uri = "amqp://"+ username +":"+ password +"@"+ ipaddress +":5672/%2f";
		Log.d(TAG, uri);
		factory = new ConnectionFactory();
		try {
			factory.setAutomaticRecoveryEnabled(false);
			factory.setUri(uri);
		} catch (KeyManagementException | NoSuchAlgorithmException | URISyntaxException e1) {
			e1.printStackTrace();
		}

		String chs_username = "hw1"; String chs_password = "hw1";
		String[] stringUrl = {"http://192.168.43.123:8080/chs/api/topics/publisher?username="+chs_username+"&pass="+chs_password,
				"http://192.168.43.123:8080/chs/api/topics/subscriber?username="+chs_username+"&pass="+chs_password,
		};

		ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected()) {
			new DownloadWebpageTask().execute(stringUrl);
		} else {
			Toast.makeText(mContext, "No network connection available!!", Toast.LENGTH_LONG).show();
		}


		// start the publish thread
		publishToAMQP();
		//start the subscribe thread

		Subscribe();


		Log.v(TAG, "Started PUblish and Subscribe Thread");

	}

	@Override
	public int onStartCommand (Intent intent, int flags, int startId) {
		Log.v(TAG, "AMQP onStartCommand");

		String msg = intent.getStringExtra(Constants.AMQP_PUBLISH_MESSAGE);
		String queue = intent.getStringExtra(Constants.AMQP_PUBLISH_QUEUE);

		if(msg == null || queue == null)
			return 0;
		try {
			Pair<String, String> msq_queue = new Pair<>(msg, queue);
			Log.d(TAG,"[q] " + msg +" q=" + queue);
			publishQueue.putLast(msq_queue);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return 1;
	}

	@Override
	protected void onHandleIntent(Intent intent) {

		Log.v(TAG, "AMQP on Handle Event");

		String msg = intent.getStringExtra(Constants.AMQP_PUBLISH_MESSAGE);
		String queue = intent.getStringExtra(Constants.AMQP_PUBLISH_QUEUE);
		Pair<String, String> msq_queue = new Pair<>(msg, queue);
		if(msg == null)
			return;
		try {
			Log.d(TAG,"[q] " + msg +" q=" + queue);
			publishQueue.putLast(msq_queue);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}


	public void publishToAMQP()
	{
		publishThread = new Thread(new Runnable() {
			@Override
			public void run() {
				while(true) {
					try {
						isRunning=true;
						Connection connection = factory.newConnection();
						Channel ch = connection.createChannel();
						ch.confirmSelect();
						ch.queueDeclare("healthworker_to_doctor", true, false, false, null);
//						ch.exchangeDeclare("chs", "direct", true);
						while (true) {
							Pair msg_queue = publishQueue.takeFirst();
							String message = (String)msg_queue.getLeft();
							String queue = (String)msg_queue.getRight();
							try{
//								ch.basicPublish("", topicname, null, message.getBytes());

								ch.basicPublish("", queue, MessageProperties.PERSISTENT_BASIC, message.getBytes());
								Log.d("ActivityHome", "[s] " + message);
								ch.waitForConfirmsOrDie();
							} catch (Exception e){
								Log.d("ActivityHome","[f] " + message);
								publishQueue.putFirst(msg_queue);
								throw e;
							}
						}
					} catch (InterruptedException e) {
						isRunning=false;
						break;
					} catch (Exception e) {
						Log.d("", "Connection broken: " + e.getMessage());
						try {
							Thread.sleep(5000); //sleep and then try again
						} catch (InterruptedException e1) {
							isRunning=false;
							break;
						}
					}
				}
			}
		});
		publishThread.start();
	}


	void Subscribe()
	{
		subscribeThread = new Thread(new Runnable() {
			@Override
			public void run() {
				while(true) {
					try {
						Connection connection = factory.newConnection();
						Channel channel = connection.createChannel();
						channel.basicQos(1);
						DeclareOk q = channel.queueDeclare();
						channel.queueBind(q.getQueue(), "chs", "doc_hw");
						for(String topic: stopicList){
							channel.queueBind(q.getQueue(), "chs", topic);
						}
						QueueingConsumer consumer = new QueueingConsumer(channel);
						channel.basicConsume(q.getQueue(), true, consumer);

						// Process deliveries
						while (true) {
							QueueingConsumer.Delivery delivery = consumer.nextDelivery();

							String message = new String(delivery.getBody());
							String routingKey = delivery.getEnvelope().getRoutingKey();

							Log.d(TAG,"[r] " + routingKey + ":" + message);

//							Message msg = handler.obtainMessage();
							Bundle bundle = new Bundle();
							String temp = routingKey + ":" + message;
							bundle.putString("msg", temp);
//							bundle.putString("msg", message);
//							msg.setData(bundle);
							//subscribeQueue.addLast(msg.toString());
							Intent localIntent = new Intent(Constants.BROADCAST_ACTION);

							localIntent.putExtra(Constants.AMQP_SUBSCRIBED_MESSAGE, message);
							// Broadcasts the Intent to receivers in this app.
							LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(localIntent);


//							handler.sendMessage(msg);
						}
					} catch (InterruptedException e) {
						break;
					} catch (Exception e1) {
						Log.d(TAG, "Connection broken: " + e1.getClass().getName());
						try {
							Thread.sleep(4000); //sleep and then try again
						} catch (InterruptedException e) {
							break;
						}
					}
				}
			}
		});
		subscribeThread.start();
	}

	private class DownloadWebpageTask extends AsyncTask<String, Void, List<String>> {
		@Override
		protected List<String> doInBackground(String... urls) {

			// params comes from the execute() call: params[0] is the url.
			try {
				return downloadUrl(urls);
			} catch (IOException e) {
				return null;
			}
		}
		@Override
		protected void onPostExecute(List<String> result) {

			String[] publishList = (result.get(0).substring(0, result.get(0).length()-1)).split(",");
			for(int i = 0; i< publishList.length; i++){
				ptopicList.add(publishList[i]);
			}
			String[] subscribeList = (result.get(1).substring(0, result.get(1).length()-1)).split(",");
			for(int i = 0; i < subscribeList.length; i++){
				stopicList.add(subscribeList[i]);
			}
			Log.d(TAG, "resulta=");


		}
	}

	// Given a URL, establishes an HttpUrlConnection and retrieves
	// the web page content as a InputStream, which it returns as
	// a string.
	private List<String> downloadUrl(String... urls) throws IOException {
		InputStream is = null;
		// Only display the first 500 characters of the retrieved
		// web page content.
		int len = 500;

		try {
			List<String> responseList = new ArrayList<>();
			for(int i =0 ; i< urls.length; i++){
				URL url = new URL(urls[i]);
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.setReadTimeout(10000 /* milliseconds */);
				conn.setConnectTimeout(15000 /* milliseconds */);
				conn.setRequestMethod("GET");
				conn.setDoInput(true);
				// Starts the query
				conn.connect();
				int response = conn.getResponseCode();

				is = conn.getInputStream();

				// Convert the InputStream into a string
				String contentAsString = readIt(is, len);
				Log.d(TAG, "The response code and response: " + response +"-" + contentAsString.trim());
				responseList.add(contentAsString.trim());
			}
			return responseList;

			// Makes sure that the InputStream is closed after the app is
			// finished using it.
		} finally {
			if (is != null) {
				is.close();
			}
		}
	}

	// Reads an InputStream and converts it to a String.
	public String readIt(InputStream stream, int len) throws IOException, UnsupportedEncodingException {
		Reader reader = null;
		reader = new InputStreamReader(stream, "UTF-8");
		char[] buffer = new char[len];
		reader.read(buffer);
		return new String(buffer);
	}

}

