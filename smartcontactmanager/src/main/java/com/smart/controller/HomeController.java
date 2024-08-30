package com.smart.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.smart.dao.UserRepository;
import com.smart.entities.User;
import com.smart.helper.Message;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class HomeController {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	@RequestMapping("/")
	public String home(Model model) {
		model.addAttribute("title", "Home- Smart Contact Manager");
		
		return "home";
	}
	
	//handler for custom login
	@GetMapping("/signin")
	public String customLogin(Model model) {
		
		model.addAttribute("title", "login page");
		
		return "login";
	}
	
	
	
	
	@RequestMapping("/about")
	public String about(Model model) {
		model.addAttribute("title", "About- Smart Contact Manager");
		
		return "about";
	}
	
	@RequestMapping("/signup")
	public String signup(Model model) {
		model.addAttribute("title", "Register- Smart Contact Manager");
		model.addAttribute("user", new User());
		return "signup";
	}
	
	//this handler registering user
	
	  @RequestMapping(value="/do_register", method = RequestMethod.POST) public
	  String registerUser(@Valid @ModelAttribute("user") User
	  user, BindingResult result1, @RequestParam(value="agreement", defaultValue = "false") boolean
	  agreement, Model model, HttpSession session)
	  
	  { 
		  try {
			  if(!agreement)
			  {
			  System.out.println("you have not agreed term and condition"); 
			  throw new Exception("you have not agreed term and condition");
			  }
			  
			  if(result1.hasErrors()) {
				model.addAttribute("user",user);
				  
				  return "signup";
			  }
			  
			  user.setRole("ROLE_USER"); user.setEnables(true);
			  user.setImageUrl("default.png"); 
			  user.setPassword(passwordEncoder.encode(user.getPassword()));
			  User result=userRepository.save(user);
	  
	  
			  System.out.println("agreement:"+agreement);
			  System.out.println("user"+result); //model.addAttribute("user",result);
			  model.addAttribute("user", new User());
			  session.setAttribute("message", new Message("successfully Registered!!", "alert-success"));
			  
			  return "signup";
	  
			  } catch (Exception e)
		  		{ 
				  e.printStackTrace();
				  model.addAttribute("user", user);
				  System.out.println(e.getMessage());
				  session.setAttribute("message", new Message("Something went wrong!!"+e.getMessage(), "alert-danger")); 
				  return "signup";
				  }
			  
			  }
	 
	
	
	
}
