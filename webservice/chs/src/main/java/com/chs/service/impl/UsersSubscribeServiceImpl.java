package com.chs.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.chs.dao.UsersSubscribeDAO;
import com.chs.entity.UserEntity;
import com.chs.entity.UsersSubscribe;
import com.chs.service.UsersSubscribeService;

@Service
public class UsersSubscribeServiceImpl implements UsersSubscribeService{

	@Autowired
	private UsersSubscribeDAO userSubscribeDao;
	
	@Override
	public void save(UsersSubscribe userSubscribe) {
		userSubscribeDao.save(userSubscribe);		
	}

	@Override
	public List<UsersSubscribe> getAllMappings() {
		return userSubscribeDao.getAllMappings();
	}

	@Override
	public List<UsersSubscribe> getUserMappings(UserEntity user) {
		return userSubscribeDao.getUserMappings(user);
	}

	@Override
	public void deleteMapping(Integer mappingId) {
		
		userSubscribeDao.deleteMapping(mappingId);
		
	}

}
