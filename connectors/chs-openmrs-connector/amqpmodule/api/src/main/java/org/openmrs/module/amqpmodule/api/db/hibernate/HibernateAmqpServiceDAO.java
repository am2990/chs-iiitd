/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.amqpmodule.api.db.hibernate;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.openmrs.module.amqpmodule.AmqpModule;
import org.openmrs.module.amqpmodule.api.db.AmqpServiceDAO;

/**
 * It is a default implementation of  {@link AmqpServiceDAO}.
 */
public class HibernateAmqpServiceDAO implements AmqpServiceDAO {
	protected final Log log = LogFactory.getLog(this.getClass());
	
	private SessionFactory sessionFactory;
	
	/**
     * @param sessionFactory the sessionFactory to set
     */
    public void setSessionFactory(SessionFactory sessionFactory) {
	    this.sessionFactory = sessionFactory;
    }
    
	/**
     * @return the sessionFactory
     */
    public SessionFactory getSessionFactory() {
	    return sessionFactory;
    }
    
    @Override
    public void addPerson(AmqpModule p) {
        Session session = this.sessionFactory.getCurrentSession();
        session.persist(p);
//        logger.info("Person saved successfully, Person Details="+p);
    }
 
    @Override
    public void updatePerson(AmqpModule p) {
        Session session = this.sessionFactory.getCurrentSession();
        session.update(p);
//        logger.info("Person updated successfully, Person Details="+p);
    }
 
    @SuppressWarnings("unchecked")
    @Override
    public List<AmqpModule> listPersons() {
        Session session = this.sessionFactory.getCurrentSession();
        List<AmqpModule> personsList = session.createQuery("from AmqpModule").list();
        for(AmqpModule p : personsList){
            System.out.println("Person List::"+p);
        }
        return personsList;
    }
 
    @Override
    public AmqpModule getPersonById(int id) {
        Session session = this.sessionFactory.getCurrentSession();      
        AmqpModule p = (AmqpModule) session.load(AmqpModule.class, new Integer(id));
//        logger.info("Person loaded successfully, Person details="+p);
        return p;
    }
 
    @Override
    public void removePerson(int id) {
        Session session = this.sessionFactory.getCurrentSession();
        AmqpModule p = (AmqpModule) session.load(AmqpModule.class, new Integer(id));
        if(null != p){
            session.delete(p);
        }
//        logger.info("Person deleted successfully, person details="+p);
    }
    
    
}