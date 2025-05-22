package com.example.MusicApp.service;

import com.example.MusicApp.DTO.LoginRequestDTO;
import com.example.MusicApp.DTO.LoginResponseDTO;
import com.example.MusicApp.DTO.RefreshTokenRequestDTO;
import com.example.MusicApp.model.Account;
import com.example.MusicApp.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final AccountRepository accountRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;


    @Transactional
    public LoginResponseDTO login(LoginRequestDTO request) {

        Authentication authentication;
        try {

            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );
        } catch (BadCredentialsException ex) {

            return new LoginResponseDTO(null, null, "Invalid username or password.");
        }

        // Set authentication in security context
        SecurityContextHolder.getContext().setAuthentication(authentication);


        String accessToken = jwtService.generateAccessToken(request.getUsername());
        String refreshToken = jwtService.generateRefreshToken(request.getUsername());


        Account account = accountRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new IllegalStateException("User not found after authentication"));
        account.setRefreshToken(refreshToken);
        accountRepository.save(account);


        return new LoginResponseDTO(accessToken, refreshToken, "Login successfully");
    }

    @Transactional
    public LoginResponseDTO refreshToken(RefreshTokenRequestDTO request) {
        String refreshToken = request.getRefreshToken();


        if (!jwtService.validateRefreshToken(refreshToken)) {
            return new LoginResponseDTO(null, null, "Refresh token expired or invalid.");
        }


        String username = jwtService.extractUsername(refreshToken);



        Optional<Account> optionalAccount = accountRepository.findByUsername(username);
        if (optionalAccount.isEmpty()) {
            return new LoginResponseDTO(null, null, "User not found.");
        }

        Account account = optionalAccount.get();


        if (!refreshToken.equals(account.getRefreshToken())) {
            return new LoginResponseDTO(null, null, "Invalid refresh token. Please login again.");
        }

        // Generate new tokens
        String newAccessToken = jwtService.generateAccessToken(username);
        String newRefreshToken = jwtService.generateRefreshToken(username);


        account.setRefreshToken(newRefreshToken);
        accountRepository.save(account);

        return new LoginResponseDTO(newAccessToken, newRefreshToken, "Token refreshed successfully.");
    }

}
