package com.chs.dao.impl;


import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.chs.dao.UserDAO;
import com.chs.entity.UserEntity;
 
@Transactional 
@Repository
public class UserDaoImpl implements UserDAO
{
    @Autowired
    private SessionFactory sessionFactory;
    
    public void addUser(UserEntity user) {
        this.sessionFactory.getCurrentSession().save(user);
    }
    @SuppressWarnings("unchecked")
    public List<UserEntity> getAllUsers() {
        return this.sessionFactory.getCurrentSession().createQuery("from UserEntity").list();
    }
    public void deleteUser(Integer userId) {
        UserEntity user = (UserEntity) sessionFactory.getCurrentSession().load(
                UserEntity.class, userId);
        if (null != user) {
            this.sessionFactory.getCurrentSession().delete(user);
        }
    }
	
    
	@Override
	public UserEntity isUser(String user, String pass) {
		try {
		    Session session = this.sessionFactory.getCurrentSession();
		    Criteria cr = session.createCriteria(UserEntity.class);
		    cr.add(Restrictions.eq("email", user)).add(Restrictions.eq("password", pass));
		    UserEntity User = (UserEntity) cr.uniqueResult();
		    return User;
		}catch(Exception e) {
			e.printStackTrace();
		}
	    	    
		return null;
	}
	@Override
	public UserEntity getUserById(String userId) {
		
		Integer intuserId = Integer.parseInt(userId);
		try {
		    Session session = this.sessionFactory.getCurrentSession();
		    Criteria cr = session.createCriteria(UserEntity.class);
		    cr.add(Restrictions.eq("id", intuserId));
		    UserEntity User = (UserEntity) cr.uniqueResult();
		    return User;
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
}