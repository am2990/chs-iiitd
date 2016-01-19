package org.openmrs.module.testmodule.utils;

import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public abstract class AbstractRabbitConfiguration {
	
	/**
	 * The server-side queue that provides return communication based on device id.
	 */
	protected static String SELECTION_EXCHANGE_NAME = "exchange.openmrs.return";

	/**
	 * The server-side queue that provides receives query and device resources.
	 */
	protected static String RESOURCE_QUERY_EXCHANGE_NAME = "exchange.openmrs.data";
	

	/**
	 * Shared topic exchange used for publishing any market data (e.g. stock quotes) 
	 */
	protected static String MARKET_DATA_EXCHANGE_NAME = "app.stock.marketdata";

	
	/**
	 * The server-side consumer's queue that provides point-to-point semantics for stock requests.
	 */
	protected static String STOCK_REQUEST_QUEUE_NAME = "app.stock.request";
	protected static String PATIENT_DATA_QUEUE_NAME = "patient.data";

	/**
	 * Key that clients will use to send to the stock request queue via the default direct exchange.
	 */
	protected static String STOCK_REQUEST_ROUTING_KEY = STOCK_REQUEST_QUEUE_NAME;
	
	@Value("${amqp.port:5672}") 
	private int port = 5672;
	

	protected abstract void configureRabbitTemplate(RabbitTemplate template);

	@Bean
	public ConnectionFactory connectionFactory() {
		//TODO make it possible to customize in subclasses.
		CachingConnectionFactory connectionFactory = new CachingConnectionFactory("localhost");
		connectionFactory.setUsername("chs");
		connectionFactory.setPassword("chs123");
		connectionFactory.setPort(port);
		return connectionFactory;
	}

	@Bean 
	public RabbitTemplate rabbitTemplate() {
		RabbitTemplate template = new RabbitTemplate(connectionFactory());
//		template.setMessageConverter(jsonMessageConverter()); //DNT
		configureRabbitTemplate(template);
		return template;
	}

	@Bean
	public MessageConverter jsonMessageConverter() {
		return new JsonMessageConverter();
	}
	
	@Bean
	public TopicExchange marketDataExchange() {
		return new TopicExchange(MARKET_DATA_EXCHANGE_NAME);
	}

	@Bean
	public TopicExchange selectionExchange() {
		return new TopicExchange(SELECTION_EXCHANGE_NAME);
	}

	@Bean
	public TopicExchange resourceQueryExchange() {
		return new TopicExchange(RESOURCE_QUERY_EXCHANGE_NAME);
	}

	/**
	 * @return the admin bean that can declare queues etc.
	 */
	@Bean
	public AmqpAdmin amqpAdmin() {
		RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory());
		return rabbitAdmin ;
	}

}
