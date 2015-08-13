package com.iiitd.networking;

import java.io.Serializable;
import java.util.List;

public class NetworkDevice implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8692497235316058617L;
	private int id;
	private String deviceName;
	private String ipAddress;
	private String macAddress;
	private List<Sensor> sensorList; 
	
	
	public int getId(){
		return this.id;
	}
	
	public void setId(int id){
		this.id = id;
	}
	
	public String getDeviceName(){
		return this.deviceName;
	}
	
	public void setDeviceName(String deviceName){
		this.deviceName = deviceName;
	}
	
	public String getIpAddress(){
		return this.ipAddress;
	}
	
	public void setIpAddress(String ipAddress){
		
		this.ipAddress = ipAddress;
	}
	
	public String getMacAddress(){
		return this.macAddress;
	}
	
	public void setMacAddress(String macAddress){
		this.macAddress = macAddress;
	}
	
	public List<Sensor> getSensorList(){
		return this.sensorList;
	}
	
	public void setSensorList(List<Sensor> sensorList){
		this.sensorList = sensorList;
	}
	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((deviceName == null) ? 0 : deviceName.hashCode());
		result = prime * result
				+ ((ipAddress == null) ? 0 : ipAddress.hashCode());
		result = prime * result
				+ ((macAddress == null) ? 0 : macAddress.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		NetworkDevice other = (NetworkDevice) obj;
		if (deviceName == null) {
			if (other.deviceName != null)
				return false;
		} else if (!deviceName.equals(other.deviceName))
			return false;
		if (ipAddress == null) {
			if (other.ipAddress != null)
				return false;
		} else if (!ipAddress.equals(other.ipAddress))
			return false;
		if (macAddress == null) {
			if (other.macAddress != null)
				return false;
		} else if (!macAddress.equals(other.macAddress))
			return false;
		return true;
	}

	
	public String toString(){
		
		return this.deviceName + ":" + this.ipAddress;
	}

}
