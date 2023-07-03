package com.smart.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.smart.dao.UserRepository;
import com.smart.entities.User;
import com.smart.helper.Message;

import jakarta.servlet.http.HttpSession;

@Controller
public class HomeController {
	
	@Autowired
	private UserRepository userRepository;

	public UserRepository getUserRepository() {
		return userRepository;
	}

	public void setUserRepository(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@GetMapping("/")
	public String home(Model model) {
		model.addAttribute("title", "Home-Smart Contact Manager");
		return "home";
	}
	
	@GetMapping("/about")
	public String about(Model model) {
		model.addAttribute("title", "About-Smart Contact Manager");
		return "about";
	}
	
	@GetMapping("/signup")
	public String signup(Model model) {
		model.addAttribute("title", "Register-Smart Contact Manager");
		model.addAttribute("user",new User());
		return "signup";
	}
	
	@PostMapping("/do_register")
	public String registerUser(@ModelAttribute("user") User user,@RequestParam(value = "agreement",defaultValue = "false") boolean agreement,Model model,HttpSession session) {
		
		try {
			if(!agreement) {
				throw new Exception("You have NOT agreed the terms and conditions");
			}
			user.setRole("ROLE_USER");
			user.setEnabled(true); 
			user.setImageUrl("default.png");
			
			
			User result=userRepository.save(user);
			model.addAttribute("user", new User());
			session.setAttribute("message", new Message("Registered Successfully !!", "alert-success"));
			return "signup";
		} catch (Exception e) {
			model.addAttribute("user", user);
			session.setAttribute("message", new Message("Something went wrong !!"+e.getMessage(), "alert-danger"));
			
			e.printStackTrace();
			
			return "signup";
		}
	}
}
