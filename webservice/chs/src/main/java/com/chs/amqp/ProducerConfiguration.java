package com.chs.amqp;



import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class ProducerConfiguration{

	
	protected String helloWorldQueueName = "hello.world.not";
	private RabbitTemplate template = null; 
	
	

	public void setQueueName(String queueName) {
		this.helloWorldQueueName = queueName;
	//	this.rabbitTemplate = rabbitTemplate();
	}
	
	public String getQueueName() {
		return this.helloWorldQueueName;
	}

	
	public RabbitTemplate rabbitTemplate() {
		template = new RabbitTemplate(connectionFactory());
		System.out.println("Setting routing key:"+this.helloWorldQueueName);
		template.setRoutingKey(this.helloWorldQueueName);
		return template;
	}

	@Bean
	public ConnectionFactory connectionFactory() {
		CachingConnectionFactory connectionFactory = new CachingConnectionFactory("localhost");
		connectionFactory.setUsername("guest");
		connectionFactory.setPassword("guest");
		return connectionFactory;
	}


	public boolean sendMessage(String data) {
		//TODO add bean dependency on PublishData instead of direct data
		rabbitTemplate();
		template.convertAndSend(data);
		return true;
	}


	

}
