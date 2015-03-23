package com.chs.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.chs.entity.Topic;
import com.chs.entity.UserEntity;
import com.chs.entity.UsersTopic;
import com.chs.service.TopicService;
import com.chs.service.UserService;
import com.chs.service.UsersTopicService;
 


 
@Controller
public class RegistrationController 
{
	@Autowired
    private UserService userManager;
	private TopicService topicService;
	private UsersTopicService userTopicService;
	
	@Autowired(required=true)
    @Qualifier(value="topicService")
    public void setTopicService(TopicService ts){
        this.topicService = ts;
    }
	
	@Autowired(required=true)
    @Qualifier(value="usersTopicService")
    public void setUsersTopicService(UsersTopicService uts){
        this.userTopicService = uts;
    }
	
	
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String listUsers(ModelMap map)
    {
        return "index";
    }
    
    //TODO Capture Role During Log IN
    @RequestMapping(value = "/add", method = RequestMethod.GET)
    public String addNewUsers(ModelMap map)
    {
        map.addAttribute("user", new UserEntity());
        map.addAttribute("userList", userManager.getAllUsers());
        return "editUserList";
    }
    
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public String addUser(@ModelAttribute(value="user") UserEntity user, BindingResult result)
    {
    	//Producer p = new Producer(user.getFirstname());
    	//p.publish();
        userManager.addUser(user);
        return "redirect:/";
    }
    
    //TODO (Will have to use Spring Security for User Login)
    @RequestMapping(value = "/dashboard", method = RequestMethod.GET)
    public String UserDashboard( ModelMap map)
    {
    	//Producer p = new Producer(user.getFirstname());
    	//p.publish();
        //userManager.addUser(user);
    	List<Topic> tl =  this.topicService.getAllTopics();
    	System.out.println("Topic Size"+tl.size());
    	map.addAttribute("topicList", this.topicService.getAllTopics());
        return "dashboard";
    }
    
    
    @RequestMapping(value = "/dashboard", method = RequestMethod.POST)
    public String loginUser(HttpServletRequest request, 
            @RequestParam(value="username", required=false) String email, 
            @RequestParam(value="pass", required=false) String password,
            ModelMap map)
    {
    	//Producer p = new Producer(user.getFirstname());
    	//p.publish();
        //userManager.addUser(user);
    	UserEntity User = userManager.isUser(email,password);
    	if(User != null){
    		map.addAttribute("user", User);
    		System.out.println("Logging in User-" +email);
    		List<Topic> tl =  this.topicService.getAllTopics();
        	List<UsersTopic> utl = this.userTopicService.getUserMappings(User);
//        	System.out.println("initial topic list size and utl size-"+tl.size()+"|utl-"+utl.size());
        	for(UsersTopic t : utl) {
        		System.out.println("removing topic:"+t.toString());
        		tl.remove(t.getTopic());
        	}
//        	System.out.println("final topic list size-"+tl.size());
        	map.addAttribute("topicList", tl);
        	map.addAttribute("subscribed", utl);
    		return "dashboard";
    	}
        return "redirect:/";
    }
    
    
    @RequestMapping("/delete/{userId}")
    public String deleteUser(@PathVariable("userId") Integer employeeId)
    {
        userManager.deleteUser(employeeId);
        return "redirect:/";
    }
    public void setUserManager(UserService userManager) {
        this.userManager = userManager;
    }
    
    
////    @RequestMapping(value = { "/", "/welcome**" }, method = RequestMethod.GET)
////	public ModelAndView defaultPage() {
//// 
////	  ModelAndView model = new ModelAndView();
////	  model.addObject("title", "Spring Security Login Form - Database Authentication");
////	  model.addObject("message", "This is default page!");
////	  model.setViewName("hello");
////	  return model;
//// 
////	}
// 
//	@RequestMapping(value = "/admin**", method = RequestMethod.GET)
//	public ModelAndView adminPage() {
// 
//	  ModelAndView model = new ModelAndView();
//	  model.addObject("title", "Spring Security Login Form - Database Authentication");
//	  model.addObject("message", "This page is for ROLE_ADMIN only!");
//	  model.setViewName("admin");
//	  return model;
// 
//	}
// 
//	@RequestMapping(value = "/login", method = RequestMethod.GET)
//	public ModelAndView login(@RequestParam(value = "error", required = false) String error,
//		@RequestParam(value = "logout", required = false) String logout) {
// 
//	  ModelAndView model = new ModelAndView();
//	  if (error != null) {
//		model.addObject("error", "Invalid username and password!");
//	  }
// 
//	  if (logout != null) {
//		model.addObject("msg", "You've been logged out successfully.");
//	  }
//	  model.setViewName("login");
// 
//	  return model;
// 
//	}
// 
//	//for 403 access denied page
//	@RequestMapping(value = "/403", method = RequestMethod.GET)
//	public ModelAndView accesssDenied() {
// 
//	  ModelAndView model = new ModelAndView();
// 
//	  //check if user is login
//	  Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//	  if (!(auth instanceof AnonymousAuthenticationToken)) {
//		UserDetails userDetail = (UserDetails) auth.getPrincipal();	
//		model.addObject("username", userDetail.getUsername());
//	  }
// 
//	  model.setViewName("403");
//	  return model;
// 
//	}
//    
    
}