package com.example.society.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/user")
public class OtpController {

    @GetMapping("/send-otp")
    public String showSendOtpPage() {
        return "user-view/send-otp";  // resolves to templates/user-view/send-otp.html
    }

    @PostMapping("/send-otp")
    public String handleSendOtp(
            @RequestParam("mobileNo") String mobileNo,
            Model model) {
        
        // TODO: Call your backend API here (e.g., using RestTemplate)
        // e.g., restTemplate.postForObject("http://localhost:8080/auth/send-otp", ...);

        model.addAttribute("message", "OTP sent to " + mobileNo);
        return "user-view/send-otp";
    }
}
