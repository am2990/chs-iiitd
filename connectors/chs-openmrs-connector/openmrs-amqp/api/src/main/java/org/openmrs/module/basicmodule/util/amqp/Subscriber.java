package org.openmrs.module.basicmodule.util.amqp;

import java.io.IOException;
import java.util.concurrent.TimeoutException;


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
        // factory.setHost( "192.168.48.21" );
        factory.setHost( "192.168.43.123" );
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
                delivery = consumer.nextDelivery();

                String message = new String( delivery.getBody() );
                String routingKey = delivery.getEnvelope().getRoutingKey();

                System.out.println( "[x] Received '" + routingKey + "':'" + message + "'" );
//                DHIS2ReportingService service = Context.getService( DHIS2ReportingService.class );
//                AmqpDetails amqp = new AmqpDetails();
//                amqp.setFirstName( "lalu" );
//                amqp.setLastName( "prasad" );
//                amqp.setGender( "Male" );
//                amqp.setDob( new Date( "21-02-1912" ) );
//                amqp.setCreatedDate( new Date( "21-12-2015" ) );
//                amqp.setObs( message );
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