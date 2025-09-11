package com.udacity.jwdnd.course1.cloudstorage.security;

import com.udacity.jwdnd.course1.cloudstorage.Repository.UserRepository;
import com.udacity.jwdnd.course1.cloudstorage.services.HashService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class CustomPasswordEncoder implements PasswordEncoder {
    
    private final HashService hashService;
    private final UserRepository userRepository;
    
    public CustomPasswordEncoder(HashService hashService, UserRepository userRepository) {
        this.hashService = hashService;
        this.userRepository = userRepository;
    }
    
    @Override
    public String encode(CharSequence rawPassword) {
        String salt = PasswordHasher.newSaltBase64();
        return salt + ":" + PasswordHasher.hashBase64(rawPassword.toString().toCharArray(), salt);
    }
    
    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        String[] parts = encodedPassword.split(":", 2);
        if (parts.length != 2) {
            return false;
        }
        
        String salt = parts[0];
        String storedHash = parts[1];
        
        String computedHash = PasswordHasher.hashBase64(rawPassword.toString().toCharArray(), salt);
        return storedHash.equals(computedHash);
    }
}
