package com.smart.controllers;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.smart.dao.ContactRepository;
import com.smart.dao.UserRepository;
import com.smart.entities.Contact;
import com.smart.entities.User;
import com.smart.helper.Message;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/user")
public class UserController {
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ContactRepository contactRepository;
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

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

	// opening add contact form handler
	@GetMapping("/add-contact")
	public String opemAddContactForm(Model model) {
		model.addAttribute("title", "Add Contact");
		model.addAttribute("contact", new Contact());
		return "normal/add_contact_form";
	}

	// processing add contact form
	@PostMapping("/process-contact")
	public String processAddContactForm(@ModelAttribute Contact contact,
			@RequestParam("profileImage") MultipartFile file, Principal principal, HttpSession session) {
		try {
			String name = principal.getName();
			User user = userRepository.getUserByUserName(name);

			// processing and uploading file
			if (file.isEmpty()) {
				// if file is empty try our message
				System.out.println("file is empty");
				contact.setImage("contact.png");

			} else {

				// upload the file to folder and update the name in Contact

				contact.setImage(file.getOriginalFilename());

				File file1 = new ClassPathResource("/static/img").getFile();
				Path path = Paths.get(file1.getAbsolutePath() + File.separator + file.getOriginalFilename());
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

			}

			// since it is bidirectional mapping
			contact.setUser(user);
			user.getContacts().add(contact);

			userRepository.save(user);

			// success message
			session.setAttribute("message", new Message("Contact Added Succesfully!!  Add More", "alert-success"));

		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
			// error message
			session.setAttribute("message", new Message("Something went wrong !! Try Again", "alert-danger"));

		}

		return "normal/add_contact_form";
	}

	// show contacts handler
	@GetMapping("/show-contacts/{page}")
	public String showcontact(@PathVariable("page") Integer page, Model model, Principal principal) {
		model.addAttribute("title", "View Contacts");
		String userName = principal.getName();
		User userByUserName = userRepository.getUserByUserName(userName);
		int id = userByUserName.getId();

		Pageable pageable = PageRequest.of(page, 5);

		Page<Contact> contacts = contactRepository.findContactsByUser(id, pageable);
		model.addAttribute("contacts", contacts);
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", contacts.getTotalPages());
		System.out.println( "currentPage"+ page +"total pages "+ contacts.getTotalPages());
		return "normal/show_contacts";
	}

	// show particular contact details
	@GetMapping(value = "/contact/{cId}")
	public String contact(Model model, @PathVariable("cId") int cId, Principal principal) {

		String userName = principal.getName();
		User user = userRepository.getUserByUserName(userName);

		Contact contact = contactRepository.findBycIdAndUser(cId, user);
		if (contact == null || user.getId() == contact.getUser().getId()) {
			model.addAttribute("contact", contact);
		}

		model.addAttribute("title", "View Contact");
		return "normal/contact";
	}

	// delete contact handler
	@GetMapping(value = "/delete/{cId}")
	public String delete(@PathVariable("cId") int cId, Principal principal, Model model, HttpSession session) {

		String name = principal.getName();
		User user = userRepository.getUserByUserName(name);

		Contact contact = contactRepository.findBycIdAndUser(cId, user);
		if ((contact == null)) {
			session.setAttribute("message",
					new Message("You do not have the permission to delete such Contact ", "alert-success"));

		} else if (user.getId() == contact.getUser().getId()) {

			// unlink contact from user
			contact.setUser(null);

			contactRepository.delete(contact);

			session.setAttribute("message", new Message("Contact Deleted Successfully ", "alert-success"));
		} else {
			session.setAttribute("message",
					new Message("You do not have the permission to delete such Contact ", "alert-success"));
		}
		return "redirect:/user/show-contacts/0";

	}

	// Handler to show update form
	@PostMapping("/updateContact/{cId}")
	public String updateForm(@PathVariable("cId") int cId, Model model ,Principal principal) {
		
		String name = principal.getName();
		User user = userRepository.getUserByUserName(name);
		
		model.addAttribute("title", "Update Contact");
		Contact contact = contactRepository.findBycIdAndUser(cId, user);
		model.addAttribute("contact", contact);

		return "normal/updateform";
	}

