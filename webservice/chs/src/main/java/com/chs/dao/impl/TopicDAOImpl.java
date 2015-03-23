package com.chs.dao.impl;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.chs.dao.TopicDAO;
import com.chs.entity.Topic;

@Transactional
@Repository
public class TopicDAOImpl implements TopicDAO{
	@Autowired
    private SessionFactory sessionFactory;
    
    	

	@Override
	public void saveTopic(Topic topic) {
		
		this.sessionFactory.getCurrentSession().saveOrUpdate(topic);
		
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Topic> getAllTopics() {
		return this.sessionFactory.getCurrentSession().createQuery("from Topic").list();
	}

	@Override
	public void deleteTopic(Integer topicId) {
		Topic topic = (Topic) sessionFactory.getCurrentSession().load(
                Topic.class, topicId);
        if (null != topic) {
            this.sessionFactory.getCurrentSession().delete(topic);
        }
	}


	@Override
	public Topic getTopicByName(String topicname) {
		
		Session session = this.sessionFactory.getCurrentSession();
	    Criteria cr = session.createCriteria(Topic.class);
	    System.out.println("have access to topicname:"+topicname);
	    cr.add(Restrictions.eq("topicname", topicname));
	    Topic result = (Topic) cr.uniqueResult();
	    System.out.println("got topic by name:"+result.getTopicName());
	    return result;
	 
	}

	@Override
	public Topic getTopicById(Integer topicId) {
		Session session = this.sessionFactory.getCurrentSession();
	    Criteria cr = session.createCriteria(Topic.class);
//	    System.out.println("have access to topicname:"+topicname);
	    cr.add(Restrictions.eq("id", topicId));
	    Topic result = (Topic) cr.uniqueResult();
	    System.out.println("got topic by id:"+result.getTopicName());
	    return result;
	}

}
