package com.udacity.jwdnd.course1.cloudstorage.services;

import com.udacity.jwdnd.course1.cloudstorage.Entity.CredentialEntity;
import com.udacity.jwdnd.course1.cloudstorage.Entity.UserEntity;
import com.udacity.jwdnd.course1.cloudstorage.Repository.CredentialRepository;
import com.udacity.jwdnd.course1.cloudstorage.Repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.springframework.http.HttpStatus.*;

@Service
@Transactional
public class CredentialService {

    private final CredentialRepository credentialRepository;
    private final UserRepository userRepository;
    private final EncryptionService encryptionService;

    public CredentialService(CredentialRepository credentialRepository, UserRepository userRepository, EncryptionService encryptionService) {
        this.credentialRepository = credentialRepository;
        this.userRepository = userRepository;
        this.encryptionService = encryptionService;
    }

    public List<CredentialEntity> list(Authentication authentication) {
        var user = getUserEntity(authentication);
        return credentialRepository.listForUser(user.getId());
    }

    public void upsert(Authentication auth, Long credentialId, String url, String username, String password) {
        var user = getUserEntity(auth);
        url = validateUrl(url);
        username = validateUsername(username);
        password = validatePassword(password);
        
        // Encrypt password before storing
        String encryptedPassword = encryptionService.encryptValue(password, getEncryptionKey(user));

        if (credentialId == null) {
            var entity = CredentialEntity.builder()
                    .user(user)
                    .url(url)
                    .username(username)
                    .password(encryptedPassword)
                    .build();
            credentialRepository.save(entity);
        } else {
            var existing = credentialRepository.findByIdAndUserId(credentialId, user.getId())
                    .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Credential not found"));
            existing.setUrl(url);
            existing.setUsername(username);
            existing.setPassword(encryptedPassword);
            credentialRepository.save(existing);
        }
    }

    public void delete(Authentication auth, Long credentialId) {
        var user = getUserEntity(auth);
        int affected = credentialRepository.deleteByIdAndOwner(credentialId, user.getId());
        if (affected == 0) throw new ResponseStatusException(NOT_FOUND, "Credential not found");
    }

    private UserEntity getUserEntity(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            throw new ResponseStatusException(UNAUTHORIZED, "Unauthenticated");
        }
        var username = authentication.getName().trim().toLowerCase();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(UNAUTHORIZED, "User not found"));
    }

    private String validateUrl(String url) {
        if (url == null) throw new ResponseStatusException(BAD_REQUEST, "URL is required");
        url = url.trim();
        if (url.isEmpty()) throw new ResponseStatusException(BAD_REQUEST, "URL is empty");
        if (url.length() > 100) throw new ResponseStatusException(BAD_REQUEST, "URL max length is 100");
        return url;
    }

    private String validateUsername(String username) {
        if (username == null) throw new ResponseStatusException(BAD_REQUEST, "Username is required");
        username = username.trim();
        if (username.isEmpty()) throw new ResponseStatusException(BAD_REQUEST, "Username is empty");
        if (username.length() > 30) throw new ResponseStatusException(BAD_REQUEST, "Username max length is 30");
        return username;
    }

    private String validatePassword(String password) {
        if (password == null) throw new ResponseStatusException(BAD_REQUEST, "Password is required");
        password = password.trim();
        if (password.isEmpty()) throw new ResponseStatusException(BAD_REQUEST, "Password is empty");
        if (password.length() > 30) throw new ResponseStatusException(BAD_REQUEST, "Password max length is 30");
        return password;
    }

    public String getDecryptedPassword(Authentication auth, Long credentialId) {
        var user = getUserEntity(auth);
        var credential = credentialRepository.findByIdAndUserId(credentialId, user.getId())
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Credential not found"));
        return encryptionService.decryptValue(credential.getPassword(), getEncryptionKey(user));
    }

    private String getEncryptionKey(UserEntity user) {
        // Use username as encryption key (you might want to use a more sophisticated approach)
        String key = user.getUsername();
        // Pad or truncate to 16 characters for AES
        if (key.length() < 16) {
            key = String.format("%-16s", key).replace(' ', '0');
        } else if (key.length() > 16) {
            key = key.substring(0, 16);
        }
        return key;
    }
}
