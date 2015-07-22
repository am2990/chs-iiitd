package org.openmrs.module.basicexample;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;


/**
 * 
 * @author amehra
 */
@Entity
@Table(name = "amqp_obs")
public class AmqpObs
{

	@Id @GeneratedValue
	@Column(name = "id")
	protected Integer id;

	@Column(name = "name")
	protected String name;

	@Column(name = "uuid")
    protected String uuid;

	@Column(name = "dob")
    protected String dob;

	@Column(name = "gender")
	protected String gender;

	@Column(name = "obs")
	protected String obs;

	@Column(name = "created_date")
	protected String created_date;

    public Integer getId()
    {
        return id;
    }

    public void setId( Integer id )
    {
        this.id = id;
    }

    public String getFirstName()
    {
        return name;
    }

    public void setFirstName( String Name )
    {
        this.name = Name;
    }

    public String getUuid()
    {
        return uuid;
    }

    public void setUuid( String uuid )
    {
        this.uuid = uuid;
    }

    public String getDob()
    {
        return dob;
    }

    public void setDob( String dob )
    {
        this.dob = dob;
    }

    public String getGender()
    {
        return gender;
    }

    public void setGender( String gender )
    {
        this.gender = gender;
    }

    public String getObs()
    {
        return obs;
    }

    public void setObs( String obs )
    {
        this.obs = obs;
    }

    public String getCreatedDate()
    {
        return created_date;
    }

    public void setCreatedDate( String created_date )
    {
        this.created_date = created_date;
    }

}
