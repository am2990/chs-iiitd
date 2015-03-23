package com.chs.dao;

import java.util.List;

import com.chs.entity.Topic;
import com.chs.entity.UserEntity;
import com.chs.entity.UsersTopic;

public interface UsersTopicDAO {

	public void save(UsersTopic usertopic);
    public List<UsersTopic> getAllMappings();
    public List<UsersTopic> getUserMappings(UserEntity user);
    public List<UsersTopic> getUserTopicMapping(UserEntity user, Topic topic);
	public void deleteMapping(Integer mappingId);
}
