package com.chs.service;

import java.util.List;

import com.chs.entity.UserEntity;
import com.chs.entity.UsersSubscribe;

 
public interface UsersSubscribeService {
    public void save(UsersSubscribe usertopic);
    public List<UsersSubscribe> getAllMappings();
    public List<UsersSubscribe> getUserMappings(UserEntity user);
	public void deleteMapping(Integer mappingId);
  
}