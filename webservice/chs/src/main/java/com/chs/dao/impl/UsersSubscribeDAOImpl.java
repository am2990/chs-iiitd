package com.chs.dao.impl;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.chs.dao.UsersSubscribeDAO;
import com.chs.dao.UsersTopicDAO;
import com.chs.entity.UserEntity;
import com.chs.entity.UsersSubscribe;
import com.chs.entity.UsersTopic;

@Transactional 
@Repository
public class UsersSubscribeDAOImpl implements UsersSubscribeDAO {
	
	@Autowired
	private SessionFactory sessionFactory;

	public void save(UsersSubscribe usersSubscribe) {
		//TODO make user same user topic are only updated and not added as duplicate entries
		System.out.println("inside save users subscribe");
        this.sessionFactory.getCurrentSession().saveOrUpdate(usersSubscribe);
		
	}

	@SuppressWarnings("unchecked")
	public List<UsersSubscribe> getAllMappings() {
        return this.sessionFactory.getCurrentSession().createQuery("from UsersSubscribe").list();
	}

	@SuppressWarnings("unchecked")
	public List<UsersSubscribe> getUserMappings(UserEntity user) {
	    
		Session session = this.sessionFactory.getCurrentSession();
	    Criteria cr = session.createCriteria(UsersSubscribe.class);
	    cr.add(Restrictions.eq("user", user));
	    List<UsersSubscribe> usersSubList = cr.list();
	    
	    return usersSubList;
	}

	
	public void deleteMapping(Integer mappingId) {
		UsersSubscribe us = (UsersSubscribe) sessionFactory.getCurrentSession().load(
				UsersSubscribe.class, mappingId);
        if (null != us) {
            this.sessionFactory.getCurrentSession().delete(us);
        }
    }

}