	// update contact handler
	@PostMapping(value = "/process-update")
	public String upadateProcessHandler(@ModelAttribute Contact contact,
			@RequestParam("profileImage") MultipartFile file, Model model, HttpSession session, Principal principal) {
		
		String name = principal.getName();
		User user = userRepository.getUserByUserName(name);
		
		// old contact details which is in db can be accessed using cId
		Contact oldContact = contactRepository.findBycIdAndUser(contact.getcId(),user);

		try {

			if (!file.isEmpty()) {
				// delete old photo
				File deleteFile = new ClassPathResource("/static/img").getFile();
				File file1 = new File(deleteFile, oldContact.getImage());
				file1.delete();
				// upload new photo

				File saveFile = new ClassPathResource("/static/img").getFile();
				Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename());
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

				contact.setImage(file.getOriginalFilename());

			} else {
				contact.setImage(oldContact.getImage());
			}

			contact.setUser(user);
			contactRepository.save(contact);
			session.setAttribute("message", new Message("Your Contact is Updated", "alert-success"));

		} catch (Exception e) {
			// TODO: handle exception
		}
		return "redirect:/user/contact/" + contact.getcId();
	}

	// User Profile Handler
	@GetMapping("/profile")
	public String openUserProfile(Model model) {
		model.addAttribute("title", "Profile");
		return "normal/profile";
	}
	

	// Handler to show update form
	@PostMapping("/updateProfile/{id}")
	public String updateProfileForm(@PathVariable("id") int id, Model model) {
		model.addAttribute("title", "Update Profile");

		return "normal/update-profile";
	}

	// update Profile handler
	@PostMapping(value = "/profile-update")
	public String upadateProfileProcessHandler(@Valid @ModelAttribute("user") User user, BindingResult result,
			@RequestParam("profilePicture") MultipartFile file, Model model, HttpSession session, Principal principal) {

		// old contact details which is in db can be accessed using cId
		User oldUser = user;

		try {

			if (!file.isEmpty()) {
				// delete old photo
				File deleteFile = new ClassPathResource("/static/img").getFile();
				File file1 = new File(deleteFile, oldUser.getImageUrl());
				file1.delete();
				// upload new photo

				File saveFile = new ClassPathResource("/static/img").getFile();
				Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename());
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

				user.setImageUrl(file.getOriginalFilename());

			} else {
				user.setImageUrl(oldUser.getImageUrl());
			}
			user.setContacts(oldUser.getContacts());
			user.setContacts(null);
			userRepository.save(user);
			session.setAttribute("message", new Message("Your Profile is Updated", "alert-success"));

		} catch (Exception e) {
			e.printStackTrace();
			session.setAttribute("message", new Message("Something went wrong !!" + e.getMessage(), "alert-danger"));

		}
		return "redirect:/user/profile";
	}
	
	//open settings handler
	@GetMapping("/settings")
	public String openSettings(Model model) {
		model.addAttribute("title", "Settings");
		return "normal/settings";
	}
	
	
	//handler for changing password
	@PostMapping("/change-password")
	public String changepassword(@RequestParam("oldPassword") String oldPassword,
								@RequestParam("newPassword") String newPassword,@RequestParam("confirmPassword") String confirmPassword,
								Principal principal,HttpSession session){
		
		String name = principal.getName();
		User currentUser = userRepository.getUserByUserName(name);
		
		if(this.bCryptPasswordEncoder.matches(oldPassword, currentUser.getPassword())) {
			//Change password
			if(newPassword.equals(confirmPassword)) {
				currentUser.setPassword(bCryptPasswordEncoder.encode(newPassword));
				this.userRepository.save(currentUser);
				session.setAttribute("message", new Message("Your Password is Updated", "alert-success"));
			}else {
				session.setAttribute("message", new Message("Password confirmation failed .Please confirm new Password properly", "alert-danger"));
				return "redirect:/user/settings";
			}
			
		}else {
			//error message
			session.setAttribute("message", new Message("Old Password is Incorrect Please write correct password", "alert-danger"));
			return "redirect:/user/settings";
		}
		return "redirect:/user/index";
	}
}
