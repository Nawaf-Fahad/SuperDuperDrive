package com.udacity.jwdnd.course1.cloudstorage.services;

import com.udacity.jwdnd.course1.cloudstorage.Entity.FileEntity;
import com.udacity.jwdnd.course1.cloudstorage.Entity.UserEntity;
import com.udacity.jwdnd.course1.cloudstorage.Repository.FileRepository;
import com.udacity.jwdnd.course1.cloudstorage.Repository.UserRepository;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URLConnection;
import java.text.Normalizer;
import java.util.List;

import static org.springframework.http.HttpStatus.*;

@Service
public class FileService {

    private final FileRepository fileRepository;
    private final UserRepository userRepository;

    public FileService(FileRepository fileRepository, UserRepository userRepository) {
        this.fileRepository = fileRepository;
        this.userRepository = userRepository;
    }
    public List<FileEntity> getFiles(Authentication  authentication) {
        var user = getUserEntity(authentication);
        return fileRepository.listForUser(user.getId());


    }

    public FileEntity getOwned(Authentication auth, Long fileId) {
        var user = getUserEntity(auth);
        return fileRepository.findByIdAndUsername(fileId, user.getId())
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "File not found"));
    }


    public void upload(Authentication auth, MultipartFile file) {
        var user = getUserEntity(auth);

        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(BAD_REQUEST, "No file provided");
        }

        var filename = sanitizeFilename(file.getOriginalFilename());
        if (filename.isBlank()) {
            throw new ResponseStatusException(BAD_REQUEST, "Filename is empty");
        }

        final byte[] bytes;
        try {
            bytes = file.getBytes();
        } catch (IOException e) {
            throw new ResponseStatusException(INTERNAL_SERVER_ERROR, "Failed to read uploaded file");
        }

        var contentType = detectContentType(bytes, file.getContentType());

        var entity = FileEntity.builder()
                .filename(filename)
                .contentType(contentType)
                .fileSize(String.valueOf(file.getSize())) // DDL uses VARCHAR
                .fileData(bytes)
                .user(user)
                .build();

        fileRepository.save(entity);
    }


    public void delete(Authentication authentication, Long fileId) {
        var user = getUserEntity(authentication);
        int affected = fileRepository.deleteByIdAndOwner(fileId, user.getId());
        if (affected == 0) throw new ResponseStatusException(NOT_FOUND, "File not found");

    }

    private UserEntity getUserEntity(Authentication  authentication) {
        if (authentication == null || authentication.getName() == null) {
            throw new ResponseStatusException(UNAUTHORIZED, "Unauthenticated");
        }

        var username = authentication.getName().trim().toLowerCase();

        return userRepository.findByUsername(username).orElseThrow(() -> new ResponseStatusException(UNAUTHORIZED, "User not found"));
    }

    private String sanitizeFilename(String original) {
        var cleaned = StringUtils.cleanPath(original == null ? "" : original);
        if (cleaned.contains("..")) throw new ResponseStatusException(BAD_REQUEST, "Invalid filename");
        return Normalizer.normalize(cleaned, Normalizer.Form.NFC);
    }

    private String detectContentType(byte[] bytes, String clientType) {
        try (var is = new ByteArrayInputStream(bytes)) {
            var guess = URLConnection.guessContentTypeFromStream(is);
            if (guess != null) return guess;
        } catch (IOException ignored) {}
        return (clientType != null && !clientType.isBlank())
                ? clientType
                : MediaType.APPLICATION_OCTET_STREAM_VALUE;
    }
}
