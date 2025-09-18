package com.udacity.jwdnd.course1.cloudstorage.Controller;

import com.udacity.jwdnd.course1.cloudstorage.Entity.FileEntity;
import com.udacity.jwdnd.course1.cloudstorage.services.FileService;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Controller
@RequestMapping("/file")
public class FileController {

    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping("/upload")
    public String upload(@RequestParam("fileUpload") MultipartFile file,
                         Authentication authentication,
                         RedirectAttributes redirectAttributes) {
        try {
            fileService.upload(authentication, file);
            redirectAttributes.addFlashAttribute("message", "File uploaded.");
            redirectAttributes.addFlashAttribute("success", true);
        } catch (ResponseStatusException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getReason());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Upload failed. Please try again.");
        }
        return "redirect:/home#nav-files";
    }

    @GetMapping("/view/{id}")
    public ResponseEntity<ByteArrayResource> view(@PathVariable("id") Long id,
                                                  Authentication authentication) {
        FileEntity entity = fileService.getOwned(authentication, id);
        long length;
        try {
            length = Long.parseLong(entity.getFileSize());
        } catch (Exception ignored) {
            length = entity.getFileData() != null ? entity.getFileData().length : 0;
        }
        String filename = entity.getFilename();
        String encoded = URLEncoder.encode(filename, StandardCharsets.UTF_8);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(entity.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + encoded + "\"")
                .contentLength(length)
                .body(new ByteArrayResource(entity.getFileData()));
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable("id") Long id,
                         Authentication authentication,
                         RedirectAttributes redirectAttributes) {
        try {
            fileService.delete(authentication, id);
            redirectAttributes.addFlashAttribute("message", "File deleted.");
            redirectAttributes.addFlashAttribute("messageClass", "alert-danger");
            redirectAttributes.addFlashAttribute("success", true);
        } catch (ResponseStatusException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getReason());
        }
        return "redirect:/home#nav-files";
    }
}
