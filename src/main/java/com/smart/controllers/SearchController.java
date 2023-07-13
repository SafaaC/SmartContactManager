package com.smart.controllers;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.smart.dao.ContactRepository;
import com.smart.dao.UserRepository;
import com.smart.entities.Contact;
import com.smart.entities.User;

@RestController  //dos not return a page or view ,it returns json or string
public class SearchController {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private ContactRepository contactRepository;
	
	//search handler
	@GetMapping("search/{query}")
	public ResponseEntity<?> search(@PathVariable("query") String query,Principal principal){
	
		String name = principal.getName();
		User user = userRepository.getUserByUserName(name);
		List<Contact> contacts = contactRepository.findByNameContainingAndUser(query, user);
		return ResponseEntity.ok(contacts);
	}

}
