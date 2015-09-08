package com.iiitd.amqp;

import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.AMQP.Queue.DeclareOk;

public class AMQPImpl {
	
	private static BlockingDeque<String> publishQueue ;
	private static BlockingDeque<String> subscribeQueue ;

	Thread subscribeThread;
	Thread publishThread;
	
	private ConnectionFactory factory;
	
	private static String TAG = "com.iiitd.amqp.AMQPImpl";
	
	public AMQPImpl(){
		
		publishQueue = new LinkedBlockingDeque<String>();
		subscribeQueue = new LinkedBlockingDeque<String>();
		
		String uri = "amqp://chs:chs123@192.168.48.21:5672/%2f";
		
		factory = new ConnectionFactory();
		try {
			factory.setAutomaticRecoveryEnabled(false);	
			factory.setUri(uri);
		} catch (KeyManagementException | NoSuchAlgorithmException | URISyntaxException e1) {
			e1.printStackTrace();
		}
		
		publishToAMQP();
	}
	
	
	public static void addToPublish(String msg){
		
		try {
			Log.d(TAG,"[q] " + msg);
			publishQueue.putLast(msg);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public static List<String> getNotifications(){
		List<String> notifications = new ArrayList<String>();
		subscribeQueue.drainTo(notifications);
		return notifications;
	}
	
	
	void Subscribe(final Handler handler)
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

							Message msg = handler.obtainMessage();
							Bundle bundle = new Bundle();
							String temp = routingKey + ":" + message;
							bundle.putString("msg", temp);
//							bundle.putString("msg", message);
							msg.setData(bundle);
							subscribeQueue.addLast(msg.toString());
							handler.sendMessage(msg);
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
						ch.exchangeDeclare("chs", "direct");
						while (true) {
							String message = publishQueue.takeFirst();
							try{
//								ch.basicPublish("", topicname, null, message.getBytes());
								ch.basicPublish("chs", "hw_doc", null, message.getBytes());
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
}
