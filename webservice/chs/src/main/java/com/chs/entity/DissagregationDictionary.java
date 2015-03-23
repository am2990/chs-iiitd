package com.chs.entity;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
@Table(name="dissagregation_dictionary")
public class DissagregationDictionary {
	
	
	@Id
    @Column(name="Dissag_Id")
    @GeneratedValue
    private Integer id;
    @Column(name="Dissagregation_Name")
    private String dissagName;
    
    
    public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getdissagName() {
		return dissagName;
	}
	public void setdissagName(String dissagname) {
		this.dissagName = dissagname;
	}

}
