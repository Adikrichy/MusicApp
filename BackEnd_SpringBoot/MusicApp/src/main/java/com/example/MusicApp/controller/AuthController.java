package com.example.MusicApp.controller;

import com.example.MusicApp.model.Account;
import com.example.MusicApp.repository.AccountRepository;
import com.example.MusicApp.service.MailService;
import com.example.MusicApp.service.VerificationCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;
@CrossOrigin(origins = "*") // For testing only
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private VerificationCodeService codeService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MailService mailService;

    @Autowired
    private AccountRepository accountRepository;

    @PostMapping("/send-code")
    public ResponseEntity<String> sendResetCode(@RequestBody Map<String, String> request) {
        String email = request.get("email");

        Optional<Account> accountOptional = accountRepository.findByEmail(email);
        if (accountOptional.isEmpty()) {
            System.out.println("Email not found: " + email);
            return ResponseEntity.badRequest().body("Email not found");
        }

        Account account = accountOptional.get();
        String code = codeService.generateCode(email);

        System.out.println("Generated Code: " + code);


        mailService.sendResetCodeEmail(email, "Password Reset Code", account.getUsername(), code);

        return ResponseEntity.ok("Verification code sent to " + email);
    }




    @PostMapping("/verify-code")
    public ResponseEntity<String> verifyResetCode(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String code = request.get("code");

        if (codeService.verifyCode(email, code)) {
            codeService.invalidateCode(email);
            return ResponseEntity.ok("Code verified, you can now reset the password.");
        } else {
            return ResponseEntity.badRequest().body("Invalid code");
        }
    }


    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String newPassword = request.get("password");

        Optional<Account> accountOptional = accountRepository.findByEmail(email);
        if (accountOptional.isPresent()) {
            Account account = accountOptional.get();
            account.setPassword(passwordEncoder.encode(newPassword)); // 🔐 Hash with BCrypt
            accountRepository.save(account);
            return ResponseEntity.ok("Password reset successfully");
        } else {
            return ResponseEntity.badRequest().body("User not found");
        }
    }


}
