package com.smart.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.smart.dao.ContactRepository;
import com.smart.dao.UserRepository;
import com.smart.entities.Contact;
import com.smart.entities.User;
import com.smart.helper.Message;

import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.websocket.Session;

@Controller
@RequestMapping("/user")
public class UserController {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private ContactRepository contactRepository;
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	
	//method run evrytime
	//method for addingcomon data
	@ModelAttribute
	public void addCommonData(Model model, Principal principal) {
		
		String userName=principal.getName();
		//System.out.println("Username:"+userName);
		//get userdetails by email
		User user=userRepository.getUerByUserName(userName);
		model.addAttribute("user",user);
		System.out.println(user);
		
	}
	
	@RequestMapping("/index")
	public String dashboard(Model model, Principal principal) {
		model.addAttribute("title", "User Dashboard");
		return "normal/user_dashboard";
	}
	
	//open add form handler
	
	@GetMapping("/add-contact")
	public String openAddcontactForm(Model model) {
		
		model.addAttribute("title", "Add Contact");
		model.addAttribute("contact", new Contact());
		
		return "normal/add_contact_form";
	}
	
	//processing add contact form
	
	@PostMapping("/process-contact")
	public String processContact(@Valid @ModelAttribute("contact") Contact contact,
			@RequestParam("profileImage") MultipartFile file ,
			BindingResult result, Principal principal, HttpSession session) {
		
	try {
		
		String name=principal.getName();
		User user=userRepository.getUerByUserName(name);
		
		if(file.isEmpty()) {
			//if the file is empty then try our message
			
			contact.setImage("contact.png");
		}else{
			//file the file to the folder and update the name to contact
			contact.setImage(file.getOriginalFilename()+contact.getPhone());
			 File savefile=new ClassPathResource("static/img").getFile();
			 
			 Path path=Paths.get(savefile.getAbsolutePath()+File.separator+contact.getImage());
			 
			 Files.copy(file.getInputStream(),path,StandardCopyOption.REPLACE_EXISTING);
			 
			 System.out.println("image uploaded");
			
		}
		
		contact.setUser(user);
		user.getContacts().add(contact);
		userRepository.save(user);
		System.out.println("contact:"+contact);
		
		//success message print
		
		session.setAttribute("message", new Message("Your contact is added !! And more..", "success"));
		
		
	} catch (Exception e) {
		System.out.println("Error:"+e.getMessage());
		e.printStackTrace();
		//error message
		session.setAttribute("message", new Message("Something went wrong try again!!", "danger"));
		
	}	
		
	
		return "normal/add_contact_form";
	}
	
	//show contact handler
	//per page=5[n]
	//current page=0[page]
	
	@GetMapping("/show-contacts/{page}")
	public String showContact(@PathVariable("page") Integer page,Model model, Principal pricipal) {
		
		model.addAttribute("title", "show User contacts");
		
		//send contact list
		//this is one method we have another method 
			
		/*
		 * String userName =pricipal.getName(); User
		 * user=userRepository.getUerByUserName(userName); List<Contact>
		 * contact=user.getContacts();
		 */
		 String userName =pricipal.getName();
		 User user=userRepository.getUerByUserName(userName);
		
		 //current page
		 //contact perpage-5
		 Pageable pageable=PageRequest.of(page, 5);
		 
		 
		Page<Contact> contacts=contactRepository.findContactsByUser(user.getId(),pageable);
		model.addAttribute("contacts",contacts);
		model.addAttribute("currentPage",page);
		model.addAttribute("totalPages", contacts.getTotalPages());
		
		
		return"normal/show_contacts";
	}
	
	
	//showing particular contatct details
	
	@RequestMapping("/{cid}/contact")
	public String showContactDetail(@PathVariable("cid") Integer cid, Model model,Principal Principal) {
		
		Optional<Contact> contactoptional=contactRepository.findById(cid);
		 Contact contact=contactoptional.get();
		 	String name=Principal.getName();
		 	User user=userRepository.getUerByUserName(name);
		 	
		 	if(user.getId()==contact.getUser().getId()) 
		 		 model.addAttribute("contact", contact);
		 		
		 			model.addAttribute("title", contact.getName());
		 	
	
		return "normal/contact_detail";
	}
	
