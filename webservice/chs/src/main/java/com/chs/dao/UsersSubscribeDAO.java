package com.chs.dao;

import java.util.List;

import com.chs.entity.UserEntity;
import com.chs.entity.UsersSubscribe;

public interface UsersSubscribeDAO {

	public void save(UsersSubscribe usersSubscribe);
    public List<UsersSubscribe> getAllMappings();
    public List<UsersSubscribe> getUserMappings(UserEntity user);
	public void deleteMapping(Integer mappingId);
}
