package com.example.MusicApp.service;

import com.example.MusicApp.model.Account;
import com.example.MusicApp.model.VerificationToken;
import com.example.MusicApp.repository.AccountRepository;
import com.example.MusicApp.repository.VerificationTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class VerifyEmailService {

    @Autowired
    private VerificationTokenRepository tokenRepo;

    @Autowired
    private AccountRepository accountRepo;

    @Autowired
    private MailService mailService;

    public void sendVerificationEmail(Account account) {
        String token =  UUID.randomUUID().toString();
        LocalDateTime expiry = LocalDateTime.now().plusMinutes(5);
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(token);
        verificationToken.setExpiryDate(expiry);
        verificationToken.setAccount(account);
        tokenRepo.save(verificationToken);



        String link = "http://192.168.94.51:8080/api/auth/verify-email?token=" + token;

        String subject = "Verify your Music App account";

        mailService.send(account.getEmail(), subject, account.getUsername(), link);
    }

    public boolean confirmToken(String token) {

        Optional<VerificationToken> optionalToken = tokenRepo.findByToken(token);

        if (optionalToken.isEmpty()) {
            return false;
        }

        VerificationToken vt = optionalToken.get();

        if (vt.getExpiryDate().isBefore(LocalDateTime.now())) {
            accountRepo.delete(vt.getAccount());
            tokenRepo.delete(vt);
            return false;
        }

        Account acc = vt.getAccount();
        acc.setEnabled(true);
        accountRepo.save(acc);
        tokenRepo.delete(vt);
        return true;
    }

}
