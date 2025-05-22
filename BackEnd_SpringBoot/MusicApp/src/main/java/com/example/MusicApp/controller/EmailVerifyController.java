package com.example.MusicApp.controller;

import com.example.MusicApp.service.VerifyEmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/api/auth")
public class EmailVerifyController {

    @Autowired
    private VerifyEmailService verifyEmailService;

    @GetMapping("/verify-email")
    public String verify(@RequestParam String token, Model model) {
        boolean isVerified = verifyEmailService.confirmToken(token);
        if (isVerified) {
            model.addAttribute("message", "Verify Email Success! Welcome to Music App");
        } else {
            model.addAttribute("message", "Failed to verify or the Token has expired!Please try register again");
        }
        return "verifyEmail-status";
    }
}
