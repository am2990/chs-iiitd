/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.amqpmodule.utils.impl;

import java.util.concurrent.TimeoutException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.amqpmodule.api.AmqpService;
import org.openmrs.module.amqpmodule.utils.Publisher;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;

/**
 * It is a default implementation of {@link AmqpService}.
 */
public class PublisherServiceImpl extends BaseOpenmrsService implements Publisher {
	
	protected final Log log = LogFactory.getLog(this.getClass());
	
	private String msg;
	private String topic;
	
	@Override
	public String getMsg() {
		return msg;
	}
	@Override
	public void setMsg(String msg) {
		this.msg = msg;
	}
	@Override
	public String getTopic() {
		return topic;
	}
	@Override
	public void setTopic(String topic) {
		this.topic = topic;
	}
	
	@Override
	public boolean PublisherCreateConnection() throws java.io.IOException
	    {

	        ConnectionFactory factory = new ConnectionFactory();
	        factory.setHost( "192.168.43.123" );
	        factory.setPort( 5672 );
	        factory.setUsername( "chs" );
	        factory.setPassword( "chs123" );
	        Connection connection = null;
	        try
	        {
	            connection = factory.newConnection();
	            Channel channel = connection.createChannel();

	            channel.exchangeDeclare( EXCHANGE_NAME, "direct" );

	            channel.basicPublish( EXCHANGE_NAME, topic, MessageProperties.PERSISTENT_TEXT_PLAIN, msg.getBytes() );
	            System.out.println( " [x] Sent '" + msg + "'" );

	            channel.close();

	        }
	        catch ( TimeoutException e )
	        {
	            System.out.println( "Connection Timed out" );
	            e.printStackTrace();
	        }
	        
	        connection.close();

	        return true;
	    }

	
	
	   
}