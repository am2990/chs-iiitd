package com.chs.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.chs.dao.UserDAO;
import com.chs.entity.UserEntity;
import com.chs.service.UserService;
 
 
@Service
public class UserServiceImpl implements UserService 
{
    @Autowired
    private UserDAO userDAO;
    @Override
    @Transactional
    public void addUser(UserEntity user) {
        userDAO.addUser(user);
    }
    
    @Override
    @Transactional
    public List<UserEntity> getAllUsers() {
        return userDAO.getAllUsers();
    }
    
    @Override
    @Transactional
    public void deleteUser(Integer userId) {
        userDAO.deleteUser(userId);
    }
    
    public void setUserDAO(UserDAO userDAO) {
        this.userDAO = userDAO;
    }
    
    public UserEntity isUser(String user, String pass){
    	return userDAO.isUser(user, pass);
    }

	@Override
	public UserEntity getUserById(String userId) {
    	return userDAO.getUserById(userId);
	}
    
}