package com.chs.service;

public interface PublishService {
	
	public boolean createTopic(String topicName);
	public boolean publishData(String topicname, String data);

}
