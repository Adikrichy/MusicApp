package com.example.MusicApp.service;

import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class VerificationCodeService {
    private final Map<String, String> codeStorage = new ConcurrentHashMap<>();

    public String generateCode(String email) {
        String code = String.valueOf((int)(Math.random() * 900000) + 100000);
        codeStorage.put(email, code);
        return code;
    }

    public boolean verifyCode(String email, String code) {
        return code.equals(codeStorage.get(email));
    }

    public void invalidateCode(String email) {
        codeStorage.remove(email);
    }
}
