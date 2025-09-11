package com.udacity.jwdnd.course1.cloudstorage.services;

import com.udacity.jwdnd.course1.cloudstorage.Entity.UserEntity;
import com.udacity.jwdnd.course1.cloudstorage.Repository.UserRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    
    private final UserRepository userRepository;
    
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity user = userRepository.findByUsername(username.trim().toLowerCase())
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        
        // For now, we'll return the salt + password combined
        // Later, we'll need a custom AuthenticationProvider to handle salt separately
        String saltAndPassword = user.getSalt() + ":" + user.getPassword();
        
        return User.builder()
                .username(user.getUsername())
                .password(saltAndPassword)
                .authorities(new ArrayList<>()) // No roles for now
                .build();
    }
}
