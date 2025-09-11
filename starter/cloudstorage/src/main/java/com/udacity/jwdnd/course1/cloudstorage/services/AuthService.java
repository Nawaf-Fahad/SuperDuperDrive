package com.udacity.jwdnd.course1.cloudstorage.services;


import com.udacity.jwdnd.course1.cloudstorage.DTOs.SignupRequest;
import com.udacity.jwdnd.course1.cloudstorage.DTOs.SignupResponse;
import com.udacity.jwdnd.course1.cloudstorage.Entity.UserEntity;
import com.udacity.jwdnd.course1.cloudstorage.Repository.UserRepository;
import com.udacity.jwdnd.course1.cloudstorage.security.PasswordHasher;
import com.udacity.jwdnd.course1.cloudstorage.services.exceptions.UsernameAlreadyTakenException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserRepository users;

    public AuthService(UserRepository users) {
        this.users = users;
    }


    @Transactional
    public SignupResponse signup(SignupRequest signupRequest) {
        final String username = signupRequest.username().trim().toLowerCase();
        if(users.existsByUsername(username)){
            throw new UsernameAlreadyTakenException(username);
        }
        String salt = PasswordHasher.newSaltBase64();
        String hashed = PasswordHasher.hashBase64(signupRequest.password().toCharArray(), salt);

        UserEntity userEntity = UserEntity.builder()
                .username(username)
                .salt(salt)
                .password(hashed)
                .firstName(signupRequest.firstName().trim())
                .lastName(signupRequest.lastname().trim())
                .build();

        UserEntity saved = users.save(userEntity);

        return new SignupResponse(
                saved.getId(),
                saved.getUsername(),
                saved.getFirstName(),
                saved.getLastName()

        );

    }
}
