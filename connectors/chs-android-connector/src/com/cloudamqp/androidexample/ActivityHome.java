package com.cloudamqp.androidexample;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TextView.BufferType;

import com.cloudamqp.R;
import com.cloudamqp.StartActivity;
import com.rabbitmq.client.AMQP.Queue.DeclareOk;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;

public class ActivityHome extends Activity implements OnItemSelectedListener {
	
	private String username, password;
	private String topicname = "";
	private String[] stopicList;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Log.d("ActivityHome", "reached activity home");
		
		Intent intent = getIntent();
        String ptopics = intent.getStringExtra(StartActivity.PTOPICS);
        ptopics = ptopics.substring(0, ptopics.trim().length()-1); //take care of trailing coma
        String stopics = intent.getStringExtra(StartActivity.STOPICS);
        stopics = stopics.substring(0, (stopics.trim().length()-1));
        username = intent.getStringExtra(StartActivity.USERNAME);
        password = intent.getStringExtra(StartActivity.USERNAME);
        
        Log.d("ActivityHome", "Publish Topics-"+ptopics.trim());
        Log.d("ActivityHome", "Subscribe Topics-"+stopics.trim());
        String[] topicList = ptopics.split(",");
        stopicList = stopics.split(",");
        
        TextView tv = (TextView) findViewById(R.id.textViewUser);
        tv.setText("Hello "+username, BufferType.NORMAL);
        Spinner spinner = (Spinner) findViewById(R.id.topics_spinner);
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, 
        		android.R.layout.simple_spinner_item, topicList); //selected item will look like a spinner set from XML
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerArrayAdapter);
        
        setupConnectionFactory();		
		publishToAMQP();
		setupPubButton();
		
		final Handler incomingMessageHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				String message = msg.getData().getString("msg");
				TextView tv = (TextView) findViewById(R.id.textView);
				Date now = new Date();
				SimpleDateFormat ft = new SimpleDateFormat ("hh:mm:ss");
				tv.append(ft.format(now) + ' ' + message + '\n');
			}
		};
		subscribe(incomingMessageHandler);
	}

	void setupPubButton() {
		Button button = (Button) findViewById(R.id.publish);
		button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				
				Spinner s = (Spinner)findViewById(R.id.topics_spinner);
				String topicSelected = (String)s.getSelectedItem();
				topicname = topicSelected;
		        Log.d("ActivityHome", "Topic Name:"+topicSelected);
				EditText et = (EditText) findViewById(R.id.text);
				String pubVal = et.getText().toString();
				if(pubVal.trim().equalsIgnoreCase(""))
		        {
		            et.setHint("Please Enter Value");//it gives user to hint
		            et.setError("This Field is Mandatory");//it gives user to info message //use any one //
		        }
				else{
					Date now = new Date();
					SimpleDateFormat ft = new SimpleDateFormat ("hh:mm:ss");
					String p = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><Report>  <DataUnit type=\"daily\">  	<concept>"+topicname+"</concept>        <gender>None</gender>        <timestamp>"+ft.format(now)+"</timestamp>        <value>"+pubVal+"</value>   </DataUnit></Report>"; 
					publishMessage(pubVal);
					et.setText("");
				}
			}
		});
	}
	
	Thread subscribeThread;
	Thread publishThread;
	@Override
	protected void onDestroy() {
		super.onDestroy();
		publishThread.interrupt();
		subscribeThread.interrupt();
	}
	
	
	public void onItemSelected(AdapterView<?> parent, View view, 
            int pos, long id) {
        // An item was selected. You can retrieve the selected item using
         String selectedTopic = (String) parent.getItemAtPosition(pos);
         Log.d("ActivityHome", "User Selected Topics-"+selectedTopic);
         
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }
	
	
	private BlockingDeque<String> queue = new LinkedBlockingDeque<String>();
	void publishMessage(String message) {
		//Adds a message to internal blocking queue
		try {
			Log.d("ActivityHome","[q] " + message);
			queue.putLast(message);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	ConnectionFactory factory = new ConnectionFactory();
	private void setupConnectionFactory() {
		String uri = "amqp://sahyog:sahyog@192.168.48.21:5672/%2f";
		try {
			factory.setAutomaticRecoveryEnabled(false);	
			factory.setUri(uri);
		} catch (KeyManagementException | NoSuchAlgorithmException | URISyntaxException e1) {
			e1.printStackTrace();
		}
	}

	void subscribe(final Handler handler)
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
//						channel.queueBind(q.getQueue(), "amq.fanout", "chat");
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
				            
							Log.d("ActivityHome","[r] " + routingKey + ":" + message);

							Message msg = handler.obtainMessage();
							Bundle bundle = new Bundle();
							String temp = routingKey + ":" + message;
							bundle.putString("msg", temp);
//							bundle.putString("msg", message);
							msg.setData(bundle);
							handler.sendMessage(msg);
						}
					} catch (InterruptedException e) {
						break;
					} catch (Exception e1) {
						Log.d("ActivityHome", "Connection broken: " + e1.getClass().getName());
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
							String message = queue.takeFirst();
							try{
//								ch.basicPublish("", topicname, null, message.getBytes());
								ch.basicPublish("chs", topicname, null, message.getBytes());
								Log.d("ActivityHome", "[s] " + message);
								ch.waitForConfirmsOrDie();
							} catch (Exception e){
								Log.d("ActivityHome","[f] " + message);
								queue.putFirst(message);
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
