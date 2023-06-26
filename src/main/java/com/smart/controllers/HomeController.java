package com.smart.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

	@GetMapping("/home")
	public String home(Model model) {
		model.addAttribute("title", "Home-Smart Contact Manager");
		return "home";
	}
	
	@GetMapping("/about")
	public String about(Model model) {
		model.addAttribute("title", "About-Smart Contact Manager");
		return "about";
	}
}
