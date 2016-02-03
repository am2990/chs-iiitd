package org.openmrs.module.testmodule.utils;

import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HelloWorldConfiguration {

	protected static String MARKET_DATA_EXCHANGE_NAME = "chs";
	
	protected final String helloWorldQueueName = "healthworker_to_doctor";

	@Bean
	public ConnectionFactory rabbitmqConnectionFactory() {
		// TODO fetch this from settings
		CachingConnectionFactory connectionFactory = new CachingConnectionFactory("192.168.43.123");
		connectionFactory.setUsername("chs");
		connectionFactory.setPassword("chs123");
		return connectionFactory;
	}
	
	@Bean
	public AmqpAdmin amqpAdmin() {
		return new RabbitAdmin(rabbitmqConnectionFactory());
	}

	@Bean
	public RabbitTemplate rabbitTemplate() {
		RabbitTemplate template = new RabbitTemplate(rabbitmqConnectionFactory());
		//The routing key is set to the name of the queue by the broker for the default exchange.
		template.setRoutingKey(this.helloWorldQueueName);
		//Where we will synchronously receive messages from
		template.setQueue(this.helloWorldQueueName);
		return template;
	}

	@Bean
	// Every queue is bound to the default direct exchange
	public Queue helloWorldQueue() {
		return new Queue(this.helloWorldQueueName);
	}
	
//	@Bean
//	public TopicExchange chsExchange() {
//		return new TopicExchange(MARKET_DATA_EXCHANGE_NAME);
//	}
//
//	@Bean
//	public Binding marketDataBinding() {		
//		return BindingBuilder.bind(helloWorldQueue()).to(chsExchange()).with("hw_doc");
//	}
//	
	
	/*
	@Bean 
	public Binding binding() {
		return declare(new Binding(helloWorldQueue(), defaultDirectExchange()));
	}*/
	
	/*	
	@Bean
	public TopicExchange helloExchange() {
		return declare(new TopicExchange("hello.world.exchange"));
	}*/
	
	/*
	public Queue declareUniqueQueue(String namePrefix) {
		Queue queue = new Queue(namePrefix + "-" + UUID.randomUUID());
		rabbitAdminTemplate().declareQueue(queue);
		return queue;
	}
	
	// if the default exchange isn't configured to your liking....
	@Bean Binding declareP2PBinding(Queue queue, DirectExchange exchange) {
		return declare(new Binding(queue, exchange, queue.getName()));
	}
	
	@Bean Binding declarePubSubBinding(String queuePrefix, FanoutExchange exchange) {
		return declare(new Binding(declareUniqueQueue(queuePrefix), exchange));
	}
	
	@Bean Binding declarePubSubBinding(UniqueQueue uniqueQueue, TopicExchange exchange) {
		return declare(new Binding(uniqueQueue, exchange));
	}
	
	@Bean Binding declarePubSubBinding(String queuePrefix, TopicExchange exchange, String routingKey) {
		return declare(new Binding(declareUniqueQueue(queuePrefix), exchange, routingKey));
	}*/

}
