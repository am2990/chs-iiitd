package com.chs.amqp;

public class PublishData {

	private String topicname;
	private String value;
	
	public String getTopicName() {
		return this.topicname;
	}
	
	public void setTopicName(String topicname) {
		this.topicname = topicname;
	}
	
	public String getValue() {
		return this.value;
	}
	
	public void setValue(String value) {
		this.value = value;
	}
}
