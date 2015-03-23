package com.chs.entity;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;



@Entity
@Table(name="Users_Topic")
@XmlRootElement(name = "topic")  
public class UsersTopic {
	
 

    @Id
    @Column(name="id")
    @GeneratedValue
    @XmlElement
    private Integer id;

    @Column(name="user_role")
    private String userRole;
    
    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name="user_id")
    private UserEntity user;
    
    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name="topic_id")
    @XmlElement
    private Topic topic;
    

    public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	public String getUserRole() {
		return this.userRole;
	}
	
	public void setUserRole(String userRole) {
		this.userRole = userRole;
	}
	
	public UserEntity getUser() {
		return user;
	}
	public void setUser(UserEntity user) {
		this.user = user;
	}
	
    public void setTopic(Topic topic){
    	this.topic = topic;
    }
    
    public Topic getTopic(){
    	return this.topic;
    }
 
    public String toString() {
    	return id+"|"+user.getEmail()+"|"+topic.getTopicName();
    }
}
