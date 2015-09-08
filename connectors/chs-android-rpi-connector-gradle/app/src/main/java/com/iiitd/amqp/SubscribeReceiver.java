package com.iiitd.amqp;

import com.iiitd.chs.Constants;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class SubscribeReceiver extends BroadcastReceiver{
	

	@Override
	public void onReceive(Context context, Intent intent) {
		
		String msg = intent.getStringExtra(Constants.AMQP_SUBSCRIBED_MESSAGE);
		System.out.println("received --"+ msg);
		
		//TODO Add to the db
		//TODO Add message to notification bar
		
	}

}
