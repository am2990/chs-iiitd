package com.chs.dao.impl;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.chs.dao.UsersTopicDAO;
import com.chs.entity.Topic;
import com.chs.entity.UserEntity;
import com.chs.entity.UsersTopic;

@Transactional 
@Repository
public class UsersTopicDAOImpl implements UsersTopicDAO {
	
	@Autowired
	private SessionFactory sessionFactory;

	public void save(UsersTopic userstopic) {
		//TODO make user same user topic are only updated and not added as duplicate entries
		System.out.println("inside save userstopic");
        this.sessionFactory.getCurrentSession().saveOrUpdate(userstopic);
		
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<UsersTopic> getAllMappings() {
        return this.sessionFactory.getCurrentSession().createQuery("from UsersTopic").list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<UsersTopic> getUserMappings(UserEntity user) {
	    
		Session session = this.sessionFactory.getCurrentSession();
	    Criteria cr = session.createCriteria(UsersTopic.class);
	    cr.add(Restrictions.eq("user", user));
	    List<UsersTopic> usersTopicList = cr.list();
	    
	    return usersTopicList;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<UsersTopic> getUserTopicMapping(UserEntity user, Topic topic) {
	    
		Session session = this.sessionFactory.getCurrentSession();
	    Criteria cr = session.createCriteria(UsersTopic.class);
	    cr.add(Restrictions.eq("user", user)).add(Restrictions.eq("topic", topic));
	    List<UsersTopic> usersTopicList = cr.list();
	    
	    return usersTopicList;
	}

	
	
	
	public void deleteMapping(Integer mappingId) {
        UsersTopic ut = (UsersTopic) sessionFactory.getCurrentSession().load(
                UsersTopic.class, mappingId);
        if (null != ut) {
            this.sessionFactory.getCurrentSession().delete(ut);
        }
    }

}
