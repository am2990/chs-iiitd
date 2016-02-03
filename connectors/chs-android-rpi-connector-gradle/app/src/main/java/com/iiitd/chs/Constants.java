package com.iiitd.chs;

public final class Constants {
    
    // Defines a custom Intent action
    public static final String BROADCAST_ACTION = "com.iiitd.chs.amqpIntent.BROADCAST";
    
    // Defines the key for the status "extra" in an Intent
    public static final String AMQP_SUBSCRIBED_MESSAGE = "com.iiitd.chs.amqpIntent.SUBSCRIBE";
    
    public static final String AMQP_PUBLISH_MESSAGE = "com.iiitd.chs.amqpIntent.PUBLISH";

    public static final String PATIENT = "intent.patient";
    public static final String NEW_PATIENT = "intent.new_patient";
    public static final String PATIENT_OBS = "intent.patient_obs";

    public static final String AMQP_PUBLISH_QUEUE = "intent.amqp.publish_queue";
}