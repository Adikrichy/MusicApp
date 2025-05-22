package com.example.MusicApp.service;

import com.example.MusicApp.model.VerificationToken;
import com.example.MusicApp.repository.AccountRepository;
import com.example.MusicApp.repository.VerificationTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CleanupAccountService {
    @Autowired
    private VerificationTokenRepository tokenRepo;

    @Autowired
    private AccountRepository accountRepo;

    @Scheduled(fixedRate = 60000)
    public void deleteExpiredAccounts() {
        try {
            System.out.println("Running cleanup job at " + LocalDateTime.now());
            List<VerificationToken> expired = tokenRepo.findAllByExpiryDateBefore(LocalDateTime.now());
            System.out.println("Found " + expired.size() + " expired tokens");
            for (VerificationToken token : expired) {
                if (!token.getAccount().isEnabled()) {
                    System.out.println("Deleting account: " + token.getAccount().getId());
                    tokenRepo.delete(token);
                    accountRepo.delete(token.getAccount());

                }
            }
        } catch (Exception e) {
            e.printStackTrace(); // or log with a logger
        }
    }

}
