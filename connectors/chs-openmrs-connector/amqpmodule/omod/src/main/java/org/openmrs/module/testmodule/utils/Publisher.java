package org.openmrs.module.testmodule.utils;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;


public class Publisher
{

    private final String EXCHANGE_NAME = "chs";

    private String msg;

    private String topic;

    public Publisher( String topic, String msg )
    {
        this.topic = topic;
        this.msg = msg;
    }

    public boolean Publish()
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

            channel.exchangeDeclare( EXCHANGE_NAME, "direct", true );

            channel.basicPublish( EXCHANGE_NAME, topic, MessageProperties.PERSISTENT_TEXT_PLAIN, msg.getBytes() );
            System.out.println( " [x] Sent '" + msg + "'" );

            channel.close();

        }
        catch ( Exception e )
        {
            System.out.println( "Connection Timed out" );
            e.printStackTrace();
        }
        
        try {
			connection.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

        return true;
    }

}