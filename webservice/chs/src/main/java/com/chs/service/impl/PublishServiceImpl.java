package com.chs.service.impl;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.chs.amqp.ProducerConfiguration;
import com.chs.service.PublishService;

public class PublishServiceImpl implements PublishService{

	ApplicationContext applicationContext;
	@Override
	public boolean createTopic(String topicName) {
		System.out.println("inside create topic with topicname:" + topicName);
		try {
			applicationContext = new AnnotationConfigApplicationContext(ProducerConfiguration.class);
			ProducerConfiguration producerConfig = (ProducerConfiguration)applicationContext.getBean(ProducerConfiguration.class);
			producerConfig.setQueueName(topicName);
			//producerConfig.sendMessage("test message");
			return true;
		}catch(Exception e) {
			e.printStackTrace();
			return false;
		}
		
	}

	@Override
	public boolean publishData(String topicname, String data) {
		
		//TODO get objects from XML and send to queues based on concept name
		
		System.out.println("inside Publish Data:" + topicname +"and data:"+data);
		try {
			applicationContext = new AnnotationConfigApplicationContext(ProducerConfiguration.class);
			ProducerConfiguration producerConfig = (ProducerConfiguration)applicationContext.getBean(ProducerConfiguration.class);
			producerConfig.setQueueName(topicname);
			producerConfig.sendMessage(data);
			return true;
		}catch(Exception e) {
			e.printStackTrace();
			return false;
		}

	}

}
