package com.chs.entity;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;



@Entity
@Table(name="topic")
public class Topic {
	
 

    @Id
    @Column(name="T_Id")
    @GeneratedValue
    private Integer id;
    
    @Column(name="topic_name")
    private String topicname;
    
    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name="Concept_Id")
    private ConceptDictionary Concept_Id;
    
    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name="Dissag_Id")
    private DissagregationDictionary Dissag_Id;

    public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getTopicName() {
		return topicname;
	}
	public void setTopicName(String topicname) {
		this.topicname = topicname;
	}
	
    public void setConcept(ConceptDictionary concept){
    	this.Concept_Id = concept;
    }
    
    public ConceptDictionary getConcept(){
    	return this.Concept_Id;
    }
    
    public void setDisagregation(DissagregationDictionary dissag){
    	this.Dissag_Id = dissag;
    }
    
    public DissagregationDictionary getDissagreagtion(){
    	return this.Dissag_Id;
    }
    
    public boolean equals(Object o) {
    	
//    	System.out.println("comparing in side equals");
    	if (!(o instanceof Topic)) {
    	    return false;
    	}
    	Topic other = (Topic) o;
//    	System.out.println("details----"+this.topicname+"=="+other.getTopicName());
    	if(this.topicname.equals(other.getTopicName())) {
//    		System.out.println("retunrign true");
    		return true;
    	}
    	return false;
    }
    
    
}
