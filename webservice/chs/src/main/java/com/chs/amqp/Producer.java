package com.chs.amqp;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Producer {

	@SuppressWarnings("resource")
	public static void main(String[] args) throws Exception {
		new AnnotationConfigApplicationContext(ProducerConfiguration.class);
	}

}
