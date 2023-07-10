package com.smart.controllers;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.smart.dao.UserRepository;
import com.smart.entities.Contact;
import com.smart.entities.User;

@Controller
@RequestMapping("/user")
public class UserController {
	@Autowired
	private UserRepository userRepository;

	@ModelAttribute
	public void addCommonData(Model model, Principal principal) {
		String userName = principal.getName();
		User user = userRepository.getUserByUserName(userName);
		model.addAttribute("user", user);

	}

	@GetMapping("/index")
	public String Dashboard(Model model, Principal principal) {
		model.addAttribute("title", "User Dashboard");
		return "normal/user_dashboard";
	}

	//opening add contact form handler
	@GetMapping("/add-contact")
	public String opemAddContactForm(Model model) {
		model.addAttribute("title", "Add Contact");
		model.addAttribute("contact", new Contact());
		return "normal/add_contact_form";
	}
	
	//processing add contact form
	@PostMapping("process-contact")
	public String processAddContactForm(@ModelAttribute Contact contact,@RequestParam("profileImage") MultipartFile file,Principal principal) {
		try {
			String name = principal.getName();
			User user = userRepository.getUserByUserName(name);
			
			//processing and uploading file
			if(file.isEmpty()) {
				//if file is empty try our message
				System.out.println("file is empty");
			}else {
				
				//upload the file to folder and update the name in Contact
				
				contact.setImage(file.getOriginalFilename());
				
				File file1 = new ClassPathResource("/static/img").getFile();
				Path path = Paths.get(file1.getAbsolutePath()+File.separator+file.getOriginalFilename());
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				
				
			}
			
			//since it is bidirectional mapping
			contact.setUser(user);
			user.getContacts().add(contact);
			
			userRepository.save(user);
			
		}catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		
		return "normal/add_contact_form";
	}

}
