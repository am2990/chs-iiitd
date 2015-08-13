package org.openmrs.module.amqpmodule.utils;

import java.util.concurrent.TimeoutException;


import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;

import org.openmrs.api.OpenmrsService;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface Publisher extends OpenmrsService
{
	public final String EXCHANGE_NAME = "chs";

   
    public void setMsg(String id);
	public String getMsg();

	public void setTopic(String message);
	public String getTopic();


	 public boolean PublisherCreateConnection() throws java.io.IOException;
}
/*
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
        throws java.io.IOException
    {

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost( "192.168.48.21" );
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

}*/