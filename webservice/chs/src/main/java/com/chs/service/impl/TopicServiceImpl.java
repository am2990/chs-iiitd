package com.chs.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.chs.dao.TopicDAO;
import com.chs.entity.Topic;
import com.chs.service.TopicService;

@Service
public class TopicServiceImpl implements TopicService{

    @Autowired
    private TopicDAO topicDAO;
    
	@Override
	public void saveTopic(Topic topic) {
		topicDAO.saveTopic(topic);
	}

	@Override
	public List<Topic> getAllTopics() {
		return topicDAO.getAllTopics();
	}

	@Override
	public void deleteTopic(Integer topicId) {
		topicDAO.deleteTopic(topicId);
	}

	@Override
	public Topic getTopicByName(String topicname) {
		return topicDAO.getTopicByName(topicname);
	}

	@Override
	public Topic getTopicById(Integer topicId) {
		return topicDAO.getTopicById(topicId);
	}


}
