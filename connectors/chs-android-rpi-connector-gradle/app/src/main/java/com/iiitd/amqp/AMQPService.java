package com.iiitd.amqp;

import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.iiitd.chs.Constants;
import com.iiitd.chs.SettingsActivity;
import com.rabbitmq.client.AMQP.Queue.DeclareOk;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;
import com.rabbitmq.client.QueueingConsumer;

public class AMQPService extends IntentService{
	
	private  BlockingDeque<String> publishQueue ;
	private  BlockingDeque<String> subscribeQueue ;
	
	Thread subscribeThread;
	Thread publishThread;
	
	private ConnectionFactory factory;
	
	private static String TAG = "com.iiitd.amqp.AMQPService";

	private boolean isRunning;
	private Context mContext;
	
	
	public AMQPService(){
		super("amqpService");
	}
	
	
	@Override
	public void onCreate() {	
		super.onCreate();
		Log.v(TAG, "AMQP Service Created");
		
//		mContext = AMQPService.this;
		publishQueue = new LinkedBlockingDeque<String>();
		subscribeQueue = new LinkedBlockingDeque<String>();
		
		isRunning = false;
		//TODO from settings
		
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
		
		// start the publish thread
		publishToAMQP();
		//start the subscribe thread
		
		Subscribe();
		
		
		Log.v(TAG, "Started PUblish and Subscribe Thread");
	
	}

	@Override
	protected void onHandleIntent(Intent intent) {

		Log.v(TAG, "AMQP on Handle Event");

		String msg = intent.getStringExtra(Constants.AMQP_PUBLISH_MESSAGE);

		if(msg == null)
			return;
		try {
			Log.d(TAG,"[q] " + msg);
			publishQueue.putLast(msg);
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
						Connection connection = factory.newConnection();
						Channel ch = connection.createChannel();
						ch.confirmSelect();
						ch.queueDeclare("hello.world.queue.android", true, false, false, null);
//						ch.exchangeDeclare("chs", "direct", true);
						while (true) {
							String message = publishQueue.takeFirst();
							try{
//								ch.basicPublish("", topicname, null, message.getBytes());

								ch.basicPublish("", "hello.world.queue.android", MessageProperties.PERSISTENT_BASIC , message.getBytes());
								Log.d("ActivityHome", "[s] " + message);
								ch.waitForConfirmsOrDie();
							} catch (Exception e){
								Log.d("ActivityHome","[f] " + message);
								publishQueue.putFirst(message);
								throw e;
							}
						}
					} catch (InterruptedException e) {
						break;
					} catch (Exception e) {
						Log.d("", "Connection broken: " + e.getMessage());
						try {
							Thread.sleep(5000); //sleep and then try again
						} catch (InterruptedException e1) {
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
//						for(String topic: stopicList){
//							channel.queueBind(q.getQueue(), "chs", topic);
//						}
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


}
