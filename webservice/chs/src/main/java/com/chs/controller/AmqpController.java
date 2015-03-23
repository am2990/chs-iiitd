package com.chs.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.chs.entity.Topic;
import com.chs.entity.UserEntity;
import com.chs.entity.UsersTopic;
import com.chs.service.ConceptService;
import com.chs.service.DissagregationService;
import com.chs.service.PublishService;
import com.chs.service.TopicService;
import com.chs.service.UserService;
import com.chs.service.UsersTopicService;

@Controller
public class AmqpController {

	@Autowired
    private UserService userManager;
	private ConceptService conceptService;
	private DissagregationService dissagService;
	private TopicService topicService;
	private UsersTopicService usersTopicService;
	private PublishService publishService;
	
	
	@Autowired(required=true)
    @Qualifier(value="conceptService")
    public void setConceptService(ConceptService cs){
        this.conceptService = cs;
    }
	
	@Autowired(required=true)
    @Qualifier(value="dissagregationService")
    public void setDissagService(DissagregationService ds){
        this.dissagService = ds;
    }
	
	@Autowired(required=true)
    @Qualifier(value="topicService")
    public void setTopicService(TopicService ts){
        this.topicService = ts;
    }
	
	@Autowired(required=true)
    @Qualifier(value="publishService")
    public void setUsersTopicService(PublishService ps){
        this.publishService = ps;
    }
	
	@Autowired(required=true)
    @Qualifier(value="usersTopicService")
    public void setUsersTopicService(UsersTopicService uts){
        this.usersTopicService = uts;
    }
	
	
	//TODO: The function will receive the data to be published as a post request. Data will have topicname, values apart from username and password.
    @RequestMapping(value = "/api/publish", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK) 
    public void Publish(HttpServletRequest request, 
            @RequestParam(value="username", required=true) String email, 
            @RequestParam(value="pass", required=true) String password,
            @RequestParam(value="topicname", required=true) String topicname,
            @RequestParam(value="value", required=true) String data)
    {
    	UserEntity user = userManager.isUser(email,password);
    	if(user != null){
			System.out.println("Recived Params:username"+email+".Pass"+password+".topicname:"+topicname+".value"+data);

    		Topic t = topicService.getTopicByName(topicname);
    		if(t != null) {
    			
    			publishService.publishData(topicname, data);
    			
    		}
    	}

    }
        

    /**
     * Returns an XML after authenticating the user through the credentials.
     * The XML contains list of the topics user is subscribed to. 
     * @param  request Standard HTTPServletRequest
     * @param  username  user name of the user
     * @param  pass password of the user
     * @return      List of user subscribed topics in XML format
     * <pre> 
     *{@code<TopicList> 
     *   <topic id = topic#>Topic One</topic>  
     *   <topic id = topic#>Topic Two</topic>  
     *</TopicList>
     *}
     *</pre>
     */
    @RequestMapping(value = "/api/topics/publisher", method=RequestMethod.GET) 
    @ResponseStatus(value = HttpStatus.OK) 
    @ResponseBody
    public  String PublisherTopics(HttpServletRequest request, 
            @RequestParam(value="username", required=true) String username, 
            @RequestParam(value="pass", required=true) String pass)
    {
        //TODO return the subscribed topics user in a XML format
    	
    	System.out.println("Recived Params:username"+username+".Pass"+pass);
		
    	UserEntity user = userManager.isUser(username,pass);
    	
        String topiclist = "";  

    	if(user != null){
			List<UsersTopic> utl = this.usersTopicService.getUserMappings(user);
			System.out.println("List size"+utl.size());
	    	for(UsersTopic iter : utl) {
	    		String temp = iter.getTopic().getTopicName();
	    		//TODO Use enumerate instead of plain text
	    		if(iter.getUserRole().equalsIgnoreCase("Publisher") || iter.getUserRole().equalsIgnoreCase("Both")) {
	    			System.out.println("appending:"+ temp);
	    			topiclist = topiclist + temp +",";
	    		}
	    	}
			System.out.println("Reached here:"+topiclist);
	    	return topiclist;
    	}
    	
    	return topiclist;

    }
    
    
    @RequestMapping(value = "/api/topics/subscriber", method=RequestMethod.GET) 
    @ResponseStatus(value = HttpStatus.OK) 
    @ResponseBody
    public  String SubscriberTopics(HttpServletRequest request, 
            @RequestParam(value="username", required=true) String username, 
            @RequestParam(value="pass", required=true) String pass)
    {
        //TODO return the subscribed topics user in a XML format
    	
    	System.out.println("Recived Params:username"+username+".Pass"+pass);
		
    	UserEntity user = userManager.isUser(username,pass);
    	
        String topiclist = "";  

    	if(user != null){
			List<UsersTopic> utl = this.usersTopicService.getUserMappings(user);
			System.out.println("List size"+utl.size());
	    	for(UsersTopic iter : utl) {
	    		String temp = iter.getTopic().getTopicName();
	    		//TODO Use enumerate instead of plain text
    			System.out.println("user role:"+ iter.getUserRole());
	    		if(iter.getUserRole().equalsIgnoreCase("Subscriber") || iter.getUserRole().equalsIgnoreCase("Both")) {
	    			System.out.println("appending:"+ temp);
	    			topiclist = topiclist + temp +",";
	    		}
	    	}
			System.out.println("Reached here:"+topiclist);
	    	return topiclist;
    	}
    	
    	return topiclist;

    }
    
}
