package com.example.MusicApp.service;

import com.example.MusicApp.DTO.UpdateDTO;
import com.example.MusicApp.DTO.UserDTO;
import com.example.MusicApp.DTO.UpdateProfileResponseDTO;
import com.example.MusicApp.model.Account;
import com.example.MusicApp.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AccountService {
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private JwtService jwtService;

    public UpdateProfileResponseDTO updateProfile(Long accountId, UpdateDTO request) {
        Account account = accountRepository.findById(accountId).
                orElseThrow(() -> new RuntimeException("Account not found"));
        boolean usernameChanged = false;
        if(request.getUsername() !=null && !request.getUsername().equals(account.getUsername())) {
            Optional<Account> existing = accountRepository.findByUsername(request.getUsername());
            if(existing.isPresent()) {
                throw new RuntimeException("Account already exists");
            }
            account.setUsername(request.getUsername());
            usernameChanged = true;
        }
        if(request.getEmail() !=null && !request.getEmail().equals(account.getEmail())) {
            Optional<Account> existing = accountRepository.findByEmail(request.getEmail());
            if(existing.isPresent()) {
                throw new RuntimeException("Email already exists");
            }
            account.setEmail(request.getEmail());
        }

        String newAccessToken = null;
        String newRefreshToken = null;
        if (usernameChanged) {
            newAccessToken = jwtService.generateAccessToken(account.getUsername());
            newRefreshToken = jwtService.generateRefreshToken(account.getUsername());
            account.setRefreshToken(newRefreshToken);
        }

        accountRepository.save(account);
        return new UpdateProfileResponseDTO(newAccessToken, newRefreshToken, "Profile updated");
    }

    public UserDTO getCurrentUser(String username) {
        Optional<Account> account = accountRepository.findByUsername(username);
        if (account.isEmpty()) return null;
        return new UserDTO(account.get().getId(), account.get().getUsername(), account.get().getEmail());
    }

    public UpdateProfileResponseDTO updateProfileByUsername(String username, UpdateDTO request) {
        Account account = accountRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("Account not found"));
        boolean usernameChanged = false;
        if(request.getUsername() !=null && !request.getUsername().equals(account.getUsername())) {
            Optional<Account> existing = accountRepository.findByUsername(request.getUsername());
            if(existing.isPresent()) {
                throw new RuntimeException("Account already exists");
            }
            account.setUsername(request.getUsername());
            usernameChanged = true;
        }
        if(request.getEmail() !=null && !request.getEmail().equals(account.getEmail())) {
            Optional<Account> existing = accountRepository.findByEmail(request.getEmail());
            if(existing.isPresent()) {
                throw new RuntimeException("Email already exists");
            }
            account.setEmail(request.getEmail());
        }
        String newAccessToken = null;
        String newRefreshToken = null;
        if (usernameChanged) {
            newAccessToken = jwtService.generateAccessToken(account.getUsername());
            newRefreshToken = jwtService.generateRefreshToken(account.getUsername());
            account.setRefreshToken(newRefreshToken);
        }
        accountRepository.save(account);
        return new UpdateProfileResponseDTO(newAccessToken, newRefreshToken, "Profile updated");
    }
}