	//delete handler
	
	@GetMapping("/delete/{cid}")
	@Transactional
	public String deleteContact(@PathVariable("cid") Integer cid, Model model,Principal principal,HttpSession session) {
		
		
		String name=principal.getName();
	 	User user=userRepository.getUerByUserName(name);
	 	Contact contact=contactRepository.findById(cid).get();
		
			//
		if(user.getId()==contact.getUser().getId()) 
		
			
			//we are getting prb bcoz cascade style use in contact entity thats why we perform trick
			user.getContacts().remove(contact);
			userRepository.save(user);
			
			
			
		System.out.println("DELETED");
		session.setAttribute("message", new Message("contact is deleted successfully..", "success"));
		
		
		return"redirect:/user/show-contacts/0";
	}
	
	
	//open update form open
	
	@PostMapping("/update-contact/{cid}")
	public String updateForm(@PathVariable("cid") Integer cid,Model model) {
		
		model.addAttribute("title","update contact");
		
		Contact contact=contactRepository.findById(cid).get();
		model.addAttribute("contact", contact);
		
		
		return"normal/update_form";
	}
	
	//update contact handler
	
	@RequestMapping(value="/process-update", method = RequestMethod.POST)
	public String updateHandler (@Valid @ModelAttribute Contact contact, @RequestParam("profileImage") MultipartFile file, 
									 Model model, HttpSession session, Principal principal ) {
		
		try {
			//old contact details
			Contact oldcontactDetails=contactRepository.findById(contact.getCid()).get();
			
			
			if(!file.isEmpty()) {
				
				
				 //file work, //rewrite //delete old photo 
				 File deletefile=new ClassPathResource("static/img").getFile(); 
				 File file1=new File(deletefile,oldcontactDetails.getImage()); 
				  file1.delete();
				 
				
				//update new photo
				contact.setImage(file.getOriginalFilename()+oldcontactDetails.getPhone());
				 File savefile=new ClassPathResource("static/img").getFile();
				 
				 Path path=Paths.get(savefile.getAbsolutePath()+File.separator+contact.getImage());
				 
				 Files.copy(file.getInputStream(),path,StandardCopyOption.REPLACE_EXISTING);
				 
				 
				
			}else {
				contact.setImage(oldcontactDetails.getImage());
			}
				User user=userRepository.getUerByUserName(principal.getName());
				contact.setUser(user);
			contactRepository.save(contact);
			
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		

		
		session.setAttribute("message", new Message("your contact is updated", "success"));
		
		
		
		return"redirect:/user/"+contact.getCid()+"/contact";
		
	}
	
	//your profile handler
	@GetMapping("/profile")
	public String yourProfile(Model model) {
		
		model.addAttribute("title", "profilepage ");
		
		return "normal/profile";
	}
	
	//change password
	
	@GetMapping("/settings")
	public String changeSetting() {
		
		
		return "/normal/setting";
	}
	
	@PostMapping("/change-password")
	public String changePassword(@RequestParam("oldPassword") String olPass, @RequestParam("newPassword") String newPass,
									Principal principal, HttpSession session) {
		
		User currentUser=userRepository.getUerByUserName(principal.getName());
		
		if(bCryptPasswordEncoder.matches(olPass,currentUser.getPassword())) {
			//change password
			currentUser.setPassword(bCryptPasswordEncoder.encode(newPass));
			userRepository.save(currentUser);
			
			session.setAttribute("message", new Message ("Your password is successfully change..!", "success"));
			return "redirect:/user/index";
		}else {
			//error
			session.setAttribute("message", new Message ("You have Enter wrong password", "danger"));
			return "redirect:/user/settings";
		}
		
		
	}
	
	
	

}
