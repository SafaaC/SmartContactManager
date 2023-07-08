package com.smart.controllers;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.smart.dao.UserRepository;
import com.smart.entities.Contact;
import com.smart.entities.User;

@Controller
@RequestMapping("/user")
public class UserController {
	@Autowired
	private UserRepository userRepository;
	
	@ModelAttribute
	public void addCommonData(Model model,Principal principal) {
		String userName=principal.getName();
		User user=userRepository.getUserByUserName(userName);
		model.addAttribute("user", user);
		
	}
	@GetMapping("/index")
	public String Dashboard(Model model,Principal principal) {
		model.addAttribute("title", "User Dashboard");
		return "normal/user_dashboard";
	}
	
	//add contact handler
		@GetMapping("/add-contact")
		public String opemAddContactForm(Model model) {
			model.addAttribute("title", "Add Contact");
			model.addAttribute("contact", new Contact());
			return "normal/add_contact_form";
		}

}
