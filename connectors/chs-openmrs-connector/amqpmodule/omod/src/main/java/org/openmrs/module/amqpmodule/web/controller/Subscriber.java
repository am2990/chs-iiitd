package org.openmrs.module.amqpmodule.web.controller;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.TimeoutException;

import org.openmrs.api.context.Context;
import org.openmrs.module.amqpmodule.AmqpModule;
import org.openmrs.module.amqpmodule.api.AmqpService;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;


public class Subscriber
    implements Runnable
{

    private Thread t;

    private final String EXCHANGE_NAME = "chs";

    private String[] subscribe;

    public Subscriber( String[] subscribe )
    {
        this.subscribe = subscribe;
    }

    public void run()
    {

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost( "192.168.43.123" );
//        factory.setHost( "192.168.43.123" );
        factory.setPort( 5672 );
        factory.setUsername( "chs" );
        factory.setPassword( "chs123" );

        Connection connection;
        QueueingConsumer consumer = null;
        ;
        try
        {
            connection = factory.newConnection();
            Channel channel = connection.createChannel();

            channel.exchangeDeclare( EXCHANGE_NAME, "direct" );
            // Queue made durable so that it receives message even during mode
            // is not running
            boolean durable = true;
            String queueName = channel.queueDeclare( "aiims", durable, false, false, null ).getQueue();

            channel.queueBind( queueName, EXCHANGE_NAME, "hw_doc" );
            for ( String severity : subscribe )
            {
                channel.queueBind( queueName, EXCHANGE_NAME, severity );
            }

            System.out.println( " [*] Waiting for messages. To exit press CTRL+C" );

            consumer = new QueueingConsumer( channel );
            channel.basicConsume( queueName, true, consumer );
        }
        catch ( IOException e )
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch ( TimeoutException e )
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        while ( true )
        {
            QueueingConsumer.Delivery delivery;
            try
            {
            	if(consumer.nextDelivery()!=null)
            	{
                delivery = consumer.nextDelivery();
                
                String message = new String( delivery.getBody() );
                String routingKey = delivery.getEnvelope().getRoutingKey();
                System.out.println( "[x] Received '" + routingKey + "':'" + message + "'" );
                
                try{
                    Context.openSession();
                    Context.authenticate("admin", "Admin123");
                    AmqpService amqpService = Context.getService(AmqpService.class);
                    AmqpModule amqp = new AmqpModule();
                    String[] arr = message.split(":");
                    amqp.setIsVisited(0);
                    amqp.setUuid(arr[1].substring(0, 10));
                    amqp.setName(arr[2]);
                    amqp.setObs(arr[3]+" "+ arr[4] +" " +arr[5]+ " "+arr[6]+" "+ arr[7]);
                    amqpService.addPerson(amqp);
                }finally{
                    Context.closeSession();
                }
                
            	}
                // service.saveDetails( amqp );

            }
            catch ( Exception e )
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public void start()
    {
        System.out.println( "Subscriber For ICMR Started" );
        if ( t == null )
        {
            t = new Thread( this );
            t.start();
        }
    }

}