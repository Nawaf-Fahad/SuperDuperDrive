package com.udacity.jwdnd.course1.cloudstorage.services;

import com.udacity.jwdnd.course1.cloudstorage.Entity.NoteEntity;
import com.udacity.jwdnd.course1.cloudstorage.Entity.UserEntity;
import com.udacity.jwdnd.course1.cloudstorage.Repository.NoteRepository;
import com.udacity.jwdnd.course1.cloudstorage.Repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.springframework.http.HttpStatus.*;
@Service
@Transactional
public class NoteService {
    private final NoteRepository noteRepository;
    private final UserRepository userRepository;

    public NoteService(NoteRepository noteRepository, UserRepository userRepository) {
        this.noteRepository = noteRepository;
        this.userRepository = userRepository;
    }


    public List<NoteEntity> list(Authentication  authentication){
        var user = getUserEntity(authentication);

        return noteRepository.findByUser_IdOrderByIdDesc(user.getId());

    }


    @Transactional
    public void upsert(Authentication auth, Long noteId, String title, String description){
        var user = getUserEntity(auth);
        title = validateTitle(title);
        description = validateDescription(description);

        if (noteId == null){
            var noteEntity = NoteEntity.builder()
                    .user(user)
                    .title(title)
                    .description(description)
                    .build();
            noteRepository.save(noteEntity);
        }

    }


    @Transactional
    public void deleteNote(Authentication  authentication, Long noteId){
        var user = getUserEntity(authentication);
        int affected = noteRepository.deleteByIdAndOwner(noteId, user.getId());
        if (affected == 0) {
            throw new ResponseStatusException(NOT_FOUND, "Note not found");
        }
    }
    public UserEntity getUserEntity(Authentication  authentication) {
        if (authentication == null || authentication.getName() == null) {
            throw new ResponseStatusException(UNAUTHORIZED, "Unauthenticated");
        }

        var username = authentication.getName().trim().toLowerCase();

        return userRepository.findByUsername(username).orElseThrow(() -> new ResponseStatusException(UNAUTHORIZED, "User not found"));

    }


    private String validateTitle(String title) {
        if (title == null) throw new ResponseStatusException(BAD_REQUEST, "Title is required");
        title = title.trim();
        if (title.isEmpty()) throw new ResponseStatusException(BAD_REQUEST, "Title is empty");
        if (title.length() > 20) throw new ResponseStatusException(BAD_REQUEST, "Title max length is 20");
        return title;
    }

    private String validateDescription(String description) {
        if (description == null) return null; // nullable per DDL
        description = description.trim();
        if (description.length() > 1000)
            throw new ResponseStatusException(BAD_REQUEST, "Description max length is 1000");
        return description;
    }
}
