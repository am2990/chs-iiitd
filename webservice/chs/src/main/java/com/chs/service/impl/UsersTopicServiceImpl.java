package com.chs.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.chs.dao.UsersTopicDAO;
import com.chs.entity.Topic;
import com.chs.entity.UserEntity;
import com.chs.entity.UsersTopic;
import com.chs.service.UsersTopicService;

@Service
public class UsersTopicServiceImpl implements UsersTopicService{

	@Autowired
	private UsersTopicDAO userTopicDao;
	
	@Override
	public void save(UsersTopic usertopic) {
		userTopicDao.save(usertopic);		
	}

	@Override
	public List<UsersTopic> getAllMappings() {
		return userTopicDao.getAllMappings();
	}

	@Override
	public List<UsersTopic> getUserMappings(UserEntity user) {
		return userTopicDao.getUserMappings(user);
	}

	@Override
	public void deleteMapping(Integer mappingId) {
		
		userTopicDao.deleteMapping(mappingId);
		
	}

	@Override
	public List<UsersTopic> getUserTopicMappings(UserEntity user, Topic topic) {
		return userTopicDao.getUserTopicMapping(user, topic);
	}

}
