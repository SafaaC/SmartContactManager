package com.smart.controllers;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.smart.dao.UserRepository;
import com.smart.entities.User;

@Controller
@RequestMapping("/user")
public class UserController {
	@Autowired
	private UserRepository userRepository;
	
	@GetMapping("/index")
	public String Dashboard(Model model,Principal principal) {
		String userName=principal.getName();
		User user=userRepository.getUserByUserName(userName);
		model.addAttribute("user", user);
		return "normal/user_dashboard";
	}

}
