package com.example.MusicApp.controller;

import com.example.MusicApp.DTO.UpdateDTO;
import com.example.MusicApp.DTO.UserDTO;
import com.example.MusicApp.DTO.UpdateProfileResponseDTO;
import com.example.MusicApp.model.Account;
import com.example.MusicApp.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    private AccountService accountService;
    @PutMapping("/update")
    public UpdateProfileResponseDTO updateAccount(@RequestBody UpdateDTO account) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return accountService.updateProfileByUsername(username, account);
    }

    @GetMapping("/profile")
    public UserDTO getProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return accountService.getCurrentUser(username);
    }

}
