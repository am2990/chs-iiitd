package com.iiitd.networking;

import android.content.Context;

public class UDPMulticast extends UDPMessenger{

	public UDPMulticast(Context context, String tag, int multicastPort)
			throws IllegalArgumentException {
		super(context, tag, multicastPort);
		
	}

	@Override
	protected Runnable getIncomingMessageAnalyseRunnable() {
		
		return null;
	}

}
