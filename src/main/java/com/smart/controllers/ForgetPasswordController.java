package com.smart.controllers;

import java.util.Random;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.smart.service.EmailService;

import jakarta.servlet.http.HttpSession;

@Controller
public class ForgetPasswordController {
	Random random = new Random(1000);
	@Autowired
	private EmailService emailService;
	
	//email id form Open Handler
	@GetMapping("/forgot")
	public String openEmailForm(Model model) {
		model.addAttribute("title", "Forget Password");
		return "forget_email_form";
	}
	
	@PostMapping("/send-otp")
	public String sendOTP(Model model,@RequestParam("email") String email,HttpSession session) {
		model.addAttribute("title", "Email Verification");
		
		//generate OTP of 4 digit
		
		int otp = random.nextInt(9999);
		System.out.println(otp);
		
		//send otp to email
		
		String subject="OTP From SmartContactManager";
		String message="OTP is "+otp;
		String to=email;
		
		Boolean flag=emailService.sendEmail(subject, message, to);
		
		if(flag) {
			session.setAttribute("otp", otp);
			return "verify_otp"; 
		}
		session.setAttribute("message", "Check your email id");
		return "redirect:/forgot";
	}
}
