package com.chs.parser;

class Report{

  String concept;
  String dissagregation;
  String timestamp;
  String frequency;
  int dataValue;

 
  public String getTopicName() {
	  return this.concept;
  }
  

  public void setTopicName(String topicname) {
	  this.concept = topicname;
  }
  
  @Override
  public String toString() {
    return concept+" "+dissagregation+"("+timestamp+")"+frequency+":"+dataValue;
  }
  
}
