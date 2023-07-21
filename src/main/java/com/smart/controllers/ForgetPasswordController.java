package com.smart.controllers;

import java.util.Random;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.smart.dao.UserRepository;
import com.smart.entities.User;
import com.smart.helper.Message;
import com.smart.service.EmailService;

import jakarta.servlet.http.HttpSession;

@Controller
public class ForgetPasswordController {
	Random random = new Random(1000);
	@Autowired
	private EmailService emailService;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	// email id form Open Handler
	@GetMapping("/forgot")
	public String openEmailForm(Model model) {
		model.addAttribute("title", "Forget Password");
		return "forget_email_form";
	}

	@PostMapping("/send-otp")
	public String sendOTP(Model model, @RequestParam("email") String email, HttpSession session) {
		model.addAttribute("title", "Email Verification");

		// generate OTP

		int otp = random.nextInt(999999);
		System.out.println(otp);

		// send otp to email

		String subject = "OTP From SmartContactManager";
		String message = "OTP is " + otp;
		String to = email;

		Boolean flag = emailService.sendEmail(subject, message, to);

		if (flag) {
			session.setAttribute("myotp", otp);
			session.setAttribute("email", email);
			return "verify_otp";
		}
		session.setAttribute("message", "Check your email id");
		return "redirect:/forgot";
	}

	@PostMapping("/verify-otp")
	public String verifyOTP(@RequestParam("otp") int otp, Model model, HttpSession session) {

		Integer myOTP = (Integer) session.getAttribute("myotp");
		String email = (String) session.getAttribute("email");

		if (myOTP == otp) {
			// password change form

			User user = userRepository.getUserByUserName(email);
			if (user == null) {
				// error
				session.setAttribute("message", "User Does not exist with this email id");
				return "redirect:/forgot";
			}
			model.addAttribute("title", "Change Password");
			return "password_change_form";
		}
		session.setAttribute("message", "You have entered Wrong OTP ...");
		return "verify_otp";

	}

	@PostMapping("/change-password")
	public String changePassword(@RequestParam("newPassword") String newPassword,@RequestParam("confirmPassword") String confirmPassword, HttpSession session) {
		String email = (String) session.getAttribute("email");
		User user = userRepository.getUserByUserName(email);
		System.out.println(user.getPassword());
		if(newPassword.equals(confirmPassword)) {
			user.setPassword(bCryptPasswordEncoder.encode(newPassword));
			System.out.println("new" +user.getPassword());
			this.userRepository.save(user);
			session.setAttribute("message", "Your Password is Updated");
			return "redirect:/signin";
		}
		else {
			session.setAttribute("message", "Please confirm your Password Correctly !!");
			return "password_change_form";
		}

	}
}
