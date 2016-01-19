package org.openmrs.module.testmodule.utils;

import java.util.Map;

import org.springframework.amqp.core.Exchange;

public class ChsExchange implements Exchange{

	@Override
	public String getName() {
		
		return "chs";
	}

	@Override
	public String getType() {
		
		return "direct";
	}

	@Override
	public boolean isDurable() {
		
		return true;
	}

	@Override
	public boolean isAutoDelete() {

		return false;
	}

	@Override
	public Map<String, Object> getArguments() {
		// TODO Auto-generated method stub
		return null;
	}

}
