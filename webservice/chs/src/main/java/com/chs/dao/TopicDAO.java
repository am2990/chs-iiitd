package com.chs.dao;

import java.util.List;

import com.chs.entity.Topic;

public interface TopicDAO {
	
	 public void saveTopic(Topic topic);
	 
	 public List<Topic> getAllTopics();
     public Topic getTopicByName(String topicname);

	 public void deleteTopic(Integer topicId);

	public Topic getTopicById(Integer topicId);

	 
}
