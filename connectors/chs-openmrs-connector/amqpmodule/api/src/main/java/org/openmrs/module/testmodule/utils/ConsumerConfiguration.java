package org.openmrs.module.testmodule.utils;

import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConsumerConfiguration extends HelloWorldConfiguration {

	@Bean
	public SimpleMessageListenerContainer listenerContainer() {
		System.out.println("Listener Startted");
		SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
		container.setConnectionFactory(rabbitmqConnectionFactory());
		container.setQueueNames(this.helloWorldQueueName);
		container.setMessageListener(new MessageListenerAdapter(new HelloWorldHandler()));
		return container;
	}

}
