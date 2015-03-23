package com.chs.amqp;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import com.chs.entity.Topic;

@XmlRootElement(name = "TopicList")
public class TopicList {

 private List<Topic> topiclist;

 protected TopicList() {
 }

 public TopicList(List<Topic> topiclist) {
  this.topiclist = topiclist;
 }

 @javax.xml.bind.annotation.XmlElement(name = "topic")
 public List<Topic> getStudentList() {
  return topiclist;
 }
}
