package com.example.MusicApp.service;

import com.example.MusicApp.model.*;
import com.example.MusicApp.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final AccountRepository accountRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = accountRepository.findByUsername(username)
                .orElseThrow(()->new UsernameNotFoundException("User's account not found"));

        if (!account.isEnabled()) {
            throw new DisabledException("Account is disabled");
        }
        Collection<GrantedAuthority> authorities = getAuthority(account);

        return org.springframework.security.core.userdetails.User.builder()
                .username(account.getUsername())
                .password(account.getPassword())
                .authorities(authorities)
                .disabled(!account.isEnabled())
                .accountExpired(false)
                .credentialsExpired(false)
                .accountLocked(false)
                .build();
    }

    private Collection<GrantedAuthority> getAuthority(Account account) {
        User user = account.getUser();
        GrantedAuthority authority = switch (user) {
            case Customer customer -> new SimpleGrantedAuthority("ROLE_CUSTOMER");
            case Artist artist -> new SimpleGrantedAuthority("ROLE_ARTIST");
            case Staff staff -> new SimpleGrantedAuthority("ROLE_STAFF");
            case Owner owner -> new SimpleGrantedAuthority("ROLE_OWNER");
            case null, default -> new SimpleGrantedAuthority("ROLE_USER");
        };

        return List.of(authority);
    }
}
/*
Role Management: In CustomUserDetailsService,
we're checking the type of user (Customer, Artist, Staff, Owner) and assigning
corresponding roles (ROLE_CUSTOMER, ROLE_ARTIST, etc.) using GrantedAuthority.

Single Table Inheritance: The User class uses SINGLE_TABLE inheritance strategy,
and each subclass (Customer, Artist, Staff, Owner) has its own discriminator value.

Security: The roles are assigned via SimpleGrantedAuthority and
are used in the UserDetails returned by CustomUserDetailsService.
* */
