package com.example.MusicApp.service;

import com.example.MusicApp.DTO.RegisterRequestDTO;
import com.example.MusicApp.DTO.RegisterResponseDTO;
import com.example.MusicApp.model.Account;
import com.example.MusicApp.model.Customer;
import com.example.MusicApp.model.CustomerType;
import com.example.MusicApp.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;



@Service
@RequiredArgsConstructor
public class RegisterService {

    private final AccountRepository accountRepo;
    private final PasswordEncoder passwordEncoder;
    private final RegisterResponseDTO registerResponseDTO = new RegisterResponseDTO();

    @Autowired
    private VerifyEmailService verifyEmailService;


    public RegisterResponseDTO registerCustomer(RegisterRequestDTO req){

        registerResponseDTO.setStatus("Failed");
        if (!req.getPassword().equals(req.getConfirmPassword())){
            registerResponseDTO.setMessage("Password doesn't match.");
            return registerResponseDTO;
        }


        if (accountRepo.findByUsername(req.getUsername()).isPresent()) {
            registerResponseDTO.setMessage("Username not available!");
            return registerResponseDTO;
        }


        if (accountRepo.findByEmail(req.getEmail()).isPresent()) {
            registerResponseDTO.setMessage("Email not available!");
            return registerResponseDTO;
        }



        registerResponseDTO.setStatus("Success");
        // tạo Customer
        Customer customer = new Customer();
        customer.setFullName(null);
        customer.setPhone(null);
        customer.setMembership(CustomerType.NORMAL);

        Account acc = new Account();
        acc.setUsername(req.getUsername());
        acc.setEmail(req.getEmail());
        acc.setEnabled(false);
        acc.setRefreshToken(null);
        acc.setPassword(passwordEncoder.encode(req.getPassword()));

        customer.setAccount(acc);
        acc.setUser(customer);

        
        accountRepo.save(acc);
        verifyEmailService.sendVerificationEmail(acc);

        registerResponseDTO.setMessage("Register successfully.Please check your email for email verification!");
        return registerResponseDTO;

    }



}
